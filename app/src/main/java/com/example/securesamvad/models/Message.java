package com.example.securesamvad.models;

public class Message {
    private String senderId;
    private String receiverId;
    private String cipherText;
    private String iv;
    private long timestamp;

    // Transient field (not stored in Firebase)
    private transient String decryptedText;

    public Message() {
        // Required for Firebase
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getCipherText() { return cipherText; }
    public void setCipherText(String cipherText) { this.cipherText = cipherText; }

    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDecryptedText() { return decryptedText; }
    public void setDecryptedText(String decryptedText) { this.decryptedText = decryptedText; }

    public void setEncryptedAesKey(String encryptedAesKey) {
    }
}
