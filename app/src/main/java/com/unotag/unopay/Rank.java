package com.unotag.unopay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rank extends BaseActivity {
    String[] ranks = {"Not Achieved", "Opal", "Topaz", "Jasper", "Alexander", "Diamond", "Blue Diamond", "Crown Diamond"};
    private int selectedRank = -1;
    private RecyclerView recyclerView;
    private ActiveDirectAdapter adapter;
    private List<ActiveDirect> activeDirectList;
    private List<ActiveDirect> filteredList;
    private RequestQueue requestQueue;
    private int progress=0;
    private TextView progress_text,rank_ka_name,ranked_members_count;
    private LinearLayout progress_layout,oops_layout;
    private SharedPreferences sharedPreferences;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));

        progress_text = findViewById(R.id.progress_text);
        progress_layout = findViewById(R.id.progress_layout);
        oops_layout = findViewById(R.id.oops_layout);
        rank_ka_name = findViewById(R.id.rank_ka_name);
        ranked_members_count = findViewById(R.id.ranked_members_count);
        progress_layout.setVisibility(VISIBLE);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");
        pre(username,memberId);

        recyclerView = findViewById(R.id.recyclerView);
        activeDirectList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new ActiveDirectAdapter(activeDirectList, item -> {
            Toast.makeText(this, "Opening details for: " + item.getMemberId(), Toast.LENGTH_SHORT).show();

        });


        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        requestQueue = Volley.newRequestQueue(this);
        fetchActiveDirects(memberId);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        RadioButton toggleAll = findViewById(R.id.toggleAll);
        RadioButton toggleOpal = findViewById(R.id.toggleOpal);
        RadioButton toggleTopaz = findViewById(R.id.toggleTopaz);
        RadioButton toggleJasper = findViewById(R.id.toggleJasper);
        RadioButton toggleAlexander = findViewById(R.id.toggleAlexander);
        RadioButton toggleDiamond = findViewById(R.id.toggleDiamond);
        RadioButton toggleBlueDiamond = findViewById(R.id.toggleBlueDiamond);
        RadioButton toggleCrownDiamond = findViewById(R.id.toggleCrownDiamond);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.toggleAll) {
                selectedRank = -1;
            } else if (checkedId == R.id.toggleOpal) {
                selectedRank = 1;
            } else if (checkedId == R.id.toggleTopaz) {
                selectedRank = 2;
            } else if (checkedId == R.id.toggleJasper) {
                selectedRank = 3;
            } else if (checkedId == R.id.toggleAlexander) {
                selectedRank = 4;
            } else if (checkedId == R.id.toggleDiamond) {
                selectedRank = 5;
            }else if (checkedId == R.id.toggleBlueDiamond) {
                selectedRank = 6;
            }else if (checkedId == R.id.toggleCrownDiamond) {
                selectedRank = 7;
            }
            else {
                selectedRank = -1;
            }
            filterDataByRank();
        });

    }

    @SuppressLint("SetTextI18n")
    private void filterDataByRank() {
        recyclerView.setVisibility(VISIBLE);
        if (filteredList != null) {
            filteredList.clear();
        } else {
            filteredList = new ArrayList<>();
        }
        if (selectedRank == -1) {
            filteredList.addAll(activeDirectList);
            rank_ka_name.setText("All");
        } else {
            rank_ka_name.setText(ranks[selectedRank]);
            for (ActiveDirect activeDirect : activeDirectList) {
                if (activeDirect.getRank() == selectedRank) {
                    filteredList.add(activeDirect);
                }
            }
        }
        if(filteredList.isEmpty()){
            recyclerView.setVisibility(GONE);
            oops_layout.setVisibility(VISIBLE);
        }
        adapter.updateList(filteredList);
        int count = adapter.getItemCount();
        ranked_members_count.setText(String.valueOf(count));
    }


    private void fetchActiveDirects(String memberId) {
        String url = BuildConfig.api_url + "getUserRank";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("member_id", memberId);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating request body", Toast.LENGTH_SHORT).show();
            return;
        }
        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"}) JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    Log.d("Response", response.toString());

                    try {
                        progress_layout.setVisibility(GONE);
                        if(response.has("rank_no")){
                            int n = response.getInt("rank_no");
                            if(n==0)
                                progress_text.setTextColor(ContextCompat.getColor(this, R.color.msg_grey));
                            else
                                progress_text.setTextColor(ContextCompat.getColor(this, R.color.endColor));
                            progress_text.setText(ranks[n]);
                        }
                        if (response.has("active_directs_list")) {
                            JSONArray activeDirectsArray = response.getJSONArray("active_directs_list");
                            activeDirectList.clear();
                            for (int i = 0; i < activeDirectsArray.length(); i++) {
                                JSONObject activeDirectObject = activeDirectsArray.getJSONObject(i);
                                int rank = activeDirectObject.getInt("rank");
                                String activeMemberId = activeDirectObject.getString("member_id");
                                String membership = activeDirectObject.getString("membership");
                                activeDirectList.add(new ActiveDirect(rank, activeMemberId, membership));
                                recyclerView.setVisibility(VISIBLE);
                            }
                            Log.d("ActiveDirectListSize", "Size: " + activeDirectList.size());
                            if (activeDirectList.isEmpty()) {
                                oops_layout.setVisibility(VISIBLE);
                                Toast.makeText(this, "No data to display", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                            int count = adapter.getItemCount();
                            ranked_members_count.setText(String.valueOf(count));
                        } else {
                            oops_layout.setVisibility(VISIBLE);
                            Toast.makeText(this, "No Active Directs Found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        oops_layout.setVisibility(VISIBLE);
                        Log.e("rank_err", "JSON Parsing Error: " + e);
                        Toast.makeText(this, "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    oops_layout.setVisibility(VISIBLE);
                    Log.e("rank_err1", "Volley Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("rank_err1", "Status Code: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data);
                            Log.e("rank_err1", "Response Body: " + responseBody);
                        }
                    }
                    Toast.makeText(this, "Error Fetching Data", Toast.LENGTH_SHORT).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void pre(String name, String id) {
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            finish();
        });
        TextView tb_username = findViewById(R.id.memberName);
        TextView tb_memberid = findViewById(R.id.memberId);
        tb_username.setText(name);
        tb_memberid.setText(id);
    }
}