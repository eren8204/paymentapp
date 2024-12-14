package com.example.paymentapp;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FundRequestAdapter extends RecyclerView.Adapter<FundRequestAdapter.FundRequestViewHolder> {

    private List<FundRequest> fundRequestList;

    public FundRequestAdapter(List<FundRequest> fundRequestList) {
        this.fundRequestList = fundRequestList;
    }

    @NonNull
    @Override
    public FundRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fund_request, parent, false);
        return new FundRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FundRequestViewHolder holder, int position) {
        FundRequest request = fundRequestList.get(position);
        String formattedDate = formatDate(request.getTime_date());
        holder.toUpiIdTextView.setText(request.getTo_upi_id());
        holder.amountTextView.setText("Amount: " + request.getAmount());
        holder.dateTimeTextView.setText("Date: " + formattedDate);
        holder.statusTextView.setText("Status: " + request.getStatus());


        String status = request.getStatus().toLowerCase(Locale.ROOT);
        switch (status) {
            case "pending":
                holder.statusCard.setBackgroundResource(R.drawable.statuscardgrey); // Background for Pending
                holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey)); // Gray color for Pending
                break;
            case "approved":
                holder.statusCard.setBackgroundResource(R.drawable.statuscardgreen); // Background for Approved
                holder.statusColour.setBackgroundColor(Color.GREEN); // Set the status color (green)
                break;
            default:
                holder.statusCard.setBackgroundResource(R.drawable.statuscardred); // Default background for other statuses
                holder.statusColour.setBackgroundColor(Color.RED); // Set the status color (red)
                break;
        }
    }

    private void updateStatusCard(FundRequestViewHolder holder, int color) {
        Drawable statusCardDrawable = holder.statusCard.getBackground();
        if (statusCardDrawable != null) {
            DrawableCompat.setTint(statusCardDrawable, color);
            holder.statusCard.setBackground(statusCardDrawable);
        }
    }
    private String formatDate(String dateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputFormat = "dd MMM yyyy";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Return the original string if parsing fails
        }
    }
    @Override
    public int getItemCount() {
        return fundRequestList.size();
    }

    public static class FundRequestViewHolder extends RecyclerView.ViewHolder {
        TextView toUpiIdTextView, amountTextView, dateTimeTextView, statusTextView;
        LinearLayout statusColour;
        LinearLayout statusCard;

        public FundRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            statusColour = itemView.findViewById(R.id.statuscolour);
            toUpiIdTextView = itemView.findViewById(R.id.toUpiIdTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            statusCard = itemView.findViewById(R.id.cardview);
        }
    }
}
