package com.example.securesamvad.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securesamvad.R;
import com.example.securesamvad.adapter.UserAdapter;
import com.example.securesamvad.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

    }
}
