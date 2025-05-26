/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.registraionloginapp;

import java.util.Scanner;
import java.util.regex.*;
import javax.swing.JOptionPane;

import javax.swing.*;

/**
 *
 * @author muano
 *
 *
 */
public class RegistraionLogInApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RegistrationService registration = new RegistrationService();
        LoginService login = new LoginService();

        System.out.println("==========================================");
        System.out.println("          👋 WELCOME TO THE APP!");
        System.out.println("==========================================\n");

        System.out.println("🔐 ===============REGISTRATION==============\n");

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

        System.out.println("\n=========================================");
        System.out.println("           🔓 Login");
        System.out.println("===========================================\n");

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
            String input = JOptionPane.showInputDialog("Enter how many messages you'd like to send:");
            int messageLimit = 0;
            if (input != null && !input.trim().isEmpty()) {
                try {
                    messageLimit = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number entered. Defaulting to 0 messages.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No message limit entered. Defaulting to 0 messages.");
            }

            int count = 0; // Tracks messages processed for the current session's limit
            while (true) { // <-- START OF THE MENU LOOP
                String menu = JOptionPane.showInputDialog("Choose an option:\n1) Send Message\n2) Show Recent Messages\n3) Quit");
                if (menu == null) {
                    JOptionPane.showMessageDialog(null, "Menu cancelled. Please choose an option or select 'Quit' to exit.");
                    continue;
                }

                if (menu.equals("1")) {
                    if (count < messageLimit) {

                        String recipient = JOptionPane.showInputDialog("Enter recipient number (e.g. +27718693002):");
                        if (recipient == null || recipient.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Recipient entry cancelled. Returning to main menu.");
                            continue;
                        }

                        // TEMP message object just for validation
                        Message tempMessage = new Message(recipient, "");
                        if (!tempMessage.checkRecipientCell()) {
                            JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                            continue;
                        }


                        JTextArea messageTextArea = new JTextArea(5, 30); // 5 rows, 30 columns
                        messageTextArea.setLineWrap(true); // Wrap lines if they exceed the column width
                        messageTextArea.setWrapStyleWord(true); // Wrap at word boundaries
                        JScrollPane scrollPane = new JScrollPane(messageTextArea); // Add a scroll pane for longer messages

                        JLabel messageLabel = new JLabel("Enter your message (max 250 characters):");

                        Object[] messageInput = {messageLabel, scrollPane};

                        int option = JOptionPane.showConfirmDialog(
                                null,
                                messageInput,
                                "Enter Message",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE
                        );

                        String messageText = null;
                        if (option == JOptionPane.OK_OPTION) {
                            messageText = messageTextArea.getText();
                        }

                        // --- MODIFIED LOGIC FOR MESSAGE HANDLING STARTS HERE ---
                        // 1. Check if messageText is null or empty first
                        if (messageText == null || messageText.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Message content entry cancelled or empty. Returning to main menu.");
                            continue; // Go back to the main menu
                        }

                        // 2. Now that we know messageText is not null, validate its length
                        if (messageText.length() > 250) {
                            int excessChars = messageText.length() - 250;
                            JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + excessChars + ". Please shorten it.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            continue; // Go back to the menu
                        }

                        // 3. Create the Message object AFTER all message content validations
                        Message currentMessage = new Message(recipient, messageText);

                        // 4. Validate recipient number using the Message object's method
                        if (!currentMessage.checkRecipientCell()) {
                            JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                            continue; // Go back to the menu without processing further
                        }

                        // 5. Call the sendMessageOption method to get user's choice
                        String messageActionResult = currentMessage.sendMessageOption();

                        // 6. Display appropriate JOptionPanes based on the action result
                        if (messageActionResult.equals("Message successfully sent")) {
                            JOptionPane.showMessageDialog(null, "Message sent");
                            // Display the full message details
                            JOptionPane.showMessageDialog(null, currentMessage.toString());
                            count++; // Increment count only for successfully processed messages (sent/stored)
                        } else if (messageActionResult.equals("Message successfully stored.")) {
                            // Display both the message details and the storage confirmation string
                            JOptionPane.showMessageDialog(null, currentMessage.toString() + "\n\n" + messageActionResult);
                            count++; // Increment count
                        } else if (messageActionResult.equals("Message disregarded. Press 0 to delete the message.")) {
                            JOptionPane.showMessageDialog(null, messageActionResult); // Show only the disregard message
                            // Do NOT increment count as message was disregarded
                        } else { // No action taken (user closed dialog for message action)
                            JOptionPane.showMessageDialog(null, messageActionResult);
                            // No count increment
                        }
                        // --- MODIFIED LOGIC FOR MESSAGE HANDLING ENDS HERE ---

                    } else {
                        JOptionPane.showMessageDialog(null, "Message limit reached. You can only send " + messageLimit + " messages.");
                    }
                } else if (menu.equals("2")) {
                    JOptionPane.showMessageDialog(null, "Coming Soon.");
                } else if (menu.equals("3")) {
                    break; // This is the intended way to quit the main loop
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, or 3.");
                }
            } // <-- END OF THE MENU LOOP

            // This line will only be reached when the 'while(true)' loop is broken (by choosing '3' to Quit)
            JOptionPane.showMessageDialog(null, "Total Messages Sent in this session: " + Message.returnTotalMessages());
        }

        scanner.close();
    }
}
