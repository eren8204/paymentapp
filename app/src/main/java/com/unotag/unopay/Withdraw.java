package com.unotag.unopay;
import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Withdraw extends BaseActivity {

    private RecyclerView recyclerView;
    private WithdrawAdapter adapter;
    private List<WithdrawItem> withdrawList,filteredList;

    private EditText messageEditText,toMember;
    private static String SEND_MESSAGE_URL;

    private int transferSelect = 1;
    private int seeSelect = 1;

    private Button withdrawbtn;
    private ImageView back_button;
    private ProgressBar withdraw_progress;
    private TextView member_name,available_fund,tpin;
    private int k = 0;
    private LinearLayout progressLayout,oopsLayout;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");
        String fund = sharedPreferences.getString("commission_wallet", "0.0");
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);

        available_fund = findViewById(R.id.available_fund);
        tpin = findViewById(R.id.tpin);
        available_fund.setText("₹ "+fund);
        progressLayout = findViewById(R.id.progress_layout);
        oopsLayout = findViewById(R.id.oops_layout);
        withdrawbtn=findViewById(R.id.withdrawbtn);
        withdraw_progress = findViewById(R.id.withdraw_progress);
        toMember = findViewById(R.id.toMember);
        member_name = findViewById(R.id.member_name);
        toMember.setVisibility(GONE);
        messageEditText = findViewById(R.id.withdraw_amount);
        withdrawbtn.setEnabled(true);
        withdrawbtn.setOnClickListener(v -> {
            if(tpin.getText().toString().trim().isEmpty()){
                tpin.setError("Enter T-PIN");
                return;
            }
            check(memberId,tpin.getText().toString().trim(),response -> {
                try {
                    boolean isValid = response.getBoolean("isValid");
                    if(isValid){
                        String message = messageEditText.getText().toString();
                        if(message.isEmpty()){
                            messageEditText.setError("Enter amount");
                            return;
                        } else if (Integer.valueOf(message)<100) {
                            messageEditText.setError("Minimum Withdraw Amount is 100");
                        } else if(k==1){
                            toMember.setError("Valid Member Required");
                            return;
                        }
                        else{
                            try {
                                withdrawbtn.setVisibility(GONE);
                                withdraw_progress.setVisibility(VISIBLE);
                                sendMessageAndFetchChat(memberId,message);
                            } catch (JSONException e) {
                                withdraw_progress.setVisibility(GONE);
                                withdrawbtn.setVisibility(VISIBLE);
                                Toast.makeText(this, "Try again later", Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else{
                        showError("Invalid T-PIN");
                    }
                }
                catch (Exception e){
                    showError("Some error occured");
                }
            });
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
                k=0;
                member_name.setVisibility(GONE);
                withdrawbtn.setEnabled(true);
                transferSelect = 1;
                toMember.setVisibility(GONE);
            }
            else if(checkedId==R.id.togglePerson){
                k=1;
                transferSelect = 2;
                toMember.setVisibility(VISIBLE);
            }
            else if(checkedId==R.id.toggleBank){
                k=0;
                member_name.setVisibility(GONE);
                withdrawbtn.setEnabled(true);
                transferSelect = 3;
                toMember.setVisibility(GONE);
            }
            else{
                k=0;
                member_name.setVisibility(GONE);
                withdrawbtn.setEnabled(true);
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
            filterDataByType();
        });

        toMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) {
                    k=1;
                    member_name.setVisibility(View.GONE);
                    toMember.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 8) {
                    checkSponsorID(toMember.getText().toString().trim());
                } else if (s.length() > 0) {
                    toMember.setError("ID should be 8 characters in length");
                }
            }
        });
        ;

        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        fetchData(memberId);
    }

    private void check(String memberId, String tpin, Response.Listener<JSONObject> responseListener) {
        String url = BuildConfig.api_url+"checktpin";

        JSONObject payload = new JSONObject();
        try {
            payload.put("member_id", memberId);
            payload.put("tpin", tpin);
        } catch (JSONException e) {
            showError("Error! Try Again");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                responseListener,
                error -> showError("Error! Try Again")){
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(request);
    }

    private void checkSponsorID(String sponsorID) {
        String baseUrl = BuildConfig.api_url+"checkSponserId";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("sponser_id", sponsorID);
        } catch (JSONException e) {
            Log.d("Sponsor Id: ",e.getMessage());
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(Withdraw.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                baseUrl,
                requestBody,
                response -> {
                    try {
                        boolean isValid = response.getBoolean("isValid");
                        String name = response.getString("sponserName");
                        if (isValid) {
                            k=0;
                            member_name.setTextColor(ContextCompat.getColor(this, R.color.endColor));
                            withdrawbtn.setEnabled(true);
                            member_name.setText(name);
                            member_name.setVisibility(View.VISIBLE);
                        } else {
                            if(transferSelect==2){
                                k=1;
                                withdrawbtn.setEnabled(false);
                            }else{
                                k=0;
                                withdrawbtn.setEnabled(true);
                            }
                            member_name.setTextColor(ContextCompat.getColor(this, R.color.reject));
                            member_name.setText("Invalid! Try Again");
                            member_name.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        if(transferSelect==2){
                            k=1;
                            withdrawbtn.setEnabled(false);
                        }else{
                            k=0;
                            withdrawbtn.setEnabled(true);
                        }
                        Log.d("Sponsor Id: ",e.getMessage());
                    }
                },
                error -> {
                    if(transferSelect==2){
                        k=1;
                        withdrawbtn.setEnabled(false);
                    }else{
                        k=0;
                        withdrawbtn.setEnabled(true);
                    }
                    Log.e("Bhenkeloada", "An error occurred: " + error.getMessage(), error);
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(jsonObjectRequest);
    }

    private void filterDataByType() {
        recyclerView.setVisibility(VISIBLE);
        if (filteredList != null) {
            filteredList.clear();
        } else {
            filteredList = new ArrayList<>();
        }
        if (seeSelect == 1) {
            filteredList.addAll(withdrawList);
        } else if(seeSelect == 2){
            for (WithdrawItem withdrawItem : withdrawList) {
                if (withdrawItem.type.equalsIgnoreCase("Self Transfer")) {
                    filteredList.add(withdrawItem);
                }
            }
        }
        else if(seeSelect == 3){
            for (WithdrawItem withdrawItem : withdrawList) {
                if (withdrawItem.type.equalsIgnoreCase("Money Transfer")) {
                    filteredList.add(withdrawItem);
                }
            }
        }
        else if(seeSelect == 4){
            for (WithdrawItem withdrawItem : withdrawList) {
                if (withdrawItem.type.equalsIgnoreCase("Bank Transfer")) {
                    filteredList.add(withdrawItem);
                }
            }
        }
        adapter.updateList(filteredList);

    }

    private void sendMessageAndFetchChat(final String memberId,String message) throws JSONException {
        JSONObject requestBody = new JSONObject();
        if(transferSelect==1){
            SEND_MESSAGE_URL = BuildConfig.api_url+"commissin-wallet-to-flexi-wallet";
            requestBody.put("member_id", memberId);
            requestBody.put("commission_amount", message);
        } else if (transferSelect==2) {
            SEND_MESSAGE_URL = BuildConfig.api_url+"person-to-person-transfer";
            requestBody.put("sender_member_id", memberId);
            String receiver_id = toMember.getText().toString();
            requestBody.put("receiver_member_id", receiver_id);
            requestBody.put("commission_amount", Integer.parseInt(message));
        } else if(transferSelect==3){
            SEND_MESSAGE_URL = BuildConfig.api_url+"user-withdraw-request";
            requestBody.put("member_id", memberId);
            requestBody.put("amount", message);
        }
        else{
            SEND_MESSAGE_URL = BuildConfig.api_url+"commissin-wallet-to-flexi-wallet";
            requestBody.put("member_id", memberId);
            requestBody.put("commission_amount", message);
        }
        Log.d("arsh_gendu",SEND_MESSAGE_URL);
        Log.d("arsh_gendu",requestBody.toString());
        JsonObjectRequest sendRequest = new JsonObjectRequest(
                Request.Method.POST,
                SEND_MESSAGE_URL,
                requestBody,
                response -> {
                    withdraw_progress.setVisibility(GONE);
                    withdrawbtn.setVisibility(VISIBLE);
                    try {
                        if(response.has("status") && response.getString("status").equals("true")){
                            messageEditText.setText("");
                            toMember.setText("");
                            String msg = "Done";
                            if(response.has("message"))
                                msg = response.getString("message");
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                            Log.d("arsh_gendu",msg);
                            fetchData(memberId);
                        }
                        else{
                            String msg = "Error";
                            if(response.has("message"))
                                msg = response.getString("message");
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                            Log.d("arsh_gendu",msg);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                    finally {
                        fetchWalletBalance(memberId);
                    }
                },
                error -> {
                    Log.d("arsh_gendu",error.getMessage());
                    Toast.makeText(Withdraw.this, "Error", Toast.LENGTH_SHORT).show();
                    withdraw_progress.setVisibility(GONE);
                    withdrawbtn.setVisibility(VISIBLE);
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

    private void fetchWalletBalance(String memberId){
        String url = BuildConfig.api_url+"user-wallet-wise-balance";
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);

        JSONObject requestBody = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,

                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("balance_response",response.toString());
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                String flexi_wallet = response.optString("flexi_wallet","0.0");
                                String commission_wallet = response.optString("commission_wallet","0.0");
                                String signup_bonus = response.optString("signup_bonus","0.0");
                                String today_income = response.optString("today_income","0");

                                flexi_wallet = String.format("%.2f", Double.parseDouble(flexi_wallet));
                                commission_wallet = String.format("%.2f", Double.parseDouble(commission_wallet));
                                signup_bonus = String.format("%.2f", Double.parseDouble(signup_bonus));

                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("flexi_wallet",flexi_wallet);
                                editor.putString("commission_wallet",commission_wallet);
                                editor.putString("signup_bonus",signup_bonus);
                                editor.putString("today_income",today_income);
                                editor.apply();

                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                }

        ) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token","");
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void fetchData(String memberId) {
        String url = BuildConfig.api_url+"get-user-withdraw-request";

        JSONObject postData = new JSONObject();
        try {
            postData.put("member_id", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("googlekitesting",memberId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                response -> {
                    progressLayout.setVisibility(GONE);
                    try {
                        if (response.getString("success").equals("true")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                String type = item.getString("type");
                                if(type.equalsIgnoreCase("Bank Transfer")){
                                    WithdrawItem withdrawItem = new WithdrawItem(
                                            item.getString("type"),
                                            formatAmount(item.getString("amount")),
                                            item.getString("date_time"),
                                            item.getString("status")
                                    );
                                    withdrawList.add(withdrawItem);
                                }
                                else if(type.equalsIgnoreCase("Money Transfer")){
                                    WithdrawItem withdrawItem = new WithdrawItem(
                                            item.getString("type"),
                                            formatAmount(item.getString("amount")),
                                            item.getString("date_time"),
                                            item.getString("receiver")
                                    );
                                    withdrawList.add(withdrawItem);
                                }
                                else{
                                    WithdrawItem withdrawItem = new WithdrawItem(
                                            item.getString("type"),
                                            formatAmount(item.getString("amount")),
                                            item.getString("date_time"),
                                            item.optString("status","NA")
                                    );
                                    withdrawList.add(withdrawItem);
                                }
                                recyclerView.setVisibility(VISIBLE);
                            }
                            withdrawList.sort((request1, request2) -> {
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
                            adapter.notifyDataSetChanged();
                        }
                        else
                            oopsLayout.setVisibility(VISIBLE);
                    } catch (JSONException e) {
                        oopsLayout.setVisibility(VISIBLE);
                        e.printStackTrace();
                    }
                    finally {
                        fetchWalletBalance(memberId);
                    }
                },
                error -> {
                    error.printStackTrace();
                    oopsLayout.setVisibility(VISIBLE);
                }){
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private String formatAmount(String amount) {
        try {
            double value = Double.parseDouble(amount);
            DecimalFormat decimalFormat = new DecimalFormat("₹0.00");
            return decimalFormat.format(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return amount;
        }
    }

    private void showError(String message) {
        Log.e("arsh", "Error: " + message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    static class WithdrawItem {
        String type;
        String amount;
        String dateTime;
        String status;

        public WithdrawItem(String type,String amount, String dateTime, String status) {
            this.type = type;
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

            holder.status.setVisibility(View.VISIBLE);
            holder.status_img.setVisibility(View.VISIBLE);
            holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accept)); // Default background color
            holder.status.setText("");
            holder.status.setTypeface(null, Typeface.NORMAL);
            holder.status.setTextSize(14);

            WithdrawItem item = withdrawItems.get(position);
            String formattedDate = formatDate(item.dateTime);
            String formattedTime = formatTime(item.dateTime);
            holder.typeText.setText(item.type);
            holder.amount.setText(item.amount);
            holder.time.setText(formattedTime);
            holder.date.setText(formattedDate);
            holder.status.setText(item.status);

            String status = item.status;

            switch (item.status.toLowerCase()) {
                case "pending":
                    holder.status.setText("Pending");
                    holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.pending_small));
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
                    break;

                case "done":
                    holder.status.setText("Approved");
                    holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.approved));
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accept));
                    break;

                case "rejected":
                    holder.status.setText("Rejected");
                    holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rejected));
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.reject));
                    break;

                case "na":
                    holder.status.setVisibility(GONE);
                    holder.status.setText("");
                    holder.status_img.setVisibility(View.GONE);
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accept));
                    break;

                default:
                    holder.status.setTypeface(null, Typeface.BOLD);
                    holder.status.setTextSize(20);
                    holder.status.setText("To: "+item.status);
                    holder.status_img.setVisibility(View.GONE);
                    holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accept));
                    break;
            }



        }

        public void updateList(List<WithdrawItem> newList) {
            this.withdrawItems = newList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return withdrawItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView  amount, date, status , time,typeText;

            ImageView status_img;
            LinearLayout statusColour;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                typeText = itemView.findViewById(R.id.typeTextView);
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