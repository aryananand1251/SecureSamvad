package com.example.securesamvad.models;

public class userPublicKey {


    private String userId;
    private String publicKey;

    public userPublicKey() {
    }

    public void UserPublicKey(String userId, String publicKey) {
        this.userId = userId;
        this.publicKey = publicKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
