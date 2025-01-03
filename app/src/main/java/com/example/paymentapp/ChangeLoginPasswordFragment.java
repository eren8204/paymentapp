package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeLoginPasswordFragment extends Fragment {


private EditText old_password,new_password,confirm_password;
private Button update_password;
private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_change_login_password, container, false);

        old_password=view.findViewById(R.id.old_password);
        new_password=view.findViewById(R.id.new_password);
        confirm_password=view.findViewById(R.id.confirm_password);
        update_password=view.findViewById(R.id.update_password);
        progressBar=view.findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        update_password.setOnClickListener(v -> {
            try {
                submitFormData(memberId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });





        return view;
    }


    private void submitFormData(String memberId) throws JSONException {
        // Collect input data
        String oldPass = old_password.getText().toString().trim();
        String newPass = new_password.getText().toString().trim();
        String confirmPass = confirm_password.getText().toString().trim();


        Log.d("KYC", "Member ID: " + memberId);
        // Validate inputs
        if (!validateInputs(oldPass,newPass,confirmPass)) {
            return;
        }

        // Submit data via API
        sendRequest(oldPass,newPass,memberId);
    }

    private boolean validateInputs(String oldPass, String newPass,String confirmPass) {
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPass.equals(confirmPass) ) {
            Toast.makeText(getActivity(), "Confirm Password and New Password do not match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (oldPass.equals(newPass)) {
            Toast.makeText(getActivity(), "Old Password and New Password can not be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendRequest(String oldPass, String newPass, String memberId) throws JSONException {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/";

        // Set up logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient and attach the interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Create Retrofit instance with the custom OkHttpClient
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client) // Attach OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Initialize API service
        ApiService apiService = retrofit.create(ApiService.class);

        // Create JSON body
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("member_id", memberId);
        jsonObject.put("oldPassword", oldPass);
        jsonObject.put("newPassword", newPass);

        System.out.println("JSON Body: " + jsonObject);
        // Convert JSON object to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        // Make the API call
        Call<ResponseBody> call = apiService.submitDetailsJson(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API", "Submission failed: " + response.message());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API", "Error body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("API", "Error reading error body", e);
                    }
                    Toast.makeText(getActivity(), "Submission failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("API", "Error: " + t.getMessage(), t);
                Toast.makeText(getActivity(), "Error submitting request", Toast.LENGTH_SHORT).show();
            }
        });
    }


}