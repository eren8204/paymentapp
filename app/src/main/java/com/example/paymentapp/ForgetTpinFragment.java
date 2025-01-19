package com.example.paymentapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import org.json.JSONObject;

public class ForgetTpinFragment extends Fragment {

    TextView name, sendOtpText;
    EditText memberid, newPin, otp, coinPin;
    ProgressBar progressBar;
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

        name = view.findViewById(R.id.textname);
        name.setText(username);

        memberid = view.findViewById(R.id.memberid);
        memberid.setText(memberId);

        sendOtpText = view.findViewById(R.id.sendotp);
        progressBar = view.findViewById(R.id.progressbarlogin);
        updatePinButton = view.findViewById(R.id.save);


        card=view.findViewById(R.id.card);
        success_layout=view.findViewById(R.id.success_layout);

        card.setVisibility(View.VISIBLE);
        success_layout.setVisibility(View.GONE);
        newPin = view.findViewById(R.id.new_pin);
        otp = view.findViewById(R.id.otp);
        coinPin = view.findViewById(R.id.coin_pin);

        sendOtpText.setOnClickListener(v -> sendOtp(memberId));

        if(newPin.getText().toString().equals(coinPin.getText().toString())&& newPin!=null) {
            updatePinButton.setOnClickListener(v -> {
                updatePin(memberId);
            });
        }
        return view;
    }


    private void sendOtp(String memberId) {
        String apiUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/send-otp2";

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
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Response: " + response.toString());
                            sendOtpText.setText("Resend OTP");
                            Toast.makeText(getContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error in sendOtp", error);
                            Toast.makeText(getContext(), "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        }
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

    private void updatePin(String memberId) {
        String apiUrl = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/forgetTpin";
        String newpin = newPin.getText().toString();
        String otpText = otp.getText().toString();
        String coinPinText = coinPin.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        updatePinButton.setVisibility(View.GONE);
        if(newpin.equals(coinPinText)) {
            Log.d(TAG, "Updating PIN with coinPin: " + coinPinText + ", otp: " + otpText);

            try {
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("member_id", memberId);
                jsonInput.put("newTpin", coinPinText);
                jsonInput.put("otp", otpText);

                Log.d(TAG, "API URL: " + apiUrl);
                Log.d(TAG, "Sending update PIN request data: " + jsonInput.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        apiUrl,
                        jsonInput,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Response: " + response.toString());
                                progressBar.setVisibility(View.GONE);
                                updatePinButton.setVisibility(View.VISIBLE);
                                card.setVisibility(View.GONE);
                                success_layout.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "PIN changed successfully", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Error in updatePin", error);
                                progressBar.setVisibility(View.GONE);
                                updatePinButton.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Failed to change PIN", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                // Set a custom RetryPolicy for 1 minute timeout with no retries
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        250000,
                        0, // No retries
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
                ));

                // Add the request to the RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonObjectRequest);

            } catch (Exception e) {
                Log.e(TAG, "Error in constructing the JSON request", e);
                progressBar.setVisibility(View.GONE);
                updatePinButton.setVisibility(View.VISIBLE);
            }

        }
        else {
            Toast.makeText(getContext(), "Pin do not match", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            updatePinButton.setVisibility(View.VISIBLE);
        }
    }
}
