package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {


    private EditText countryCodeEditText,sponsorID;
    private EditText phoneNumberEditText;
    private TextView sponsor_name;
    TextView login;

    @SuppressLint({"MissingInflatedId","SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        login=findViewById(R.id.login);
        sponsorID=findViewById(R.id.sponserID);
        countryCodeEditText = findViewById(R.id.country_code);
        phoneNumberEditText = findViewById(R.id.phonenumber);
        sponsor_name = findViewById(R.id.sponsor_name);
        sponsor_name.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        sponsorID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This is called during the text change.
                if (s.length() > 8) {
                    sponsorID.setError("Maximum length is 8 characters");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This is called after the text has changed.
                // Example: Perform validation or trigger logic based on input
                String sid = sponsorID.getText().toString().trim();
                if (s.length() == 8) {
                    String baseUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/checksponserId";
                    JSONObject requestBody = new JSONObject();
                    try{
                        requestBody.put("sponser_id",sid);
                    } catch (JSONException e) {
                        Toast.makeText(Register.this, "Error in request body "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            baseUrl,
                            requestBody,
                            response -> {
                                try {
                                    // Parse the response
                                    boolean isValid = response.getBoolean("isValid");
                                    String name = response.getString("sponserName");
                                    if (isValid) {
                                        sponsor_name.setText(name);
                                        sponsor_name.setVisibility(View.VISIBLE);
                                    } else {
                                        sponsor_name.setText("Invalid! Try Again");
                                        sponsor_name.setVisibility(View.VISIBLE);
                                        sponsorID.setError("Invalid Sponsor ID");
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(Register.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> {
                                // Handle network or server errors
                                Toast.makeText(Register.this, "Network error: "+error.toString(), Toast.LENGTH_SHORT).show();
                            }
                    );
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });


        //submitPhoneNumber();

    }
    private void submitPhoneNumber() {
        String countryCode = countryCodeEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (countryCode.isEmpty()) {
            countryCodeEditText.setError("Country code is required");
            countryCodeEditText.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Phone number is required");
            phoneNumberEditText.requestFocus();
            return;
        }

        String fullPhoneNumber = countryCode + phoneNumber;

    }
}