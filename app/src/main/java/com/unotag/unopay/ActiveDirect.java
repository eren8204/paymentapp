package com.unotag.unopay;

public class ActiveDirect {
    private int rank;
    private String username;
    private String phonenumber;
    private String level;
    private String memberId;
    private String membership;

    public ActiveDirect(int rank, String username, String phoneNumber, String level, String memberId, String membership) {
        this.rank = rank;
        this.username = username;
        this.phonenumber = phoneNumber;
        this.level = level;
        this.memberId = memberId;
        this.membership = membership;
    }

    // Getters
    public int getRank() {
        return rank;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMembership() {
        return membership;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phonenumber;
    }

    public String getLevel() {
        return level;
    }
}
