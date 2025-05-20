package com.example.securesamvad.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securesamvad.R;
import com.example.securesamvad.adapter.chatAdapter;
import com.example.securesamvad.firebase.firebaseManager;
import com.example.securesamvad.models.Message;
import com.example.securesamvad.models.encryptionpayLoad;
import com.example.securesamvad.utils.cryptoUtils;
import com.example.securesamvad.utils.keyStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class chat extends AppCompatActivity {

    private EditText messageEditText;
    private ImageView sendButton;
    private RecyclerView chatRecyclerView;
    private chatAdapter chatAdapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef;

    private String currentUserId;
    private String receiverId;
    private String receiverName;
    private String receiverNumber;
    private PublicKey receiverPublicKey;
    private PrivateKey currentUserPrivateKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 🔧 Initialize views
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        TextView userName = findViewById(R.id.userName);
        TextView number = findViewById(R.id.number);

        // 🔐 Get Firebase Auth user
        currentUserId = FirebaseAuth.getInstance().getUid();
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        receiverNumber = getIntent().getStringExtra("receiverNumber");

        userName.setText(receiverName);
        number.setText(receiverNumber);

        // 🔧 RecyclerView setup
        messageList = new ArrayList<>();
        chatAdapter = new chatAdapter(this, messageList,currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // 📡 Firebase path for chat
        messagesRef = FirebaseDatabase.getInstance().getReference("messages")
                .child(getChatId(currentUserId, receiverId));

        // 🧠 Fetch receiver's public key and current user's private key
        firebaseManager.getUserPublicKey(receiverId, key -> {
            receiverPublicKey = key;
        });

        try {
            keyStorage.generateRSAKeyPair(this); // ensure generated
            currentUserPrivateKey = keyStorage.getPrivateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 📨 Listen for messages
        loadMessages();

        // 📤 Send button listener
        sendButton.setOnClickListener(v -> {
            String plainText = messageEditText.getText().toString().trim();
            if (!plainText.isEmpty() && receiverPublicKey != null) {
                sendEncryptedMessage(plainText);
                messageEditText.setText("");
            }
        });
    }

    // 🔐 Encrypt and send message
    private void sendEncryptedMessage(String plainText) {
        encryptionpayLoad payload = cryptoUtils.encryptMessage(plainText, receiverPublicKey);

        Message message = new Message();
        message.setSenderId(currentUserId);
        message.setReceiverId(receiverId);
        message.setTimestamp(new Date().getTime());
        message.setCipherText(payload.getCipherText());
        message.setIv(payload.getIv());
        message.setEncryptedAesKey(payload.getEncryptedAesKey());

        messagesRef.push().setValue(message);
    }

    // 📥 Load and decrypt messages
    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Message msg = snap.getValue(Message.class);
                    if (msg != null && msg.getSenderId() != null) {
                        String decryptedText = cryptoUtils.decryptMessage(
                                chat.this,
                                msg.getCipherText(),
                                msg.getIv(),
                                currentUserPrivateKey
                        );
                        msg.setDecryptedText(decryptedText);
                        messageList.add(msg);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Generate chat ID unique between sender and receiver
    private String getChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}
