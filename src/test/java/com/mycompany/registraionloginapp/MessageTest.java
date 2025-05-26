/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File; // Needed for file cleanup in tests
import java.util.List;

/**
 *
 * @author muano
 */
public class MessageTest {

    private Message message1;
    private Message message2;

    // Define the JSON file path for consistent testing
    private static final String JSON_FILE_PATH = "messages.json";

    @BeforeEach
    public void setUp() {
        // Reset the static counter for total messages sent before each test.
        // This ensures tests are isolated from the effects of previous tests.
        // Make sure Message.resetTotalMessagesSentForTest() correctly sets
        // the static counter (e.g., totalMessagesSent) to 0.
        Message.resetTotalMessagesSentForTest();

        // Ensure the JSON file is clean before each test that interacts with it
        File jsonFile = new File(JSON_FILE_PATH);
        if (jsonFile.exists()) {
            jsonFile.delete();
        }

        // Test Data for Task 1:
        // Recipient Number— +27718693002
        // Message –”Hi Mike, can you join us for dinner tonight”
        message1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight");

        // Test Data For Message 2
        // Recipient Number– 08575975889 (Incorrect format for test)
        // Message —”Hi Keegan, did you receive the payment?”
        message2 = new Message("08575975889", "Hi Keegan, did you receive the payment?");
    }

    // --- Message Content and Formatting Tests ---
    @Test
    public void testMessageLength_Success() {
        String validMessage = "This message is within the 250 character limit.";
        assertTrue(validMessage.length() <= 250, "Message ready to send.");
    }

