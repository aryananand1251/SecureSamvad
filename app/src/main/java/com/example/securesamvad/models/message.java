package com.example.securesamvad.models;

public class message {
    private String senderId;
    private String messageText;

    public message() { }

    public message(String senderId, String messageText) {
        this.senderId = senderId;
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessageText() {
        return messageText;
    }
}
