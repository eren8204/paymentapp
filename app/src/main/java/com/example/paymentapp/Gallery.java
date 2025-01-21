package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.shadow.ShadowRenderer;

import java.util.Arrays;
import java.util.List;

public class Gallery extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        pre();
        List<String> imageUrls = Arrays.asList(
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200",
                "https://picsum.photos/200"
        );
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 images per row
        recyclerView.setAdapter(new ImageAdapter(this, imageUrls));
    }
    private void pre() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
        String username = sharedPreferences.getString("username","Hello, User");
        String memberId = sharedPreferences.getString("memberId","UP100000");
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            finish();
        });
        TextView tb_username = findViewById(R.id.username);
        TextView tb_memberid = findViewById(R.id.memberId);

        tb_username.setText(username);
        tb_memberid.setText(memberId);
    }
}