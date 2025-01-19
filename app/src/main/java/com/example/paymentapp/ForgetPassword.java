package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgetPassword extends AppCompatActivity {

    private static final String TAG = "adityi";

    private EditText newPassword, confirmPassword, otp, memberId;
    private Button updatePasswordButton;
    private ImageView back_button;
    private ProgressBar progressBar,progress;
    private TextView sendOtpText;
    private CountDownTimer otpTimer;
    private long timeLeftInMillis = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));

        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.coin_password);
        otp = findViewById(R.id.otp);
        updatePasswordButton = findViewById(R.id.update_password);
        progressBar = findViewById(R.id.progressbar);
        sendOtpText = findViewById(R.id.sendotp);
        memberId = findViewById(R.id.memberid);
        progress=findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        sendOtpText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        updatePasswordButton.setEnabled(false);
        updatePasswordButton.setVisibility(View.VISIBLE);
        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> finish());


        sendOtpText.setOnClickListener(v -> {
            String memberid = memberId.getText().toString();
            if(memberid.length()<8)
                memberId.setError("Enter Valid ID");
            else {
                progress.setVisibility(View.VISIBLE);
                sendOtpText.setVisibility(View.GONE);
                sendOtp(memberid);
            }
        });

        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordButton.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        if(newPassword.getText().toString().equals(confirmPassword.getText().toString())&& newPassword!=null) {
            updatePasswordButton.setOnClickListener(v -> {
                String memberid = memberId.getText().toString();
                Log.d(TAG, "Update Password clicked with member ID: " + memberid);
                progressBar.setVisibility(View.VISIBLE);
                updatePasswordButton.setVisibility(View.GONE);
                updatePassword(memberid);
            });
        }
    }



    private void sendOtp(String memberId) {
        String apiUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/send-otp";

        try {
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("identifier", memberId);

            Log.d(TAG, "API URL: " + apiUrl);
            Log.d(TAG, "Sending OTP request data: " + jsonInput.toString());

            @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    apiUrl,
                    jsonInput,
                    response -> {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            if(response.has("success") && response.getString("success").equals("true")){
                                sendOtpText.setText("Resend OTP");
                                progress.setVisibility(View.GONE);
                                sendOtpText.setVisibility(View.VISIBLE);
                                Toast.makeText(ForgetPassword.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                sendOtpText.setEnabled(false);
                                startOtpCountdown();
                            }
                            else{
                                progress.setVisibility(View.GONE);
                                sendOtpText.setVisibility(View.VISIBLE);
                                Toast.makeText(this, "Unable to send OTP", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progress.setVisibility(View.GONE);
                            sendOtpText.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Unable to send OTP", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error in sendOtp", error);
                        progress.setVisibility(View.GONE);
                        sendOtpText.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgetPassword.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
            );
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            // Add the request to the RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {

            Log.e(TAG, "Error in constructing the JSON request", e);
        }
    }
    private void updatePassword(String memberId) {
        String apiUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/forgetPassword";
        String newPasswordText = newPassword.getText().toString();
        String otpText = otp.getText().toString();

        Log.d(TAG, "Updating password with newPassword: " + newPasswordText + ", otp: " + otpText);
        progressBar.setVisibility(View.VISIBLE);
        updatePasswordButton.setVisibility(View.GONE);


        if(newPasswordText.equals(confirmPassword.getText().toString())) {

            try {
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("identifier", memberId);
                jsonInput.put("newPassword", newPasswordText);
                jsonInput.put("otp", otpText);

                Log.d(TAG, "API URL: " + apiUrl);
                Log.d(TAG, "Sending update password request data: " + jsonInput.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        apiUrl,
                        jsonInput,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Response: " + response.toString());
                                progressBar.setVisibility(View.GONE);
                                updatePasswordButton.setVisibility(View.VISIBLE);
                                Toast.makeText(ForgetPassword.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetPassword.this, Login.class));
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Error in updatePassword", error);
                                progressBar.setVisibility(View.GONE);
                                updatePasswordButton.setVisibility(View.VISIBLE);
                                Toast.makeText(ForgetPassword.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                // Set a custom RetryPolicy for 1 minute timeout with no retries
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000, // 60 seconds timeout
                        0, // No retries
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
                ));

                // Add the request to the RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(jsonObjectRequest);

            } catch (Exception e) {
                Log.e(TAG, "Error in constructing the JSON request", e);
                progressBar.setVisibility(View.GONE);
                updatePasswordButton.setVisibility(View.VISIBLE);

            }

        }
        else
        {
            Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            updatePasswordButton.setVisibility(View.VISIBLE);
        }
    }
    private void startOtpCountdown() {
        otpTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendOtpText.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                sendOtpText.setText("Resend OTP");
                sendOtpText.setEnabled(true);
            }
        }.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otpTimer != null) {
            otpTimer.cancel();
        }
    }
}
