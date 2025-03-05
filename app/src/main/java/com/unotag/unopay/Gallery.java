package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Gallery extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Bitmap> imageUrls = new ArrayList<>();
    private int width = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        width = getResources().getDisplayMetrics().widthPixels / 3;
        imageAdapter = new ImageAdapter(this,imageUrls,width);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(0));
        recyclerView.setAdapter(imageAdapter);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP100000");
        fetchGalleryImages();

        // Load user details
        pre();

        // Share functionality
        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v -> ShareUtil.shareApp(this, memberId));
    }


    private void fetchGalleryImages() {
        OkHttpClient client = new OkHttpClient();
        String token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Authentication error!", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
                .url(BuildConfig.api_url + "get-gallery-images")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(Gallery.this, "No Images Found", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }

                assert response.body() != null;
                String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray imagesArray = jsonObject.getJSONArray("images");

                    imageUrls.clear();
                    for (int i = 0; i < imagesArray.length(); i++) {
                        String imageName = imagesArray.getString(i);
                        fetchImageData(imageName);  // Fetch actual image data
                    }
                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(Gallery.this, "Parsing error!", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchImageData(String imageName) {
        String url = BuildConfig.api_url + "get-gallery-image-file";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("filename", imageName);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                jsonBody,
                response -> Log.e("Gallery", "Unexpected JSON response: " + response),
                error -> Log.e("Gallery", "Volley Error: " + error.getMessage())) {
            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
                    if (bitmap != null) {
                        runOnUiThread(() -> {
                            imageAdapter.addImage(bitmap);
                        });
                    }
                }
                return com.android.volley.Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


    private void showError(String message) {
        Toast.makeText(Gallery.this, message, Toast.LENGTH_SHORT).show();
    }


    private void pre() {
        String username = sharedPreferences.getString("username", "Hello, User");
        String memberId = sharedPreferences.getString("memberId", "UP100000");
        TextView tb_username = findViewById(R.id.username);
        TextView tb_memberid = findViewById(R.id.memberId);
        tb_username.setText(username);
        tb_memberid.setText(memberId);

        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> finish());
    }
}