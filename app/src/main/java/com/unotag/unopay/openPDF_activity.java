package com.unotag.unopay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class openPDF_activity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private PDFView pdfView;
    private String pdfName = "unopay.pdf";  // Your PDF file name
    private ImageButton back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdf);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        pdfView = findViewById(R.id.pdfView);
        back_button = findViewById(R.id.back_button);
        Button btnDownload = findViewById(R.id.btnDownload);


        // Display the PDF from assets
        displayPDF(pdfName);

        // Set up the Download button
        btnDownload.setOnClickListener(v -> {
            downloadPDF(pdfName);
            Toast.makeText(openPDF_activity.this, "PDF downloaded", Toast.LENGTH_SHORT).show();
        });

        sharedPreferences= getSharedPreferences("UserPrefs",MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);
        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> finish());
        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        // Set up the Share button

    }

    private void displayPDF(String pdfName) {
        try {
            InputStream asset = getAssets().open(pdfName);
            pdfView.fromStream(asset)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadPDF(String pdfName) {
        try {
            // Ensure the external directory is available for storing the PDF
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyPDFs");
            if (!dir.exists()) {
                dir.mkdirs(); // Create the directory if it doesn't exist
            }

            // Copy the PDF from assets to external storage
            File file = new File(dir, pdfName);
            if (file.exists()) {
                file.delete(); // Delete the existing file if it exists
            }

            InputStream asset = getAssets().open(pdfName);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = asset.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            asset.close();
            outputStream.close();

            // Notify the user
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
