package com.example.securesamvad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.securesamvad.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {

    private EditText d1, d2, d3, d4,d5,d6;
    private Button verify;
    private ProgressBar progressbar;
    private TextView textMobile, resendCode;
    private String verificationId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        d1 = findViewById(R.id.d1);
        d2 = findViewById(R.id.d2);
        d3 = findViewById(R.id.d3);
        d4 = findViewById(R.id.d4);
        d5 = findViewById(R.id.d5);
        d6 = findViewById(R.id.d6);
        verify = findViewById(R.id.verify);
        progressbar = findViewById(R.id.progressbar);
        textMobile = findViewById(R.id.textMobile);
        resendCode = findViewById(R.id.resendCode); // Make sure you set this TextView ID in XML

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");
        String phoneNumber = getIntent().getStringExtra("Mobile");

        textMobile.setText(phoneNumber);

        setupOtpInputs();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = d1.getText().toString() + d2.getText().toString() +
                        d3.getText().toString() + d4.getText().toString() + d5.getText().toString() + d6.getText().toString();

                if (code.length() != 6) {
                    Toast.makeText(Otp.this, "Please enter a valid 4-digit OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (verificationId != null) {
                    progressbar.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.INVISIBLE);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressbar.setVisibility(View.GONE);
                            verify.setVisibility(View.VISIBLE);

                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Otp.this, chat.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Otp.this, "OTP is not valid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phoneNumber);
            }
        });
    }

    private void setupOtpInputs() {
        d1.addTextChangedListener(new GenericTextWatcher(d2));
        d2.addTextChangedListener(new GenericTextWatcher(d3));
        d3.addTextChangedListener(new GenericTextWatcher(d4));
        d4.addTextChangedListener(new GenericTextWatcher(d5));
        d5.addTextChangedListener(new GenericTextWatcher(d6));
    }

    private void resendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(Otp.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = newVerificationId;
                        Toast.makeText(Otp.this, "OTP resent", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private class GenericTextWatcher implements TextWatcher {
        private final EditText next;

        public GenericTextWatcher(EditText next) {
            this.next = next;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                next.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

}