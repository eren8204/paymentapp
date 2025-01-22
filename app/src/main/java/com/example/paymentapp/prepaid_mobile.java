package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class prepaid_mobile extends AppCompatActivity {
    HashMap<String, String> operatorMap = new HashMap<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> circleAdapter;
    HashMap<String, String> stateCircleMap = new HashMap<>();
    String selectedState = "";
    String selectedCircleCode = "";
    String selectedOperator="";
    String selectedOperatorCode="";
    JSONObject jsonObject;
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepaid_mobile);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        EditText mobile_no = findViewById(R.id.mobile_no);
        EditText recharge_amount = findViewById(R.id.recharge_amount);
        Button recharge = findViewById(R.id.recharge);
        Spinner spinner = findViewById(R.id.operator_spinner);
        Spinner circleSpinner = findViewById(R.id.circle_spinner);
        ImageView back_button = findViewById(R.id.back_button);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, User!");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
            memberName.setText(username);
            userId.setText(memberId);

        initializeStateCircleMap();
        List<String> circleList = new ArrayList<>();
        circleList.add("Select Region");
        circleList.addAll(stateCircleMap.keySet());
        circleAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, circleList);
        circleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        circleSpinner.setAdapter(circleAdapter);

        initializeOperatorMap();
        List<String> operatorList = new ArrayList<>();
        operatorList.add("Select Operator");
        operatorList.addAll(operatorMap.keySet());
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, operatorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        circleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedState = "";
                    selectedCircleCode = "";
                } else {
                    selectedState = circleList.get(position);
                    selectedCircleCode = stateCircleMap.get(selectedState);
                }

                if(position>0)
                    Toast.makeText(prepaid_mobile.this,
                        "Selected State: " + selectedState + ", Circle Code: " + selectedCircleCode,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no state is selected
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    selectedOperator = "";
                    selectedOperatorCode = "";
                }
                else
                {
                    selectedOperator = operatorList.get(position);
                    selectedOperatorCode = operatorMap.get(selectedOperator);
                }
                Toast.makeText(prepaid_mobile.this, "Operator: "+selectedOperator+" Code: "+selectedOperatorCode,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection if needed
            }
        });

        back_button.setOnClickListener(v -> finish());

        recharge.setOnClickListener(v -> {
            Intent intent = new Intent(this, payment.class);
            intent.putExtra("ptype","Mobile Recharge");
            intent.putExtra("stype",selectedOperator);
            intent.putExtra("circle_code",selectedCircleCode);
            intent.putExtra("operator_code",selectedOperatorCode);
            intent.putExtra("stype_num",mobile_no.getText().toString().trim());
            intent.putExtra("amount",recharge_amount.getText().toString().trim());
            startActivity(intent);
            finish();
        });

    }
    private void initializeOperatorMap(){
        operatorMap.put("Airtel","A");
        operatorMap.put("Vodafone Idea","VI");
        operatorMap.put("JIO","RC");
        operatorMap.put("BSNL - TOPUP","BT");
        operatorMap.put("BSNL - STV","BS");
    }
    private void initializeStateCircleMap() {
        stateCircleMap.put("Andhra Pradesh", "13");
        stateCircleMap.put("Assam", "24");
        stateCircleMap.put("Bihar", "17");
        stateCircleMap.put("Chhattisgarh", "27");
        stateCircleMap.put("Gujarat", "12");
        stateCircleMap.put("Haryana", "20");
        stateCircleMap.put("Himachal Pradesh", "21");
        stateCircleMap.put("Jammu And Kashmir", "25");
        stateCircleMap.put("Jharkhand", "22");
        stateCircleMap.put("Karnataka", "9");
        stateCircleMap.put("Kerala", "14");
        stateCircleMap.put("Madhya Pradesh", "16");
        stateCircleMap.put("Maharashtra", "4");
        stateCircleMap.put("Orissa", "23");
        stateCircleMap.put("Punjab", "1");
        stateCircleMap.put("Rajasthan", "18");
        stateCircleMap.put("Tamil Nadu", "8");
        stateCircleMap.put("Uttar Pradesh East", "10");
        stateCircleMap.put("West Bengal", "2");
        stateCircleMap.put("Uttar Pradesh West", "11");
        stateCircleMap.put("Mumbai", "3");
        stateCircleMap.put("Delhi", "5");
        stateCircleMap.put("CHENNAI", "7");
        stateCircleMap.put("NORTH EAST", "26");
        stateCircleMap.put("Kolkata", "6");
    }
}