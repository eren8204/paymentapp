package com.unotag.unopay;
import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FundRequestAdapter extends RecyclerView.Adapter<FundRequestAdapter.FundRequestViewHolder> {

    private List<FundRequest> fundRequestList;
    private static final String INPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
    public FundRequestAdapter(List<FundRequest> fundRequestList) {
        this.fundRequestList = fundRequestList;
    }

    @NonNull
    @Override
    public FundRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fund_request, parent, false);
        return new FundRequestViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull FundRequestViewHolder holder, int position) {
        FundRequest request = fundRequestList.get(position);
        String formattedDate = formatDate(request.getTime_date());
        String formattedTime = formatTime(request.getTime_date());
        holder.toUpiIdTextView.setText(request.getTo_upi_id());
        holder.amountTextView.setText("Amount: ₹" + request.getAmount());
        holder.dateTimeTextView.setText("Date: " + formattedDate);
        holder.timeTextView.setText("Time: "+ formattedTime);
        holder.utr_textView.setText(request.getUtr_no());
        String status = request.getStatus().toLowerCase(Locale.ROOT);
        switch (status) {
            case "pending":
                holder.status_text.setText("Pending");
                holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.pending_small));
                holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey)); // Gray color for Pending
                break;
            case "approved":
                holder.status_text.setText("Approved");
                holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.approved));
                holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.accept)); // Set the status color (green)
                break;
            default:
                holder.status_text.setText("Rejected");
                holder.status_img.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.rejected));
                holder.statusColour.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.reject)); // Set the status color (red)
                break;
        }
    }

    private String formatDate(String dateString) {
        return formatDateTime(dateString, INPUT_FORMAT, DATE_FORMAT);
    }

    private String formatTime(String dateString) {
        return formatDateTime(dateString, INPUT_FORMAT, TIME_FORMAT);
    }

    private String formatDateTime(String dateString, String inputFormat, String outputFormat) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        // Input is in UTC
        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Output should be in IST
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            Date date = inputDateFormat.parse(dateString);
            if (date != null) {
                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            Log.e("DateFormat", "Error parsing date: " + dateString, e);
        }
        return dateString;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<FundRequest> newList) {
        this.fundRequestList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fundRequestList.size();
    }

    public static class FundRequestViewHolder extends RecyclerView.ViewHolder {
        TextView toUpiIdTextView, amountTextView, dateTimeTextView, status_text, timeTextView, utr_textView;
        LinearLayout statusColour;
        LinearLayout statusCard;
        ImageView status_img;

        public FundRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            statusColour = itemView.findViewById(R.id.statuscolour);
            toUpiIdTextView = itemView.findViewById(R.id.toUpiIdTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            statusCard = itemView.findViewById(R.id.cardview);
            status_text = itemView.findViewById(R.id.status_text);
            status_img = itemView.findViewById(R.id.status_img);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            utr_textView = itemView.findViewById(R.id.utr_no);
        }
    }
}
