package com.example.paymentapp;

public class ActiveDirect {
    private int rank;
    private String memberId;
    private String membership;

    public ActiveDirect(int rank, String memberId, String membership) {
        this.rank = rank;
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
}
