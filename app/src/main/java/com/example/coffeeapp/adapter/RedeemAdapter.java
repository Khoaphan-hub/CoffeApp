package com.example.coffeeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.Coffee;

import java.util.List;
import java.util.Locale;

public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.RedeemViewHolder> {

    private final List<Coffee> coffeeList;
    private final OnRedeemClickListener listener;

    // Giao diện (Interface) để bắt sự kiện click nút đổi điểm
    public interface OnRedeemClickListener {
        void onRedeemClick(Coffee coffee);
    }

    public RedeemAdapter(List<Coffee> coffeeList, OnRedeemClickListener listener) {
        this.coffeeList = coffeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RedeemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_redeem.xml mà chúng ta vừa tạo
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_redeem, parent, false);
        return new RedeemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RedeemViewHolder holder, int position) {
        Coffee coffee = coffeeList.get(position);

        // Đổ dữ liệu hình ảnh và tên
        holder.ivCoffee.setImageResource(coffee.getImageResId());
        holder.tvCoffeeName.setText(coffee.getName());

        // Tính toán số điểm cần để đổi (Giá * 300) và hiển thị lên nút
        int pointsNeeded = (int) (coffee.getBasePrice() * 300);
        holder.btnPoints.setText(String.format(Locale.getDefault(), "%d pts", pointsNeeded));

        // Bắt sự kiện khi người dùng bấm vào nút điểm
        holder.btnPoints.setOnClickListener(v -> listener.onRedeemClick(coffee));
    }

    @Override
    public int getItemCount() {
        return coffeeList.size();
    }

    // Class ViewHolder để ánh xạ các view trong item_redeem.xml
    static class RedeemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCoffee;
        TextView tvCoffeeName;
        TextView tvValidDate;
        Button btnPoints;

        public RedeemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCoffee = itemView.findViewById(R.id.ivCoffee);
            tvCoffeeName = itemView.findViewById(R.id.tvCoffeeName);
            tvValidDate = itemView.findViewById(R.id.tvValidDate);
            btnPoints = itemView.findViewById(R.id.btnPoints);
        }
    }
}