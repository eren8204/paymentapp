package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
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

public class BankKYCFragment extends Fragment {

    private Uri imageUripan, imageUriaadhar, imageUripassbook, imageUriimage;
    private ImageView pancardcheck,Aadharcardcheck,passbookcheck,imagecheck,kyc_img;
    private EditText username, Pannumber, IFSCcode, BankName, AccountNumber, ConfirmAccountNumber, Aadharnumber, nomineeName;
    private Dialog dialog2;
    private ProgressBar progressBar,progressBar2;
    private Button submitButton,fillAgain;
    private TextView responseText,statusText,kyc_response;
    private LinearLayout form,kyc_status_layout;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_k_y_c, container, false);

        // Initialize EditText fields
        username = view.findViewById(R.id.User_name);
        Pannumber = view.findViewById(R.id.Pan_number);
        IFSCcode = view.findViewById(R.id.IFSCcode);
        BankName = view.findViewById(R.id.BankName);
        AccountNumber = view.findViewById(R.id.AccountNumber);
        ConfirmAccountNumber = view.findViewById(R.id.ConfirmAccountNumber);
        Aadharnumber = view.findViewById(R.id.Aadharnumber);
        nomineeName = view.findViewById(R.id.NomineeName);
        pancardcheck = view.findViewById(R.id.pancardcheck);
        Aadharcardcheck = view.findViewById(R.id.Aadharcardcheck);
        passbookcheck = view.findViewById(R.id.passbookcheck);
        imagecheck = view.findViewById(R.id.imagecheck);
        progressBar = view.findViewById(R.id.progressbarUpdate);
        kyc_status_layout = view.findViewById(R.id.kyc_status_layout);
        kyc_img = view.findViewById(R.id.status_img);
        statusText = view.findViewById(R.id.statusText);
        kyc_response = view.findViewById(R.id.kyc_response);

        dialog2 = new Dialog(view.getContext());

        progressBar2=view.findViewById(R.id.progressbar);
        responseText=view.findViewById(R.id.responseText);
        fillAgain=view.findViewById(R.id.fill_again);
        form=view.findViewById(R.id.form);


        fillAgain.setOnClickListener(v -> {
            kyc_status_layout.setVisibility(View.GONE);
            form.setVisibility(View.VISIBLE);
            fillAgain.setVisibility(View.GONE);
            responseText.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
        });
        pancardcheck.setEnabled(false);
        Aadharcardcheck.setEnabled(false);
        passbookcheck.setEnabled(false);
        imagecheck.setEnabled(false);
        // Get memberId from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        // Set up ImageView click listeners
        setUpImageUploadListeners(view);
        fetchKYCStatus(memberId);
        // Set submit button listener
        submitButton = view.findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> submitFormData(memberId));

        return view;
    }

    private void fetchKYCStatus(String memberId) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/userkycstatus";

        // Create a request body
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);

        // Convert the parameters to a JSONObject
        JSONObject requestBody = new JSONObject(params);

        // Create a Volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the response
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                JSONObject data = response.getJSONObject("data");
                                String kycStatus = data.getString("Kyc_status");
                                String kycMessage = data.getString("Kyc_message");

                                if(kycStatus.equals("pending"))
                                {
                                    kyc_img.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.pending));
                                    statusText.setText("Pending");
                                    form.setVisibility(View.GONE);
                                    fillAgain.setVisibility(View.GONE);
                                    responseText.setVisibility(View.GONE);
                                    kyc_response.setVisibility(View.GONE);
                                    progressBar2.setVisibility(View.GONE);

                                } else if (kycStatus.equals("approved")) {
                                    kyc_img.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.kyc_accept));
                                    statusText.setText("Approved");
                                    responseText.setText("KYC Already Done");
                                    form.setVisibility(View.GONE);
                                    fillAgain.setVisibility(View.GONE);
                                    responseText.setVisibility(View.VISIBLE);
                                    progressBar2.setVisibility(View.GONE);
                                }
                                else
                                {
                                    kyc_img.setBackground(ContextCompat.getDrawable(requireContext(),R.drawable.kyc_reject));
                                    statusText.setText("Rejected");
                                    responseText.setText(kycMessage);
                                    form.setVisibility(View.GONE);
                                    fillAgain.setVisibility(View.VISIBLE);
                                    responseText.setVisibility(View.VISIBLE);
                                    progressBar2.setVisibility(View.GONE);
                                }
                                kyc_status_layout.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(),
                                        "KYC Status: " + kycStatus + "\nMessage: " + kycMessage,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                form.setVisibility(View.VISIBLE);
                                fillAgain.setVisibility(View.GONE);
                                responseText.setVisibility(View.GONE);
                                progressBar2.setVisibility(View.GONE);
                                Toast.makeText(requireContext(),
                                        "Failed to fetch KYC status.",
                                        Toast.LENGTH_SHORT).show();
                                progressBar2.setVisibility(View.GONE);
                                kyc_status_layout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar2.setVisibility(View.GONE);
                            kyc_status_layout.setVisibility(View.VISIBLE);
                            kyc_img.setVisibility(View.GONE);
                            responseText.setVisibility(View.GONE);
                            kyc_response.setVisibility(View.GONE);
                            statusText.setVisibility(View.VISIBLE);
                            statusText.setText("Error! Try Again");
                            Toast.makeText(requireContext(),
                                    "Error parsing response.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    // Handle error
                    progressBar2.setVisibility(View.GONE);
                    kyc_status_layout.setVisibility(View.GONE);
                    form.setVisibility(View.VISIBLE);
                    error.printStackTrace();
                    Toast.makeText(requireContext(),
                            "Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Add headers if needed
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void setUpImageUploadListeners(View view) {
        view.findViewById(R.id.uploadPancard).setOnClickListener(v -> openImagePicker(1));
        view.findViewById(R.id.uploadAadharcard).setOnClickListener(v -> openImagePicker(2));
        view.findViewById(R.id.uploadPassBook).setOnClickListener(v -> openImagePicker(3));
        view.findViewById(R.id.uploadUserimage).setOnClickListener(v -> openImagePicker(4));
    }

    private void openImagePicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void submitFormData(String memberId) {
        // Collect input data
        submitButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String userName = username.getText().toString().trim();
        String panNumber = Pannumber.getText().toString().trim();
        String ifscCode = IFSCcode.getText().toString().trim();
        String bankName = BankName.getText().toString().trim();
        String accountNumber = AccountNumber.getText().toString().trim();
        String confirmAccountNumber = ConfirmAccountNumber.getText().toString().trim();
        String aadharNumber = Aadharnumber.getText().toString().trim();
        String nomineeNameInput = nomineeName.getText().toString().trim();

        Log.d("KYC", "Member ID: " + memberId);
        // Validate inputs
        if (!validateInputs(userName, panNumber, ifscCode, bankName, accountNumber, confirmAccountNumber, aadharNumber, nomineeNameInput)) {
            progressBar.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
            return;
        }

        // Submit data via API
        sendKycRequest(userName, panNumber, memberId, ifscCode, bankName, accountNumber, aadharNumber, nomineeNameInput);
    }

    private boolean validateInputs(String userName, String panNumber, String ifscCode, String bankName, String accountNumber, String confirmAccountNumber, String aadharNumber, String nomineeName) {
        if (userName.isEmpty() || panNumber.isEmpty() || ifscCode.isEmpty() || bankName.isEmpty() ||
                accountNumber.isEmpty() || confirmAccountNumber.isEmpty() || aadharNumber.isEmpty() || nomineeName.isEmpty() ||
                imageUripan == null || imageUriaadhar == null || imageUripassbook == null || imageUriimage == null) {
            Toast.makeText(getActivity(), "Please fill all fields and upload all required images", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!accountNumber.equals(confirmAccountNumber)) {
            Toast.makeText(getActivity(), "Account Number and Confirm Account Number do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendKycRequest(String userName, String panNumber, String memberId, String ifscCode, String bankName, String accountNumber, String aadharNumber, String nomineeName) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/";
        form.setEnabled(false);
        try {
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();

            // Save images
            File imageFilePan = saveImageToFile(imageUripan, directory, "panCard.jpg");
            File imageFileAadhar = saveImageToFile(imageUriaadhar, directory, "aadharCard.jpg");
            File imageFilePassbook = saveImageToFile(imageUripassbook, directory, "passbook.jpg");
            File imageFileUser = saveImageToFile(imageUriimage, directory, "userImage.jpg");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            // Prepare request bodies
            MultipartBody.Part partPanCard = prepareMultipartBody(imageFilePan, "kycPancard");
            MultipartBody.Part partAadharCard = prepareMultipartBody(imageFileAadhar, "kycAadharcard");
            MultipartBody.Part partPassbook = prepareMultipartBody(imageFilePassbook, "kycPassbook");
            MultipartBody.Part partUserImage = prepareMultipartBody(imageFileUser, "kycUserImage");

            Call<ResponseBody> call = apiService.submitUserBankKycDetails(
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    RequestBody.create(MediaType.parse("text/plain"), userName),
                    RequestBody.create(MediaType.parse("text/plain"), panNumber),
                    RequestBody.create(MediaType.parse("text/plain"), ifscCode),
                    RequestBody.create(MediaType.parse("text/plain"), bankName),
                    RequestBody.create(MediaType.parse("text/plain"), accountNumber),
                    RequestBody.create(MediaType.parse("text/plain"), aadharNumber),
                    RequestBody.create(MediaType.parse("text/plain"), nomineeName),
                    partPanCard, partAadharCard, partPassbook, partUserImage
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                    if (response.isSuccessful()) {
                        Fragment selectedFragment = new HomeFragment();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.nav_host_fragment, selectedFragment);
                        transaction.commit();
                        Toast.makeText(getActivity(), "KYC submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        form.setEnabled(true);
                        Log.e("KYC", "Submission failed: " + response.message());
                        try {
                            assert response.errorBody() != null;
                            String errorBody = response.errorBody().string();  // Log the error body if available
                            Log.e("KYC", "Error body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("KYC", "Error reading error body", e);
                        }
                        Toast.makeText(getActivity(), "Submission failed", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    form.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Log.e("KYC", "Error: " + t.getMessage(), t);
                    Toast.makeText(getActivity(), "Error submitting KYC", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            form.setEnabled(true);
            Log.e("KYC", "File processing error: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error processing images", Toast.LENGTH_SHORT).show();
        }
    }

    private File saveImageToFile(Uri imageUri, File directory, String fileName) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        File imageFile = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        return imageFile;
    }

    private MultipartBody.Part prepareMultipartBody(File file, String formFieldName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData(formFieldName, file.getName(), requestFile);
    }
public void imagePreview(Uri selectedImageUri, ImageView btn_id, int i){
            btn_id.setOnClickListener(v -> {
            dialog2.setContentView(R.layout.img_confirm_dialog);
            dialog2.setCancelable(true);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog2.getWindow().setAttributes(layoutParams);

            ImageView img = dialog2.findViewById(R.id.confirm_img);
            img.setImageURI(selectedImageUri);

            Button change = dialog2.findViewById(R.id.change);
            Button ok = dialog2.findViewById(R.id.ok);

            change.setOnClickListener(s -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, i);
                dialog2.dismiss();
            });

            ok.setOnClickListener(s ->{

                dialog2.dismiss();
            });

            dialog2.show();
            Toast.makeText(requireView().getContext(), "Image selected successfully", Toast.LENGTH_SHORT).show();
        });
    dialog2.setContentView(R.layout.img_confirm_dialog);
    dialog2.setCancelable(true);

    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    layoutParams.copyFrom(Objects.requireNonNull(dialog2.getWindow()).getAttributes());
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    dialog2.getWindow().setAttributes(layoutParams);

    ImageView img = dialog2.findViewById(R.id.confirm_img);
    img.setImageURI(selectedImageUri);

    Button change = dialog2.findViewById(R.id.change);
    Button ok = dialog2.findViewById(R.id.ok);

    change.setOnClickListener(s -> {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, i);
        dialog2.dismiss();
    });

    ok.setOnClickListener(s ->{
        dialog2.dismiss();
    });

    dialog2.show();
    Toast.makeText(requireView().getContext(), "Image selected successfully", Toast.LENGTH_SHORT).show();
}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri == null) return;
            long imageSize = getFileSize(selectedImageUri);
            if (imageSize > 2 * 1024 * 1024) { // 2MB in bytes
                Toast.makeText(requireContext(), "Image size exceeds 2MB. Please select a smaller image.", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (requestCode) {
                case 1: imageUripan = selectedImageUri;
                    pancardcheck.setVisibility(View.VISIBLE);
                    pancardcheck.setEnabled(true);
                    imagePreview(selectedImageUri,pancardcheck,1);
                break;
                case 2: imageUriaadhar = selectedImageUri;
                    Aadharcardcheck.setVisibility(View.VISIBLE);
                    Aadharcardcheck.setEnabled(true);
                    imagePreview(selectedImageUri,Aadharcardcheck,2);
                    break;
                case 3: imageUripassbook = selectedImageUri;
                    passbookcheck.setVisibility(View.VISIBLE);
                    passbookcheck.setEnabled(true);
                    imagePreview(selectedImageUri,passbookcheck,3);
                    break;
                case 4: imageUriimage = selectedImageUri;
                    imagecheck.setVisibility(View.VISIBLE);
                    imagecheck.setEnabled(true);
                    imagePreview(selectedImageUri,imagecheck,4);
                    break;
            }
        }
    }
    private long getFileSize(Uri uri) {
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
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


}
