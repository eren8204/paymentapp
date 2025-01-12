package com.example.paymentapp;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class payment extends AppCompatActivity {

    private Button pay;
    private ImageButton back_button;
    private TextView payment_type,subtype,subtype_num,amount,memberName,userId;
    private LinearLayout pay_layout,success_layout;
    private ProgressBar progressBar;

    private EditText tpin_text,ctpin_text;
    private boolean isTpinVissible = false;
    private boolean isCtpinVissible = false;
    private String operatorCode="",circleCode="",number_id="",money="";
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payment_type = findViewById(R.id.payment_type);
        subtype = findViewById(R.id.subtype);
        subtype_num = findViewById(R.id.subtype_num);
        amount = findViewById(R.id.amount);
        pay = findViewById(R.id.pay);
        tpin_text = findViewById(R.id.tpin_text);
        ctpin_text = findViewById(R.id.ctpin_text);
        pay_layout = findViewById(R.id.pay_layout);
        success_layout = findViewById(R.id.success_layout);
        progressBar = findViewById(R.id.pay_progress);
        back_button = findViewById(R.id.back_button);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        String type="";
        pre(username,memberId);

        ctpin_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (ctpin_text.getRight() - ctpin_text.getCompoundDrawables()[2].getBounds().width())) {
                        isCtpinVissible = !isCtpinVissible;
                        if (isCtpinVissible) {
                            ctpin_text.setInputType(InputType.TYPE_CLASS_NUMBER);
                        } else {
                            ctpin_text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        }
                        ctpin_text.setSelection(ctpin_text.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

        tpin_text.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tpin_text.getRight() - tpin_text.getCompoundDrawables()[2].getBounds().width())) {
                        isTpinVissible = !isTpinVissible;
                        if (isTpinVissible) {
                            tpin_text.setInputType(InputType.TYPE_CLASS_NUMBER);
                        } else {
                            tpin_text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        }
                        tpin_text.setSelection(tpin_text.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });


        Intent passed_intent = getIntent();
        if(passed_intent!=null){
            if(passed_intent.hasExtra("ptype")){
                payment_type.setText(passed_intent.getStringExtra("ptype"));
                type = passed_intent.getStringExtra("ptype");
            }

            if(passed_intent.hasExtra("stype")) {
                subtype.setText(passed_intent.getStringExtra("stype"));
            } else {
                type = "";
            }
            if(passed_intent.hasExtra("stype_num")){
                number_id = passed_intent.getStringExtra("stype_num");
                subtype_num.setVisibility(View.VISIBLE);
                subtype_num.setText(passed_intent.getStringExtra("stype_num"));
            }
            if(passed_intent.hasExtra("amount")){
                amount.setText(passed_intent.getStringExtra("amount"));
                money = passed_intent.getStringExtra("amount");
            }

            if(passed_intent.hasExtra("operator_code"))
                operatorCode = passed_intent.getStringExtra("operator_code");
            if(passed_intent.hasExtra("circle_code"))
                circleCode = passed_intent.getStringExtra("circle_code");
        } else {
            type = "";
        }

        LottieAnimationView lottieAnimationzoom = findViewById(R.id.success);
        lottieAnimationzoom.playAnimation();
        lottieAnimationzoom.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        String finalType = type;
        pay.setOnClickListener(v -> {
            pay.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            String tpin = tpin_text.getText().toString().trim();
            String ctpin = ctpin_text.getText().toString().trim();

            if (checkTPin(tpin, ctpin)) {

                check(memberId, tpin, response -> {
                    try {
                        boolean isValid = response.getBoolean("isValid");
                        if (isValid) {
                            Log.d("recharge_type",finalType);
                            if(finalType.equals("BASIC PACKAGE")) {
                                buyMembership(memberId, "BASIC", buyResponse -> {
                                    try {
                                        String status = buyResponse.getString("status");
                                        String message = buyResponse.getString("message");

                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        if ("success".equals(status)) {
                                            pay_layout.setVisibility(View.GONE);
                                            success_layout.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception e) {
                                        showError("Error parsing membership response: " + e.getMessage());
                                    } finally {
                                        progressBar.setVisibility(View.GONE);
                                        pay.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                            else if(finalType.equals("PREMIUM PACKAGE")){
                                buyMembership(memberId, "PREMIUM", buyResponse -> {
                                    try {
                                        String status = buyResponse.getString("status");
                                        String message = buyResponse.getString("message");

                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        if ("success".equals(status)) {
                                            pay_layout.setVisibility(View.GONE);
                                            success_layout.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception e) {
                                        showError("Error parsing membership response: " + e.getMessage());
                                    } finally {
                                        progressBar.setVisibility(View.GONE);
                                        pay.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                            else if(finalType.equals("Mobile Recharge")){
                                mobile_recharge(memberId,operatorCode,circleCode,number_id,money);

                            }
                        } else {
                            showError("Invalid T-PIN");
                        }
                    } catch (Exception e) {
                        showError("Error parsing T-PIN response: " + e.getMessage());
                    } finally {
                        progressBar.setVisibility(View.GONE);
                        pay.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }
    private void check(String memberId, String tpin, Response.Listener<JSONObject> responseListener) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/checktpin";

        JSONObject payload = new JSONObject();
        try {
            payload.put("member_id", memberId);
            payload.put("tpin", tpin);
        } catch (JSONException e) {
            showError("Error creating payload: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                responseListener,
                error -> showError("T-PIN API Error: " + error.getMessage()));

        Volley.newRequestQueue(this).add(request);
    }
    private void buyMembership(String memberId, String packageName, Response.Listener<JSONObject> responseListener) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/buymembership";

        Log.d("arsh", "Calling buyMembership API with member_id: " + memberId + " and package_name: " + packageName);

        JSONObject payload = new JSONObject();
        try {
            payload.put("package_name", packageName);
            payload.put("member_id", memberId);
        } catch (JSONException e) {
            showError("Error creating payload: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                response -> {
                    try {
                        // Log the successful response
                        updateMembership(memberId);
                        Log.d("arsh", "Membership purchase response: " + response.toString());

                        // Pass the response to the listener
                        responseListener.onResponse(response);
                    } catch (Exception e) {
                        Log.e("arsh", "Error parsing response: " + e.getMessage());
                        showError("Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMessage = "Network error: " + error.getMessage();
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody = new String(error.networkResponse.data);

                        Log.e("arsh", "Membership API Error - Status Code: " + statusCode + ", Response: " + responseBody);
                        JSONObject errorJson = null;
                        try {
                            errorJson = new JSONObject(responseBody);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String errorDetail = errorJson.optString("error", "Unknown error occurred");
                        showError("Message: " + errorDetail);
                    } else {
                        Log.e("arsh", "Membership API Error: " + error.getMessage());
                        showError("Membership API Error: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void updateMembership(String memberId){
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/getmembershipStatus";
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("member_id",memberId);
        }
        catch (JSONException e){
            //
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,jsonObject,response -> {
            if(response!=null){
                try {
                    if(response.getString("status").equals("true")){
                        String membership = response.getString("membership");
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("membership",membership);
                        editor.apply();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        },
                error -> {
                    Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show();
                    //
                });
    }
    private void mobile_recharge(String memberId, String operatorCode, String circleCode, String number, String amount) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/doMobileRecharge";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("circlecode", circleCode);
            jsonObject.put("operatorcode", operatorCode);
            jsonObject.put("number", number);
            jsonObject.put("amount", amount);
            jsonObject.put("member_id", memberId);

            Log.d("mobile_recharge", "Request URL: " + url);
            Log.d("mobile_recharge", "Request Body: " + jsonObject.toString());

        } catch (JSONException e) {
            Log.e("mobile_recharge", "JSONException: " + e.getMessage());
            Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            Log.d("mobile_recharge", "Response: " + response.toString());
            try {
                String message = response.getString("message");
                Log.d("mobile_recharge", "Response Message: " + message);

                String status = response.getString("status");
                if ("true".equals(status)) {
                    pay_layout.setVisibility(View.GONE);
                    success_layout.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Recharge Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e("mobile_recharge", "Error parsing response: " + e.getMessage());
                Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            error.printStackTrace();
            Log.d("mobile_recharge_error", "Error: " + error.getMessage());
            Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }



    private void showError(String message) {
        Log.e("arsh", "Error: " + message);
        progressBar.setVisibility(View.GONE);
        pay.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean checkTPin(String tpin, String ctpin){

        if(tpin.isEmpty()){
            Toast.makeText(this, "Enter T-Pin", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
            return false;
        }
        if(ctpin.isEmpty()){
            Toast.makeText(this, "Enter Confirm T-Pin", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
            return false;
        }
        if(!ctpin.equals(tpin)){
            Toast.makeText(this, "T-Pin and Confirm T-Pin does not match", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
            return  false;
        }


        return true;
    }
    private void pre(String username,String memberId){
        back_button.setOnClickListener(v -> finish());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        memberName = findViewById(R.id.memberName);
        userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);
    }
}