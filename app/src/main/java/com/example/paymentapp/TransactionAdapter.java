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
    public void updateData(List<JSONObject> newList) {
        this.transactionsList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }

        int adjustedPosition = position - 1;

        try {
            JSONObject transaction = transactionsList.get(adjustedPosition);

            holder.type.setText(transaction.getString("type"));
            holder.date.setText(formatDate(transaction.getString("created_at")));
            holder.amount.setText(transaction.getString("credit").equals("0") ? transaction.getString("debit") : transaction.getString("credit"));

            if (!transaction.getString("subType").equals("null")) {
                holder.subtype.setText(transaction.getString("subType"));
            } else {
                holder.subtype.setText("-");
            }

            if (!transaction.getString("recharge_to").equals("null")) {
                holder.rechargeto.setText(transaction.getString("recharge_to"));
            } else {
                holder.rechargeto.setText("-");
            }

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
        TextView type, date, amount, subtype, rechargeto;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            subtype = itemView.findViewById(R.id.subtype);
            rechargeto = itemView.findViewById(R.id.rechargeto);
        }
    }

    private String formatDate(String date) {
        return date.substring(0, 10);
    }
}
