package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class recharge_plans extends BaseActivity {
    HashMap<String, String> operatorMap = new HashMap<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> circleAdapter;
    HashMap<String, String> stateCircleMap = new HashMap<>();
    String selectedState = "";
    String selectedCircleCode = "";
    String selectedOperator="";
    String selectedOperatorCode="";
    private SharedPreferences sharedPreferences;
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_plans);
        TextView customer_id = findViewById(R.id.customer_id);
        Spinner spinner = findViewById(R.id.operator_spinner);
        Spinner circleSpinner = findViewById(R.id.circle_spinner);
        TextView recharge_amount = findViewById(R.id.recharge_amount);
        ImageView back_button = findViewById(R.id.back_button);
        Button recharge = findViewById(R.id.recharge);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, User!");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));

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
                    selectedOperator = operatorList.get(position); // Update global variable
                    selectedOperatorCode = operatorMap.get(selectedOperator);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection if needed
            }
        });

        back_button.setOnClickListener(v -> finish());

        recharge.setOnClickListener(v -> {
            if(selectedState.isEmpty() || selectedOperator.isEmpty() || customer_id.getText().toString().trim().length()<10) {
                Toast.makeText(this, "Please select all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, payment.class);
            intent.putExtra("ptype","DTH Recharge");
            intent.putExtra("stype",selectedOperator);
            intent.putExtra("circle_code",selectedCircleCode);
            intent.putExtra("operator_code",selectedOperatorCode);
            intent.putExtra("stype_num",customer_id.getText().toString().trim());
            intent.putExtra("amount",recharge_amount.getText().toString().trim());
            startActivity(intent);
            finish();
        });

    }

    private void initializeOperatorMap(){
        operatorMap.put("Airtel DTH TV","ATV");
        operatorMap.put("SUNDIRECT DTH TV","STV");
        operatorMap.put("TATASKY DTH TV","TTV");
        operatorMap.put("VIDEOCON DTH TV","VTV");
        operatorMap.put("DISH TV","DTV");
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