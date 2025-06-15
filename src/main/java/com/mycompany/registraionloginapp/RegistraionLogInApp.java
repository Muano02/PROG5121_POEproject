/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.registraionloginapp;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

import javax.swing.*;

/**
 *
 * @author muano
 *
 *
 */
public class RegistraionLogInApp {

    public static void main(String[] args) throws IOException {

        try (Scanner scanner = new Scanner(System.in)) {
            RegistrationService registration = new RegistrationService();
            LoginService login = new LoginService();

            System.out.println("==================================================================");
            System.out.println("          👋      WELCOME TO THE APP!");
            System.out.println("=============================================================\n");

            System.out.println("🔐===================REGISTRATION==================================\n");

            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine();

            // Phone number input and validation
            String phoneNumber;
            while (true) {
                System.out.println("\n📱 PHONE NUMBER REQUIREMENTS:");
                System.out.println("- Must be in international South African format (starts with +27)");
                System.out.println("- Followed by 9 digits, starting with 6, 7, or 8");
                System.out.println("- May include spaces or dashes for readability");
                System.out.println("  Examples: +27821234567, +27 82 123 4567, +27-82-123-4567, +27 821-234-567\n");

                System.out.print("Enter cell phone number: ");
                phoneNumber = scanner.nextLine();

                if (registration.checkCellPhoneNumber(phoneNumber)) {
                    System.out.println("Cell phone number successfully added.");
                    break;
                } else {
                    System.out.println("Cell phone number incorrectly formatted or does not contain South African code.");
                }
            }

            // Username input and validation
            String username;
            while (true) {

                System.out.println("\n USERNAME REQUIREMENTS:");
                System.out.println("Username must contain an underscore (_) and be no more than 5 characters long.\n");
                System.out.print("Enter username: ");
                username = scanner.nextLine();

                if (registration.checkUserName(username)) {
                    System.out.println("Username captured successfully.");
                    break;
                } else {
                    System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
                }
            }

            // Password input and validation
            String password;
            while (true) {
                System.out.println("\n🔒 PASSWORD REQUIREMENTS:");
                System.out.println("- At least 8 characters long");
                System.out.println("- Contains a capital letter");
                System.out.println("- Contains a number");
                System.out.println("- Contains a special character (e.g., !@#$%)\n");
                System.out.print("Enter password: ");
                password = scanner.nextLine();
                String Result = registration.registerUser(username, password);
                System.out.println("\n" + Result);

                if (registration.checkPasswordComplexity(password)) {
                    System.out.println("Password successfully captured.");
                    break;
                } else {
                    System.out.println("Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
                }
            }

            // Register user once all inputs are validated
            String finalResult = registration.registerUser(username, password);
            System.out.println("\n" + finalResult);

            // Proceed to Login
            User registeredUser = new User(firstName, lastName, username, password, phoneNumber);
            login.setUser(registeredUser);

            System.out.println("\n=========================================================");
            System.out.println("           🔓          Login                             ");
            System.out.println("=========================================================\n");

            String loginUsername;
            String loginPassword;
            boolean isLoggedIn = false;

            while (!isLoggedIn) {
                System.out.print("Enter Username: ");
                loginUsername = scanner.nextLine();

                System.out.print("Enter Password: ");
                loginPassword = scanner.nextLine();

                isLoggedIn = login.logUser(loginUsername, loginPassword);
                System.out.println(login.returnLoginStatus(isLoggedIn));
            }

            if (isLoggedIn) {
                JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

                MessageManager manager = new MessageManager();

                int messageLimit;
                while (true) {
                    String input = JOptionPane.showInputDialog("Enter how many messages you'd like to send:");
                    if (input == null) {
                        JOptionPane.showMessageDialog(null, "Application cancelled. Goodbye!");
                        return;
                    }
                    try {
                        messageLimit = Integer.parseInt(input.trim());
                        if (messageLimit > 0) {
                            break;
                        }
                        JOptionPane.showMessageDialog(null, "Please enter a positive number greater than zero.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a whole number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }

                int count = 0;

                while (true) {
                    String menu = """
                                  Choose an option:
                                  
                                  1) Send Message
                                  2) Show Recently Sent Messages
                                  3) Message Center
                                  4) Quit
                                  
                                  Messages Sent: """ + count + " / " + messageLimit + "\n"
                            + "Enter your choice (1-4):";
                    String choice = JOptionPane.showInputDialog(menu);

                    if (choice == null) {
                        continue;
                    }

                    switch (choice.trim()) {
                        case "1":
                            //Check the limit *before* trying to send a message.
                            if (count < messageLimit) {
                                // sendMessageWorkflow now returns a String ("Sent", "Stored", etc.)
                                String messageResult = sendMessageWorkflow(manager);

                                //Only increment the counter if the message was actually "Sent".
                                if ("Sent".equals(messageResult)) {
                                    count++;
                                }
                            } else {
                                // If the limit is reached, show a message but DO NOT quit.
                                JOptionPane.showMessageDialog(null, "You have reached your message limit of " + messageLimit + ".\n"
                                        + "You can no longer send messages, but you can use other options.", "Message Limit Reached", JOptionPane.WARNING_MESSAGE);
                            }
                            break;
                        case "2":
                            String summary = manager.getRecentSentMessagesSummary();
                            displayInTextArea(summary, "Recently Sent Messages");
                            break;
                        case "3":
                            showMessageCenterMenu(manager, registeredUser);
                            break;
                        case "4":
                            JOptionPane.showMessageDialog(null, "Total Messages Sent in this session: " + Message.returnTotalMessagesSent());
                            return; // This is now the only way for the user to exit the loop.
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, 3, or 4.");
                    }
                }
            }
        }
    }

    /**
     * This workflow now returns a String indicating the exact outcome.
     *
     * @return "Sent", "Stored", "Disregard", or "Cancelled".
     */
    private static String sendMessageWorkflow(MessageManager manager) {
        String recipient;
        while (true) {
            recipient = JOptionPane.showInputDialog("Enter recipient number (e.g., +27821234567):");
            if (recipient == null) {
                return "Cancelled";
            }
            Message tempValidator = new Message(recipient, "", "Temp");
            if (tempValidator.checkRecipientCell()) {
                break;
            }
            JOptionPane.showMessageDialog(null, "Invalid recipient number. Please use the correct international format (e.g., +27821234567).", "Input Error", JOptionPane.ERROR_MESSAGE);
        }

        String messageText = "";
        while (true) {
            JTextArea messageTextArea = new JTextArea(5, 30);
            messageTextArea.setLineWrap(true);
            messageTextArea.setWrapStyleWord(true);
            messageTextArea.setText(messageText);
            JScrollPane scrollPane = new JScrollPane(messageTextArea);
            int option = JOptionPane.showConfirmDialog(null, scrollPane, "Enter Your Message (max 250 characters)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                messageText = messageTextArea.getText();
                if (messageText.length() <= 250) {
                    if (messageText.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Message cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        messageText = "";
                    } else {
                        break;
                    }
                } else {
                    int excessChars = messageText.length() - 250;
                    JOptionPane.showMessageDialog(null, "Message is too long by " + excessChars + " characters. Please shorten it.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return "Cancelled";
            }
        }

        String messageActionResult = Message.sentMessage(messageText);
        String flag;

        switch (messageActionResult) {
            case "Message successfully sent":
                flag = "Sent";
                Message.incrementSentCount();
                break;
            case "Message successfully stored.":
                flag = "Stored";
                break;
            case "Message disregarded. Press 0 to delete the message.":
                flag = "Disregard";
                handleDisregard();
                break;
            default:
                return "Cancelled";
        }

        Message currentMessage = new Message(recipient, messageText, flag);
        manager.processMessage(currentMessage);

        if (flag.equals("Sent")) {
            JOptionPane.showMessageDialog(null, "Message Successfully Sent!\n\n" + currentMessage.toString(), "Confirmation", JOptionPane.INFORMATION_MESSAGE);
        } else if (flag.equals("Stored")) {
            try {
                // First, save the message to the file.
                manager.saveStoredMessagesToFile();
                // Then, show the specific confirmation dialog for stored messages.
                JOptionPane.showMessageDialog(null, "Message Successfully Stored.\n\n" + currentMessage.toString(), "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return flag;
    }

    //Handle disregard
    private static void handleDisregard() {
        while (true) {
            String deleteChoice = JOptionPane.showInputDialog("Message disregarded. Press 0 to delete the message.");
            if (deleteChoice == null) {
                JOptionPane.showMessageDialog(null, "Deletion cancelled. The message was not saved.");
                break;
            }
            if (deleteChoice.trim().equals("0")) {
                JOptionPane.showMessageDialog(null, "Message permanently deleted.");
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid input. You MUST enter '0' to confirm deletion.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //This method displays the Part 3 menu with all the new features. It uses the compliant MessageManager to perform all actions.
    private static void showMessageCenterMenu(MessageManager manager, User currentUser) {
        while (true) {
            String menu = """
                          Choose an Option:
                          
                          1. Display the sender and recipient of all sent messages
                          2. Display the longest sent message.
                          3. Search for a message ID and display the corresponding recipient and message
                          4. Search for all the messages sent to a particular recipient
                          5. Delete a message using the message hash
                          6. Display a report that lists the full details of all sent messages
                          7. Return to Main Menu
                          
                          Enter your choice (1-7):""";
            String choice = JOptionPane.showInputDialog(null, menu, "Message Center", JOptionPane.PLAIN_MESSAGE);

            if (choice == null || choice.trim().equals("7")) {
                break;
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        displayInTextArea(manager.displaySentMessageSummary(currentUser), "Sent Messages Summary");
                        break;
                    case "2":
                        JOptionPane.showMessageDialog(null, "Longest message found:\n\n\"" + manager.findLongestMessage() + "\"", "Longest Message", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case "3":
                        String id = JOptionPane.showInputDialog("Enter Message ID to search for:");
                        if (id != null && !id.trim().isEmpty()) {
                            String searchId = id.trim();
                            Message found = manager.searchByMessageID(searchId);

                            if (found != null) {
                                // Display only message and recipient simple string with only recipient and message
                                String displayString = "Flag: " + found.getFlag() + "\n"
                                                     + "Recipient: " + found.getRecipient() + "\n"
                                                     + "Message: \"" + found.getMessageContent() + "\"";
                                JOptionPane.showMessageDialog(null, displayString, "Message Found", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Message with ID [" + searchId + "] not found.");
                            }
                        }
                        break;
                    case "4":
                        String recipient = JOptionPane.showInputDialog("Enter Recipient number to search for (eg +2772292384):");
                        if (recipient != null && !recipient.trim().isEmpty()) {
                            String recipientToFind = recipient.trim();
                            List<Message> foundMessages = manager.searchByRecipient(recipientToFind);
                            if (!foundMessages.isEmpty()) {
                                //Focused on list of messages for a particular recipient
                                StringBuilder sb = new StringBuilder("Messages for " + recipientToFind + ":\n\n");
                                for (Message msg : foundMessages) {
                                    sb.append("Flag: ").append(msg.getFlag()).append("\n"); // e.g., "Sent" or "Stored"
                                    sb.append("Message: \"").append(msg.getMessageContent()).append("\"\n");
                                    sb.append("---------------------------------------\n");
                                }
                                displayInTextArea(sb.toString(), "Search Results for " + recipientToFind);
                            } else {
                                JOptionPane.showMessageDialog(null, "No messages found for this recipient.");
                            }
                        }
                        break;
                    case "5":
                        String hash = JOptionPane.showInputDialog("Enter Message Hash to delete (from Stored messages):");
                        if (hash != null && !hash.trim().isEmpty()) {
                            String result = manager.deleteByHash(hash.trim().toUpperCase());
                            JOptionPane.showMessageDialog(null, result, "Deletion Result", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case "6":
                        displayInTextArea(manager.generateSentMessagesReport(), "Sent Messages Report");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "The choice you entered is not valid. Please select a number from the menu (1-7).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "A file error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //helper method Method when displaying, 
    private static void displayInTextArea(String text, String title) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
