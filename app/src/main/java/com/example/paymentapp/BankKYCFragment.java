package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private EditText username, Pannumber, IFSCcode, BankName, AccountNumber, ConfirmAccountNumber, Aadharnumber, nomineeName;

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

        // Get memberId from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        // Set up ImageView click listeners
        setUpImageUploadListeners(view);

        // Set submit button listener
        Button submitButton = view.findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> submitFormData(memberId));

        return view;
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
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "KYC submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("KYC", "Submission failed: " + response.message());
                        try {
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
                    Log.e("KYC", "Error: " + t.getMessage(), t);
                    Toast.makeText(getActivity(), "Error submitting KYC", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requireActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri == null) return;

            switch (requestCode) {
                case 1: imageUripan = selectedImageUri;
                    getView().findViewById(R.id.pancardcheck).setVisibility(View.VISIBLE);
                break;
                case 2: imageUriaadhar = selectedImageUri;
                    getView().findViewById(R.id.Aadharcardcheck).setVisibility(View.VISIBLE);
                    break;
                case 3: imageUripassbook = selectedImageUri;
                    getView().findViewById(R.id.passbookcheck).setVisibility(View.VISIBLE);
                    break;
                case 4: imageUriimage = selectedImageUri;
                    getView().findViewById(R.id.imagecheck).setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
