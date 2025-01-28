package com.example.paymentapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class companydocument extends AppCompatActivity {

    private ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companydocument);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        String[] imageNames = {
                "company_tancard.jpg",
                "gst_certificate.jpg",
                "gst_certificate_third.jpg",
                "incorporation_certificate.jpg",
                "pan_card_company.jpg"
        };
        String[] displayNames = {
                "TAN CARD",
                "GST CERTIFICATE",
                "GST CERTIFICATE THIRD",
                "INCORPORATION CERTIFICATE",
                "COMPANY PAN CARD"
        };

        int[] cardImageIds = {
                R.id.cardImage1,
                R.id.cardImage2,
                R.id.cardImage4,
                R.id.cardImage5,
                R.id.cardImage6
        };
        int[] cardNameIds = {
                R.id.cardName1,
                R.id.cardName2,
                R.id.cardName4,
                R.id.cardName5,
                R.id.cardName6
        };
        int[] cardViewIds = {
                R.id.cardView1,
                R.id.cardView2,
                R.id.cardView4,
                R.id.cardView5,
                R.id.cardView6
        };

        for (int i = 0; i < imageNames.length; i++) {
            ImageView cardImage = findViewById(cardImageIds[i]);
            TextView cardName = findViewById(cardNameIds[i]);
            CardView cardView = findViewById(cardViewIds[i]);

            loadImageFromAssets(cardImage,imageNames[i]);
            cardName.setText(displayNames[i]);

            final String imageName = imageNames[i];
            final String displayName = displayNames[i];
            cardView.setOnClickListener(v -> showImageDialog(imageName, displayName));
        }

    }

    private void loadImageFromAssets(ImageView imageView, String imagePath) {
        try {
            Log.d("AssetsLoader", "Loading image: " + imagePath);

            InputStream inputStream = getAssets().open(imagePath);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AssetsLoader", "Error loading image: " + imagePath, e);
        }
    }



    private void showImageDialog(String imageName, String displayName) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image);

        ImageView dialogImage = dialog.findViewById(R.id.dialogImage);
        TextView dialogImageName = dialog.findViewById(R.id.dialogImageName);
        Button closeButton = dialog.findViewById(R.id.dialogCloseButton);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        try {
            InputStream inputStream = getAssets().open(imageName);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            dialogImage.setImageDrawable(drawable);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialogImageName.setText(displayName);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}