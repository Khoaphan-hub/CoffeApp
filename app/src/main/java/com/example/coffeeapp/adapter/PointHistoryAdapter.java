package com.example.coffeeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.PointTransaction;

import java.util.List;
import java.util.Locale;

public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.PointViewHolder> {

    private final List<PointTransaction> transactions;

    public PointHistoryAdapter(List<PointTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward_history, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        PointTransaction tx = transactions.get(position);
        holder.tvSource.setText(tx.getSource());
        holder.tvDate.setText(tx.getDate());
        
        if (tx.getAmount() >= 0) {
            holder.tvAmount.setText(String.format(Locale.getDefault(), "+%d", tx.getAmount()));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));
        } else {
            holder.tvAmount.setText(String.valueOf(tx.getAmount()));
            holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class PointViewHolder extends RecyclerView.ViewHolder {
        TextView tvSource, tvDate, tvAmount;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSource = itemView.findViewById(R.id.tvPointSource);
            tvDate = itemView.findViewById(R.id.tvPointDate);
            tvAmount = itemView.findViewById(R.id.tvPointAmount);
        }
    }
}
