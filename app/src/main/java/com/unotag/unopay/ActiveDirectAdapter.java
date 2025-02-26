package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveDirectAdapter extends RecyclerView.Adapter<ActiveDirectAdapter.ViewHolder> {

    private List<ActiveDirect> activeDirectList;
    private OnItemClickListener listener;
    public ActiveDirectAdapter(List<ActiveDirect> activeDirectList, OnItemClickListener listener) {
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
        if(activeDirect.getRank()>=1)
            holder.member_rank.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.endColor));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(activeDirect));
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
        TextView memberId,member_rank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberId = itemView.findViewById(R.id.active_memberid);
            member_rank = itemView.findViewById(R.id.active_member_rank);
        }
    }
}
