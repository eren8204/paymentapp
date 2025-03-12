package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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

    private static final String INPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
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
}
