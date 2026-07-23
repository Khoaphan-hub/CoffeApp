package com.example.coffeeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.Coffee;

import java.util.List;
import java.util.Locale;

/**
 * Universal adapter for displaying coffee products.
 * Used in HomeFragment and RedeemActivity.
 */
public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder> {

    private final List<Coffee> coffeeList;
    private final OnCoffeeClickListener listener;

    public interface OnCoffeeClickListener {
        void onCoffeeClick(Coffee coffee);
    }

    public CoffeeAdapter(List<Coffee> coffeeList, OnCoffeeClickListener listener) {
        this.coffeeList = coffeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CoffeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coffee, parent, false);
        return new CoffeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoffeeViewHolder holder, int position) {
        Coffee coffee = coffeeList.get(position);
        holder.tvName.setText(coffee.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", coffee.getBasePrice()));
        holder.ivImage.setImageResource(coffee.getImageResId());
        
        holder.itemView.setOnClickListener(v -> listener.onCoffeeClick(coffee));
    }

    @Override
    public int getItemCount() {
        return coffeeList.size();
    }

    public static class CoffeeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice;

        public CoffeeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCoffee);
            tvName = itemView.findViewById(R.id.tvCoffeeName);
            tvPrice = itemView.findViewById(R.id.tvCoffeePrice);
        }
    }
}
