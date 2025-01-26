package com.example.paymentapp;
import static android.content.ContentValues.TAG;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Withdraw extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WithdrawAdapter adapter;
    private List<WithdrawItem> withdrawList,filteredList;

    private EditText messageEditText,toMember;
    private static String SEND_MESSAGE_URL = BuildConfig.api_url+"user-withdraw-request";

    private int transferSelect = 1;
    private int seeSelect = 1;

    private Button withdrawbtn;
    private ImageView back_button;

    private LinearLayout progressLayout,oopsLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        progressLayout = findViewById(R.id.progress_layout);
        oopsLayout = findViewById(R.id.oops_layout);
        withdrawbtn=findViewById(R.id.withdrawbtn);

        toMember = findViewById(R.id.toMember);
        toMember.setVisibility(GONE);
        messageEditText = findViewById(R.id.withdraw_amount);
        withdrawbtn.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            Log.d(TAG, "raiseTicketButton: Sending message: " + message);
            try {
                sendMessageAndFetchChat(memberId,message);
            } catch (JSONException e) {
                Toast.makeText(this, "Try again later", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        });

        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> finish());
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        withdrawList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new WithdrawAdapter(withdrawList);
        recyclerView.setAdapter(adapter);

        RadioGroup radioGroup1 = findViewById(R.id.radioGroup1);

        RadioButton togglePerson = findViewById(R.id.togglePerson);
        RadioButton toggleBank = findViewById(R.id.toggleBank);
        RadioButton toggleFundWallet = findViewById(R.id.toggleFundWallet);

        radioGroup1.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.toggleFundWallet){
                transferSelect = 1;
                toMember.setVisibility(GONE);
            }
            else if(checkedId==R.id.togglePerson){
                transferSelect = 2;
                toMember.setVisibility(VISIBLE);
            }
            else if(checkedId==R.id.toggleBank){
                transferSelect = 3;
                toMember.setVisibility(GONE);
            }
            else{
                transferSelect = 1;
                toMember.setVisibility(GONE);
            }
        });

        RadioGroup radioGroup2 = findViewById(R.id.radioGroup2);

        RadioButton seeAll = findViewById(R.id.seeAll);
        RadioButton seeBank = findViewById(R.id.seeBank);
        RadioButton seeFundWallet = findViewById(R.id.seeFundWallet);
        RadioButton seePerson = findViewById(R.id.seePerson);

        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.seeAll){
                seeSelect = 1;
            }
            else if(checkedId==R.id.seeFundWallet){
                seeSelect = 2;
            }
            else if(checkedId==R.id.seePerson) {
                seeSelect = 3;
            }
            else if(checkedId==R.id.seeBank){
                seeSelect = 4;
            }
            else {
                seeSelect = 1;
            }
            //filterDataByType();
        });

        fetchData(memberId);
    }

    private void sendMessageAndFetchChat(final String memberId,String message) throws JSONException {
        JSONObject requestBody = new JSONObject();
        if(transferSelect==1){
            SEND_MESSAGE_URL = BuildConfig.api_url+"commissin-wallet-to-flexi-wallet";
            requestBody.put("member_id", memberId);
            requestBody.put("commission_amount", message);
        }
        else{
            requestBody.put("member_id", memberId);
            requestBody.put("amount", message);
        }
        Log.d("commission_request",requestBody.toString());
        JsonObjectRequest sendRequest = new JsonObjectRequest(
                Request.Method.POST,
                SEND_MESSAGE_URL,
                requestBody,
                response -> {
                    try {
                        if(response.has("status") && response.getString("status").equals("true")){
                            String msg = "Done";
                            if(response.has("message"))
                                msg = response.getString("message");
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String msg = "Error";
                            if(response.has("message"))
                                msg = response.getString("message");
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(Withdraw.this, "Error", Toast.LENGTH_SHORT).show();
                }
        );
        sendRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sendRequest);

    }


    private String formatDate(String dateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputFormat = "dd MMM yyyy";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
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

                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return utcDateString;
    }


    private void fetchData(String memberId) {
        String url = BuildConfig.api_url+"get-user-withdraw-request";

        JSONObject postData = new JSONObject();
        try {
            postData.put("member_id", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> {
                    progressLayout.setVisibility(GONE);
                    try {
                        if (response.getString("status").equals("true")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                WithdrawItem withdrawItem = new WithdrawItem(
                                        formatAmount(item.getString("amount")),
                                        item.getString("date_time"),
                                        item.getString("status")
                                );
                                recyclerView.setVisibility(VISIBLE);
                                withdrawList.add(withdrawItem);
                            }
                            withdrawList.sort((request1, request2) -> {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date date1 = sdf.parse(request1.dateTime);
                                    Date date2 = sdf.parse(request2.dateTime);
                                    assert date2 != null;
                                    return date2.compareTo(date1); // Latest to oldest
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return 0;
                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                        else
                            oopsLayout.setVisibility(VISIBLE);
                    } catch (JSONException e) {
                        oopsLayout.setVisibility(VISIBLE);
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    oopsLayout.setVisibility(VISIBLE);
                });

        requestQueue.add(jsonObjectRequest);
    }

    private String formatAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            DecimalFormat decimalFormat = new DecimalFormat("₹0.000");
            return decimalFormat.format(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return amount;
        }
    }

    static class WithdrawItem {
        String amount;
        String dateTime;
        String status;

        public WithdrawItem(String amount, String dateTime, String status) {
            this.amount = amount;
            this.dateTime = dateTime;
            this.status = status;
        }
    }

    class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.ViewHolder> {

        private List<WithdrawItem> withdrawItems;

        public WithdrawAdapter(List<WithdrawItem> withdrawItems) {
            this.withdrawItems = withdrawItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WithdrawItem item = withdrawItems.get(position);
            String formattedDate = formatDate(item.dateTime);
            String formattedTime = formatTime(item.dateTime);
            holder.amount.setText(item.amount);
            holder.time.setText(formattedTime);
            holder.date.setText(formattedDate);
            holder.status.setText(item.status);

            String status = item.status;
            switch (status) {
                case "pending":
                    holder.status.setText("Pending");
                    holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.pending_small));
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey)); // Gray color for Pending
                    break;
                case "done":
                    holder.status.setText("Approved");
                    holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.approved));
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.accept)); // Set the status color (green)
                    break;
                default:
                    holder.status.setText("Rejected");
                    holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.rejected));
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.reject)); // Set the status color (red)
                    break;
            }
        }


        @Override
        public int getItemCount() {
            return withdrawItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView  amount, date, status , time;

            ImageView status_img;
            LinearLayout statusColour;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                amount = itemView.findViewById(R.id.amountTextView);
                time=itemView.findViewById(R.id.timeTextView);
                date = itemView.findViewById(R.id.dateTextView);
                status = itemView.findViewById(R.id.status_text);
                status_img = itemView.findViewById(R.id.status_img);
                statusColour = itemView.findViewById(R.id.statuscolour);
            }
        }
    }
}



