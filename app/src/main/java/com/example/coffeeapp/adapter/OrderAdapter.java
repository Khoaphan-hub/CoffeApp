package com.example.coffeeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.Order;

import java.util.List;
import java.util.Locale;

/**
 * Adapter for order history and active orders.
 * Handles different UI states for Ongoing vs History tabs.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final OnOrderInteractionListener listener;

    public interface OnOrderInteractionListener {
        void onMarkComplete(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderInteractionListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvDate.setText(order.getDate());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", order.getTotalPrice()));
        holder.tvItems.setText(order.getItemsSummary());
        holder.tvStatus.setText(order.getStatus());

        // Cập nhật tên và hình ảnh
        holder.tvUserName.setText(order.getUserName());
        if (order.getImageResId() != 0) {
            holder.ivImage.setImageResource(order.getImageResId());
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_cart);
        }

        if (order.getStatus().equals("Ongoing")) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_ongoing));
            holder.btnMarkComplete.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_history));
            holder.btnMarkComplete.setVisibility(View.GONE);
        }

        holder.btnMarkComplete.setOnClickListener(v -> listener.onMarkComplete(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        // ĐÃ KHAI BÁO THÊM tvUserName và ivImage
        TextView tvDate, tvStatus, tvItems, tvPrice, tvUserName;
        ImageView ivImage;
        Button btnMarkComplete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvItems = itemView.findViewById(R.id.tvOrderItems);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            btnMarkComplete = itemView.findViewById(R.id.btnMarkComplete);

            // ĐÃ THÊM ÁNH XẠ VIEW
            tvUserName = itemView.findViewById(R.id.tvOrderUserName);
            ivImage = itemView.findViewById(R.id.ivOrderImage);
        }
    }
}