/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author muano
 */
public class MessageManagerTest {

    private MessageManager manager;

    // Test Data Messages
    private Message msg1, msg2, msg3, msg4, msg5;

    // The @BeforeEach annotation ensures this setup code runs before every single test.
    @BeforeEach
    void setUp() throws IOException {
        // 1. Clean up old files and reset counters for a fresh start.
        Files.deleteIfExists(Paths.get("messages.json"));
        Message.resetTotalMessagesSentForTest();

        // 2. Create a new instance of the manager. This fixes "this.manager" is null.
        manager = new MessageManager();

        // 3. Create all the test messages. This fixes "this.msg1" is null, "this.msg2" is null, etc.
        msg1 = new Message("+27834557896", "Did you get the cake?", "Sent");
        Message.incrementSentCount();

        msg2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");

        msg3 = new Message("+27834484567", "Yohoooo, I am at your gate.", "Disregard");

        msg4 = new Message("0838884567", "It is dinner time !", "Sent");
        Message.incrementSentCount();

        msg5 = new Message("+27838884567", "Ok, I am leaving without you.", "Stored");

        // 4. Populate the manager with the test data.
        manager.processMessage(msg1);
        manager.processMessage(msg2);
        manager.processMessage(msg3);
        manager.processMessage(msg4);
        manager.processMessage(msg5);

        // 5. Simulate saving and loading stored messages.
        manager.saveStoredMessagesToFile();
        manager.loadStoredMessagesFromJson();
    }

    @Test
    void testSentMessagesArrayCorrectlyPopulated() {
        List<String> sentContents = manager.getSentMessagesContent();
        assertEquals(2, sentContents.size(), "Sent Messages array correctly populated");
        assertTrue(sentContents.contains("Did you get the cake?"), "System must return: 'Did you get the cake?'");
        assertTrue(sentContents.contains("It is dinner time !"), "System must return: 'It is dinner time !'");
    }

    @Test
    void testDisplayTheLongestMessage() {
        // The longest SENT message in the session is "Did you get the cake?"
        String expectedLongestSent = msg1.getMessageContent();
        String actual = manager.findLongestMessage();
        assertEquals(expectedLongestSent, actual, "Display the longest sent Message");
    }

    @Test
    void testSearchForMessageID() {
        String idToSearch = msg4.getMessageID();
        Message result = manager.searchByMessageID(idToSearch);
        assertNotNull(result, "A message should be found for the given ID.");
        assertEquals("It is dinner time !", result.getMessageContent(), "'Search for messageID'");
    }

    @Test
    void testSearchByRecipientFindsTheCorrectSentMessage() {
        String recipientToSearch = "+27834557896";

        List<Message> results = manager.searchByRecipient(recipientToSearch);

        assertEquals(1, results.size(), "The search should find exactly one sent message for this recipient.");
        Message foundMessage = results.get(0);
        assertEquals("Did you get the cake?", foundMessage.getMessageContent(), "The content of the found message is incorrect.");
        assertEquals("+27834557896", foundMessage.getRecipient(), "The recipient of the found message is incorrect.");
    }

    @Test
    void testDeleteMessageUsingMessageHash() throws IOException {
        String hashToDelete = msg2.getMessageHash();
        String expectedResponse = "STORED message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.";

        String actualResponse = manager.deleteByHash(hashToDelete);

        assertEquals(expectedResponse, actualResponse, "Delete a message using a message hash.");
        assertNull(manager.searchByMessageID(msg2.getMessageID()), "The deleted message should no longer be findable.");
    }

    @Test
    void testDisplayReport() {
        String report = manager.generateSentMessagesReport();
        assertTrue(report.contains(msg1.getMessageContent()), "Report should contain content for sent message 1.");
        assertTrue(report.contains(msg1.getRecipient()), "Report should contain recipient for message 1.");
        assertTrue(report.contains(msg1.getMessageHash()), "Report should contain hash for message 1.");
        assertTrue(report.contains(msg4.getMessageContent()), "Report should contain content for sent message 4.");
        assertFalse(report.contains(msg2.getMessageContent()), "Report should NOT contain a stored message.");
    }
}
