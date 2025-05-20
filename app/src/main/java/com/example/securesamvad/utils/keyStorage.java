package com.example.securesamvad.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class keyStorage {

    private static final String KEY_ALIAS = "SecureSamvadKey";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String PREF_NAME = "secure_prefs";
    private static final String PREF_AES_KEY = "aes_key";

    // Generate RSA Key Pair
    public static void generateRSAKeyPair(Context context) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 25);

            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(new X500Principal("CN=" + KEY_ALIAS))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
            generator.initialize(spec);
            generator.generateKeyPair();
        }
    }

    public static PublicKey getPublicKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new Exception("Not an instance of a PrivateKeyEntry");
        }
        return ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
    }

    public static PrivateKey getPrivateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new Exception("Not an instance of a PrivateKeyEntry");
        }
        return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
    }

    public static String getBase64PublicKey() throws Exception {
        PublicKey publicKey = getPublicKey();
        return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
    }

    public static String getBase64PrivateKey() throws Exception {
        PrivateKey privateKey = getPrivateKey();
        return Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);
    }

    // 🔐 Store AES key encrypted with RSA
    public static void storeAesKey(Context context, byte[] aesKey) throws Exception {
        PublicKey publicKey = getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey);
        String encryptedBase64 = Base64.encodeToString(encryptedKey, Base64.DEFAULT);

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_AES_KEY, encryptedBase64).apply();
    }

    // 🔓 Retrieve and decrypt AES key
    public static byte[] getStoredAesKey(Context context) throws Exception {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String encryptedBase64 = prefs.getString(PREF_AES_KEY, null);
        if (encryptedBase64 == null) return null;

        byte[] encryptedKey = Base64.decode(encryptedBase64, Base64.DEFAULT);
        PrivateKey privateKey = getPrivateKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedKey);
    }
}
