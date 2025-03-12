package com.unotag.unopay;// Change this according to your package name

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LevelStatusAdapter extends RecyclerView.Adapter<LevelStatusAdapter.ViewHolder> {
    private final List<LevelStatusItem> itemList;
    private final Context context;

    public LevelStatusAdapter(Context context, List<LevelStatusItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.level_status_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LevelStatusItem item = itemList.get(position);
        holder.index.setText(String.valueOf(item.getIndex()));
        holder.activeCount.setText(String.valueOf(item.getActiveCount()));
        holder.freeCount.setText(String.valueOf(item.getFreeCount()));

        int total = item.getActiveCount() + item.getFreeCount();
        holder.totalCount.setText(String.valueOf(total));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<LevelStatusItem> newList) {
        this.itemList.clear();
        this.itemList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView index, activeCount, freeCount, totalCount;
        View activeProgress, freeProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            activeCount = itemView.findViewById(R.id.active_count);
            freeCount = itemView.findViewById(R.id.free_count);
            activeProgress = itemView.findViewById(R.id.activeProgress);
            freeProgress = itemView.findViewById(R.id.freeProgress);
            totalCount = itemView.findViewById(R.id.total_count);

        }
    }
}
