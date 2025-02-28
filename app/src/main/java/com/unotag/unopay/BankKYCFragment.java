package com.unotag.unopay;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
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
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BankKYCFragment extends Fragment {

    private Uri imageUripan, imageUriaadhar, imageUripassbook, imageUriimage, imageUriaadharback;
    private ImageView pancardcheck,Aadharcardcheck,passbookcheck,imagecheck,kyc_img,Aadharcardcheckback;
    private EditText username, Pannumber, IFSCcode, BankName, AccountNumber, ConfirmAccountNumber, Aadharnumber, nomineeName, nomineeRelation;

    private EditText otpedittext;
    private EditText registeredemail;

    private CountDownTimer otpTimer;
    private SharedPreferences sharedPreferences;

    private long timeLeftInMillis = 60000;
    private Dialog dialog2;
    private ProgressBar progressBar,progressBar2,otpprogress;
    private Button submitButton,fillAgain;
    private TextView responseText,statusText,kyc_response,sendotptext;

    private TextView panview,Adharfrontview,Adharbackview,passbookview,userimageView;
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
        nomineeRelation = view.findViewById(R.id.NomineeRelation);
        pancardcheck = view.findViewById(R.id.pancardcheck);
        Aadharcardcheck = view.findViewById(R.id.Aadharcardcheck);
        Aadharcardcheckback = view.findViewById(R.id.Aadharcardbackcheck);
        passbookcheck = view.findViewById(R.id.passbookcheck);
        imagecheck = view.findViewById(R.id.imagecheck);
        progressBar = view.findViewById(R.id.progressbarUpdate);
        kyc_status_layout = view.findViewById(R.id.kyc_status_layout);
        kyc_img = view.findViewById(R.id.status_img);
        statusText = view.findViewById(R.id.statusText);
        kyc_response = view.findViewById(R.id.kyc_response);
        sendotptext=view.findViewById(R.id.sendotp);

        registeredemail = view.findViewById(R.id.registeredemail);
        panview = view.findViewById(R.id.panview);
        Adharfrontview = view.findViewById(R.id.Adharfrontview);
        Adharbackview = view.findViewById(R.id.Adharbackview);
        passbookview = view.findViewById(R.id.passbookview);
        userimageView = view.findViewById(R.id.imageview);

        otpprogress=view.findViewById(R.id.otp_progress);


        otpedittext=view.findViewById(R.id.otp);




        dialog2 = new Dialog(view.getContext());

        progressBar2=view.findViewById(R.id.progressbar);
        responseText=view.findViewById(R.id.responseText);
        fillAgain=view.findViewById(R.id.fill_again);
        form=view.findViewById(R.id.form);


        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String mail = sharedPreferences.getString("email","mail not found");
        String name = sharedPreferences.getString("username","");
        registeredemail.setText(mail);
        registeredemail.setFocusable(false);
        username.setText(name);
        sendotptext.setOnClickListener(v -> {
                sendotptext.setVisibility(View.GONE);
                sendOtp(mail);
                otpprogress.setVisibility(VISIBLE);
        });


        fillAgain.setOnClickListener(v -> {
            kyc_status_layout.setVisibility(View.GONE);
            form.setVisibility(VISIBLE);
            fillAgain.setVisibility(View.GONE);
            responseText.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
        });
        pancardcheck.setEnabled(false);
        Aadharcardcheck.setEnabled(false);
        Aadharcardcheckback.setEnabled(false);
        passbookcheck.setEnabled(false);
        imagecheck.setEnabled(false);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        setUpImageUploadListeners(view);
        fetchKYCStatus(memberId);
        submitButton = view.findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> submitFormData(memberId));

        return view;
    }

    private void sendOtp(String mail) {
        String apiUrl = BuildConfig.api_url_non_auth+"forget/send-otp2";

        try {
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("identifier", mail);
            jsonInput.put("type", "kyc");

            @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    apiUrl,
                    jsonInput,
                    response -> {
                        Log.d("bankotp", "Response: " + response.toString());
                        try {
                            if(response.has("success") && response.getString("success").equals("true")){
                                sendotptext.setText("Resend OTP");
                                otpprogress.setVisibility(View.GONE);
                                sendotptext.setVisibility(VISIBLE);
                                Toast.makeText(requireContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                sendotptext.setEnabled(false);
                                startOtpCountdown();
                            }
                            else{
                                otpprogress.setVisibility(View.GONE);
                                sendotptext.setVisibility(VISIBLE);
                                Toast.makeText(requireContext(), "Unable to send OTP", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            otpprogress.setVisibility(View.GONE);
                            sendotptext.setVisibility(VISIBLE);
                            Toast.makeText(requireContext(), "Unable to send OTP", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        Log.e("bankotp", "Error in sendOtp", error);
                        otpprogress.setVisibility(View.GONE);
                        sendotptext.setVisibility(VISIBLE);
                        Toast.makeText(requireContext(), "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
            );
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            // Add the request to the RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {

            Log.e("bankotp", "Error in constructing the JSON request", e);
        }
    }


    private void startOtpCountdown() {
        otpTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                sendotptext.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                sendotptext.setText("Resend OTP");
                sendotptext.setEnabled(true);
            }
        }.start();
    }

    private void fetchKYCStatus(String memberId) {
        String url = BuildConfig.api_url+"userkycstatus";

        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);

        JSONObject requestBody = new JSONObject(params);
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
                                if(response.has("data")) {
                                    JSONObject data = response.getJSONObject("data");
                                    String kycStatus = data.getString("Kyc_status");
                                    String kycMessage = data.getString("Kyc_message");


                                    if (kycStatus.equals("pending")) {
                                        kyc_img.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.pending));
                                        statusText.setText("Pending");
                                        form.setVisibility(View.GONE);
                                        fillAgain.setVisibility(View.GONE);
                                        responseText.setVisibility(View.GONE);
                                        kyc_response.setVisibility(View.GONE);
                                        progressBar2.setVisibility(View.GONE);

                                    } else if (kycStatus.equals("approved")) {
                                        kyc_img.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.kyc_accept));
                                        statusText.setText("Approved");
                                        responseText.setText("KYC Already Done");
                                        form.setVisibility(View.GONE);
                                        fillAgain.setVisibility(View.GONE);
                                        responseText.setVisibility(VISIBLE);
                                        progressBar2.setVisibility(View.GONE);
                                    } else {
                                        kyc_img.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.kyc_reject));
                                        statusText.setText("Rejected");
                                        responseText.setText(kycMessage);
                                        form.setVisibility(View.GONE);
                                        fillAgain.setVisibility(VISIBLE);
                                        responseText.setVisibility(VISIBLE);
                                        progressBar2.setVisibility(View.GONE);
                                    }
                                    kyc_status_layout.setVisibility(VISIBLE);
                                }
                                else{
                                    form.setVisibility(VISIBLE);
                                    fillAgain.setVisibility(View.GONE);
                                    responseText.setVisibility(View.GONE);
                                    progressBar2.setVisibility(View.GONE);
                                    progressBar2.setVisibility(View.GONE);
                                    kyc_status_layout.setVisibility(View.GONE);
                                }

                            } else {
                                form.setVisibility(VISIBLE);
                                fillAgain.setVisibility(View.GONE);
                                responseText.setVisibility(View.GONE);
                                progressBar2.setVisibility(View.GONE);
                                kyc_status_layout.setVisibility(VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressBar2.setVisibility(View.GONE);
                            kyc_status_layout.setVisibility(VISIBLE);
                            kyc_img.setVisibility(View.GONE);
                            responseText.setVisibility(View.GONE);
                            kyc_response.setVisibility(View.GONE);
                            statusText.setVisibility(VISIBLE);
                            statusText.setText("Error! Try Again");
                        }
                    }
                },
                error -> {
                    progressBar2.setVisibility(View.GONE);
                    kyc_status_layout.setVisibility(View.GONE);
                    form.setVisibility(VISIBLE);
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void setUpImageUploadListeners(View view) {
        view.findViewById(R.id.uploadPancard).setOnClickListener(v -> openImagePicker(1));
        view.findViewById(R.id.uploadAadharcard).setOnClickListener(v -> openImagePicker(2));
        view.findViewById(R.id.uploadAadharcardback).setOnClickListener(v -> openImagePicker(3));
        view.findViewById(R.id.uploadUserimage).setOnClickListener(v -> openImagePicker(4));
        view.findViewById(R.id.uploadPassBook).setOnClickListener(v -> openImagePicker(5));

    }

    private void openImagePicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void submitFormData(String memberId) {
        // Collect input data
        submitButton.setVisibility(View.GONE);
        progressBar.setVisibility(VISIBLE);
        String userName = username.getText().toString().trim();
        String panNumber = Pannumber.getText().toString().trim();
        String ifscCode = IFSCcode.getText().toString().trim();
        String bankName = BankName.getText().toString().trim();
        String accountNumber = AccountNumber.getText().toString().trim();
        String confirmAccountNumber = ConfirmAccountNumber.getText().toString().trim();
        String aadharNumber = Aadharnumber.getText().toString().trim();
        String nomineeNameInput = nomineeName.getText().toString().trim();
        String nomineeRelationInput = nomineeRelation.getText().toString().trim();
        String otp = otpedittext.getText().toString().trim();

        Log.d("KYC", "Member ID: " + memberId);
        // Validate inputs
        if (!validateInputs(userName, panNumber, ifscCode, bankName, accountNumber, confirmAccountNumber, aadharNumber, nomineeNameInput,nomineeRelationInput)) {
            progressBar.setVisibility(View.GONE);
            submitButton.setVisibility(VISIBLE);
            return;
        }

        // Submit data via API
        sendKycRequest(userName, panNumber, memberId, ifscCode, bankName, accountNumber, aadharNumber, nomineeNameInput, nomineeRelationInput ,otp);
    }

    private boolean validateInputs(String userName, String panNumber, String ifscCode, String bankName, String accountNumber, String confirmAccountNumber, String aadharNumber, String nomineeName, String nomineeRelation) {
        if (userName.isEmpty() || panNumber.isEmpty() || ifscCode.isEmpty() || bankName.isEmpty() ||
                accountNumber.isEmpty() || confirmAccountNumber.isEmpty() || aadharNumber.isEmpty() || nomineeName.isEmpty() || nomineeRelation.isEmpty() ||
                imageUripan == null || imageUriaadhar == null || imageUriaadharback == null || imageUripassbook == null || imageUriimage == null) {
            Toast.makeText(getActivity(), "Please fill all fields and upload all required images", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!accountNumber.equals(confirmAccountNumber)) {
            Toast.makeText(getActivity(), "Account Number and Confirm Account Number do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendKycRequest(String userName, String panNumber, String memberId, String ifscCode, String bankName, String accountNumber, String aadharNumber, String nomineeName, String nomineeRelation,String otp) {
        String url = BuildConfig.api_url;
        form.setEnabled(false);
        try {
            File directory = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BankKYC");
            if (!directory.exists()) directory.mkdirs();

            // Save images
            File imageFilePan = saveImageToFile(imageUripan, directory, "panCard.jpg");
            File imageFileAadhar = saveImageToFile(imageUriaadhar, directory, "aadharCard.jpg");
            File imageFileAadharback = saveImageToFile(imageUriaadharback, directory, "aadharCardback.jpg");
            File imageFileUser = saveImageToFile(imageUriimage, directory, "userImage.jpg");
            File imageFilePassbook = saveImageToFile(imageUripassbook, directory, "passbook.jpg");


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            // Prepare request bodies
            MultipartBody.Part partPanCard = prepareMultipartBody(imageFilePan, "kycPancard");
            MultipartBody.Part partAadharCard = prepareMultipartBody(imageFileAadhar, "kycAadharcardFront");
            MultipartBody.Part partAadharCardback = prepareMultipartBody(imageFileAadharback, "kycAadharcardBack");
            MultipartBody.Part partUserImage = prepareMultipartBody(imageFileUser, "kycUserImage");
            MultipartBody.Part partPassbook = prepareMultipartBody(imageFilePassbook, "kycPassbook");
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = apiService.submitUserBankKycDetails(
                    "Bearer " +token,
                    RequestBody.create(MediaType.parse("text/plain"), memberId),
                    RequestBody.create(MediaType.parse("text/plain"), userName),
                    RequestBody.create(MediaType.parse("text/plain"), panNumber),
                    RequestBody.create(MediaType.parse("text/plain"), ifscCode),
                    RequestBody.create(MediaType.parse("text/plain"), bankName),
                    RequestBody.create(MediaType.parse("text/plain"), accountNumber),
                    RequestBody.create(MediaType.parse("text/plain"), aadharNumber),
                    RequestBody.create(MediaType.parse("text/plain"), nomineeName),
                    RequestBody.create(MediaType.parse("text/plain"), nomineeRelation),
                    RequestBody.create(MediaType.parse("text/plain"), otp),
                    partPanCard,partAadharCard,partAadharCardback,partUserImage,partPassbook
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setVisibility(VISIBLE);
                    Log.d("kyc_ka_response",response.toString());
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
                            String errorBody = response.errorBody().string();
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
public void imagePreview(Uri selectedImageUri, TextView btn_id, int i){
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
            if (imageSize > 5 * 1024 * 1024) { // 5MB in bytes
                Toast.makeText(requireContext(), "Image size exceeds 5MB. Please select a smaller image.", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (requestCode) {
                case 1: imageUripan = selectedImageUri;
                    pancardcheck.setVisibility(VISIBLE);
                    pancardcheck.setEnabled(true);
                    imagePreview(selectedImageUri,panview,1);
                break;
                case 2: imageUriaadhar = selectedImageUri;
                    Aadharcardcheck.setVisibility(VISIBLE);
                    Aadharcardcheck.setEnabled(true);
                    imagePreview(selectedImageUri,Adharfrontview,2);
                    break;
                    case 3: imageUriaadharback = selectedImageUri;
                    Aadharcardcheckback.setVisibility(VISIBLE);
                    Aadharcardcheckback.setEnabled(true);
                    imagePreview(selectedImageUri,Adharbackview,3);
                    break;
                case 4:
                    imageUriimage = selectedImageUri;
                    imagecheck.setVisibility(VISIBLE);
                    imagecheck.setEnabled(true);
                    imagePreview(selectedImageUri,userimageView,4);
                    break;
                case 5:
                    imageUripassbook = selectedImageUri;
                    passbookcheck.setVisibility(VISIBLE);
                    passbookcheck.setEnabled(true);
                    imagePreview(selectedImageUri,passbookview,5);
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
