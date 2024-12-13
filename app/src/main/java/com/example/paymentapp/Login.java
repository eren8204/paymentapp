package com.example.paymentapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {


    TextView signup;
    EditText email,password;
    Button login;
    String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        signup=findViewById(R.id.signup);
        login=findViewById(R.id.login);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        ProgressBar progressbarlogin = findViewById(R.id.progressbarlogin);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (username.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                login.setVisibility(View.GONE);
                progressbarlogin.setVisibility(View.VISIBLE);
                RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("identifier", username);
                    requestBody.put("password", pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        requestBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    String memberId = response.getString("memberid");
                                    String userName = response.getString("username");

                                    if (message.equalsIgnoreCase("User logged in successfully")) {
                                        // Navigate to MainActivity
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        intent.putExtra("memberId",memberId);
                                        intent.putExtra("userName",userName);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        progressbarlogin.setVisibility(View.GONE);
                                        login.setVisibility(View.VISIBLE);
                                        String msg = response.getString("message");
                                        Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    progressbarlogin.setVisibility(View.GONE);
                                    login.setVisibility(View.VISIBLE);
                                    Log.d("login_error",e.toString());
                                    Toast.makeText(Login.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressbarlogin.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(Login.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                requestQueue.add(jsonObjectRequest);
            }
        });

    }
}