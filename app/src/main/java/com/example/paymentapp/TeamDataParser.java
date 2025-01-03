package com.example.paymentapp;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class TeamDataParser {

    public static Map<Integer, List<Member>> parseTeamData(String jsonResponse) {
        Map<Integer, List<Member>> teamData = new HashMap<>();
        try {
            Gson gson = new Gson();
            TeamResponse teamResponse = gson.fromJson(jsonResponse, TeamResponse.class);

            if (teamResponse.isSuccess()) {
                for (Member member : teamResponse.getTeamMembers()) {
                    int level = member.getLevel();
                    teamData.computeIfAbsent(level, k -> new ArrayList<>()).add(member);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return teamData;
    }
}
