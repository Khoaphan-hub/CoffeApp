package com.example.coffeeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.CartItem;

import java.util.List;
import java.util.Locale;

/**
 * Adapter for managing and displaying items in the shopping cart.
 * Handles item binding and provides a listener for deletions.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onDeleteClick(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnItemInteractionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_row, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getCoffeeName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getTotalPrice()));
        holder.tvDetails.setText(String.format("%s | %s | %s Ice", item.getShot(), item.getSize(), item.getIce()));
        holder.tvQuantity.setText(String.format(Locale.getDefault(), "x %d", item.getQuantity()));
        
        // Setup swipe to delete handled by ItemTouchHelper in Activity, 
        // but can add a delete button or handle long click here if needed.
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDetails, tvPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartItem);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvDetails = itemView.findViewById(R.id.tvCartItemDetails);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
        }
    }
}
