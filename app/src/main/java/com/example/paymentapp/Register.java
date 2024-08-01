package com.example.paymentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {


    private EditText countryCodeEditText;
    private EditText phoneNumberEditText;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        login=findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        countryCodeEditText = findViewById(R.id.country_code);
        phoneNumberEditText = findViewById(R.id.phonenumber);


        submitPhoneNumber();

    }
    private void submitPhoneNumber() {
        String countryCode = countryCodeEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (countryCode.isEmpty()) {
            countryCodeEditText.setError("Country code is required");
            countryCodeEditText.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Phone number is required");
            phoneNumberEditText.requestFocus();
            return;
        }

        String fullPhoneNumber = countryCode + phoneNumber;

    }
}