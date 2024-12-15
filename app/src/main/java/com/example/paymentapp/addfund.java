package com.example.paymentapp;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Type;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class addfund extends AppCompatActivity {

    private ImageView imageView;
    private TextView upiId;
    private CardView cardView;
    private static final String TAG = "addfund";
    private Bitmap qrBitmap;
    private Uri imageUri;

    private RecyclerView recyclerView;
    private FundRequestAdapter adapter;
    private List<FundRequest> fundRequestList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout progress_layout;
    private ImageView back_button;
    private TextView qr_error;
    private LottieAnimationView lottieAnimationsave,lottieAnimationshare;
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfund);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        imageView = findViewById(R.id.imageView);
        upiId = findViewById(R.id.upiID);
        cardView = findViewById(R.id.qr_cardview);
        progress_layout = findViewById(R.id.progress_layout);
        progressBar = findViewById(R.id.progressbar_addfund);
        qr_error = findViewById(R.id.qr_error);

//        Button shareButton = findViewById(R.id.shareButton);
       // Button saveButton = findViewById(R.id.saveButton);
        Button addFundButton = findViewById(R.id.addfund);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FundRequestAdapter(fundRequestList);
        recyclerView.setAdapter(adapter);

        // Fetch Data
        fetchImageName();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        fetchFundRequests(memberId);

        // Add Fund button functionality
        addFundButton.setOnClickListener(v -> showAddFundDialog(memberId));

        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
