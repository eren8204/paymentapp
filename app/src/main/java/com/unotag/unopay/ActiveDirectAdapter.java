package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveDirectAdapter extends RecyclerView.Adapter<ActiveDirectAdapter.ViewHolder> {

    private final Context context;
    private List<ActiveDirect> activeDirectList;
    private OnItemClickListener listener;
    public ActiveDirectAdapter(Context context,List<ActiveDirect> activeDirectList, OnItemClickListener listener) {
        this.context = context;
        this.activeDirectList = activeDirectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_direct_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] rank = {"Not Achieved", "Opal", "Topaz", "Jasper","Alexander","Diamond","Blue Diamond","Crown Diamond"};
        ActiveDirect activeDirect = activeDirectList.get(position);
        holder.memberId.setText(activeDirect.getMemberId());
        holder.member_rank.setText(rank[activeDirect.getRank()]);
        holder.number.setText(activeDirect.getPhoneNumber());
        holder.username.setText(activeDirect.getUsername());
        holder.active_member_level.setText("Level: "+activeDirect.getLevel());
        String phone = activeDirect.getPhoneNumber();
        if(activeDirect.getRank()>=1)
            holder.member_rank.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.endColor));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(activeDirect));
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

    @Override
    public int getItemCount() {
        return activeDirectList.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ActiveDirect> newList) {
        this.activeDirectList = newList;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberId,member_rank,number,username,active_member_level;
        ImageButton call_member,wsp_member;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberId = itemView.findViewById(R.id.active_memberid);
            member_rank = itemView.findViewById(R.id.active_member_rank);
            number = itemView.findViewById(R.id.number);
            username = itemView.findViewById(R.id.username);
            active_member_level = itemView.findViewById(R.id.active_member_level);
            call_member = itemView.findViewById(R.id.call_sponsor);
            wsp_member = itemView.findViewById(R.id.wsp_sponsor);
        }
    }
}
