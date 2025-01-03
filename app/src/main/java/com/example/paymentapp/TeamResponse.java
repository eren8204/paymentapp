package com.example.paymentapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TeamResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("teamMembers")
    private List<Member> teamMembers;

    public boolean isSuccess() {
        return success;
    }

    public List<Member> getTeamMembers() {
        return teamMembers;
    }
}