package com.unotag.unopay;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class payment extends BaseActivity {

    private Button pay;
    private ImageButton back_button;
    private TextView payment_type,subtype,subtype_num,amount,memberName,userId,paymenttype,rechargeamount,rechargetype,date,time;
    private LinearLayout pay_layout,success_layout;
    private ProgressBar progressBar;
    private LinearLayout cashbacklayout;
    private TextView cashbacktext,transactionid;
    private TextView avabalance;
    private EditText tpin_text,ctpin_text;
    private boolean isTpinVissible = false;
    private SharedPreferences sharedPreferences;
    private ImageView successimage,unoimage;

    private boolean isCtpinVissible = false;
    private String operatorCode="",circleCode="",number_id="",money="",stype="";
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
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
        rechargeamount = findViewById(R.id.rechargeamount);
        rechargetype = findViewById(R.id.rechargetype);
        avabalance = findViewById(R.id.avaliablebalance);
        unoimage = findViewById(R.id.unoimage);
        cashbacktext = findViewById(R.id.cashback);
        transactionid = findViewById(R.id.transactionid);
        transactionid.setVisibility(View.GONE);
        cashbacklayout = findViewById(R.id.cashbacklayout);
        cashbacklayout.setVisibility(View.GONE);

        successimage = findViewById(R.id.successimage);
        paymenttype = findViewById(R.id.paymenttype);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String flexi_wallet = sharedPreferences.getString("flexi_wallet", "0.0");

        avabalance.setText(flexi_wallet);

        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        String type="";
        pre(username,memberId);

        unoimage.setVisibility(View.GONE);

        ctpin_text.setOnTouchListener((v, event) -> {

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
        });

        tpin_text.setOnTouchListener((v, event) -> {
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
        });


        Intent passed_intent = getIntent();
        if(passed_intent!=null){
            if(passed_intent.hasExtra("ptype")){
                payment_type.setText(passed_intent.getStringExtra("ptype"));
                type = passed_intent.getStringExtra("ptype");
            }

            if(passed_intent.hasExtra("stype")) {
                subtype.setText(passed_intent.getStringExtra("stype"));
                stype=passed_intent.getStringExtra("stype");
            } else {
                type = "";
            }
            if(passed_intent.hasExtra("stype_num")){
                number_id = passed_intent.getStringExtra("stype_num");
                subtype_num.setVisibility(View.VISIBLE);
                subtype_num.setText(passed_intent.getStringExtra("stype_num"));
            }
            if(passed_intent.hasExtra("amount")){
                amount.setText("₹"+passed_intent.getStringExtra("amount"));
                money = passed_intent.getStringExtra("amount");
            }

            if(passed_intent.hasExtra("operator_code"))
                operatorCode = passed_intent.getStringExtra("operator_code");
            if(passed_intent.hasExtra("circle_code"))
                circleCode = passed_intent.getStringExtra("circle_code");
        } else {
            type = "";
        }


        String finalType = stype;
        String ttype=type;
        Log.d("recharge_type",type);
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
                                            rechargeamount.setText("649/-");
                                            rechargetype.setText(finalType);
                                            showDateTime();
                                        }
                                    } catch (Exception e) {
                                        Log.d(ttype, Objects.requireNonNull(e.getMessage()));
                                    } finally {
                                        fetchWalletBalance(memberId);
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
                                            rechargeamount.setText("1298/-");
                                            rechargetype.setText(finalType);
                                            showDateTime();
                                        }
                                    } catch (Exception e) {
                                        Log.d(ttype, Objects.requireNonNull(e.getMessage()));
                                    } finally {
                                        fetchWalletBalance(memberId);
                                        progressBar.setVisibility(View.GONE);
                                        pay.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                            else if(ttype.equals("Mobile Recharge")){
                                try{
                                    mobile_recharge(memberId,operatorCode,circleCode,number_id,money);
                                }
                                catch (Exception e){
                                    Log.d(ttype, Objects.requireNonNull(e.getMessage()));
                                }
                                finally {
                                    fetchWalletBalance(memberId);
                                }
                            }
                            else if(ttype.equals("DTH Recharge")){
                                try{
                                    dth_recharge(memberId,operatorCode,circleCode,number_id,money);
                                }
                                catch (Exception e){
                                    Log.d(ttype, Objects.requireNonNull(e.getMessage()));
                                }
                                finally {
                                    fetchWalletBalance(memberId);
                                }
                            }
                            else{
                                Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            showError("Invalid T-PIN");
                        }
                    } catch (Exception e) {
                        Log.d("recharge_type", response.toString());
                        showError("Error! Try Again");
                    }
                    finally {
                        fetchWalletBalance(memberId);
                    }
                });
            }
        });

    }

    private void check(String memberId, String tpin, Response.Listener<JSONObject> responseListener) {
        String url = BuildConfig.api_url+"checktpin";

        JSONObject payload = new JSONObject();
        try {
            payload.put("member_id", memberId);
            payload.put("tpin", tpin);
        } catch (JSONException e) {
            showError("Error! Try Again");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                responseListener,
                error -> showError("Error! Try Again")){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void buyMembership(String memberId, String packageName, Response.Listener<JSONObject> responseListener) {
        String url = BuildConfig.api_url+"buymembership";

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
                        updateMembership(memberId);
                        Log.d("arsh", "Membership purchase response: " + response.toString());
                        responseListener.onResponse(response);
                    } catch (Exception e) {
                        Log.e("arsh", "Error parsing response: " + e.getMessage());
                        showError("Error! Try Again");
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
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
                        showError(errorDetail);
                    } else {
                        Log.e("arsh", "Membership API Error: " + error.getMessage());
                        showError("Error! Try Again");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void updateMembership(String memberId){
        String url = BuildConfig.api_url+"getmembershipStatus";
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("member_id",memberId);
        }
        catch (JSONException e){
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
                        //
                }){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void mobile_recharge(String memberId, String operatorCode, String circleCode, String number, String amount) {
        String url = BuildConfig.api_url + "doMobileRecharge";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("circlecode", circleCode);
            jsonObject.put("operatorcode", operatorCode);
            jsonObject.put("number", number);
            jsonObject.put("amount", amount);
            jsonObject.put("member_id", memberId);
        } catch (JSONException e) {
            Log.e("mobile_recharge", "JSONException: " + e.getMessage());
            Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                String message = response.getString("message");
                Log.d("mobile_recharge", "Response Message: " + message);

                String status = response.getString("status");
                if ("true".equals(status)) {
                    if (response.has("data")) {
                        Object dataObject = response.get("data");

                        String transaction = "";
                        String pay_status = "";

                        if (dataObject instanceof JSONObject) {
                            JSONObject data = (JSONObject) dataObject;
                            transaction = data.getString("orderid").trim();
                            pay_status = data.getString("status").trim();
                        } else if (dataObject instanceof String) {
                            Toast.makeText(this, dataObject.toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(pay_status.equalsIgnoreCase("Success")||pay_status.equalsIgnoreCase("Pending")){
                            switch (operatorCode) {
                                case "VI":
                                    successimage.setImageResource(R.drawable.vi);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "A":
                                    successimage.setImageResource(R.drawable.airtel);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "RC":
                                    successimage.setImageResource(R.drawable.jio);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "BT":
                                case "BS":
                                    successimage.setImageResource(R.drawable.bsnl);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                default:
                                    successimage.setImageResource(R.drawable.unopayimg);
                                    paymenttype.setText("Payment Successful");
                                    break;
                            }

                            progressBar.setVisibility(View.GONE);
                            pay.setVisibility(View.VISIBLE);
                            transactionid.setVisibility(View.VISIBLE);
                            transactionid.setText(transaction);
                            pay_layout.setVisibility(View.GONE);
                            success_layout.setVisibility(View.VISIBLE);
                            rechargeamount.setText("₹" + amount);
                            rechargetype.setText(number);
                            cashbacklayout.setVisibility(View.VISIBLE);
                            unoimage.setVisibility(View.VISIBLE);


                            double amountValue = Double.parseDouble(amount);
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            if (amountValue >= 10) {
                                String membership = sharedPreferences.getString("membership", "FREE");
                                double cashback = 0;
                                switch (membership) {
                                    case "FREE":
                                        cashback = (amountValue * 1.5) / 100;
                                        break;
                                    case "BASIC":
                                        cashback = (amountValue * 3) / 100;
                                        break;
                                    case "PREMIUM":
                                        cashback = (amountValue * 5) / 100;
                                        break;
                                }
                                String cash= String.format(String.valueOf(cashback));
                                cashbacktext.setText("₹" + cash);
                            } else {
                                cashbacktext.setText("No Cashback");
                            }

                            showDateTime();

                            Toast.makeText(this, "Recharge Successful", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(this, "Recharge Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("mobile_recharge", "Response does not contain 'data' key");
                        Toast.makeText(this, "Recharge Failed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e("mobile_recharge", "Error parsing response: " + e.getMessage());
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            }
            finally {
                progressBar.setVisibility(View.GONE);
                pay.setVisibility(View.VISIBLE);
            }
        }, error -> {
            error.printStackTrace();
            Log.d("mobile_recharge_error", "Error: " + error.getMessage());
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void dth_recharge(String memberId, String operatorCode, String circleCode, String number, String amount) {
        String url = BuildConfig.api_url + "doDthRecharge";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("circlecode", circleCode);
            jsonObject.put("operatorcode", operatorCode);
            jsonObject.put("number", number);
            jsonObject.put("amount", amount);
            jsonObject.put("member_id", memberId);
        } catch (JSONException e) {
            Log.e("dth_recharge", "JSONException: " + e.getMessage());
            Toast.makeText(this, "Error Try Again", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                String message = response.getString("message");
                Log.d("dth_recharge", "Response Message: " + message);

                String status = response.getString("status");
                if ("true".equals(status)) {
                    if (response.has("data")) {
                        Object dataObject = response.get("data");

                        String transaction = "";
                        String pay_status = "";

                        if (dataObject instanceof JSONObject) {
                            JSONObject data = (JSONObject) dataObject;
                            transaction = data.getString("orderid").trim();
                            pay_status = data.getString("status").trim();
                        } else if (dataObject instanceof String) {
                            Toast.makeText(this, dataObject.toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(pay_status.equalsIgnoreCase("Success")||pay_status.equalsIgnoreCase("Pending")){
                            switch (operatorCode) {
                                case "ATV":
                                    successimage.setImageResource(R.drawable.airtel_tv);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "STV":
                                    successimage.setImageResource(R.drawable.sundirect);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "TTV":
                                    successimage.setImageResource(R.drawable.tatasky);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "VTV":
                                    successimage.setImageResource(R.drawable.videocon);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                case "DTV":
                                    successimage.setImageResource(R.drawable.dishtv);
                                    paymenttype.setText("Recharge Successful");
                                    break;
                                default:
                                    successimage.setImageResource(R.drawable.unopayimg);
                                    paymenttype.setText("Payment Successful");
                                    break;
                            }

                            progressBar.setVisibility(View.GONE);
                            pay.setVisibility(View.VISIBLE);
                            transactionid.setVisibility(View.VISIBLE);
                            transactionid.setText(transaction);
                            pay_layout.setVisibility(View.GONE);
                            success_layout.setVisibility(View.VISIBLE);
                            rechargeamount.setText("₹" + amount);
                            rechargetype.setText(number);
                            cashbacklayout.setVisibility(View.VISIBLE);
                            unoimage.setVisibility(View.VISIBLE);


                            double amountValue = Double.parseDouble(amount);
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            if (amountValue >= 10) {
                                String membership = sharedPreferences.getString("membership", "FREE");
                                double cashback = 0;
                                switch (membership) {
                                    case "FREE":
                                        cashback = (amountValue * 1.5) / 100;
                                        break;
                                    case "BASIC":
                                        cashback = (amountValue * 3) / 100;
                                        break;
                                    case "PREMIUM":
                                        cashback = (amountValue * 5) / 100;
                                        break;
                                }
                                String cash= String.format(String.valueOf(cashback));
                                cashbacktext.setText("₹" + cash);
                            } else {
                                cashbacktext.setText("No Cashback");
                            }
                            showDateTime();

                            Toast.makeText(this, "Recharge Successful", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(this, "Recharge Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("mobile_recharge", "Response does not contain 'data' key");
                        Toast.makeText(this, "Recharge Failed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e("mobile_recharge", "Error parsing response: " + e.getMessage());
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            }
            finally {
                progressBar.setVisibility(View.GONE);
                pay.setVisibility(View.VISIBLE);
            }
        }, error -> {
            error.printStackTrace();
            Log.d("mobile_recharge_error", "Error: " + error.getMessage());
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
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
        if(!ctpin.equals(tpin)) {
            Toast.makeText(this, "T-Pin and Confirm T-Pin does not match", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            pay.setVisibility(View.VISIBLE);
            return false;
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

    private void fetchWalletBalance(String memberId){
        String url = BuildConfig.api_url+"user-wallet-wise-balance";
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);

        JSONObject requestBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,

                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("balance_response",response.toString());
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                String flexi_wallet = response.optString("flexi_wallet","0.0");
                                String commission_wallet = response.optString("commission_wallet","0.0");
                                String signup_bonus = response.optString("signup_bonus","0.0");
                                String today_income = response.optString("today_income","0");
                                String membership = response.optString("membership","FREE");

                                flexi_wallet = String.format("%.2f", Double.parseDouble(flexi_wallet));
                                commission_wallet = String.format("%.2f", Double.parseDouble(commission_wallet));
                                signup_bonus = String.format("%.2f", Double.parseDouble(signup_bonus));

                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("flexi_wallet",flexi_wallet);
                                editor.putString("commission_wallet",commission_wallet);
                                editor.putString("signup_bonus",signup_bonus);
                                editor.putString("today_income",today_income);
                                editor.putString("membership",membership);
                                editor.apply();

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void showDateTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        TextView dateTextView = findViewById(R.id.date);
        TextView timeTextView = findViewById(R.id.time);
        dateTextView.setText(dateFormat.format(calendar.getTime()));
        timeTextView.setText(timeFormat.format(calendar.getTime()));
    }
}