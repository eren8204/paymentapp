package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<JSONObject> transactionsList;
    private final Context context;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public TransactionAdapter(Context context, List<JSONObject> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.header_transaction, parent, false);
            return new TransactionViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(itemView);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<JSONObject> newList) {
        this.transactionsList = newList;
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }

        int adjustedPosition = position - 1;

        try {
            JSONObject transaction = transactionsList.get(adjustedPosition);
            String desc = transaction.getString("type");
            String time = formatTime(transaction.getString("date_time"));
            String date = formatDate(transaction.getString("date_time"));
            holder.date.setText(date+" "+time);
            holder.amount.setText(transaction.getString("total_balance"));

            if (!transaction.getString("subType").equals("null")) {
                desc = desc + " - " + transaction.getString("subType");
            }

            if (!transaction.getString("recharge_to").equals("null")) {
                desc = desc + " - " + transaction.getString("recharge_to");
            }
            holder.type.setText(desc);
            holder.cr.setText(transaction.getString("credit"));
            holder.dr.setText(transaction.getString("debit"));
            holder.sno.setText(String.valueOf(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return transactionsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView type, date, amount, cr, dr, sno;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            cr = itemView.findViewById(R.id.cr);
            dr = itemView.findViewById(R.id.dr);
            sno = itemView.findViewById(R.id.sno);
        }
    }

    public String formatTime(String utcDateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        String outputFormat = "hh:mm a";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            Date date = inputDateFormat.parse(utcDateString);
            if (date != null) {

                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return utcDateString;
    }
    public String formatDate(String dateString) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputFormat = "dd MMM yyyy";
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = inputDateFormat.parse(dateString);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
