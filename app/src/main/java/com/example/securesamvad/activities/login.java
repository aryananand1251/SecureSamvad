package com.example.securesamvad.activities;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.securesamvad.R;

public class login extends AppCompatActivity {

    Spinner countrySpinner;
    TextView countryCodeText;
    EditText phoneNumberInput;
    Button verifyButton;

    String[] countryNames = {"India", "USA", "UK"};
    String[] countryCodes = {"+91", "+1", "+44"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        //Codes written by GOURAB
        countrySpinner = findViewById(R.id.countrySpinner);
        countryCodeText = findViewById(R.id.countryCodeText);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        verifyButton = findViewById(R.id.verifyButton);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryNames);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCodeText.setText(countryCodes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
}