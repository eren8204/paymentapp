package com.example.paymentapp;

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

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<JSONObject> transactionsList;
    private final Context context;

    // Constructor to initialize the list and context
    public TransactionAdapter(Context context, List<JSONObject> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_transaction layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        try {
            // Get the current transaction
            JSONObject transaction = transactionsList.get(position);

            // Set the type
            holder.type.setText(transaction.getString("type"));
            if (transaction.getString("type").equals("Add Fund Request")) {
                holder.type.setText("Fund Request");
            }

            // Set the date (formatted)
            holder.date.setText(formatDate(transaction.getString("created_at")));

            // Set the amount (credit or debit)
            if (transaction.getString("credit").equals("0")) {
                holder.amount.setText(transaction.getString("debit"));
            } else {
                holder.amount.setText(transaction.getString("credit"));
            }

            // Handle the subtype visibility
            if (!transaction.getString("subType").equals("null")) {
                holder.subtype.setText(transaction.getString("subType"));
                holder.subtype.setVisibility(View.VISIBLE);
            } else {
                holder.subtype.setVisibility(View.GONE);
            }

            // Handle recharge_to visibility and background color
            if (!transaction.getString("recharge_to").equals("null")) {
                holder.rechargeto.setText(transaction.getString("recharge_to"));
                holder.rechargeto.setVisibility(View.VISIBLE);
                holder.statusColour.setBackgroundColor(context.getResources().getColor(R.color.reject));
            } else {
                holder.rechargeto.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    // ViewHolder class for transaction items
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView type, date, amount, subtype, rechargeto;
        LinearLayout statusColour;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components
            type = itemView.findViewById(R.id.type);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            subtype = itemView.findViewById(R.id.subtype);
            rechargeto = itemView.findViewById(R.id.rechargeto);
            statusColour = itemView.findViewById(R.id.statuscolour);
        }
    }

    // Helper method to format the date
    private String formatDate(String date) {
        // Placeholder: Add your actual date formatting logic here
        return date.substring(0, 10); // Example: Return only the date part
    }
}
