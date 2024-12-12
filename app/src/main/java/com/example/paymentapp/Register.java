package com.example.paymentapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText countryCodeEditText, sponsorID, phoneNumberEditText, nameEditText, emailEditText, passwordEditText, confirmPasswordEditText, tPinEditText;
    private TextView sponsor_name;
    private CheckBox termsCheckBox;
    private Button registerButton;
    TextView login;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.white));

        login = findViewById(R.id.login);
        sponsorID = findViewById(R.id.sponserID);
        countryCodeEditText = findViewById(R.id.country_code);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        sponsor_name = findViewById(R.id.sponsor_name);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.registeremail);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.coinfirmpassword);
        tPinEditText = findViewById(R.id.T_PIN);
        termsCheckBox = findViewById(R.id.checkbox_example);
        registerButton = findViewById(R.id.save);

        sponsor_name.setVisibility(View.GONE);

        login.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });




        sponsorID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This is called during the text change.
                if (s.length() < 8 ) {
                    sponsor_name.setVisibility(View.GONE);
                    k=1;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This is called after the text has changed.
                if (s.length() == 8) {
                    checkSponsorID(sponsorID.getText().toString().trim());
                    k=0;
                }
                else   {
                    sponsorID.setError("Id should be 8 characters in length");
                }
            }
        });

        registerButton.setOnClickListener(v -> {
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


            String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,24}$";




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

            if (mail.isEmpty()) {
                emailEditText.setError("Email is required");
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
                Toast.makeText(Register.this, "Password must be 12-24 characters long and include uppercase, lowercase, a number, and a special character. ", Toast.LENGTH_SHORT).show();
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

            if(k==0) {
                submitRegistration();
            }

        });
    }

    int k=0;
    private void checkSponsorID(String sponsorID) {
        String baseUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/checkSponserId";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("sponser_id", sponsorID);
        } catch (JSONException e) {
            Toast.makeText(Register.this, "Error in request body " + e.toString(), Toast.LENGTH_SHORT).show();
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
                            sponsor_name.setText(name);
                            sponsor_name.setVisibility(View.VISIBLE);
                        } else {
                            sponsor_name.setText("Invalid! Try Again");
                            sponsor_name.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                        Toast.makeText(Register.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    k=1;
                    Log.e(TAG, "An error occurred: " + error.getMessage(), error);
                    Toast.makeText(Register.this, "Network error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void submitRegistration() {
        String sponsorIDText = sponsorID.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String username = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String tPin = tPinEditText.getText().toString().trim();

        String baseUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/register";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("sponser_id", sponsorIDText);
            requestBody.put("phoneno", phoneNumber);
            requestBody.put("username", username);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("tpin", tPin);
        } catch (JSONException e) {
            Toast.makeText(Register.this, "Error in request body " + e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                baseUrl,
                requestBody,
                response -> {
                    Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Registration response: " + response.toString());
                },
                error -> {
                    Log.e(TAG, "An error occurred: " + error.getMessage(), error);
                    Toast.makeText(Register.this, "Network error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}