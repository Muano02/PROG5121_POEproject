package com.mycompany.registraionloginapp;

import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class Message {

    private String messageID;
    private static int totalMessagesSent = 0; // Static counter for all messages sent
    private int numMessageSent; // Instance specific counter
    private String recipient;
    private String messageContent;
    private String messageHash;

    private static final String MESSAGES_FILE = "messages.json"; // File to store messages

    // Default constructor for Gson deserialization
    public Message() {
    }

    public Message(String recipient, String messageContent) {
        
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.numMessageSent = ++totalMessagesSent; // Increment static counter and assign to instance
        this.messageHash = createMessageHash(); // Generate hash upon creation
    }

    // Getters for testing and display (needed for Gson serialization)
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

    // This method ensures that the message ID is a 10-digit number.
    private String generateMessageID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // This method ensures that the message ID is not more than ten characters.
    public boolean checkMessageID() {
        return this.messageID.length() == 10 && this.messageID.matches("\\d+");
    }

    // This method ensures that the recipient cell number is no more than ten characters long and starts with an international code.
    // Prioritizing robust SA international format over strict 10-char total length.
    public boolean checkRecipientCell() {
        return this.recipient.matches("^\\+27[-\\s]?[6-8][0-9]{1}[-\\s]?[0-9]{3}[-\\s]?[0-9]{4}$");
    }

    // This method creates and returns the Message Hash
    public String createMessageHash() {
        if (messageContent == null || messageContent.trim().isEmpty()) { // trim() to handle spaces-only messages
            return "MESSAGE_EMPTY";
        }
        // Split by one or more whitespace characters
        String[] words = messageContent.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        String idPrefix = messageID.substring(0, Math.min(messageID.length(), 2));

        return (idPrefix + ":" + numMessageSent + ":" + firstWord + lastWord).toUpperCase();
    }

    // This method should allow the user to choose if they want to send, store or disregard the message.
       public String sendMessageOption() {
        String input;
        int choice;

        while (true) { // Loop until valid input is received
            input = JOptionPane.showInputDialog(null,
                    "Choose an option for the message:\n" +
                    "1. Send Message\n" +
                    "2. Disregard Message\n" +
                    "3. Store Message to send later\n" +
                    "Enter your choice (1, 2, or 3):",
                    "Message Action",
                    JOptionPane.QUESTION_MESSAGE);

            // Handle null input (user clicked Cancel or closed dialog)
            if (input == null) {
                return "No action taken."; // Or handle as a disregard, depending on desired behavior
            }

            try {
                choice = Integer.parseInt(input.trim()); // Convert input to integer
                if (choice >= 1 && choice <= 3) {
                    break; // Valid input, exit loop
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid choice. Please enter 1, 2, or 3.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Now process the valid choice
        switch (choice) {
            case 1: // Send Message
                return "Message successfully sent";
            case 2: // Disregard Message
                return "Message disregarded. Press 0 to delete the message.";
            case 3: // Store Message
                storeMessage(); // Call the method to store the message
                return "Message successfully stored.";
            default: // Should not be reached due to the while loop
                return "An unexpected error occurred.";
        }
    }

    // This method returns the details of the current message as a formatted string.
    @Override
    public String toString() {
        return "Message Details:\n" +
               "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient + "\n" +
               "Message: " + messageContent + "\n" +
               "Number Sent: " + numMessageSent;
    }

    // This method returns the total number of messages sent.
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Helper method for JUnit tests to reset the static counter
    public static void resetTotalMessagesSentForTest() {
        totalMessagesSent = 0;
    }

    // Method to store the message in a JSON file using Gson
    public void storeMessage() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty printing for readability

        // 1. Read existing messages from the file
        List<Message> messages = new ArrayList<>();
        File file = new File(MESSAGES_FILE);

        if (file.exists() && file.length() > 0) { // Check if file exists and is not empty
            try (FileReader reader = new FileReader(file)) {
                // Define the type for Gson to deserialize a list of Message objects
                Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
                List<Message> existingMessages = gson.fromJson(reader, listType);
                if (existingMessages != null) {
                    messages.addAll(existingMessages);
                }
            } catch (IOException e) {
                System.err.println("Error reading messages file: " + e.getMessage());
                // Handle JSON parsing errors if file is corrupted
                if (e.getCause() instanceof com.google.gson.JsonSyntaxException) {
                    JOptionPane.showMessageDialog(null, "Error: messages.json file is corrupted. Creating new file.");
                    messages.clear(); // Clear any partially read data
                }
            }
        }

        // 2. Add the current message to the list
        messages.add(this); // 'this' refers to the current Message object

        // 3. Write the updated list back to the file
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(messages, writer);
            JOptionPane.showMessageDialog(null, "Message successfully stored to " + MESSAGES_FILE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
     /**
     * Return A List of Message objects loaded from the JSON file.
     * Returns an empty list if the file doesn't exist, is empty, or
     * if an error occurs during reading/parsing.
     * @return 
     */
    public static List<Message> loadMessagesFromJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<Message> messages = new ArrayList<>();
        File file = new File(MESSAGES_FILE);

        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<Message>>() {}.getType();
                List<Message> existingMessages = gson.fromJson(reader, listType);
                if (existingMessages != null) {
                    messages.addAll(existingMessages);
                }
            } catch (IOException e) {
                System.err.println("Error reading messages file: " + e.getMessage());
                // HTML and bold removed from JOptionPane
                JOptionPane.showMessageDialog(null, "Error loading stored messages: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            } catch (com.google.gson.JsonSyntaxException e) {
                System.err.println("Error parsing messages.json: " + e.getMessage());
              
                JOptionPane.showMessageDialog(null, "Error: messages.json file is corrupted. Displaying empty list.", "File Corrupted", JOptionPane.WARNING_MESSAGE);
            }
        }
        return messages;
    }

    /**
     * Formats and returns a string containing details of all messages loaded from the JSON file.
     * Return A formatted string of all stored messages, or a message indicating no messages are stored.
     * @return 
     */
    public static String printStoredMessages() {
        List<Message> allStoredMessages = loadMessagesFromJson();
        if (allStoredMessages.isEmpty()) {
            return "No messages stored yet.";
        }
       
        StringBuilder sb = new StringBuilder("All Stored Messages:\n\n");
        for (int i = 0; i < allStoredMessages.size(); i++) {
            Message msg = allStoredMessages.get(i);
            sb.append("--- Message #").append(i + 1).append(" ---\n");
            sb.append(msg.toString().replace("<html>", "").replace("</html>", "").replace("<br>", "\n"));
            sb.append("\n--------------------\n");
        }
        return sb.toString();
    }
}
