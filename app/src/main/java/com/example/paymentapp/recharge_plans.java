package com.example.paymentapp;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class recharge_plans extends AppCompatActivity {
    HashMap<String, String> operatorMap = new HashMap<>();
    ArrayAdapter<String> adapter;
    String selectedOperator="";
    String selectedOperatorCode="";
    @Override
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_plans);
        TextView customer_id = findViewById(R.id.customer_id);
        Spinner spinner = findViewById(R.id.operator_spinner);
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

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, User!");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        initializeOperatorMap();
        List<String> operatorList = new ArrayList<>();
        operatorList.add("Select Operator");
        operatorList.addAll(operatorMap.keySet());
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, operatorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                Toast.makeText(recharge_plans.this, "Operator: "+selectedOperator+" Code: "+selectedOperatorCode,
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
            intent.putExtra("ptype","Recharge");
            intent.putExtra("stype",selectedOperator);
            intent.putExtra("stype_num",customer_id.getText().toString().trim());
            intent.putExtra("amount","₹"+recharge_amount.getText().toString().trim());
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
}