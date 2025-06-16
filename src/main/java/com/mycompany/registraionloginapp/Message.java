package com.mycompany.registraionloginapp;

import javax.swing.*;
import java.util.List;
import java.util.Random;

/**
 *
 * @author muano
 */
public class Message {

    private final String messageID;
    private static int totalMessagesSent = 0;
    private int numMessageSent;
    private final String recipient;
    private final String messageContent;
    private final String messageHash;
    private final String flag; // Added to communicate with MessageManager

    //original constructor, adapted to set a flag
    public Message(String recipient, String messageContent, String flag) {
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.flag = flag;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // Private constructor for reconstructing from DTO/JSON
    private Message(String recipient, String messageContent, String flag, String messageID, String messageHash) {
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.flag = flag;
        this.messageID = messageID;
        this.messageHash = messageHash;
    }

    //Getters 
    public String getMessageID() {
        return messageID;
    }

    public int getNumMessageSent() {
        return numMessageSent;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getFlag() {
        return flag;
    }

    // --- Your Original Static Methods ---
    public static void resetTotalMessagesSentForTest() {
        totalMessagesSent = 0;
    }

    public static int returnTotalMessagesSent() {
        return totalMessagesSent;
    }

    public static void incrementSentCount() {
        totalMessagesSent++;
    }

    private String generateMessageID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    //checking message ID
    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() == 10;
    }
    
    //Validates the content of a message string.
     public static String validateMessageContent(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return "Message cannot be empty.";
        }
        if (messageText.length() > 250) {
            int excessChars = messageText.length() - 250;
            return "Message exceeds 250 characters by " + excessChars + ", please reduce size.";
        }
        return "Message ready to send.";
    }
    //Checking cell phone number validation
    public boolean checkRecipientCell() {
        if (this.recipient == null) {
            return false;
        }

        //Check for the international format.
        return this.recipient.matches("^\\+27[-\\s]?[6-8][0-9]{1}[-\\s]?[0-9]{3}[-\\s]?[0-9]{4}$");
    }

    //createMessageHash for each message
    public final String createMessageHash() {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return "MESSAGE_EMPTY";
        }
        this.numMessageSent = totalMessagesSent + 1;
        String[] words = messageContent.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        String idPrefix = messageID.substring(0, Math.min(messageID.length(), 2));
        return (idPrefix + ":" + this.numMessageSent + ":" + firstWord + lastWord).toUpperCase();
    }

    //returnTotalMessages sent by the user
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    //sentMessage, store or disregard menu
    public static String sentMessage(String messageContent) {
        while (true) {
            String prompt = "What would you like to do with this message?\n\n"
                    + "\"" + messageContent + "\"\n\n"
                    + "Choose an option:\n\n"
                    + "1. Send Message\n"
                    + "2. Disregard Message\n"
                    + "3. Store Message to send later\n\n"
                    + "Enter your choice (1-3):";
            String input = JOptionPane.showInputDialog(null, prompt, "Message Action", JOptionPane.QUESTION_MESSAGE);

            if (input == null) {
                return "No action taken.";
            }

            try {
                int choice = Integer.parseInt(input.trim());
                switch (choice) {
                    case 1:
                        return "Message successfully sent";
                    case 2:
                        return "Message disregarded. Press 0 to delete the message.";
                    case 3:
                        return "Message successfully stored.";
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid choice. Please enter 1, 2, or 3.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //printMessages (Works with parallel arrays)
    public static String printMessages(List<String> recipients, List<String> contents, List<String> ids, List<String> hashes) {
        if (recipients.isEmpty()) {
            return "--- No Sent Messages to Report ---";
        }
        StringBuilder sb = new StringBuilder("--- Full Report of Sent Messages ---\n\n");
        for (int i = 0; i < recipients.size(); i++) {
            sb.append("Message Details:\n\n");
            sb.append("  Message Hash: ").append(hashes.get(i)).append("\n");
            sb.append("  Recipient: ").append(recipients.get(i)).append("\n");
            sb.append("  Message: \"").append(contents.get(i)).append("\"\n");
            sb.append("----------------------------------\n");
        }
        return sb.toString();
    }

    //A factory method to create a Message instance from a DTO loaded from JSON.
    public static Message fromDTO(MessageDataTransferObject dto) {
        return new Message(
                dto.getRecipient(), dto.getMessageContent(), "Stored",
                dto.getMessageID(), dto.getMessageHash()
        );
    }

    @Override
    public String toString() {
        return """
               Message Details:
               
                 Message ID: """ + messageID + "\n"
                + "  Message Hash: " + messageHash + "\n"
                + "  Recipient: " + recipient + "\n"
                + "  Message: \"" + messageContent + "\"";
    }
}
