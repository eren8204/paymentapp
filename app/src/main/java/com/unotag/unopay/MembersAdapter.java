package com.unotag.unopay;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    private final Context context;
    private final List<Member> members;

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
        String doa = member.getDoa();
        String phone = member.getPhone();
        holder.doa_layout.setVisibility(VISIBLE);
        if(doa.equals("0"))
            holder.doa_layout.setVisibility(GONE);
        else{
            String newDoa = formatDate(doa);
            holder.dateOfActivation.setText(newDoa);
            holder.doa_layout.setVisibility(VISIBLE);
        }
        String newDate = formatDate(date);

        holder.dateText.setText(newDate);
        holder.useridText.setText(member.getMember());
        holder.usernameTextView.setText(member.getUsername().toUpperCase());
        holder.membershipTextView.setText(member.getMembership());
        holder.number.setText(phone);


        if (member.getMembership().trim().equalsIgnoreCase("BASIC")) {
            holder.membershipTextView.setTextColor(context.getResources().getColor(R.color.endColor));
            holder.member_card.setBackgroundColor(context.getResources().getColor(R.color.end_bg));
        }
        else if(member.getMembership().trim().equalsIgnoreCase("PREMIUM")){
            holder.membershipTextView.setTextColor(context.getResources().getColor(R.color.accept));
            holder.member_card.setBackgroundColor(context.getResources().getColor(R.color.green_bg));
        }else {
            holder.membershipTextView.setTextColor(context.getResources().getColor(R.color.startColor));
            holder.member_card.setBackgroundColor(context.getResources().getColor(R.color.start_bg));
        }

        holder.call_member.setOnClickListener(v-> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.wsp_member.setOnClickListener(v-> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + "+91"+phone));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
    public static String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            assert date != null;
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
        LinearLayout doa_layout;
        TextView usernameTextView, membershipTextView,useridText,dateText,number,dateOfActivation;
        ImageButton call_member,wsp_member;
        CardView member_card;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            member_card = itemView.findViewById(R.id.member_item_card);
            useridText = itemView.findViewById(R.id.userid);
            dateText = itemView.findViewById(R.id.date);
            usernameTextView = itemView.findViewById(R.id.username);
            membershipTextView = itemView.findViewById(R.id.membership);
            number = itemView.findViewById(R.id.number);
            call_member = itemView.findViewById(R.id.call_sponsor);
            wsp_member = itemView.findViewById(R.id.wsp_sponsor);
            doa_layout = itemView.findViewById(R.id.doa_layout);
            dateOfActivation = itemView.findViewById(R.id.date_of_activation);
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

    @SerializedName("activationDate")
    private String activationDate;

    @SerializedName("phone")
    private String phone;

    // Constructor, getters, and setters
    public Member() {
    }

    public Member(String superUpline, String member, int level, String username, String membership, String dateOfJoining, String phone, String activationDate) {
        this.superUpline = superUpline;
        this.member = member;
        this.level = level;
        this.username = username;
        this.membership = membership;
        this.dateOfJoining = dateOfJoining;
        this.phone = phone;
        this.activationDate = activationDate;
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

    public String getDoa(){
        return activationDate != null ? activationDate : "0";
    }

    public String getPhone(){
        return phone;
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
