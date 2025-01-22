package com.example.paymentapp;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText countryCodeEditText, otp_edit , sponsorID, phoneNumberEditText, nameEditText, emailEditText, passwordEditText, confirmPasswordEditText, tPinEditText;
    private TextView sponsor_name,login,sendotp,verifyotp;
    private CheckBox termsCheckBox;
    private Button registerButton;
    private ProgressBar progressbar_register;
    private  int k=0,x=1;
    private LinearLayout otp_progress_layout,verify_progress_layout;
    private boolean passwordVisible,cPasswordVisible,tpinVisible;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.login);
        sendotp = findViewById(R.id.sendotp_text);
        sponsorID = findViewById(R.id.sponserID);
        otp_edit = findViewById(R.id.otp);
        verifyotp = findViewById(R.id.verifyotp_text);
        countryCodeEditText = findViewById(R.id.country_code);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        sponsor_name = findViewById(R.id.sponsor_name);
        nameEditText = findViewById(R.id.name);
        verify_progress_layout = findViewById(R.id.verify_progress_layout);
        otp_progress_layout = findViewById(R.id.otp_progress_layout);
        emailEditText = findViewById(R.id.registeremail);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.coinfirmpassword);
        tPinEditText = findViewById(R.id.T_PIN);
        termsCheckBox = findViewById(R.id.checkbox_example);
        registerButton = findViewById(R.id.save);
        progressbar_register = findViewById(R.id.progressbar_register);

        sponsor_name.setVisibility(View.GONE);
        registerButton.setEnabled(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        sendotp.setOnClickListener(v-> {

            String email = emailEditText.getText().toString().trim();
            if(email.isEmpty() || !isValidEmail(email)){
                emailEditText.setError("Valid E-mail required");
                emailEditText.requestFocus();
            }
            else{
                sendotp.setVisibility(View.GONE);
                otp_progress_layout.setVisibility(View.VISIBLE);
                sendOtp(emailEditText.getText().toString().trim());
            }

        });

        verifyotp.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String otp = otp_edit.getText().toString().trim();

            // Validate OTP input
            if (otp.isEmpty()) {
                otp_edit.setError("OTP required");
                otp_edit.requestFocus();
            } else if (otp.length() < 6) {
                otp_edit.setError("OTP must be 6 digits");
                otp_edit.requestFocus();
            } else {
                verifyotp.setVisibility(View.GONE);
                verify_progress_layout.setVisibility(View.VISIBLE);
                verifyOtp(email, otp);
            }
        });

        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                        passwordVisible = !passwordVisible;
                        if (passwordVisible) {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        passwordEditText.setSelection(passwordEditText.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        confirmPasswordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirmPasswordEditText.getRight() - confirmPasswordEditText.getCompoundDrawables()[2].getBounds().width())) {
                        cPasswordVisible = !cPasswordVisible;
                        if (cPasswordVisible) {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        tPinEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tPinEditText.getRight() - tPinEditText.getCompoundDrawables()[2].getBounds().width())) {
                        tpinVisible = !tpinVisible;
                        if (tpinVisible) {
                            tPinEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        } else {
                            tPinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        }
                        tPinEditText.setSelection(tPinEditText.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        sponsorID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) {
                    sponsor_name.setVisibility(View.GONE);
                    sponsorID.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 8) {
                    checkSponsorID(sponsorID.getText().toString().trim());
                } else if (s.length() > 0) {
                    sponsorID.setError("ID should be 8 characters in length");
                }
            }
        });
        ;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!termsCheckBox.isChecked()) {
                    Toast.makeText(Register.this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                String phonenumber = phoneNumberEditText.getText().toString().trim();
                String mail = emailEditText.getText().toString().trim();
                String tpin = tPinEditText.getText().toString().trim();
                String username = nameEditText.getText().toString().trim();
                String sid = sponsorID.getText().toString().trim();

                String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$";

                if (sid.isEmpty()) {
                    sponsorID.setError("Sponsor ID is required");
                    sponsorID.requestFocus();
                    return;
                }

                if (phonenumber.isEmpty()) {
                    if(phonenumber.length()<10)
                    {
                        phoneNumberEditText.setError("invalid phone number");
                    }
                    phoneNumberEditText.setError("Phone number is required");
                    phoneNumberEditText.requestFocus();
                    return;
                }

                if (username.isEmpty()) {
                    nameEditText.setError("Username is required");
                    nameEditText.requestFocus();
                    return;
                }

                if (mail.isEmpty() || !isValidEmail(mail)) {
                    emailEditText.setError("Valid E-mail required");
                    emailEditText.requestFocus();
                    return;
                }

                if (tpin.isEmpty() ) {
                    if(tpin.length()!=4)
                    {
                        tPinEditText.setError("T-pin must be of four character");
                    }
                    tPinEditText.setError("T-PIN is required");
                    tPinEditText.requestFocus();
                    return;
                }

                if (!mail.endsWith("@gmail.com")) {
                    emailEditText.setError("Only Gmail addresses are allowed");
                    emailEditText.requestFocus();
                    return;
                }

                if (!password.matches(passwordPattern)) {
                    Toast.makeText(Register.this, "Password must be 8-12 characters long and include uppercase, lowercase, a number, and a special character. ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(Register.this, "Password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)){
                    Toast.makeText(Register.this, "Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(k==1){
                    Toast.makeText(Register.this, "Invalid Sponsor ID", Toast.LENGTH_SHORT).show();
                }
                else if(x==1){
                    Toast.makeText(Register.this, "Email not verified", Toast.LENGTH_SHORT).show();
                }
                else if(k==0 && x==0) {
                    submitRegistration();
                }

            }
        });
    }

    private void checkSponsorID(String sponsorID) {
        String baseUrl = BuildConfig.api_url+"checkSponserId";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("sponser_id", sponsorID);
        } catch (JSONException e) {
            Log.d("Sponsor Id: ",e.getMessage());
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                baseUrl,
                requestBody,
                response -> {
                    try {
                        boolean isValid = response.getBoolean("isValid");
                        String name = response.getString("sponserName");
                        if (isValid) {
                            k=0;
                            registerButton.setEnabled(true);
                            sponsor_name.setText(name);
                            sponsor_name.setTextColor(ContextCompat.getColor(this, R.color.accept));
                            sponsor_name.setVisibility(View.VISIBLE);
                        } else {
                            k=1;
                            registerButton.setEnabled(false);
                            sponsor_name.setTextColor(ContextCompat.getColor(this, R.color.reject));
                            sponsor_name.setText("Invalid! Try Again");
                            sponsor_name.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                        k=1;
                        registerButton.setEnabled(false);
                        Log.d("Sponsor Id: ",e.getMessage());
                    }
                },
                error -> {
                    registerButton.setEnabled(false);
                    k=1;
                    Log.e("Bhenkeloada", "An error occurred: " + error.getMessage(), error);
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void submitRegistration() {
        registerButton.setVisibility(View.GONE);
        progressbar_register.setVisibility(View.VISIBLE);
        String sponsorIDText = sponsorID.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String username = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String tPin = tPinEditText.getText().toString().trim();
        String otp=otp_edit.getText().toString().trim();




        String baseUrl = BuildConfig.api_url+"register";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("sponser_id", sponsorIDText);
            requestBody.put("phoneno", phoneNumber);
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("tpin", tPin);
            requestBody.put("emailOtp",otp);
        } catch (JSONException e) {
            progressbar_register.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
            Log.d("Register Input ",e.getMessage());
            Toast.makeText(Register.this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                baseUrl,
                requestBody,
                response -> {
                    try{
                        if(response.has("status") && response.getString("status").equals("true")) {
                            progressbar_register.setVisibility(View.GONE);
                            registerButton.setVisibility(View.VISIBLE);
                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, SuccessRegisterActivity.class);
                            intent.putExtra("SPONSOR_ID", sponsorIDText);
                            intent.putExtra("PHONE_NUMBER", phoneNumber);
                            intent.putExtra("USERNAME", username);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("PASSWORD", password);
                            intent.putExtra("TPIN", tPin);
                            intent.putExtra("MEMBER_ID", response.getString("memberId"));
                            intent.putExtra("DATE",response.getString("dateOfJoining"));
                            startActivity(intent);

                            Intent intent1 = new Intent(Register.this, SuccessRegisterActivity.class);
                            startActivity(intent);
                            finish();
                            Log.d("Bhenkeloada", "Registration response: " + response.toString());



                        }
                        else{
                            progressbar_register.setVisibility(View.GONE);
                            registerButton.setVisibility(View.VISIBLE);
                            if(response.has("error")) {
                                Log.d("Bhenkeloada", "error " + response.getString("error"));
                                Toast.makeText(this, response.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.d("Bhenkeloada", "errorrefw " + response.getString("error"));
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    catch (Exception e){
                        Log.d("Submit Error",e.getMessage());
                        Toast.makeText(this, "submit Error", Toast.LENGTH_SHORT).show();
                        progressbar_register.setVisibility(View.GONE);
                        registerButton.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressbar_register.setVisibility(View.GONE);
                    registerButton.setVisibility(View.VISIBLE);
                    Log.e("Bhenkeloada", "An error occurred: " + error.getMessage(), error);
                    Toast.makeText(Register.this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(jsonObjectRequest);
    }

    private CountDownTimer otpTimer;
    private long timeLeftInMillis = 60000;

    private void sendOtp(String memberId) {
        String apiUrl = BuildConfig.api_url+"send-register-otp";

        try {
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("identifier", memberId);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    apiUrl,
                    jsonInput,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.has("success") && response.getString("success").equals("true")){
                                    sendotp.setText("Resend OTP");
                                    otp_edit.setVisibility(View.VISIBLE);
                                    otp_progress_layout.setVisibility(View.GONE);
                                    sendotp.setVisibility(View.VISIBLE);
                                    verifyotp.setVisibility(View.VISIBLE);
                                    Toast.makeText(Register.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                                    emailEditText.setEnabled(false);
                                    sendotp.setEnabled(false); // Disable the button
                                    startOtpCountdown();
                                }
                                else{
                                    otp_progress_layout.setVisibility(View.GONE);
                                    sendotp.setVisibility(View.VISIBLE);
                                    String msg = "Unable to send OTP";
                                    if(response.has("message")){
                                        msg = response.getString("message");
                                    }
                                    Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(Register.this, "Unable to send OTP", Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Bhenkeloada", "Error in sendOtp", error);
                            otp_progress_layout.setVisibility(View.GONE);
                            sendotp.setVisibility(View.VISIBLE);
                            Toast.makeText(Register.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            Log.e("Bhenkeloada", "Error in constructing the JSON request", e);
            Toast.makeText(Register.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void startOtpCountdown() {
        otpTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendotp.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
            }

            @Override
            public void onFinish() {
                sendotp.setText("Resend OTP");
                sendotp.setEnabled(true);
            }
        }.start();
    }

    private void verifyOtp(String memberId, String otp) {
        String apiUrl = BuildConfig.api_url+"verify-register-otp";

        try {
            JSONObject jsonInput = new JSONObject();
            jsonInput.put("identifier", memberId);
            jsonInput.put("otp", otp);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    apiUrl,
                    jsonInput,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Bhenkeloada", "Response: " + response.toString());
                            try {
                                if (response.has("success") && response.getString("success").equals("true")) {
                                    // OTP verified successfully
                                    x=0;
                                    registerButton.setEnabled(true);
                                    otp_edit.setEnabled(false);
                                    emailEditText.setEnabled(false);
                                    sendotp.setEnabled(false);
                                    verify_progress_layout.setVisibility(View.GONE);
                                    verifyotp.setEnabled(false);
                                    verifyotp.setTextColor(ContextCompat.getColor(Register.this, R.color.accept));
                                    verifyotp.setText("Verified");
                                    verifyotp.setVisibility(View.VISIBLE);
                                } else {
                                    x=1;
                                    // Invalid OTP
                                    registerButton.setEnabled(false);
                                    verify_progress_layout.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Wrong OTP! Try Again", Toast.LENGTH_SHORT).show();
                                    verifyotp.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                x=1;
                                // Handle JSONException
                                registerButton.setEnabled(false);
                                Toast.makeText(Register.this, "Error Verifying OTP", Toast.LENGTH_SHORT).show();
                                Log.e("Bhenkeloada", "Error in verifying OTP", e);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle VolleyError
                            x=1;
                            registerButton.setEnabled(false);
                            verify_progress_layout.setVisibility(View.GONE);
                            verifyotp.setVisibility(View.VISIBLE);
                            Toast.makeText(Register.this, "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e("Bhenkeloada", "Error in OTP verification", error);
                        }
                    }
            );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            x=1;
            registerButton.setEnabled(false);
            Log.e("Bhenkeloada", "Error in constructing the JSON request", e);
            Toast.makeText(Register.this, "Error in OTP request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otpTimer != null) {
            otpTimer.cancel();
        }
    }
    public boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}