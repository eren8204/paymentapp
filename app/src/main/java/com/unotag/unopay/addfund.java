package com.unotag.unopay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class addfund extends BaseActivity {

    private ImageView imageView;
    private TextView upiId,filename;
    private CardView cardView;
    private static final String TAG = "addfund";
    private Bitmap qrBitmap;
    private Uri imageUri,cameraImageUri;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private FundRequestAdapter adapter;
    private List<FundRequest> fundRequestList = new ArrayList<>();
    private List<FundRequest> filteredList = new ArrayList<>();
    private ProgressBar progressBar,addfund_progress;
    private LinearLayout progress_layout;
    private ImageView back_button;
    private TextView qr_error;
    private LottieAnimationView lottieAnimationsave,lottieAnimationshare;
    private Dialog dialog1;
    private Dialog dialog2;
    private Button submitButton;
    private int seeSelect = 1;
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress","SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfund);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        imageView = findViewById(R.id.imageView);
        upiId = findViewById(R.id.upiID);
        cardView = findViewById(R.id.qr_cardview);
        progress_layout = findViewById(R.id.progress_layout);
        progressBar = findViewById(R.id.progressbar_addfund);
        qr_error = findViewById(R.id.qr_error);

        dialog1 = new Dialog(this);
        dialog2 = new Dialog(this);

