package com.example.securesamvad.activities;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.securesamvad.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class chatList extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatlist);
//

//
        FloatingActionButton button = findViewById(R.id.addContactButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(chatList.this, contact_list.class);
            startActivity(intent);
        });
    }


}
