package com.example.paymentapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SuccessRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_register);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberid = findViewById(R.id.memberid);
        memberid.setText(memberId);

        Intent intent = getIntent();
        String sponsorIDText = intent.getStringExtra("SPONSOR_ID");
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER");
        String username = intent.getStringExtra("USERNAME");
        String email = intent.getStringExtra("EMAIL");
        String password = intent.getStringExtra("PASSWORD");
        String tPin = intent.getStringExtra("TPIN");


        TextView sponsorIDTextView = findViewById(R.id.sponserID);
        TextView phoneNumberTextView = findViewById(R.id.phone_no);
        TextView usernameTextView = findViewById(R.id.name);
        TextView emailTextView = findViewById(R.id.email);
        TextView passwordTextView = findViewById(R.id.password);
        TextView tPinTextView = findViewById(R.id.tpin);

        sponsorIDTextView.setText(sponsorIDText);
        phoneNumberTextView.setText(phoneNumber);
        usernameTextView.setText(username);
        emailTextView.setText(email);
        passwordTextView.setText(password);
        tPinTextView.setText(tPin);
    }
}