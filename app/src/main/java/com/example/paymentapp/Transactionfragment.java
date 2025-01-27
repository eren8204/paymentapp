package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class Transactionfragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView errorTextView;

    private ProgressBar progressBar;

    private ImageView status_img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactionfragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        errorTextView = view.findViewById(R.id.errorTextView);

        progressBar = view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        status_img = view.findViewById(R.id.status_img);
        status_img.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        fetchTransactions(memberId);

        return view;
    }

    private void fetchTransactions(String memberId) {
        String url = BuildConfig.api_url + "selfTransactions";

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
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        boolean success = response.getBoolean("success");
                        if (success) {
                            recyclerView.setVisibility(View.VISIBLE);
                            errorTextView.setVisibility(View.GONE);


                            // Convert JSONArray to List<JSONObject>
                            JSONArray transactionsArray = response.getJSONObject("transactions").getJSONArray("data");
                            List<JSONObject> transactionsList = new ArrayList<>();
                            for (int i = 0; i < transactionsArray.length(); i++) {
                                transactionsList.add(transactionsArray.getJSONObject(i));
                            }

                            // Set RecyclerView Adapter
                            TransactionAdapter adapter = new TransactionAdapter(getContext(), transactionsList);
                            recyclerView.setAdapter(adapter);

                        } else {
                            recyclerView.setVisibility(View.GONE);
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("No transaction found.");
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        showError("Error parsing response.");
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    showError("Error: " + error.getMessage());
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    public static String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    private void showError(String message) {
        recyclerView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText("No transaction found.");
        status_img.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
