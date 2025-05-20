package com.example.securesamvad.utils;


import android.content.Context;
import android.util.Base64;

import com.example.securesamvad.models.encryptionpayLoad;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class cryptoUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_SIZE = 12; // Recommended for GCM
    private static String encryptedAesKey;

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static PublicKey getPublicKeyFromBytes(byte[] keyBytes) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    public static encryptionpayLoad encryptMessage(String plainText, PublicKey receiverPublicKey) {
        try {
            // Generate AES key
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(AES_KEY_SIZE);
            SecretKey aesKey = keyGen.generateKey();

            // Generate IV
            byte[] iv = new byte[IV_SIZE];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // Encrypt the message with AES
            Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
            byte[] encryptedText = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Encrypt AES key with RSA
            Cipher rsaCipher = Cipher.getInstance(RSA_ALGORITHM);
            rsaCipher.init(Cipher.ENCRYPT_MODE, receiverPublicKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

            // Encode all in Base64 for transmission
            return new encryptionpayLoad(
                    Base64.encodeToString(encryptedText, Base64.NO_WRAP),
                    Base64.encodeToString(iv, Base64.NO_WRAP),
                    Base64.encodeToString(encryptedAesKey, Base64.NO_WRAP)
            );
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decryptMessage(Context context, String cipherText, String iv, PrivateKey privateKey) {
        try {
            // Get the locally stored AES key
            byte[] aesKeyBytes = keyStorage.getStoredAesKey(context);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, AES_ALGORITHM);

            byte[] decodedIv = Base64.decode(iv, Base64.NO_WRAP);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, decodedIv);

            Cipher aesCipher = Cipher.getInstance(AES_TRANSFORMATION);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            byte[] decryptedBytes = aesCipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "[Decryption failed]";
        }
    }


}


