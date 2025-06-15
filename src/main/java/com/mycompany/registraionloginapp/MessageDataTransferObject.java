/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.registraionloginapp;

/**
 *
 * @author muano
 */
class MessageDataTransferObject {

    private String recipient;
    private String messageContent;
    private String messageID;
    private String messageHash;

    public MessageDataTransferObject() {
    } // Required for JSON libraries

    // Getters and Setters
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String content) {
        this.messageContent = content;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String id) {
        this.messageID = id;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String hash) {
        this.messageHash = hash;
    }
}
