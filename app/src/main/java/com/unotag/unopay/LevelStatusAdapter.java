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

        // Get total count
        int total = item.getActiveCount() + item.getFreeCount();

        // **RESET Views to Avoid Wrong Recycled Widths**
        holder.activeProgress.setLayoutParams(new RelativeLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        holder.freeProgress.setLayoutParams(new RelativeLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));

        holder.activeProgress.requestLayout();
        holder.freeProgress.requestLayout();

        // Wait until the layout is measured to get the correct width
        holder.progressContainer.post(() -> {
            int totalWidth = holder.progressContainer.getWidth();

            if (totalWidth > 0) {
                int activeWidth, freeWidth;

                if (total == 0 || item.getActiveCount() == item.getFreeCount()) {
                    // **Equal or Zero Case: Split 50-50**
                    activeWidth = totalWidth / 2;
                    freeWidth = totalWidth / 2;
                } else {
                    // **Proportional Width Calculation**
                    float activeRatio = (float) item.getActiveCount() / total;
                    activeWidth = (int) (totalWidth * activeRatio);
                    freeWidth = totalWidth - activeWidth;
                }

                // **Apply the calculated widths**
                RelativeLayout.LayoutParams activeParams = new RelativeLayout.LayoutParams(activeWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                holder.activeProgress.setLayoutParams(activeParams);

                RelativeLayout.LayoutParams freeParams = new RelativeLayout.LayoutParams(freeWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                freeParams.addRule(RelativeLayout.END_OF, holder.activeProgress.getId());
                holder.freeProgress.setLayoutParams(freeParams);
            }
        });

        // **Dynamic Coloring by Level**
        int[] levelColors = {
                R.color.level_1, R.color.level_2, R.color.level_3, R.color.level_4,
                R.color.level_5, R.color.level_6, R.color.level_7, R.color.level_8,
                R.color.level_9, R.color.level_10
        };

        int colorIndex = (item.getIndex() - 1) % levelColors.length;
        int backgroundColor = ContextCompat.getColor(holder.itemView.getContext(), levelColors[colorIndex]);

        holder.index.setBackgroundColor(backgroundColor);
        holder.activeProgress.setBackgroundColor(backgroundColor);
        holder.freeProgress.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.msg_grey));
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
        TextView index, activeCount, freeCount;
        View activeProgress, freeProgress;
        RelativeLayout progressContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            activeCount = itemView.findViewById(R.id.active_count);
            freeCount = itemView.findViewById(R.id.free_count);
            activeProgress = itemView.findViewById(R.id.activeProgress);
            freeProgress = itemView.findViewById(R.id.freeProgress);
            progressContainer = itemView.findViewById(R.id.progressContainer);
        }
    }
}
