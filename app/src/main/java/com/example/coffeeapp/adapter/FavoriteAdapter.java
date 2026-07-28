package com.example.coffeeapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.FavoriteItem;
import com.example.coffeeapp.utils.Constants;

import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final List<FavoriteItem> favorites;
    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onQuickAdd(FavoriteItem item);
        void onRemove(FavoriteItem item);
    }

    public FavoriteAdapter(List<FavoriteItem> favorites, OnFavoriteClickListener listener) {
        this.favorites = favorites;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteItem item = favorites.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getTotalPrice()));
        
        String options = String.format("%s | %s Shot | %s Ice", item.getSize(), item.getShot(), item.getIce());
        holder.tvOptions.setText(options);

        // Find image res ID from Constants
        int imageResId = Constants.getImageForCoffee(item.getCoffeeId());
        holder.ivProduct.setImageResource(imageResId);

        holder.itemView.setOnClickListener(v -> listener.onQuickAdd(item));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(item));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvOptions, tvPrice;
        ImageButton btnRemove;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivFavoriteProduct);
            tvName = itemView.findViewById(R.id.tvFavoriteName);
            tvOptions = itemView.findViewById(R.id.tvFavoriteOptions);
            tvPrice = itemView.findViewById(R.id.tvFavoritePrice);
            btnRemove = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}
