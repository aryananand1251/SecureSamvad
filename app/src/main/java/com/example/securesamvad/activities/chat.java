package com.example.securesamvad.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securesamvad.R;
import com.example.securesamvad.adapter.chatAdapter;
import com.example.securesamvad.adapter.chatAdapter;
import com.example.securesamvad.models.message;
import com.example.securesamvad.models.message;

import java.util.ArrayList;
import java.util.List;

public class chat extends AppCompatActivity {

    RecyclerView chatRecyclerView;
    EditText messageEditText;
    Button sendButton;

    chatAdapter chatAdapter;
    List<message> messageList;

    // For testing, assume currentUserId = "user1"
    String currentUserId = "user1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();

        chatAdapter = new chatAdapter(this, messageList, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Add some dummy messages for testing
        messageList.add(new message("user1", "Hello!"));
        messageList.add(new message("user2", "Hi there!"));
        messageList.add(new message("user1", "How are you?"));
        messageList.add(new message("user2", "I'm good, thanks!"));

        chatAdapter.notifyDataSetChanged();

        sendButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                // Add message as sent by current user
                message message = new message(currentUserId, text);
                messageList.add(message);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
                messageEditText.setText("");
            }
        });
    }
}
