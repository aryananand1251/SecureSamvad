package com.example.securesamvad.firebase;


import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.example.securesamvad.models.Message;
import com.example.securesamvad.models.encryptionpayLoad;
import com.example.securesamvad.models.userPublicKey;
import com.example.securesamvad.utils.cryptoUtils;
import com.example.securesamvad.utils.keyStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class firebaseManager {

    private static final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("messages");
    private static final DatabaseReference keyRef = FirebaseDatabase.getInstance().getReference("public_keys");

    private static final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Store public keys in memory
    private final List<userPublicKey> cachedPublicKeys = new ArrayList<>();

    public void sendEncryptedMessage(String receiverId, String plainText) {
        getUserPublicKey(receiverId, publicKey -> {
            encryptionpayLoad payload = cryptoUtils.encryptMessage(plainText, publicKey);
            Message message = new Message();
            message.setSenderId(currentUserId);
            message.setReceiverId(receiverId);
            message.setCipherText(payload.getCipherText());
            message.setIv(payload.getIv());
            message.setTimestamp(System.currentTimeMillis());

            String key = messageRef.push().getKey();
            if (key != null) {
                messageRef.child(currentUserId).child(receiverId).child(key).setValue(message);
                messageRef.child(receiverId).child(currentUserId).child(key).setValue(message);
            }
        });
    }

    public static void listenForMessages(Context context, String chatPartnerId, Consumer<List<Message>> onMessagesReceived) {
        messageRef.child(currentUserId).child(chatPartnerId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Message> messages = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Message message = data.getValue(Message.class);
                            if (message != null) {
                                try {
                                    String decrypted = cryptoUtils.decryptMessage(
                                            context,
                                            message.getCipherText(),
                                            message.getIv(),
                                            keyStorage.getPrivateKey()
                                    );
                                    message.setDecryptedText(decrypted);
                                } catch (Exception e) {
                                    message.setDecryptedText("[Decryption failed]");
                                }
                                messages.add(message);
                            }
                        }
                        Collections.sort(messages, (m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                        onMessagesReceived.accept(messages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
                    }
                });
    }

    public static void getUserPublicKey(String userId, Consumer<PublicKey> callback) {
        keyRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPublicKey publicKeyModel = snapshot.getValue(userPublicKey.class);
                if (publicKeyModel != null) {
                    try {
                        byte[] keyBytes = Base64.decode(publicKeyModel.getPublicKey(), Base64.DEFAULT);
                        PublicKey publicKey = cryptoUtils.getPublicKeyFromBytes(keyBytes);
                        callback.accept(publicKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.accept(null);
                    }
                } else {
                    callback.accept(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.accept(null);
            }
        });
    }
}

