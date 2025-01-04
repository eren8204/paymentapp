package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MyIncome extends AppCompatActivity {
    private static final String TAG = "adityai";

    private ImageButton back_button;
    private TextView tb_memberid, tb_username;
    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;
    private List<Transaction> transactionList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_income);

        Log.d(TAG, "onCreate: Activity started.");

        back_button = findViewById(R.id.back_button);
        recyclerView = findViewById(R.id.recyclerView);
        tb_username = findViewById(R.id.memberName);
        tb_memberid = findViewById(R.id.memberId);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !"); // Default value
        String memberId = sharedPreferences.getString("memberId", "UP000000"); // Default value
        Log.d(TAG, "SharedPreferences - Username: " + username + ", MemberId: " + memberId);
        fetchTransactions(memberId); // Fetch transactions for the member ID

        pre(username, memberId); // Set the username and member ID in the UI

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        incomeAdapter = new IncomeAdapter(transactionList);
        recyclerView.setAdapter(incomeAdapter);
    }

    private void fetchTransactions(String memberId) {
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/incomeTransactions";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("member_id", memberId);
            Log.d(TAG, "Request body: " + requestBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating request body: ", e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "Response received: " + response.toString());

                            boolean success = response.getBoolean("success");
                            if (success) {
                                Log.d(TAG, "Transaction data successfully fetched.");

                                JSONArray transactions = response.getJSONObject("transactions").getJSONArray("data");
                                Log.d(TAG, "Transactions: " + transactions.toString());

                                // Clear the existing data
                                transactionList.clear();

                                // Add new data to the list
                                for (int i = 0; i < transactions.length(); i++) {
                                    JSONObject transaction = transactions.getJSONObject(i);
                                    transactionList.add(new Transaction(
                                            transaction.getString("type"),
                                            transaction.getString("subType"),
                                            transaction.getString("commissionBy"),
                                            transaction.getString("level"),
                                            formatDate(transaction.getString("date_time")),
                                            formatTime(transaction.getString("date_time")),
                                            transaction.getString("recharge_to"),
                                            transaction.getString("amount"),
                                            transaction.getString("credit")
                                    ));
                                }

                                // Notify the adapter of the data change
                                incomeAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Transaction fetch failed.");
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing response: ", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching transactions: ", error);
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this); // Use 'this' instead of getContext()
        queue.add(request);
    }

    private String formatDate(String dateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputFormat = "dd MMM yyyy";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = inputDateFormat.parse(dateString);
            String formattedDate = outputDateFormat.format(date);
            Log.d(TAG, "Formatted date: " + formattedDate);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error formatting date: ", e);
            return dateString;
        }
    }

    private String formatTime(String utcDateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String outputFormat = "hh:mm a";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            Date date = inputDateFormat.parse(utcDateString);
            if (date != null) {
                String formattedTime = outputDateFormat.format(date);
                Log.d(TAG, "Formatted time: " + formattedTime);
                return formattedTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error formatting time: ", e);
        }
        return utcDateString;
    }

    private void pre(String username, String memberId) {
        Log.d(TAG, "Setting username: " + username + " and member ID: " + memberId);
        back_button.setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked.");
            finish(); // Close the activity on back button click
        });

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));

        tb_username.setText(username);
        tb_memberid.setText(memberId);
    }

    // Transaction model class
    static class Transaction {
        String type, subType, commissionBy, level, date, time, rechargeTo, amountSpent, amountGot;

        public Transaction(String type, String subType, String commissionBy, String level, String date, String time, String rechargeTo, String amountSpent, String amountGot) {
            this.type = type;
            this.subType = subType;
            this.commissionBy = commissionBy;
            this.level = level;
            this.date = date;
            this.time = time;
            this.rechargeTo = rechargeTo;
            this.amountSpent = amountSpent;
            this.amountGot = amountGot;
        }
    }

    // Adapter class
    class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {
        private final List<Transaction> transactionList;

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

        @Override
        public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
            Transaction transaction = transactionList.get(position);
            Log.d(TAG, "Binding transaction at position " + position + ": " + transaction);

            holder.type.setText(transaction.type);
            holder.subType.setText(transaction.subType);
            holder.username.setText(transaction.commissionBy);
            holder.level.setText(transaction.level);
            holder.date.setText(transaction.date);
            holder.time.setText(transaction.time);
            holder.rechargeTo.setText(transaction.rechargeTo);
            holder.amountSpent.setText(transaction.amountSpent);
            holder.amountGot.setText(transaction.amountGot);
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        class IncomeViewHolder extends RecyclerView.ViewHolder {
            TextView username, level, type, subType, rechargeTo, date, time, amountSpent, amountGot;

            public IncomeViewHolder(View itemView) {
                super(itemView);
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
