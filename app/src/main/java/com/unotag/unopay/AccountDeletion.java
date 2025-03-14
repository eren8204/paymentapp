package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountDeletion extends BaseActivity {
    private ImageView back_button;
    private Button delete;
    private EditText tpin,pass;
    private ProgressBar delete_progress;
    private LinearLayout delete_layout;
    private TextView already;
    private SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_deletion);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        tpin = findViewById(R.id.tpin_text);
        delete = findViewById(R.id.delete_btn);
        back_button=findViewById(R.id.back_button);
        delete_progress = findViewById(R.id.delete_progress);
        pass = findViewById(R.id.password_text);
        already = findViewById(R.id.already_sent);
        delete_layout = findViewById(R.id.delete_layout);
        back_button.setOnClickListener(v -> finish());

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));
        delete.setOnClickListener(v->{
            String password = pass.getText().toString().trim();
            String tpinString = tpin.getText().toString().trim();
            if(tpinString.length()<4){
                Toast.makeText(this, "Enter T-PIN", Toast.LENGTH_SHORT).show();
            }
            if(password.length()<12){
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            check(memberId,tpinString,password, response -> {
                delete.setVisibility(View.GONE);
                delete_progress.setVisibility(View.VISIBLE);
                try{
                    String status = response.getString("success");
                    if(status.equals("true")){
                        String msg = response.getString("message");
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        delete_layout.setVisibility(View.GONE);
                        already.setVisibility(View.VISIBLE);
                    }
                    else{
                        showError("Check Password and T-PIN");
                    }
                }
                catch (Exception e){
                    showError("Error "+e.getMessage());
                }
            });
            delete_progress.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
        });
    }
    private void check(String memberId, String tpin, String password, Response.Listener<JSONObject> responseListener) {
        String url = BuildConfig.api_url+"deleteRequest";

        JSONObject payload = new JSONObject();
        try {
            payload.put("memberid", memberId);
            payload.put("password",password);
            payload.put("tpin", tpin);
        } catch (JSONException e) {
            showError("Error creating payload: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                responseListener,
                error -> showError("Error Try Again")){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void showError(String message) {
        Log.e("arsh", "Error: " + message);
        delete_progress.setVisibility(View.GONE);
        delete.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}