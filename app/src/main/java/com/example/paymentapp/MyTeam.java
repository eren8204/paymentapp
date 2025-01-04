package com.example.paymentapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeam extends AppCompatActivity {
    private ImageButton back_button;
    private TextView tb_memberid,tb_username;
    private RequestQueue requestQueue;
    private ExpandableListView expandableListView;
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        back_button = findViewById(R.id.back_button);
        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");

        pre(username,memberId);
        expandableListView = findViewById(R.id.expandableListView);
        fetchTeamData(memberId);
    }

    private void fetchTeamData(String member_id) {
        MemberRequest requestBody = new MemberRequest(member_id);
        Log.d("aditya_kumar", member_id);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<TeamResponse> call = apiService.fetchTeamData(requestBody);

        call.enqueue(new Callback<TeamResponse>() {
            @Override
            public void onResponse(Call<TeamResponse> call, Response<TeamResponse> response) {
                Log.d("aditya_kumar", response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    TeamResponse teamResponse = response.body();
                    if (teamResponse.isSuccess()) {
                        List<Member> teamMembersList = teamResponse.getTeamMembers();
                        Map<Integer, List<Member>> teamData = new HashMap<>();

                        for (Member member : teamMembersList) {
                            int level = member.getLevel();
                            teamData.computeIfAbsent(level, k -> new ArrayList<>()).add(member);
                        }

                        setupExpandableListView(teamData);
                    } else {
                        Toast.makeText(MyTeam.this, "Failed to Fetch Data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyTeam.this, "No Team Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamResponse> call, Throwable t) {
                Log.d("aditya_kumar", t.getMessage());
                Toast.makeText(MyTeam.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setupExpandableListView(Map<Integer, List<Member>> teamData) {
        TeamExpandableListAdapter adapter = new TeamExpandableListAdapter(this, teamData);
        expandableListView.setAdapter(adapter);
    }


    private void pre(String username,String memberId){
        back_button.setOnClickListener(v -> finish());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.startColor));
        tb_username = findViewById(R.id.memberName);
        tb_memberid = findViewById(R.id.memberId);
        tb_username.setText(username);
        tb_memberid.setText(memberId);
    }
}