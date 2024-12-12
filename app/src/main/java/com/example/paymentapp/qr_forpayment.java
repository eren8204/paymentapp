package com.example.paymentapp;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class qr_forpayment extends AppCompatActivity {

    private ImageView imageView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_forpayment);
        imageView = findViewById(R.id.imageView);

        fetchImageName();
    }

    private void fetchImageName() {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/getRandomQR";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            if (response.getString("status").equals("true")) {
                                String imageName = response.getJSONObject("data").getString("qr");
                                Toast.makeText(qr_forpayment.this, "Image Name: " + imageName, Toast.LENGTH_SHORT).show();
                                sendImageName(imageName);
                            } else {
                                Toast.makeText(qr_forpayment.this, "Failed to get image name", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception: " + e.getMessage());
                            Toast.makeText(qr_forpayment.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        Toast.makeText(qr_forpayment.this, "Network error occurred", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void sendImageName(String imageName) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/getQRimage";

        // Create the request body as a JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("qr", imageName);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            Toast.makeText(qr_forpayment.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            return; // Exit if JSON creation fails
        }

        // Log the URL and JSON body being sent
        Log.d(TAG, "Sending URL: " + url);
        Log.d(TAG, "Sending JSON Body: " + jsonBody.toString());

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Make a POST request to send the QR code name
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // This part will not be executed since we expect an image response
                        Log.e(TAG, "Unexpected JSON response: " + response.toString());
                        Toast.makeText(qr_forpayment.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Handle the raw response here
                if (response.data != null) {
                    // The response is an image; convert it to a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
                    if (bitmap != null) {
                        // Update the UI on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadImage(bitmap); // Call method to display the image
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to decode bitmap");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(qr_forpayment.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                return super.parseNetworkResponse(response); // Return null since we are not expecting a JSON object here
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void loadImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        Toast.makeText(qr_forpayment.this, "Image loaded successfully", Toast.LENGTH_SHORT).show();
    }



    private void loadImage(String imageUrl) {
        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                        Toast.makeText(qr_forpayment.this, "Image loaded successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Image Load Error: " + error.getMessage());
                        Toast.makeText(qr_forpayment.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(imageRequest);
    }
}
