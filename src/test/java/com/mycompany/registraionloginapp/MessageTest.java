/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author muano
 */
public class MessageTest {

    @BeforeEach
    public void setUp() {
        Message.resetTotalMessagesSentForTest();
    }

    @Test
    void testRecipientNumberFormat_Success() {
        // Uses Test Data for Task 1: "+27718693002"
        Message validMsg = new Message("+27718693002", "test", "Sent");
        assertTrue(validMsg.checkRecipientCell(), "The international number from the test data should be valid.");
    }

    //Requirement: The recipient number is correctly formatted - Failure cases.
    @Test
    void testRecipientNumberFormat_Failure() {
        // Uses Test Data for Message 2: "08575975889" (This is an 11-digit invalid number)
        Message developerFormatMsg = new Message("08575975889", "test", "Sent");
        assertFalse(developerFormatMsg.checkRecipientCell(), "The 11-digit number from the test data should be invalid.");

        // Also test with a simple developer format number that is valid (10-digits)
        Message validDeveloperNumber = new Message("0831234567", "test", "Sent");
        assertFalse(validDeveloperNumber.checkRecipientCell(), "A 10-digit developer number should now be considered invalid.");
    }

    //Requirement: Validation message lenth 
    @Test
    void testValidateMessageContentLength() {
        // Success Case: A message that is well under 250 characters.
        String validContent = "This is a valid message.";
        assertEquals("Message ready to send.", Message.validateMessageContent(validContent));

        // Failure Case: A message that is over 250 characters.
        String longContent = "A".repeat(251); // 251 characters long
        String expectedError = "Message exceeds 250 characters by 1, please reduce size.";
        assertEquals(expectedError, Message.validateMessageContent(longContent));
    }

    //Requirement: Message hash is correct.
    @Test
    void testMessageHashCreation() {
        // Uses Test Data for Task 1: "Hi Mike, can you join us for dinner tonight"
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight", "Sent");
        String idPrefix = msg.getMessageID().substring(0, 2);
        String expectedHash = (idPrefix + ":1:HI" + "TONIGHT").toUpperCase();
        assertEquals(expectedHash, msg.getMessageHash(), "The generated hash must match the application's logic.");
    }

    //Requirement: Message ID is created.
    @Test
    void testMessageIDIsCreated() {
        Message msg = new Message("+27123456789", "test", "Sent");
        assertNotNull(msg.getMessageID(), "Message ID should not be null.");
        assertTrue(msg.checkMessageID(), "The checkMessageID() method should return true for a valid ID (10 digits).");
    }

    //Requirement: Return total number sent.
    @Test
    void testReturnTotalMessagesSent() {
        assertEquals(0, Message.returnTotalMessagesSent(), "Total should be 0 at the start.");
        Message.incrementSentCount();
        assertEquals(1, Message.returnTotalMessagesSent(), "Total should be 1 after one increment.");
        Message.incrementSentCount();
        assertEquals(2, Message.returnTotalMessagesSent(), "Total should be 2 after a second increment.");
    }
}
