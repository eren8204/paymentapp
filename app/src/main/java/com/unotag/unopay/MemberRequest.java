package com.unotag.unopay;

import com.google.gson.annotations.SerializedName;

public class MemberRequest {
    @SerializedName("member_id")
    private String member_id;

    public MemberRequest(String member_id) {
        this.member_id = member_id;
    }

    public String getMemberId() {
        return member_id;
    }
}

