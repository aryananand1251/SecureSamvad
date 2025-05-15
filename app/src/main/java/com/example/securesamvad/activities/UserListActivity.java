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

    RecyclerView recyclerView;
    List<User> users;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy user list
        users = new ArrayList<>();
        users.add(new User("Gourab", ""));
        users.add(new User("Aryan", ""));
        users.add(new User("Lukesh", ""));
        users.add(new User("Rajesh", ""));

        adapter = new UserAdapter(users, this);
        recyclerView.setAdapter(adapter);
    }
}