//        Button shareButton = findViewById(R.id.shareButton);
        // Button saveButton = findViewById(R.id.saveButton);
        Button addFundButton = findViewById(R.id.addfund);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FundRequestAdapter(fundRequestList);
        recyclerView.setAdapter(adapter);

        // Fetch Data

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        fetchDataSequentially(memberId);

        addFundButton.setOnClickListener(v -> showAddFundDialog(memberId));

        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));

        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        RadioButton seeAll = findViewById(R.id.seeAll);
        RadioButton seeApproved = findViewById(R.id.seeApproved);
        RadioButton seePending = findViewById(R.id.seePending);
        RadioButton seeRejected = findViewById(R.id.seeRejected);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.seeAll){
                seeSelect = 1;
            }
            else if(checkedId==R.id.seeApproved){
                seeSelect = 2;
            }
            else if(checkedId==R.id.seePending) {
                seeSelect = 3;
            }
            else if(checkedId==R.id.seeRejected){
                seeSelect = 4;
            }
            else {
                seeSelect = 1;
            }
            filterDataByType();
        });

    }

    private void filterDataByType() {
        recyclerView.setVisibility(VISIBLE);
        if (filteredList != null) {
            filteredList.clear();
        } else {
            filteredList = new ArrayList<>();
        }
        if (seeSelect == 1) {
            filteredList.addAll(fundRequestList);
        } else if(seeSelect == 2){
            for (FundRequest fundRequest : fundRequestList) {
                if (fundRequest.getStatus().trim().equalsIgnoreCase("approved")) {
                    filteredList.add(fundRequest);
                }
            }
        }
        else if(seeSelect == 3){
            for (FundRequest fundRequest : fundRequestList) {
                if (fundRequest.getStatus().trim().equalsIgnoreCase("pending")) {
                    filteredList.add(fundRequest);
                }
            }
        }
        else if(seeSelect == 4){
            for (FundRequest fundRequest : fundRequestList) {
                if (fundRequest.getStatus().trim().equalsIgnoreCase("rejected")) {
                    filteredList.add(fundRequest);
                }
            }
        }
        adapter.updateList(filteredList);

    }

    private void fetchDataSequentially(String memberId) {
        fetchFundRequests(memberId, this::fetchImageName);
    }

    private void fetchFundRequests(String memberId, Runnable onComplete) {
        String url = BuildConfig.api_url+"getUserAddFundRequest";

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                response -> {
                    try {
                        if (response.has("data")) {
                            // Parse JSON response
                            String data = response.getString("data");
                            Type listType = new TypeToken<ArrayList<FundRequest>>() {}.getType();
                            List<FundRequest> requests = new Gson().fromJson(data, listType);

                            // Update UI
                            fundRequestList.clear();
                            fundRequestList.addAll(requests);

                            // Sort the list latest to oldest
                            fundRequestList.sort((request1, request2) -> {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date1 = sdf.parse(request1.getTime_date());
                                    Date date2 = sdf.parse(request2.getTime_date());
                                    assert date2 != null;
                                    return date2.compareTo(date1); // Latest to oldest
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            });

                            adapter.notifyDataSetChanged();
                        }

                        // Trigger next API or action regardless of "data"
                        if (onComplete != null) {
                            onComplete.run();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(addfund.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                        if (onComplete != null) {
                            onComplete.run(); // Proceed even if there's an error
                        }
                    }
                },
                error -> {
                    // Handle error
                    Toast.makeText(addfund.this, "Something broke", Toast.LENGTH_SHORT).show();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }) {
            @Override
            public byte[] getBody() {
                // Send JSON body
                String json = "{\"member_id\":\"" + memberId + "\"}";
                return json.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchImageName() {
        String url = BuildConfig.api_url+"getRandomQR";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "Response: " + response);
                    try {
                        if (response.getString("status").equals("true")) {
                            String imageName = response.getJSONObject("data").getString("qr");
                            String upiIdText = response.getJSONObject("data").getString("upi_id");
                            upiId.setText(upiIdText);
                            sendImageName(imageName);
                        } else {
                            showError("Failed to get image name");
                        }
                    } catch (JSONException e) {
                        showError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    showError("Network error occurred: " + error.getMessage());
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                }){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        getRequestQueue().add(jsonObjectRequest);
    }

    private void sendImageName(String imageName) {
        String url = BuildConfig.api_url+"getQRimage";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("qr", imageName);
        } catch (JSONException e) {
            showError("Error constructing request: " + e.getMessage());
            return;
        }

        // Use a custom request to handle binary data
        RequestQueue requestQueue = getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    // This part won't be used as response isn't JSON, so leave it for debugging purposes.
                    Log.e(TAG, "Unexpected JSON response: " + response);
                },
                error -> {
                    showError("Network error occurred: " + error.getMessage());
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(response.data, 0, response.data.length);
                    if (bitmap != null) {
                        runOnUiThread(() -> {
                            loadImage(bitmap);
                            qrBitmap=bitmap;
                            showSuccess();
                        });
                    } else {
                        runOnUiThread(() -> showError("Failed to decode image"));
                    }
                }
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void loadImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        qr_error.setVisibility(View.VISIBLE);
        Toast.makeText(addfund.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess() {
        progress_layout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
        lottieAnimationsave.setEnabled(true);
        lottieAnimationshare.setEnabled(true);
    }

    private RequestQueue getRequestQueue() {
        return Volley.newRequestQueue(this);
    }

    private void showAddFundDialog(String memberId) {

        dialog1.setContentView(R.layout.dialog_add_fund);
        dialog1.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog1.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog1.getWindow().setAttributes(layoutParams);


        EditText utrNumber = dialog1.findViewById(R.id.utrNumber);
        EditText toUpiId = dialog1.findViewById(R.id.toUpiId);
        EditText amount = dialog1.findViewById(R.id.amount);
        addfund_progress = dialog1.findViewById(R.id.addfund_progress);
        filename = dialog1.findViewById(R.id.filename);
        toUpiId.setEnabled(false);
        toUpiId.setText(upiId.getText().toString().trim());
        filename.setEnabled(false);
        ImageView uploadImage = dialog1.findViewById(R.id.uploadImage);
        submitButton = dialog1.findViewById(R.id.submitButton);

        filename.setOnClickListener(v -> dialog2.show());

        uploadImage.setOnClickListener(v -> {
            openImagePicker(1);
        });

        submitButton.setOnClickListener(v -> {
            String utrNumberText = utrNumber.getText().toString().trim();
            String memberIdText = memberId;
            String toUpiIdText = upiId.getText().toString().trim();
            String amountText = amount.getText().toString().trim();

            if (utrNumberText.isEmpty() || memberIdText.isEmpty() || toUpiIdText.isEmpty() || amountText.isEmpty() || imageUri == null) {
                Toast.makeText(addfund.this, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show();
                return;
            }
            if(Integer.parseInt(amountText)<50){
                Toast.makeText(addfund.this, "Minimum amount is 50", Toast.LENGTH_SHORT).show();
                return;
            }
            submitButton.setVisibility(View.GONE);
            addfund_progress.setVisibility(View.VISIBLE);
            sendAddFundRequest(utrNumberText, memberIdText, toUpiIdText, amountText);
        });

        dialog1.show();

    }

    private void sendAddFundRequest(String utrNumber, String memberId, String toUpiId, String amount) {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BuildConfig.api_url;

        try {
            File directory = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            File imageFile = new File(directory, Timing.getCurrentTimeEpoch() + "_selectedImg.jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos); // Compress as JPEG
            fos.close();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            RequestBody requestBodyUtrNumber = RequestBody.create(MediaType.parse("text/plain"), utrNumber);
            RequestBody requestBodyMemberId = RequestBody.create(MediaType.parse("text/plain"), memberId);
            RequestBody requestBodyToUpiId = RequestBody.create(MediaType.parse("text/plain"), toUpiId);
            RequestBody requestBodyAmount = RequestBody.create(MediaType.parse("text/plain"), amount);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("screenshot", imageFile.getName(), requestFile);
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.sendAddFundRequest(
                    "Bearer " + token,
                    requestBodyUtrNumber,
                    requestBodyMemberId,
                    requestBodyToUpiId,
                    requestBodyAmount,
                    imagePart
            );

            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    addfund_progress.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                    dialog1.dismiss();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Add Fund Response: " + response);
                        Toast.makeText(addfund.this, "Fund request submitted successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        String memberId = sharedPreferences.getString("memberId", "UP000000");
                        fetchDataSequentially(memberId);
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String errorMessage = jsonObject.optString("error", "Error Try Again Later");
                                Log.d("response", errorMessage);
                                Toast.makeText(addfund.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(addfund.this, "Error Try Again Later", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(addfund.this, "Error Try Again Later", Toast.LENGTH_SHORT).show();
                        }

                    }
                }


                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, "Retrofit Error: " + t.getMessage());
                    Toast.makeText(addfund.this, "Failed to submit fund request", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.d("img_error", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Gallery
                imageUri = (data != null) ? data.getData() : null;
            } else if (requestCode == 101) { // Camera (requestCode + 100)
                imageUri = cameraImageUri; // Use the URI from the camera capture
            }

            if (imageUri != null) {
                // Check the file size
                long imageSize = getFileSize(imageUri);
                if (imageSize > 5 * 1024 * 1024) { // 5MB in bytes
                    Toast.makeText(this, "Image size exceeds 5MB. Please select a smaller image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with showing the dialog
                dialog2.setContentView(R.layout.image_confirm_add_fund);
                dialog2.setCancelable(true);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog2.getWindow().setAttributes(layoutParams);

                ImageView img = dialog2.findViewById(R.id.confirm_img);
                img.setImageURI(imageUri);

                Button change = dialog2.findViewById(R.id.change);
                Button ok = dialog2.findViewById(R.id.ok);

                change.setOnClickListener(v -> {
                    openImagePicker(1);
                    dialog2.dismiss();
                });

                ok.setOnClickListener(v -> {
                    filename.setEnabled(true);
                    filename.setText("Image Selected, Click to View");
                    dialog2.dismiss();
                });

                dialog2.show();
                Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Helper method to get the file size from a Uri
    private long getFileSize(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        long size = 0;
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (sizeIndex != -1) {
                cursor.moveToFirst();
                size = cursor.getLong(sizeIndex);
            }
            cursor.close();
        }
        return size;
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

    private void openImagePicker(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera(requestCode + 100);
                    } else {
                        openGallery(requestCode);
                    }
                })
                .show();
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    private void openCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "IMG_" + System.currentTimeMillis() + ".jpg");

            cameraImageUri = FileProvider.getUriForFile(this,
                    getPackageName()+".fileprovider",
                    imageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(intent, requestCode);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, imageFileName);
    }

    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_Code_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null && bitmap != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                assert outputStream != null;
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error saving image: " + e.getMessage());
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    public static class Timing {
        public static String getCurrentTimeEpoch() {
            long epochTime = System.currentTimeMillis() / 1000;
            return String.valueOf(epochTime);
        }
    }
}