package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyIncome extends AppCompatActivity {
    private static final String TAG = "adityai";

    private ImageButton back_button;
    private TextView tb_memberid, tb_username;
    private RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_income);

        back_button = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.recyclerView);
        tb_username = findViewById(R.id.memberName);
        tb_memberid = findViewById(R.id.memberId);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");


        pre(username, memberId);

    }

    private void pre(String username, String memberId) {
        back_button.setOnClickListener(v -> {
            finish();
        });

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));

        tb_username.setText(username);
        tb_memberid.setText(memberId);
    }
}
