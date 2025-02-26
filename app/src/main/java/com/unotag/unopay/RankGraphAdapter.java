package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RankGraphAdapter extends RecyclerView.Adapter<RankGraphAdapter.ViewHolder> {

    private final String[] ranks;
    private final double[] percentages;

    public RankGraphAdapter(String[] ranks, double[] percentages) {
        this.ranks = ranks;
        this.percentages = percentages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achiever_graph, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rankName.setText(ranks[position]);
        holder.rankPercentage.setText((int)percentages[position] + "%");

        holder.itemView.post(() -> {
            int maxWidth = holder.progressColor.getParent() instanceof ViewGroup
                    ? ((ViewGroup) holder.progressColor.getParent()).getWidth()
                    : holder.itemView.getWidth();

            // Scale the progress bar width based on percentage
            ViewGroup.LayoutParams params = holder.progressColor.getLayoutParams();
            params.width = (int) (percentages[position] / 100.0 * maxWidth);
            holder.progressColor.setLayoutParams(params);
        });

        // Set a different progress bar color for each rank if desired
        int color;
        switch (position) {
            case 0:
                color = Color.parseColor("#FFA500"); // Opal
                break;
            case 1:
                color = Color.parseColor("#FFFF00"); // Topaz
                break;
            case 2:
                color = Color.parseColor("#FF4500"); // Jasper
                break;
            case 3:
                color = Color.parseColor("#f081ec"); // Alexander
                break;
            case 4:
                color = Color.parseColor("#00BFFF"); // Diamond
                break;
            case 5:
                color = Color.parseColor("#b465fc"); // Blue Diamond
                break;
            case 6:
                color = Color.parseColor("#FFD700"); // Crown Diamond
                break;
            default:
                color = Color.GRAY; // Default
        }
        holder.progressColor.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return ranks.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankName;
        View progressColor;
        TextView rankPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankName = itemView.findViewById(R.id.rank_name);
            progressColor = itemView.findViewById(R.id.progress_color);
            rankPercentage = itemView.findViewById(R.id.rank_percentage);
        }
    }
}
