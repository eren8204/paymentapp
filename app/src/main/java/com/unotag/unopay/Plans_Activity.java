package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class Plans_Activity extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private TextView header,final_price,header2,final_price2,memberName,userId;
    private Button payBasic,payPrime;
    private ImageButton back_button;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        pre();

        header = findViewById(R.id.header);
        header2 = findViewById(R.id.header2);
        final_price = findViewById(R.id.final_price);
        final_price2 = findViewById(R.id.final_price2);
        payBasic = findViewById(R.id.payBasic);
        payPrime = findViewById(R.id.payPrime);

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            finish();
        });


        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        payBasic.setOnClickListener(v -> {
            Intent intent = new Intent(this, payment.class);
            intent.putExtra("ptype","E-Book");
            intent.putExtra("stype",header.getText().toString().trim());
            intent.putExtra("amount",final_price.getText().toString().trim());
            startActivity(intent);
        });

        payPrime.setOnClickListener(v -> {
            Intent intent = new Intent(this, payment.class);
            intent.putExtra("ptype","E-Book Bundle");
            intent.putExtra("stype",header2.getText().toString().trim());
            intent.putExtra("amount",final_price2.getText().toString().trim());
            startActivity(intent);
        });
    }
    private void pre(){
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        memberName = findViewById(R.id.memberName);
        userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);
    }
}