// Share button functionality
        lottieAnimationshare = findViewById(R.id.shareButton);
        lottieAnimationshare.playAnimation();
        lottieAnimationshare.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        lottieAnimationshare.setOnClickListener(v -> shareCardView());


        // Save button functionality

        lottieAnimationsave = findViewById(R.id.saveButton);
        lottieAnimationsave.playAnimation();
        lottieAnimationsave.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });



        lottieAnimationsave.setOnClickListener(v -> {
            if (qrBitmap != null) {
                saveImageToGallery(qrBitmap);
            } else {
                Toast.makeText(addfund.this, "QR Image not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        lottieAnimationsave.setEnabled(false);
        lottieAnimationshare.setEnabled(false);
    }


    private void fetchFundRequests(String memberId) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/getUserAddFundRequest";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<org.json.JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        try {
                            String data = response.getString("data");
                            Type listType = new TypeToken<ArrayList<FundRequest>>() {}.getType();
                            List<FundRequest> requests = new Gson().fromJson(data, listType);
                            fundRequestList.clear();
                            fundRequestList.addAll(requests);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(addfund.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(addfund.this, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() {
                String json = "{\"member_id\":\"" + memberId + "\"}";
                return json.getBytes();
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchImageName() {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/getRandomQR";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response: " + response);
                    try {
                        if (response.getString("status").equals("true")) {
                            String imageName = response.getJSONObject("data").getString("qr");
                            String upi_id = response.getJSONObject("data").getString("upi_id");
                            upiId.setText(upi_id);
                            sendImageName(imageName);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            qr_error.setVisibility(View.VISIBLE);
                            Toast.makeText(addfund.this, "Failed to get image name", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        qr_error.setVisibility(View.VISIBLE);
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    qr_error.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                    Toast.makeText(addfund.this, "Network error occurred", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void sendImageName(String imageName) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/getQRimage";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("qr", imageName);
        } catch (JSONException e) {
            progressBar.setVisibility(View.GONE);
            qr_error.setVisibility(View.VISIBLE);
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> Log.e(TAG, "Unexpected JSON response: " + response),
                error -> Log.e(TAG, "Volley Error: " + error.getMessage())) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.data != null) {
                    qrBitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
                    if (qrBitmap != null) {
                        runOnUiThread(() -> loadImage(qrBitmap));
                        progress_layout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        cardView.setVisibility(View.VISIBLE);
                        lottieAnimationsave.setEnabled(true);
                        lottieAnimationshare.setEnabled(true);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        qr_error.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Failed to decode bitmap");
                        runOnUiThread(() -> Toast.makeText(addfund.this, "Failed to load image", Toast.LENGTH_SHORT).show());
                    }
                }
                return super.parseNetworkResponse(response);
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
       // Toast.makeText(addfund.this, "Image loaded successfully", Toast.LENGTH_SHORT).show();
    }


    private void showAddFundDialog(String memberId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_fund);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);



        EditText transactionId = dialog.findViewById(R.id.transactionId);
        EditText utrNumber = dialog.findViewById(R.id.utrNumber);
//        EditText memberId = dialog.findViewById(R.id.memberId);
        EditText toUpiId = dialog.findViewById(R.id.toUpiId);
        EditText amount = dialog.findViewById(R.id.amount);

        ImageView uploadImage = dialog.findViewById(R.id.uploadImage);
        Button submitButton = dialog.findViewById(R.id.submitButton);

        uploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        submitButton.setOnClickListener(v -> {
            String transactionIdText = transactionId.getText().toString().trim();
            String utrNumberText = utrNumber.getText().toString().trim();
            String memberIdText = memberId;
            String toUpiIdText = toUpiId.getText().toString().trim();
            String amountText = amount.getText().toString().trim();

            if (transactionIdText.isEmpty() || utrNumberText.isEmpty() || memberIdText.isEmpty() || toUpiIdText.isEmpty() || amountText.isEmpty() || imageUri == null) {
                Toast.makeText(addfund.this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            sendAddFundRequest(transactionIdText, utrNumberText, memberIdText, toUpiIdText, amountText);
            dialog.dismiss();
        });

        dialog.show();

    }

    private void sendAddFundRequest(String transactionId, String utrNumber, String memberId, String toUpiId, String amount) {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/";

        try {
            // Ensure directory exists
            File directory = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory
            }

            // Get bitmap from imageUri
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Create a file to save the bitmap
            File imageFile = new File(directory, Timing.getCurrentTimeEpoch() + "_selectedImg.jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Compress as JPEG
            fos.close();

            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create API service
            ApiService apiService = retrofit.create(ApiService.class);

            // Prepare the request body with multipart image
            RequestBody requestBodyTransactionId = RequestBody.create(MediaType.parse("text/plain"), transactionId);
            RequestBody requestBodyUtrNumber = RequestBody.create(MediaType.parse("text/plain"), utrNumber);
            RequestBody requestBodyMemberId = RequestBody.create(MediaType.parse("text/plain"), memberId);
            RequestBody requestBodyToUpiId = RequestBody.create(MediaType.parse("text/plain"), toUpiId);
            RequestBody requestBodyAmount = RequestBody.create(MediaType.parse("text/plain"), amount);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("screenshot", imageFile.getName(), requestFile);

            // Make the POST request
            Call<ResponseBody> call = apiService.sendAddFundRequest(
                    requestBodyTransactionId,
                    requestBodyUtrNumber,
                    requestBodyMemberId,
                    requestBodyToUpiId,
                    requestBodyAmount,
                    imagePart
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Add Fund Response: " + response);
                        Toast.makeText(addfund.this, "Fund request submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorMessage;
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            } else {
                                errorMessage = "Unknown error occurred";
                            }
                            Log.d("response", errorMessage);
                            Toast.makeText(addfund.this, "Failed :" + errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("response", "Error parsing error response");
                            Toast.makeText(addfund.this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }


                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, "Retrofit Error: " + t.getMessage());
                    Toast.makeText(addfund.this, "Failed to submit fund request2", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.d("img_error", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        ImageView upload = findViewById(R.id.upload);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
//                upload.setImageURI(imageUri);
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void shareCardView() {
        cardView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(cardView.getDrawingCache());
        cardView.setDrawingCacheEnabled(false);

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CardView", null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(shareIntent, "Share CardView"));
    }

    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_Code_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                assert outputStream != null;
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error saving image: " + e.getMessage());
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class Timing {
        public static String getCurrentTimeEpoch() {
            long epochTime = System.currentTimeMillis() / 1000;
            return String.valueOf(epochTime);
        }
    }

}
