package com.unotag.unopay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LevelStatusGraph extends AppCompatActivity {

    private int to=0,ato=0,fto=0;
    private ImageButton back_button;
    private TextView tb_memberid,tb_username,total,total_active,total_free;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private LevelStatusAdapter adapter;
    private List<LevelStatusItem> levelStatusList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_status_graph);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.endColor));
        back_button = findViewById(R.id.back_button);
        total = findViewById(R.id.total);
        total_active = findViewById(R.id.total_active);
        total_free = findViewById(R.id.total_free);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        pre(username,memberId);

        LottieAnimationView shareAnimation = findViewById(R.id.shareAnimation);
        shareAnimation.setOnClickListener(v->ShareUtil.shareApp(this, memberId));
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set adapter
        fetchTeamData(memberId);
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
                        updateLevelStatusList(teamData);
                        //
                    } else {
                        //
                        Toast.makeText(LevelStatusGraph.this, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //
                    Toast.makeText(LevelStatusGraph.this, "No Team Found", Toast.LENGTH_SHORT).show();
                }
                //
            }

            @Override
            public void onFailure(Call<TeamResponse> call, Throwable t) {
                Toast.makeText(LevelStatusGraph.this, "Network Error", Toast.LENGTH_SHORT).show();
                //
            }
        });
    }
    private void updateLevelStatusList(Map<Integer, List<Member>> teamData) {
        List<LevelStatusItem> levelStatusList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
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
                        assert membership != null;
                        Log.d("level_status_graph", membership);
                    }
                }
                ato+=active;
                fto+=free;
                to=ato+fto;
                levelStatusList.add(new LevelStatusItem(level, active, free));
            } else {
                Log.d("level_status_graph", level + " not found");
                levelStatusList.add(new LevelStatusItem(level, 0, 0));
            }
        }
        if (adapter == null) {
            adapter = new LevelStatusAdapter(this, levelStatusList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(levelStatusList);
        }
        if(total==null)
            total = findViewById(R.id.total);
        total.setText(String.valueOf(to));
        if(total_active==null)
            total_active = findViewById(R.id.total_active);
        total_active.setText(String.valueOf(ato));
        if(total_free==null)
            total_free = findViewById(R.id.total_free);
        total_free.setText(String.valueOf(fto));
    }

    private void pre(String username,String memberId){
        back_button.setOnClickListener(v -> finish());
        tb_username = findViewById(R.id.memberName);
        tb_memberid = findViewById(R.id.memberId);
        tb_username.setText(username);
        tb_memberid.setText(memberId);
    }
}