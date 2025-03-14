package com.unotag.unopay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MyIncome extends BaseActivity {
    private static final String INPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
    private static final String TAG = "adityai";
    private SharedPreferences sharedPreferences;
    private ImageButton back_button;
    private TextView tb_memberid, tb_username;
    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    private List<Transaction> filteredList = new ArrayList<>();
    private Integer seeSelect = 1;
    private LinearLayout progressLayout,oopsLayout;
    @SuppressLint("MissingInflatedI")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_income);

        back_button = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.recyclerView);
        tb_username = findViewById(R.id.memberName);
        tb_memberid = findViewById(R.id.memberId);
        progressLayout = findViewById(R.id.progress_layout);
        oopsLayout = findViewById(R.id.oops_layout);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        fetchTransactions(memberId);

        pre(username, memberId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        incomeAdapter = new IncomeAdapter(transactionList);
        recyclerView.setAdapter(incomeAdapter);


        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        RadioButton seeAll = findViewById(R.id.seeAll);
        RadioButton seeMembership = findViewById(R.id.seeMembership);
        RadioButton seeRecharge = findViewById(R.id.seeRecharge);
        RadioButton seeRank = findViewById(R.id.seeRank);
        RadioButton seeMoneyPlant = findViewById(R.id.seeMoneyPlant);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.seeAll){
                seeSelect = 1;
            }
            else if(checkedId==R.id.seeMembership){
                seeSelect = 2;
            }
            else if(checkedId==R.id.seeRecharge) {
                seeSelect = 3;
            }
            else if(checkedId==R.id.seeRank){
                seeSelect = 4;
            }
            else if(checkedId==R.id.seeMoneyPlant){
                seeSelect = 5;
            }
            else {
                seeSelect = 1;
            }
                filterDataByType();
        });

    }

    private void filterDataByType() {
        progressLayout.setVisibility(GONE);
        recyclerView.setVisibility(VISIBLE);
        if (filteredList != null) {
            filteredList.clear();
        } else {
            filteredList = new ArrayList<>();
        }
        if (seeSelect == 1) {
            filteredList.addAll(transactionList);
        } else if(seeSelect == 2){
            for (Transaction transaction : transactionList) {
                if (transaction.type.equalsIgnoreCase("Membership")) {
                    filteredList.add(transaction);
                }
            }
        }
        else if(seeSelect == 3){
            for (Transaction transaction : transactionList) {
                if (transaction.type.equalsIgnoreCase("Recharge")) {
                    filteredList.add(transaction);
                }
            }
        }
        else if(seeSelect == 4){
            for (Transaction transaction : transactionList) {
                if (transaction.type.equalsIgnoreCase("Rank Income")) {
                    filteredList.add(transaction);
                }
            }
        }
        else if(seeSelect == 5){
            for (Transaction transaction : transactionList) {
                if (transaction.type.equalsIgnoreCase("Magic Plant")) {
                    filteredList.add(transaction);
                }
            }
        }
        if(filteredList.isEmpty()){
            progressLayout.setVisibility(GONE);
            recyclerView.setVisibility(GONE);
            oopsLayout.setVisibility(VISIBLE);
        }
        incomeAdapter.updateList(filteredList);

    }

    private void fetchTransactions(String memberId) {
        String url = BuildConfig.api_url+"incomeTransactions";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("member_id", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Response received: " + response.toString());

                            boolean success = response.getBoolean("success");
                            if (success) {
                                progressLayout.setVisibility(GONE);
                                recyclerView.setVisibility(VISIBLE);
                                JSONArray transactions = response.getJSONObject("transactions").getJSONArray("data");
                                transactionList.clear();

                                for (int i = 0; i < transactions.length(); i++) {
                                    JSONObject transaction = transactions.getJSONObject(i);
                                    if(!transaction.getString("type").equalsIgnoreCase("Self Transfer") &&
                                            !transaction.getString("type").equalsIgnoreCase("Money Transfer") &&
                                            !transaction.getString("type").equalsIgnoreCase("Withdrawal Request") &&
                                            !transaction.getString("type").equalsIgnoreCase("Withdrawal Rejected")) {
                                        transactionList.add(new Transaction(
                                                transaction.getString("type"),
                                                transaction.getString("subType"),
                                                transaction.getString("commissionBy"),
                                                transaction.getString("level"),
                                                transaction.getString("date_time"),
                                                formatDate(transaction.getString("date_time")),
                                                formatTime(transaction.getString("date_time")),
                                                transaction.getString("recharge_to"),
                                                transaction.getString("amount"),
                                                transaction.getString("credit")
                                        ));
                                    }
                                }
                                transactionList.sort((request1, request2) -> {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date date1 = sdf.parse(request1.dateTime);
                                        Date date2 = sdf.parse(request2.dateTime);
                                        assert date2 != null;
                                        return date2.compareTo(date1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
                                });
                                if(transactionList.isEmpty()){
                                    progressLayout.setVisibility(GONE);
                                    oopsLayout.setVisibility(VISIBLE);
                                }
                                incomeAdapter.notifyDataSetChanged();
                            } else {
                                progressLayout.setVisibility(GONE);
                                oopsLayout.setVisibility(VISIBLE);
                                Log.d(TAG, "Transaction fetch failed.");
                                recyclerView.setVisibility(GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing response: ", e);
                            progressLayout.setVisibility(GONE);
                            oopsLayout.setVisibility(VISIBLE);
                        }
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching transactions: ", error);
                    progressLayout.setVisibility(GONE);
                    oopsLayout.setVisibility(VISIBLE);
                }
        ){
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

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private String formatDate(String dateString) {
        return formatDateTime(dateString, INPUT_FORMAT, DATE_FORMAT);
    }

    private String formatTime(String dateString) {
        return formatDateTime(dateString, INPUT_FORMAT, TIME_FORMAT);
    }

    private String formatDateTime(String dateString, String inputFormat, String outputFormat) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        // Input is in UTC
        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Output should be in IST
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            Date date = inputDateFormat.parse(dateString);
            if (date != null) {
                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            Log.e("DateFormat", "Error parsing date: " + dateString, e);
        }
        return dateString;
    }

    private void pre(String username, String memberId) {
        Log.d(TAG, "Setting username: " + username + " and member ID: " + memberId);
        back_button.setOnClickListener(v -> finish());

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));

        tb_username.setText(username);
        tb_memberid.setText(memberId);
    }

    // Transaction model class
    static class Transaction {
        String type, subType, commissionBy, level,dateTime, date, time, rechargeTo, amountSpent, amountGot;

        public Transaction(String type, String subType, String commissionBy, String level,String dateTime, String date, String time, String rechargeTo, String amountSpent, String amountGot) {
            this.type = type;
            this.subType = subType;
            this.commissionBy = commissionBy;
            this.level = level;
            this.dateTime = dateTime;
            this.date = date;
            this.time = time;
            this.rechargeTo = rechargeTo;
            this.amountSpent = amountSpent;
            this.amountGot = amountGot;
        }
    }

    static class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
        private List<Transaction> transactionList;

        public IncomeAdapter(List<Transaction> transactionList) {
            this.transactionList = transactionList;
        }

        @NonNull
        @Override
        public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: Creating view holder.");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_income_item, parent, false);
            return new IncomeViewHolder(view);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
            Transaction transaction = transactionList.get(position);

            holder.type.setText(transaction.type);
            holder.subType.setText(transaction.subType);
            holder.username.setText(transaction.commissionBy);
            if(transaction.level.equals("0") || transaction.level.equals("null"))
                holder.level.setVisibility(GONE);
            else{
                holder.level.setVisibility(VISIBLE);
                holder.level.setText("Level: "+transaction.level);
            }

            holder.date.setText(transaction.date);
            holder.time.setText(transaction.time);
            holder.rechargeTo.setText(transaction.rechargeTo);
            if(transaction.type.equalsIgnoreCase("Rank Income") || transaction.type.equalsIgnoreCase("Magic Plant")){
                holder.amountSpent.setVisibility(GONE);
                holder.level.setVisibility(GONE);
                holder.amountSpent.setText("");
                holder.divider.setVisibility(GONE);
            }
            else{
                holder.level.setVisibility(VISIBLE);
                holder.amountSpent.setText(transaction.amountSpent);
                holder.amountSpent.setVisibility(VISIBLE);
                holder.divider.setVisibility(VISIBLE);
            }

            double amount = Double.parseDouble(transaction.amountGot);
            String formattedAmount;
            if (amount == Math.floor(amount)) {
                // No decimal places needed
                formattedAmount = String.format("%.2f", amount);
            } else if ((amount * 100) % 1 == 0) {
                // Exactly two decimal places
                formattedAmount = String.format("%.2f", amount);
            } else if ((amount * 1000) % 1 == 0) {
                // Up to three decimal places
                formattedAmount = String.format("%.3f", amount);
            } else {
                // Up to four decimal places
                formattedAmount = String.format("%.4f", amount);
            }
            holder.amountGot.setText(formattedAmount);

        }

        public void updateList(List<Transaction> newList) {
            this.transactionList = newList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        class IncomeViewHolder extends RecyclerView.ViewHolder {
            TextView username, level, type, subType, rechargeTo, date, time, amountSpent, amountGot, divider;

            public IncomeViewHolder(View itemView) {
                super(itemView);
                divider = itemView.findViewById(R.id.divider);
                username = itemView.findViewById(R.id.username);
                level = itemView.findViewById(R.id.level);
                type = itemView.findViewById(R.id.type);
                subType = itemView.findViewById(R.id.stype);
                rechargeTo = itemView.findViewById(R.id.recharge_to);
                date = itemView.findViewById(R.id.date);
                time = itemView.findViewById(R.id.time);
                amountSpent = itemView.findViewById(R.id.amount_spent);
                amountGot = itemView.findViewById(R.id.amount_got);
            }
        }
    }
}
