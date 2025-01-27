package com.example.paymentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessRegisterActivity extends AppCompatActivity {

    Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_register);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));

        Intent intent = getIntent();

        // Fetch data with default values if missing
        String sponsorIDText = intent.getStringExtra("SPONSOR_ID");
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER");
        String username = intent.getStringExtra("USERNAME");
        String email = intent.getStringExtra("EMAIL");
        String password = intent.getStringExtra("PASSWORD");
        String tPin = intent.getStringExtra("TPIN");
        String memberId = intent.getStringExtra("MEMBER_ID");
        String date = intent.getStringExtra("DATE");

        // Check if any data is missing and log it
        if (sponsorIDText == null || phoneNumber == null || username == null || email == null ||
                password == null || tPin == null || memberId == null || date == null) {

            StringBuilder missingData = new StringBuilder("Missing data: ");

            if (sponsorIDText == null) missingData.append("SPONSOR_ID, ");
            if (phoneNumber == null) missingData.append("PHONE_NUMBER, ");
            if (username == null) missingData.append("USERNAME, ");
            if (email == null) missingData.append("EMAIL, ");
            if (password == null) missingData.append("PASSWORD, ");
            if (tPin == null) missingData.append("TPIN, ");
            if (memberId == null) missingData.append("MEMBER_ID, ");
            if (date == null) missingData.append("DATE, ");

            // Remove the trailing comma and space
            if (missingData.length() > 14) {
                missingData.setLength(missingData.length() - 2);
            }

            Log.e("Bhenkeloada", missingData.toString());
            Toast.makeText(this, missingData.toString(), Toast.LENGTH_LONG).show();
        }


        // Set default values for missing data
        sponsorIDText = sponsorIDText != null ? sponsorIDText : "N/A";
        phoneNumber = phoneNumber != null ? phoneNumber : "N/A";
        username = username != null ? username : "N/A";
        email = email != null ? email : "N/A";
        password = password != null ? password : "N/A";
        tPin = tPin != null ? tPin : "N/A";
        memberId = memberId != null ? memberId : "N/A";
        date = date != null ? date : "N/A";

        loginbtn = findViewById(R.id.login);
        loginbtn.setOnClickListener(v -> {
            Intent intent1 = new Intent(SuccessRegisterActivity.this, Login.class);
            startActivity(intent1);
            finish();
        });

        // Display data in TextViews
        TextView sponsorIDTextView = findViewById(R.id.sponserID);
        TextView phoneNumberTextView = findViewById(R.id.phone_no);
        TextView usernameTextView = findViewById(R.id.name);
        TextView emailTextView = findViewById(R.id.email);
        TextView passwordTextView = findViewById(R.id.password);
        TextView tPinTextView = findViewById(R.id.tpin);
        TextView memberidTextView = findViewById(R.id.memberid);
        TextView dojTextView = findViewById(R.id.doj);

        sponsorIDTextView.setText(sponsorIDText);
        phoneNumberTextView.setText(phoneNumber);
        usernameTextView.setText(username);
        emailTextView.setText(email);
        passwordTextView.setText(password);
        tPinTextView.setText(tPin);
        memberidTextView.setText(memberId);
        dojTextView.setText(date);
    }
}
