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
import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        fetchTeamData(memberId);
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

    private void fetchTeamData(String member_id) {
        MemberRequest requestBody = new MemberRequest(member_id);
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = sharedPreferences.getString("token","");
        Call<TeamResponse> call = apiService.fetchTeamData("Bearer " + token,requestBody);

        call.enqueue(new Callback<TeamResponse>() {
            @Override
            public void onResponse(@NonNull Call<TeamResponse> call, @NonNull Response<TeamResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TeamResponse teamResponse = response.body();
                    if (teamResponse.isSuccess()) {
                        List<Member> teamMembersList = teamResponse.getTeamMembers();
                        Map<Integer, List<Member>> teamData = new HashMap<>();

                        for (Member member : teamMembersList) {
                            int level = member.getLevel();
                            teamData.computeIfAbsent(level, k -> new ArrayList<>()).add(member);
                        }
                        updateGraph(teamData);
                        //
                    } else {
                        Toast.makeText(RankGraph.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //
                    Toast.makeText(RankGraph.this, "No Team Found", Toast.LENGTH_SHORT).show();
                }
                //
            }

            @Override
            public void onFailure(@NonNull Call<TeamResponse> call, @NonNull Throwable t) {
                Toast.makeText(RankGraph.this, "Network Error", Toast.LENGTH_SHORT).show();
                double[] percentages = new double[7];
                String[] ranks = {"Opal", "Topaz", "Jasper", "Alexander",
                        "Diamond", "Blue Diamond", "Crown Diamond"};
                recyclerView.setLayoutManager(new LinearLayoutManager(RankGraph.this));
                RankGraphAdapter adapter = new RankGraphAdapter(ranks, percentages);
                recyclerView.setAdapter(adapter);
                progress_layout.setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
            }
        });
    }

    private void updateGraph(Map<Integer, List<Member>> teamData) {
        double[] percentages = new double[7];
        int[] required = {12, 48, 192, 768, 3072, 12288, 49152};
        for (int i = 0; i < 7; i++) {
            int level = i + 1;
            if (teamData.containsKey(level)) {
                int active = 0;
                int free = 0;
                List<Member> members = teamData.get(level);
                if (members != null) {
                    for (Member member : members) {
                        String membership = member.getMembership();
                        if (membership != null && membership.trim().equalsIgnoreCase("Free")) {
                            free++;
                        } else {
                            active++;
                        }
                    }
                }
                if(active>required[i])
                    percentages[i] = 100;
                else
                    percentages[i] = (double) active / required[i] * 100;
            } else {
                percentages[i] = 0;
            }
        }
        String[] ranks = {"Opal", "Topaz", "Jasper", "Alexander",
                "Diamond", "Blue Diamond", "Crown Diamond"};
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RankGraphAdapter adapter = new RankGraphAdapter(ranks, percentages);
        recyclerView.setAdapter(adapter);
        progress_layout.setVisibility(GONE);
        recyclerView.setVisibility(VISIBLE);

    }

}