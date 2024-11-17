package com.example.paymentapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class recharge_plans extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_plans);
        String jsonResponse = getIntent().getStringExtra("response");
        TextView textView = findViewById(R.id.data);
        if (jsonResponse != null) {
            textView.setText(jsonResponse);
        } else {
            textView.setText("No response received.");
        }
    }
}