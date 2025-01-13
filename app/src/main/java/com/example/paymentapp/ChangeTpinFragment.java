package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChangeTpinFragment extends Fragment {



    private EditText oldTpin,newTpin,confirmTpin;
    private Button update_Tpin;
    private ProgressBar progressBar;
    private boolean passwordVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_change_tpin, container, false);

        oldTpin=view.findViewById(R.id.oldTpin);
        newTpin=view.findViewById(R.id.newTpin);
        confirmTpin=view.findViewById(R.id.confirmTpin);
        update_Tpin=view.findViewById(R.id.Update_Tpin);
        progressBar=view.findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");


        oldTpin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (oldTpin.getRight() - oldTpin.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            oldTpin.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            oldTpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        oldTpin.setSelection(oldTpin.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        newTpin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (newTpin.getRight() - newTpin.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            newTpin.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            newTpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        newTpin.setSelection(newTpin.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        confirmTpin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirmTpin.getRight() - confirmTpin.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            confirmTpin.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            confirmTpin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        confirmTpin.setSelection(confirmTpin.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        update_Tpin.setOnClickListener(v -> {
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
        String old_Tpin = oldTpin.getText().toString().trim();
        String new_Tpin = newTpin.getText().toString().trim();
        String confirm_Pass = confirmTpin.getText().toString().trim();


        Log.d("KYC", "Member ID: " + memberId);
        // Validate inputs
        if (!validateInputs(old_Tpin,new_Tpin,confirm_Pass)) {
            return;
        }

        sendRequest(old_Tpin,new_Tpin,memberId);
    }

    private boolean validateInputs(String old_Tpin,String new_Tpin,String confirm_Pass) {
        if (old_Tpin.isEmpty() || new_Tpin.isEmpty() || confirm_Pass.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!new_Tpin.equals(confirm_Pass) ) {
            Toast.makeText(getActivity(), "Confirm Tpin and New Tpin do not match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (old_Tpin.equals(new_Tpin)) {
            Toast.makeText(getActivity(), "Old Pin and New Pin can not be same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendRequest(String old_Tpin, String new_Tpin, String memberId) throws JSONException {
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
        jsonObject.put("oldtpin", old_Tpin);
        jsonObject.put("newtpin", new_Tpin);

        System.out.println("JSON Body: " + jsonObject);
        // Convert JSON object to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        // Make the API call
        Call<ResponseBody> call = apiService.submitDetails(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Tpin updated successfully", Toast.LENGTH_SHORT).show();

                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, new HomeFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
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