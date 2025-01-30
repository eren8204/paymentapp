package com.example.paymentapp;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transactionfragment extends Fragment {

    private EditText startDateEditText, endDateEditText;
    private Button filterButton;
    private List<JSONObject> transactionsList = new ArrayList<>();
    private TransactionAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout oops_layout,progress_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactionfragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        startDateEditText = view.findViewById(R.id.startDate);
        endDateEditText = view.findViewById(R.id.endDate);
        filterButton = view.findViewById(R.id.filterButton);
        oops_layout = view.findViewById(R.id.oops_layout);
        progress_layout = view.findViewById(R.id.progress_layout);

        progress_layout.setVisibility(VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        fetchTransactions(memberId);

        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText));
        filterButton.setOnClickListener(v -> filterTransactions());

        return view;
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
        String url = BuildConfig.api_url + "selfTransactions";

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
                            recyclerView.setAdapter(adapter);
                        } else {
                            recyclerView.setVisibility(GONE);
                            progress_layout.setVisibility(GONE);
                            oops_layout.setVisibility(VISIBLE);
                            showError("No transactions found.");
                        }
                    } catch (JSONException e) {
                        recyclerView.setVisibility(GONE);
                        progress_layout.setVisibility(GONE);
                        oops_layout.setVisibility(VISIBLE);
                        showError("Error parsing response.");
                    }
                },
                error ->{
                    recyclerView.setVisibility(GONE);
                    progress_layout.setVisibility(GONE);
                    oops_layout.setVisibility(VISIBLE);
                    showError("Error: " + error.getMessage());
                });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void filterTransactions() {
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
        List<JSONObject> filteredList = new ArrayList<>();

        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            for (JSONObject transaction : transactionsList) {
                String transactionDateStr = transaction.getString("created_at");
                Date transactionDate = sdf.parse(transactionDateStr);

                if (transactionDate != null && !transactionDate.before(startDate) && !transactionDate.after(endDate)) {
                    filteredList.add(transaction);
                }
            }

            if (filteredList.isEmpty()) {
                recyclerView.setVisibility(GONE);
                progress_layout.setVisibility(GONE);
                oops_layout.setVisibility(VISIBLE);
                adapter.updateData(filteredList);
                Toast.makeText(requireActivity(), "No Data To Display", Toast.LENGTH_SHORT).show();
            } else {
                recyclerView.setVisibility(VISIBLE);
                adapter.updateData(filteredList);
            }
        } catch (JSONException | ParseException e) {
            recyclerView.setVisibility(GONE);
            progress_layout.setVisibility(GONE);
            oops_layout.setVisibility(VISIBLE);
            showError("Error");
            Log.d("sorting_ki_error",e.toString());
            Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String message) {
        recyclerView.setVisibility(GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
