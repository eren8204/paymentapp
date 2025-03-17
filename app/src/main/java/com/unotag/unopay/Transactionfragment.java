package com.unotag.unopay;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Transactionfragment extends Fragment {

    private EditText startDateEditText, endDateEditText;
    private Button filterButton;
    private List<JSONObject> transactionsList = new ArrayList<>();
    private List<JSONObject> filteredList = new ArrayList<>();
    private TransactionAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout oops_layout,progress_layout;
    private SharedPreferences sharedPreferences;
    private TextView opening_balance,total_cr,total_dr,closing_balance;
    private int seeSelect = 1;
    @Nullable
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactionfragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        startDateEditText = view.findViewById(R.id.startDate);
        endDateEditText = view.findViewById(R.id.endDate);
        filterButton = view.findViewById(R.id.filterButton);
        oops_layout = view.findViewById(R.id.oops_layout);
        progress_layout = view.findViewById(R.id.progress_layout);
        opening_balance = view.findViewById(R.id.opening_balance);
        total_cr = view.findViewById(R.id.total_cr);
        total_dr = view.findViewById(R.id.total_dr);
        closing_balance = view.findViewById(R.id.closing_balance);

        progress_layout.setVisibility(VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        fetchTransactions(memberId);

        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText));
        filterButton.setOnClickListener(v -> filterTransactions());

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        RadioButton toggleIncome = view.findViewById(R.id.toggleIncome);
        RadioButton toggleFund = view.findViewById(R.id.toggleFund);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.toggleIncome) {
                seeSelect = 1;
                recyclerView.setVisibility(VISIBLE);
            }
            else if (checkedId == R.id.toggleFund){
                seeSelect = 2;
            }
            else{
                seeSelect = 1;
            }
            recyclerView.setVisibility(VISIBLE);
            oops_layout.setVisibility(GONE);
            try {
                filterByType();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        return view;
    }
    @SuppressLint("DefaultLocale")
    private void filterByType() throws JSONException {
        if (filteredList != null) {
            filteredList.clear();
        } else {
            filteredList = new ArrayList<>();
        }
        if(seeSelect==1){
            for (JSONObject transaction : transactionsList) {
                if((transaction.getString("wallet_type").equalsIgnoreCase("commission_wallet"))){
                                filteredList.add(transaction);
                }
            }
        }
        else{
            for (JSONObject transaction : transactionsList) {
                if((transaction.getString("wallet_type").equalsIgnoreCase("flexi_wallet"))){
                    filteredList.add(transaction);
                }
            }
        }
        if(filteredList.isEmpty()){
            opening_balance.setText("0.0");
            closing_balance.setText("0.0");
            total_cr.setText("0.0");
            total_dr.setText("0.0");
            recyclerView.setVisibility(GONE);
            oops_layout.setVisibility(VISIBLE);
        }else{
            String closing = filteredList.get(0).getString("total_balance");
            String opening = filteredList.get(filteredList.size()-1).getString("total_balance");
            opening = sanitizeValue(opening);
            closing = sanitizeValue(closing);
            closing_balance.setText(closing);
            opening_balance.setText(opening);
            adapter.updateData(filteredList);
            double cr = 0;
            double dr = 0;
            for(JSONObject transaction : filteredList){
                cr+=Double.parseDouble(transaction.optString("credit","0.0"));
                dr+=Double.parseDouble(transaction.optString("debit","0.0"));
            }
            total_cr.setText(String.format("%.2f", Math.max(cr, 0.0)));
            total_dr.setText(String.format("%.2f", Math.max(dr, 0.0)));
            recyclerView.setAdapter(adapter);
        }
    }
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    editText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void fetchTransactions(String memberId) {
        String url = BuildConfig.api_url + "passbookTransactions";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("member_id", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            recyclerView.setVisibility(VISIBLE);
                            JSONArray transactionsArray = response.getJSONObject("transactions").getJSONArray("data");
                            transactionsList.clear();
                            for (int i = 0; i < transactionsArray.length(); i++) {
                                transactionsList.add(transactionsArray.getJSONObject(i));
                            }
                            if (transactionsList.isEmpty()){
                                recyclerView.setVisibility(GONE);
                                progress_layout.setVisibility(GONE);
                                oops_layout.setVisibility(VISIBLE);
                            }else{
                                progress_layout.setVisibility(GONE);
                                oops_layout.setVisibility(GONE);
                                recyclerView.setVisibility(VISIBLE);
                            }
                            adapter = new TransactionAdapter(getContext(), transactionsList);
                            filterByType();
                        } else {
                            recyclerView.setVisibility(GONE);
                            progress_layout.setVisibility(GONE);
                            oops_layout.setVisibility(VISIBLE);
                            showError("No transactions Found.");
                        }
                    } catch (JSONException e) {
                        recyclerView.setVisibility(GONE);
                        progress_layout.setVisibility(GONE);
                        oops_layout.setVisibility(VISIBLE);
                        showError("No Transactions Found");
                    }
                },
                error ->{
                    recyclerView.setVisibility(GONE);
                    progress_layout.setVisibility(GONE);
                    oops_layout.setVisibility(VISIBLE);
                    showError("No Transactions Found");
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
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    @SuppressLint("DefaultLocale")
    private void filterTransactions() {
        if(filteredList==null)
            filteredList = new ArrayList<>();
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            Toast.makeText(requireActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(GONE);
            progress_layout.setVisibility(GONE);
            oops_layout.setVisibility(VISIBLE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        List<JSONObject> TimefilteredList = new ArrayList<>();

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            for (JSONObject transaction : filteredList) {
                String transactionDateStr = transaction.getString("date_time");
                Date transactionDate = sdf.parse(transactionDateStr);

                if (transactionDate != null && !transactionDate.before(startDate) && !transactionDate.after(endDate)) {
                    TimefilteredList.add(transaction);
                }
            }

            if (TimefilteredList.isEmpty()) {
                recyclerView.setVisibility(GONE);
                progress_layout.setVisibility(GONE);
                oops_layout.setVisibility(VISIBLE);
                adapter.updateData(TimefilteredList);
                opening_balance.setText("0.0");
                closing_balance.setText("0.0");
                total_cr.setText("0.0");
                total_dr.setText("0.0");
                Toast.makeText(requireActivity(), "No data for this time period", Toast.LENGTH_SHORT).show();
            } else {
                String closing = TimefilteredList.get(0).getString("total_balance");
                String opening  = TimefilteredList.get(TimefilteredList.size()-1).getString("total_balance");
                closing = sanitizeValue(closing);
                opening = sanitizeValue(opening);
                closing_balance.setText(closing);
                opening_balance.setText(opening);
                adapter.updateData(TimefilteredList);
                double cr = 0;
                double dr = 0;
                for(JSONObject transaction : TimefilteredList){
                    cr+=Double.parseDouble(transaction.optString("credit","0.0"));
                    dr+=Double.parseDouble(transaction.optString("debit","0.0"));
                }
                total_cr.setText(String.format("%.2f", Math.max(cr, 0.0)));
                total_dr.setText(String.format("%.2f", Math.max(dr, 0.0)));
                recyclerView.setVisibility(VISIBLE);
            }
        } catch (JSONException | ParseException e) {
            recyclerView.setVisibility(GONE);
            progress_layout.setVisibility(GONE);
            oops_layout.setVisibility(VISIBLE);
            showError("No data for this time period");
            Log.d("sorting_ki_error",e.toString());
        }
    }

    private void showError(String message) {
        recyclerView.setVisibility(GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("DefaultLocale")
    private String sanitizeValue(String income) {
        try {
            double value = Double.parseDouble(income);
            return String.format("%.2f", Math.max(value, 0.0));
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }
}
