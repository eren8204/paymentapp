package com.example.paymentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private Context context;
    private List<Member> members;

    public MembersAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_member_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = members.get(position);
        String date = member.getDateOfJoining();
        String newDate = formatDate(date);
        holder.dateText.setText(newDate);
        holder.useridText.setText(member.getMember());
        holder.usernameTextView.setText(member.getUsername());
        holder.membershipTextView.setText(member.getMembership());
        if (member.getMembership().trim().equalsIgnoreCase("BASIC") ||
                member.getMembership().trim().equalsIgnoreCase("PREMIUM")) {
            holder.member_card.setBackgroundColor(context.getResources().getColor(R.color.end_bg));
        } else {
            holder.member_card.setBackgroundColor(context.getResources().getColor(R.color.start_bg));
        }
    }
    public static String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, membershipTextView,useridText,dateText;
        CardView member_card;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            member_card = itemView.findViewById(R.id.member_item_card);
            useridText = itemView.findViewById(R.id.userid);
            dateText = itemView.findViewById(R.id.date);
            usernameTextView = itemView.findViewById(R.id.username);
            membershipTextView = itemView.findViewById(R.id.membership);
        }
    }
}


class Member {

    @SerializedName("super_upline")
    private String superUpline;

    @SerializedName("member")
    private String member;

    @SerializedName("level")
    private int level;

    @SerializedName("username")
    private String username;

    @SerializedName("membership")
    private String membership;

    @SerializedName("date_of_joining")
    private String dateOfJoining;

    // Constructor, getters, and setters
    public Member() {
    }

    public Member(String superUpline, String member, int level, String username, String membership, String dateOfJoining) {
        this.superUpline = superUpline;
        this.member = member;
        this.level = level;
        this.username = username;
        this.membership = membership;
        this.dateOfJoining = dateOfJoining;
    }

    // Getters and setters
    public String getSuperUpline() {
        return superUpline;
    }

    public void setSuperUpline(String superUpline) {
        this.superUpline = superUpline;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }
}
