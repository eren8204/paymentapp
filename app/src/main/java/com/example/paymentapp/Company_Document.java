package com.example.paymentapp;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.io.InputStream;

public class Company_Document extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company__document, container, false);


        String[] imageNames = {
                "company_tancard.jpg",
                "gst_certificate.jpg",
                "gst_certificate_second.jpg",
                "gst_certificate_third.jpg",
                "incorporation_certificate.jpg",
                "pan_card_company.jpg"
        };
        String[] displayNames = {
                "TAN CARD",
                "GST CERTIFICATE",
                "GST CERTIFICATE SECOND",
                "GST CERTIFICATE THIRD",
                "INCORPORATION CERTIFICATE",
                "COMPANY PAN CARD"
        };

        int[] cardImageIds = {
                R.id.cardImage1,
                R.id.cardImage2,
                R.id.cardImage3,
                R.id.cardImage4,
                R.id.cardImage5,
                R.id.cardImage6
        };
        int[] cardNameIds = {
                R.id.cardName1,
                R.id.cardName2,
                R.id.cardName3,
                R.id.cardName4,
                R.id.cardName5,
                R.id.cardName6
        };
        int[] cardViewIds = {
                R.id.cardView1,
                R.id.cardView2,
                R.id.cardView3,
                R.id.cardView4,
                R.id.cardView5,
                R.id.cardView6
        };

        for (int i = 0; i < imageNames.length; i++) {
            ImageView cardImage = view.findViewById(cardImageIds[i]);
            TextView cardName = view.findViewById(cardNameIds[i]);
            CardView cardView = view.findViewById(cardViewIds[i]);

            loadImageFromAssets(cardImage,imageNames[i]);
            cardName.setText(displayNames[i]);

            final String imageName = imageNames[i];
            final String displayName = displayNames[i];
            cardView.setOnClickListener(v -> showImageDialog(imageName, displayName));
        }

        return view;
    }

    private void loadImageFromAssets(ImageView imageView, String imagePath) {
        try {
            Log.d("AssetsLoader", "Loading image: " + imagePath);

            InputStream inputStream = getContext().getAssets().open(imagePath);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AssetsLoader", "Error loading image: " + imagePath, e);
        }
    }



    private void showImageDialog(String imageName, String displayName) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_image);

        ImageView dialogImage = dialog.findViewById(R.id.dialogImage);
        TextView dialogImageName = dialog.findViewById(R.id.dialogImageName);
        Button closeButton = dialog.findViewById(R.id.dialogCloseButton);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        try {
            InputStream inputStream = getContext().getAssets().open(imageName);
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
