package com.example.paymentapp;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetTpinFragment extends Fragment {

    TextView  sendOtpText,memberid;
    EditText  newPin, otp, coinPin;
    ProgressBar progressBar,otpprogressbar;
    Button updatePinButton;

    LinearLayout success_layout;

    CardView card;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_tpin, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello!");
        String memberId = sharedPreferences.getString("memberId", "UP000000");



        memberid = view.findViewById(R.id.memberid);
        memberid.setText(memberId);

        sendOtpText = view.findViewById(R.id.sendotp);
        progressBar = view.findViewById(R.id.progressbarlogin);
        otpprogressbar=view.findViewById(R.id.otpprogress);
        updatePinButton = view.findViewById(R.id.save);

        sendOtpText.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        otpprogressbar.setVisibility(GONE);
        updatePinButton.setVisibility(VISIBLE);

        card=view.findViewById(R.id.card);
        success_layout=view.findViewById(R.id.success_layout);

        card.setVisibility(VISIBLE);
        success_layout.setVisibility(GONE);
        newPin = view.findViewById(R.id.new_pin);
        otp = view.findViewById(R.id.otp);
        coinPin = view.findViewById(R.id.coin_pin);

        sendOtpText.setOnClickListener(v -> sendOtp(memberId));





        if(newPin.getText().toString().equals(coinPin.getText().toString()) && newPin.getText().toString()!=null) {
            updatePinButton.setOnClickListener(v -> {
                progressBar.setVisibility(VISIBLE);
                updatePinButton.setVisibility(View.GONE);
                if (!validateInputs(newPin.getText().toString(),coinPin.getText().toString())) {
                    return;
                }
                updatePin(memberId, newPin.getText().toString(), otp.getText().toString());
            });
        }
        else if(!newPin.getText().toString().equals(coinPin.getText().toString())){
            progressBar.setVisibility(GONE);
            updatePinButton.setVisibility(VISIBLE);
            Toast.makeText(this.getContext(), "new and confirm pin do nnot match", Toast.LENGTH_SHORT).show();
        }
        else
        {//
        }
        return view;
    }

    private boolean validateInputs(String new_Tpin,String coin_pin) {
        if (new_Tpin.isEmpty() || coin_pin.isEmpty() ) {
            progressBar.setVisibility(View.GONE);
            updatePinButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (new_Tpin.length() < 4 || coin_pin.length() < 4) {
            Toast.makeText(getActivity(), "Pin must be at least 4 characters long", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            updatePinButton.setVisibility(View.VISIBLE);
            return false;
        }

        if (!new_Tpin.equals(coin_pin) ) {
            progressBar.setVisibility(View.GONE);
            updatePinButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Confirm Tpin and New Tpin do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void sendOtp(String memberId) {
        String apiUrl = BuildConfig.api_url+"send-otp2";

        sendOtpText.setVisibility(GONE);
        otpprogressbar.setVisibility(VISIBLE);
        try {
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("identifier", memberId);
            jsonInput.put("type","forget_tpin");
            Log.d(TAG, "API URL: " + apiUrl);
            Log.d(TAG, "Sending OTP request data: " + jsonInput.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    apiUrl,
                    jsonInput,
                    response -> {
                        Log.d(TAG, "Response: " + response.toString());
                        sendOtpText.setText("Resend OTP");
                        sendOtpText.setVisibility(VISIBLE);
                        otpprogressbar.setVisibility(GONE);
                        Toast.makeText(getContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e(TAG, "Error in sendOtp", error);
                        sendOtpText.setVisibility(VISIBLE);
                        otpprogressbar.setVisibility(GONE);
                        Toast.makeText(getContext(), "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    250000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e(TAG, "Error in constructing the JSON request", e);
        }
    }

    private void updatePin(String memberId,String newPin,String otp ){
        String url = BuildConfig.api_url+"forgetTpin";
        progressBar.setVisibility(VISIBLE);
        updatePinButton.setVisibility(GONE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("member_id", memberId);
            jsonObject.put("newTpin", newPin);
            jsonObject.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error creating request body", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        if (response.getString("status").equals("true")) {
                            progressBar.setVisibility(GONE);
                            updatePinButton.setVisibility(VISIBLE);
                            card.setVisibility(GONE);
                            success_layout.setVisibility(VISIBLE);
                            Toast.makeText(getContext(), "PIN changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(GONE);
                            updatePinButton.setVisibility(VISIBLE);
                            String errorMessage = response.optString("error", "Server error");
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(GONE);
                        updatePinButton.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage;
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorResponse = new JSONObject(errorBody);
                            errorMessage = errorResponse.optString("error", "Submission failed");
                        } catch (Exception e) {
                            errorMessage = "Error processing error response";
                        }
                    } else {
                        errorMessage = "Submission failed";
                    }
                    progressBar.setVisibility(GONE);
                    updatePinButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("API", "Error: " + error.getMessage(), error);
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

}
