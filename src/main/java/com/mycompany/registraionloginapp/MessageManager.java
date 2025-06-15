/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author muano
 */
public class MessageManager {

    private static final String JSON_FILE_PATH = "messages.json";

    // --- INTERNAL STORAGE: PARALLEL ARRAYS ---
    private final List<String> sentRecipients = new ArrayList<>();
    private final List<String> sentMessagesContent = new ArrayList<>();
    private final List<String> sentMessageIDs = new ArrayList<>();
    private final List<String> sentMessageHashes = new ArrayList<>();

    private final List<String> storedRecipients = new ArrayList<>();
    private final List<String> storedMessagesContent = new ArrayList<>();
    private final List<String> storedMessageIDs = new ArrayList<>();
    private final List<String> storedMessageHashes = new ArrayList<>();

    private final List<String> disregardedRecipients = new ArrayList<>();
    private final List<String> disregardedMessagesContent = new ArrayList<>();

    private final List<Message> sentThisSession = new ArrayList<>();
    private final List<Message> storedThisSession = new ArrayList<>();
    private final List<Message> disregardedThisSession = new ArrayList<>();

    public MessageManager() {
        try {

            loadStoredMessagesFromJson();
        } catch (IOException e) {
            System.err.println("STARTUP WARNING: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Could not load previously stored messages. A new message list will be used.", "File Load Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Processes a Message object, unpacking its data into the correct set of internal parallel arrays based on the message's flag. @param message
    public void processMessage(Message message) {
        switch (message.getFlag()) {
            case "Sent":
                sentRecipients.add(message.getRecipient());
                sentMessagesContent.add(message.getMessageContent());
                sentMessageIDs.add(message.getMessageID());
                sentMessageHashes.add(message.getMessageHash());
                sentThisSession.add(message);
                break;
            case "Stored":
                storedRecipients.add(message.getRecipient());
                storedMessagesContent.add(message.getMessageContent());
                storedMessageIDs.add(message.getMessageID());
                storedMessageHashes.add(message.getMessageHash());
                storedThisSession.add(message);
                break;
            case "Disregard":
            default:
                disregardedRecipients.add(message.getRecipient());
                disregardedMessagesContent.add(message.getMessageContent());
                disregardedThisSession.add(message);
                break;
        }
    }

    //Only get the sent message summary in the runtime   
    public String getRecentSentMessagesSummary() {
        StringBuilder sb = new StringBuilder("--- Recently Sent Messages ---\n");

        if (sentThisSession.isEmpty()) {
            return "No messages have been sent in this session yet.";
        }

        // This loop ONLY looks at the sentThisSession list.
        for (Message msg : sentThisSession) {
            sb.append("\n").append(msg.toString()).append("\n");
            sb.append("-------------------------------------\n");
        }

        return sb.toString().trim();
    }

    //Find the longest sent message
    public String findLongestMessage() {
        String longestMessage = "";
        int maxLength = -1;

        // This loop ONLY looks at the list of SENT messages for this session.
        // It CANNOT see the stored or disregarded messages.
        for (Message message : sentThisSession) {
            if (message.getMessageContent() != null && message.getMessageContent().length() > maxLength) {
                maxLength = message.getMessageContent().length();
                longestMessage = message.getMessageContent();
            }
        }

        return longestMessage.isEmpty() ? "No messages have been sent in this session yet." : longestMessage;
    }

    //Searches the parallel arrays to find a message by its ID
    public Message searchByMessageID(String searchId) {
        for (int i = 0; i < sentMessageIDs.size(); i++) {
            if (sentMessageIDs.get(i).equals(searchId)) {
                return new Message(sentRecipients.get(i), sentMessagesContent.get(i), "Sent");
            }
        }
        for (int i = 0; i < storedMessageIDs.size(); i++) {
            if (storedMessageIDs.get(i).equals(searchId)) {
                return new Message(storedRecipients.get(i), storedMessagesContent.get(i), "Stored");
            }
        }
        return null;
    }

    //Searches the parallel arrays to find all messages for a Recipient
    public List<Message> searchByRecipient(String recipientToFind) {
        List<Message> results = new ArrayList<>();
        for (int i = 0; i < sentRecipients.size(); i++) {
            if (sentRecipients.get(i).equals(recipientToFind)) {
                results.add(new Message(sentRecipients.get(i), sentMessagesContent.get(i), "Sent"));
            }
        }
        for (int i = 0; i < storedRecipients.size(); i++) {
            if (storedRecipients.get(i).equals(recipientToFind)) {
                results.add(new Message(storedRecipients.get(i), storedMessagesContent.get(i), "Stored"));
            }
        }
        return results;
    }

    //Deletes from the 'sent' or 'stored' parallel arrays using a message hash
    public String deleteByHash(String hashToDelete) throws IOException {
        // --- First, search the SENT messages ---
        int sentIndexToDelete = -1;
        for (int i = 0; i < sentMessageHashes.size(); i++) {
            if (sentMessageHashes.get(i).equals(hashToDelete)) {
                sentIndexToDelete = i;
                break;
            }
        }

        if (sentIndexToDelete != -1) {
            String deletedContent = sentMessagesContent.get(sentIndexToDelete);
            // Remove from all parallel 'sent' arrays
            sentRecipients.remove(sentIndexToDelete);
            sentMessagesContent.remove(sentIndexToDelete);
            sentMessageIDs.remove(sentIndexToDelete);
            sentMessageHashes.remove(sentIndexToDelete);

            // Also remove from the session list for consistency
            sentThisSession.removeIf(msg -> msg.getMessageHash().equals(hashToDelete));

            return "SENT message \"" + deletedContent + "\" successfully deleted.";
        }

        // --- If not found in sent, search the STORED messages ---
        int storedIndexToDelete = -1;
        for (int i = 0; i < storedMessageHashes.size(); i++) {
            if (storedMessageHashes.get(i).equals(hashToDelete)) {
                storedIndexToDelete = i;
                break;
            }
        }

        if (storedIndexToDelete != -1) {

            String deletedContent = storedMessagesContent.get(storedIndexToDelete);
            // Remove from all parallel 'stored' arrays
            storedRecipients.remove(storedIndexToDelete);
            storedMessagesContent.remove(storedIndexToDelete);
            storedMessageIDs.remove(storedIndexToDelete);
            storedMessageHashes.remove(storedIndexToDelete);

            // Also remove from the session list for consistency
            storedThisSession.removeIf(msg -> msg.getMessageHash().equals(hashToDelete));

            // IMPORTANT: Only save to file if a STORED message was deleted
            saveStoredMessagesToFile();
            return "STORED message \"" + deletedContent + "\" successfully deleted.";
        }

        // --- If the hash was not found in either list ---
        return "Message with hash [" + hashToDelete + "] not found in sent or stored messages.";
    }

    //Generates a report from the 'sent' parallel arrays.
    public String generateSentMessagesReport() {
        return Message.printMessages(sentRecipients, sentMessagesContent, sentMessageIDs, sentMessageHashes);
    }

    //Loads stored messages from JSON into the internal parallel arrays.
    public void loadStoredMessagesFromJson() throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<MessageDataTransferObject>>() {
            }.getType();
            List<MessageDataTransferObject> dtoList = gson.fromJson(reader, listType);

            if (dtoList != null) {
                // Clear existing stored arrays
                storedRecipients.clear();
                storedMessagesContent.clear();
                storedMessageIDs.clear();
                storedMessageHashes.clear();

                // Unpack DTOs into the internal parallel arrays
                for (MessageDataTransferObject dto : dtoList) {
                    processMessage(Message.fromDTO(dto));
                }
            }
        } catch (java.io.FileNotFoundException e) {
            /* OK if file doesn't exist */ }
    }

    //Saves the current 'stored' parallel arrays to the JSON file.
    public void saveStoredMessagesToFile() throws IOException {
        List<MessageDataTransferObject> dtoList = new ArrayList<>();
        for (int i = 0; i < storedRecipients.size(); i++) {
            MessageDataTransferObject dto = new MessageDataTransferObject();
            dto.setRecipient(storedRecipients.get(i));
            dto.setMessageContent(storedMessagesContent.get(i));
            dto.setMessageID(storedMessageIDs.get(i));
            dto.setMessageHash(storedMessageHashes.get(i));
            dtoList.add(dto);
        }
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(JSON_FILE_PATH)) {
            gson.toJson(dtoList, writer);
        }
    }

    // --- Getters for testing and the Main app ---
    public List<String> getSentMessagesContent() {
        return this.sentMessagesContent;
    }

    public String displaySentMessageSummary(User sender) {
        if (sentRecipients.isEmpty()) {
            return "No messages have been sent in this session.";
        }
        StringBuilder sb = new StringBuilder("--- Sent Messages Summary ---\n\n");
        for (int i = 0; i < sentRecipients.size(); i++) {
            sb.append("Sender: ").append(sender.getPhoneNumber())
                    .append("  ->  Recipient: ").append(sentRecipients.get(i)).append("\n");
        }
        return sb.toString();
    }
}
