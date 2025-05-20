package com.example.securesamvad.models;

public class encryptionpayLoad {
    private String cipherText;
    private String iv;
    private String encryptedAesKey;

    public encryptionpayLoad() {
        // Required for Firebase deserialization
    }

    public encryptionpayLoad(String cipherText, String iv, String encryptedAesKey) {
        this.cipherText = cipherText;
        this.iv = iv;
        this.encryptedAesKey = encryptedAesKey;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getEncryptedAesKey() {
        return encryptedAesKey;
    }

    public void setEncryptedAesKey(String encryptedAesKey) {
        this.encryptedAesKey = encryptedAesKey;
    }



}
