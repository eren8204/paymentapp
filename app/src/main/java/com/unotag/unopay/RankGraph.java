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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RankGraph extends BaseActivity {
    private SharedPreferences sharedPreferences;
    private ImageButton back_button;
    private LinearLayout progress_layout;
    private int team_size = 0;
    private RecyclerView recyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_graph);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        //
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        pre();
        //
        progress_layout = findViewById(R.id.progress_layout);
        recyclerView = findViewById(R.id.recycler_view);
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));
        try {
            getActiveTeam(memberId);
        } catch (JSONException e) {
            progress_layout.setVisibility(GONE);
            recyclerView.setVisibility(VISIBLE);
            throw new RuntimeException(e);
        }
    }
    private void pre(){
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String username = sharedPreferences.getString("username", "Hello, !");
        TextView memberName = findViewById(R.id.memberName);
        TextView userId = findViewById(R.id.memberId);
        memberName.setText(username);
        userId.setText(memberId);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v->finish());
    }

    private void getActiveTeam(String memberId) throws JSONException {
        String url = BuildConfig.api_url + "get-active-team-no";
        JSONObject requestBody = new JSONObject();
        requestBody.put("member_id", memberId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        Log.d("active_team",response.toString());
                        double[] percentages = new double[7];
                        if (response.has("active_team")) {
                            team_size = response.getInt("active_team");
                            int[] required = {12, 48, 192, 768, 3072, 12288, 49152};
                            for (int i = 0; i < 7; i++) {
                                if (team_size >= required[i])
                                    percentages[i] = 100;
                                else
                                    percentages[i] = (double) team_size / required[i] * 100;
                                Log.d("active_number", String.valueOf(percentages[i]));
                            }
                        }
                        String[] ranks = {"Opal", "Topaz", "Jasper", "Alexander",
                                "Diamond", "Blue Diamond", "Crown Diamond"};
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        RankGraphAdapter adapter = new RankGraphAdapter(ranks, percentages);
                        recyclerView.setAdapter(adapter);
                        progress_layout.setVisibility(GONE);
                        recyclerView.setVisibility(VISIBLE);

                    } catch (Exception e) {
                        Log.d("active_team", response.toString());
                        progress_layout.setVisibility(GONE);
                        recyclerView.setVisibility(VISIBLE);
                    }
                },
                error -> {
                    Log.d("active_team", error.toString());
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("active_team", "Error: " + statusCode + " - " + responseBody);
                    } else {
                        Log.e("active_team", "Unknown error", error);
                    }
                    progress_layout.setVisibility(GONE);
                    recyclerView.setVisibility(VISIBLE);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString("token", "");
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}