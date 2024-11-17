package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class prepaid_mobile extends AppCompatActivity {
    String[] operatorList = {"JIO", "Airtel", "Vodafone Idea", "BSNL"};
    ArrayAdapter<String> adapter;
    String selectedOperator="";
    JSONObject jsonObject;
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepaid_mobile);
        Button browse_plans = findViewById(R.id.browse_plans);
        EditText mobile_no = findViewById(R.id.mobile_no);
        EditText recharge_amount = findViewById(R.id.recharge_amount);
        Button recharge = findViewById(R.id.recharge);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.select);
        TextInputLayout textInputLayout = findViewById(R.id.operator);

        adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, operatorList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedOperator = adapter.getItem(position);  // Get the selected item
                // Perform your actions here with the selected item
            }
        });
        browse_plans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mobile_no.getText().toString().isEmpty() || selectedOperator.isEmpty())
            {
                Toast.makeText(prepaid_mobile.this, "Please Enter Valid Mobile No & Operator", Toast.LENGTH_SHORT).show();
            }
            else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendPostRequest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
              }
            }
        });
    }
    private void sendPostRequest() throws Exception {
        // Define your API endpoint
        URL url = new URL("https://api.goterpay.com/Rplan?mid=G298141549&mkey=OMKU101155&operator=AT&circle=23");

        // Open a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // form data
        conn.setDoOutput(true); // Indicates this request will send data

        // Prepare the POST parameters
        String postData = "mid=G298141549&mkey=OMKU101155&operator=AT&circle=23";

        // Send the POST data
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        // Get the response code
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder result = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            result.append(inputLine);
        }
        in.close();

        // Print the response
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (responseCode == 200) {
                    // Success response
                    Toast.makeText(prepaid_mobile.this, "Login Success", Toast.LENGTH_SHORT).show();
                    // Pass the result to the next activity
                    Intent intent = new Intent(prepaid_mobile.this, recharge_plans.class);
                    intent.putExtra("response", result.toString());
                    startActivity(intent);
                } else {
                    // Handle error
                    Toast.makeText(prepaid_mobile.this, "Failed to retrieve plans", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}