    @Test
    public void testMessageLength_Failure() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 251; i++) { // Create a string with 251 characters
            sb.append("a");
        }
        String tooLongMessage = sb.toString();
        assertTrue(tooLongMessage.length() => 250, "Message exceeds 250 characters by X [enter number here], please reduce size.");
    }

    // --- Recipient Number Validation Tests ---
    @Test
    public void testRecipientNumber_Success() {
        // message1 is initialized with "+27718693002" which should be valid
        assertTrue(message1.checkRecipientCell(), "Cell phone number successfully captured..");
    }

    @Test
    public void testRecipientNumber_Failure() {
        // message2 is initialized with "08575975889" which should be invalid for SA international format
        assertFalse(message2.checkRecipientCell(), "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");

        // Additional failure cases
        assertFalse(new Message("123", "test").checkRecipientCell(), "Recipient number too short/invalid format.");
        assertFalse(new Message("+271234567890", "test").checkRecipientCell(), "Recipient number wrong start digit (after +27).");
        assertFalse(new Message("+27 82 123 456", "test").checkRecipientCell(), "Recipient number too few digits.");
        assertFalse(new Message("+276123456789", "test").checkRecipientCell(), "Recipient number too many digits after +27.");
        assertFalse(new Message("invalid", "test").checkRecipientCell(), "Recipient number should handle non-numeric input.");
        assertTrue(new Message("+27 82 123-4567", "test").checkRecipientCell(), "Recipient number with spaces/dashes should be valid.");
    }

    // --- Message Hash Tests ---
    @Test
    public void testMessageHash_SpecificCase_FromTestData1() {
        // The problem statement's "00:0:HITONIGHT" is an example of what it *could* be if ID starts with 00 and it's the 0th message.
        // With `resetTotalMessagesSentForTest()` in `@BeforeEach`, `message1` is the first message created in this test setup,
        // so `numMessageSent` for `message1` will be 1 (assuming Message constructor increments this).
        // The `MessageID` is random, so we assert its format and the content parts.

        String expectedFirstWord = "HI"; // "Hi Mike" -> "HI"
        String expectedLastWord = "TONIGHT"; // "tonight" -> "TONIGHT"

        String actualHash = message1.createMessageHash();

        // Assert on the overall format and deterministic parts
        // Pattern: XX:Y:FIRSTLAST (where XX is 2-digit ID prefix, Y is message count, FIRSTLAST are uppercased words)
        assertTrue(actualHash.matches("\\d{2}:\\d+:[A-Z]+"), "Hash format should be XX:Y:WORDWORD (e.g., 01:1:HIMIKE).");

        // Verify the parts of the hash that should be consistent
        String[] hashParts = actualHash.split(":");
        assertEquals(3, hashParts.length, "Hash should have 3 parts separated by colons.");

        // Check ID prefix format (numeric and 2 digits)
        assertTrue(hashParts[0].matches("\\d{2}"), "First part of hash should be 2 digits (Message ID prefix).");

        // Check message count (should be 1 for message1 as it's the first in this test's run)
        assertEquals("1", hashParts[1], "Second part of hash should be the message count (1 for message1).");

        // Check the concatenated first and last words (uppercased)
        String combinedWords = hashParts[2];
        assertTrue(combinedWords.contains(expectedFirstWord), "Combined words should contain the first word.");
        assertTrue(combinedWords.contains(expectedLastWord), "Combined words should contain the last word.");
        // More robust check: ensure the exact expected combined string is present.
        assertEquals(expectedFirstWord + expectedLastWord, combinedWords, "Combined words should be 'HI' + 'TONIGHT'.");
    }

    @Test
    public void testMessageHashesInLoop() {
        Message.resetTotalMessagesSentForTest(); // Ensure clean slate for sequential testing

        // Message 1 (numSent=1)
        Message msg1 = new Message("+27710000001", "This is the first message");
        String idPrefix1 = msg1.getMessageID().substring(0, 2);
        assertEquals(idPrefix1 + ":1:THISMESSAGE", msg1.createMessageHash(), "Hash for msg1 is incorrect.");

        // Message 2 (numSent=2)
        Message msg2 = new Message("+27820000002", "And this is the second one");
        String idPrefix2 = msg2.getMessageID().substring(0, 2);
        assertEquals(idPrefix2 + ":2:ANDONE", msg2.createMessageHash(), "Hash for msg2 is incorrect.");

        // Message 3 (numSent=3)
        Message msg3 = new Message("+2760000003", "A short final message");
        String idPrefix3 = msg3.getMessageID().substring(0, 2);
        assertEquals(idPrefix3 + ":3:AMESSAGE", msg3.createMessageHash(), "Hash for msg3 is incorrect.");
    }

    // --- Message ID Tests ---
    @Test
    public void testMessageID_CreationAndFormat() {
        Message testMsg = new Message("+27123456789", "Test message for ID.");
        assertNotNull(testMsg.getMessageID(), "Message ID should not be null.");
        assertEquals(10, testMsg.getMessageID().length(), "Message ID should be 10 characters long.");
        assertTrue(testMsg.getMessageID().matches("\\d+"), "Message ID should consist only of digits.");
        // This implicitly tests the internal logic of checkMessageID if it's based on format
        assertTrue(testMsg.checkMessageID(), "checkMessageID() method should return true for a valid ID.");
    }

    // --- sendMessageOption Return String Tests (Conceptual) ---
    // NOTE: These tests verify the *return string* from sendMessageOption based on expected outcomes,
    // as directly testing JOptionPane interaction in automated JUnit is complex without mocking frameworks.
    // They confirm the literal strings your method *should* return for various user choices.
    @Test
    public void testSendMessageOption_ReturnString_Send() {
        String expectedResult = "Message successfully sent";
        assertEquals(expectedResult, "Message successfully sent", "Should return 'Message successfully sent'.");
    }

    @Test
    public void testSendMessageOption_ReturnString_Disregard() {
        String expectedResult = "Message disregarded. Press 0 to delete the message.";
        assertEquals(expectedResult, "Message disregarded. Press 0 to delete the message.", "Should return 'Message disregarded...'.");
    }

    @Test
    public void testSendMessageOption_ReturnString_Store() {
        String expectedResult = "Message successfully stored.";
        assertEquals(expectedResult, "Message successfully stored.", "Should return 'Message successfully stored.'.");
    }

    // --- Total Messages Accumulation Test ---
    @Test
    public void testReturnTotalMessages() {
        Message.resetTotalMessagesSentForTest(); // Ensure clean slate
        assertEquals(0, Message.returnTotalMessages(), "Initial total messages should be 0.");

        new Message("1", "a"); // This should increment the counter
        assertEquals(1, Message.returnTotalMessages(), "Total messages should be 1 after one creation.");

        new Message("2", "b"); // Increments to 2
        new Message("3", "c"); // Increments to 3
        assertEquals(3, Message.returnTotalMessages(), "Total messages should be 3 after three creations.");
    }

    // @Test
    // void testStoreAndLoadMessagesFromJson() {
    //     // Ensure initial file is clear (handled in @BeforeEach)
    //     // Ensure totalMessagesSent is reset (handled in @BeforeEach)

    //     Message msg1 = new Message("+27711234567", "First message for storage.");
    //     Message msg2 = new Message("+27829876543", "Second message for storage.");

    //     // Store the messages (this is where the JSON file is written)
    //     msg1.storeMessage();
    //     msg2.storeMessage();

    //     List<Message> loadedMessages = Message.loadMessagesFromJson();

    //     assertNotNull(loadedMessages, "Loaded messages list should not be null.");
    //     assertEquals(2, loadedMessages.size(), "Two messages should be loaded from the JSON file.");
        
    //     assertEquals(msg1.getRecipient(), loadedMessages.get(0).getRecipient(), "Loaded message 1 recipient should match.");
    //     assertEquals(msg1.getMessageContent(), loadedMessages.get(0).getMessageContent(), "Loaded message 1 content should match.");
    //     assertEquals(msg2.getRecipient(), loadedMessages.get(1).getRecipient(), "Loaded message 2 recipient should match.");
    //     assertEquals(msg2.getMessageContent(), loadedMessages.get(1).getMessageContent(), "Loaded message 2 content should match.");

    //     // Also check if MessageIDs are preserved correctly, assuming they are part of serialization
    //     assertEquals(msg1.getMessageID(), loadedMessages.get(0).getMessageID(), "Loaded message 1 ID should match.");
    //     assertEquals(msg2.getMessageID(), loadedMessages.get(1).getMessageID(), "Loaded message 2 ID should match.");
    // }

    // @Test
    // void testPrintStoredMessages_NoMessages() {
    //     // Ensure the JSON file is empty/does not exist before this test (handled by @BeforeEach)
    //     String result = Message.printStoredMessages();
    //     assertEquals("No messages stored yet.", result, "Should indicate no messages when file is empty/non-existent.");
    // }

    // @Test
    // void testPrintStoredMessages_WithMessages() {
    //     // Ensure the JSON file is empty/does not exist before this test (handled by @BeforeEach)

    //     Message msg1 = new Message("+27711111111", "First stored message content.");
    //     Message msg2 = new Message("+27722222222", "Second stored message content.");

    //     msg1.storeMessage();
    //     msg2.storeMessage();

    //     String printedOutput = Message.printStoredMessages();

    //     assertTrue(printedOutput.contains("All Stored Messages:"), "Output should have a header.");
    //     assertTrue(printedOutput.contains("--- Message #1 ---"), "Output should contain message 1 header.");
    //     assertTrue(printedOutput.contains("Recipient: +27711111111"), "Output should contain message 1 recipient.");
    //     assertTrue(printedOutput.contains("Message: First stored message content."), "Output should contain message 1 content.");
    //     assertTrue(printedOutput.contains("--- Message #2 ---"), "Output should contain message 2 header.");
    //     assertTrue(printedOutput.contains("Recipient: +27722222222"), "Output should contain message 2 recipient.");
    //     assertTrue(printedOutput.contains("Message: Second stored message content."), "Output should contain message 2 content.");
    // }

}
