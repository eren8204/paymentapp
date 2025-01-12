package com.example.paymentapp;
import static android.content.ContentValues.TAG;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private List<WithdrawItem> withdrawList;

    private EditText messageEditText;
    private static final String SEND_MESSAGE_URL = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/user-withdraw-request";


    private Button withdrawbtn;
    private ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        withdrawbtn=findViewById(R.id.withdrawbtn);

        messageEditText = findViewById(R.id.withdraw_amount);
        withdrawbtn.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            Log.d(TAG, "raiseTicketButton: Sending message: " + message);
            sendMessageAndFetchChat(memberId,message);
        });

        back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        withdrawList = new ArrayList<>();
        adapter = new WithdrawAdapter(withdrawList);
        recyclerView.setAdapter(adapter);

        fetchData(memberId);
    }

    private void sendMessageAndFetchChat(final String memberId,String message) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("member_id", memberId);
            requestBody.put("amount", message);
            Log.d("jskbf", "sendMessageAndFetchChat: Request body = " + requestBody.toString());

            JsonObjectRequest sendRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    SEND_MESSAGE_URL,
                    requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d("jskbf", "sendMessageAndFetchChat: Message sent successfully");
                            Log.d("jskbf", "sendMessageAndFetchChat: Response = " + response.toString());
                            Log.d("jskbf", "sendMessageAndFetchChat: Message sent successfully");
                            Toast.makeText(Withdraw.this, response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("jskbf", "sendMessageAndFetchChat: Failed to send message", error);
                            Toast.makeText(Withdraw.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(sendRequest);

        } catch (JSONException e) {
            Log.e("jskbf", "sendMessageAndFetchChat: JSON exception", e);
        }
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
        String url = "https://gk4rbn12-3000.inc1.devtunnels.ms/api/auth/get-user-withdraw-request";

        JSONObject postData = new JSONObject();
        try {
            postData.put("member_id", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

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



