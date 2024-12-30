package com.example.paymentapp;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class payment extends AppCompatActivity {

    private Button pay;
    private ImageButton back_button;
    private TextView payment_type,subtype,subtype_num,amount,memberName,userId;
    private TextView tpin_text,ctpin_text;
    private LinearLayout pay_layout,success_layout;
    private ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payment_type = findViewById(R.id.payment_type);
        subtype = findViewById(R.id.subtype);
        subtype_num = findViewById(R.id.subtype_num);
        amount = findViewById(R.id.amount);
        pay = findViewById(R.id.pay);
        tpin_text = findViewById(R.id.tpin_text);
        ctpin_text = findViewById(R.id.ctpin_text);
        pay_layout = findViewById(R.id.pay_layout);
        success_layout = findViewById(R.id.success_layout);
        progressBar = findViewById(R.id.pay_progress);
        back_button = findViewById(R.id.back_button);
        ////Initializer
        pre();
        ////

        Intent passed_intent = getIntent();
        if(passed_intent!=null){
            if(passed_intent.hasExtra("ptype"))
                payment_type.setText(passed_intent.getStringExtra("ptype"));
            if(passed_intent.hasExtra("stype"))
                subtype.setText(passed_intent.getStringExtra("stype"));
            if(passed_intent.hasExtra("stype_num")){
                subtype_num.setVisibility(View.VISIBLE);
                subtype_num.setText(passed_intent.getStringExtra("stype_num"));
            }
            if(passed_intent.hasExtra("amount"))
                amount.setText(passed_intent.getStringExtra("amount"));
        }

        LottieAnimationView lottieAnimationzoom = findViewById(R.id.success);
        lottieAnimationzoom.playAnimation();
        lottieAnimationzoom.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        pay.setOnClickListener(v -> {
            pay.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String tpin = tpin_text.getText().toString().trim();
            String ctpin = ctpin_text.getText().toString().trim();
            if(checkTPin(tpin,ctpin)){
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                pay.setVisibility(View.VISIBLE);
                pay_layout.setVisibility(View.GONE);
                success_layout.setVisibility(View.VISIBLE);
            }

        });
    }
    private boolean checkTPin(String tpin, String ctpin){
        if(tpin.isEmpty()){
            Toast.makeText(this, "Enter T-Pin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ctpin.isEmpty()){
            Toast.makeText(this, "Enter Confirm T-Pin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!ctpin.equals(tpin)){
            Toast.makeText(this, "T-Pin and Confirm T-Pin does not match", Toast.LENGTH_SHORT).show();
            return  false;
        }


        return true;
    }
    private void pre(){
        back_button.setOnClickListener(v -> finish());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        memberName = findViewById(R.id.memberName);
        userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);
    }
}