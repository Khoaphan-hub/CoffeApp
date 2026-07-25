package com.example.coffeeapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.CoffeeAdapter;
import com.example.coffeeapp.ui.cart.CartActivity;
import com.example.coffeeapp.ui.details.DetailsActivity;
import com.example.coffeeapp.utils.Constants;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView tvUserName, tvStampCount;
    private LinearLayout stampContainer;
    private RecyclerView rvCoffee;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        tvUserName = view.findViewById(R.id.tvUserName);
        tvStampCount = view.findViewById(R.id.tvStampCount);
        stampContainer = view.findViewById(R.id.stampContainer);
        rvCoffee = view.findViewById(R.id.rvCoffee);
        ImageButton btnGoToCart = view.findViewById(R.id.btnGoToCart);
        
        btnGoToCart.setOnClickListener(v -> startActivity(new Intent(getActivity(), CartActivity.class)));
        
        setupUserHeader();
        setupRecyclerView();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUserHeader();
    }

    private void setupUserHeader() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(Constants.KEY_USER_NAME, "Anderson");
        int stamps = prefs.getInt(Constants.KEY_STAMPS, 0);
        
        tvUserName.setText(name);
        tvStampCount.setText(String.format(Locale.getDefault(), "%d / 8", stamps));

        // Update stamp icons
        for (int k = 0; k < stampContainer.getChildCount(); k++) {
            ImageView iv = (ImageView) stampContainer.getChildAt(k);
            if (k < stamps) {
                iv.setImageResource(R.drawable.ic_stamp_filled);
            } else {
                iv.setImageResource(R.drawable.ic_stamp_empty);
            }
        }
    }

    private void setupRecyclerView() {
        rvCoffee.setLayoutManager(new GridLayoutManager(getContext(), 2));
        CoffeeAdapter adapter = new CoffeeAdapter(Constants.getCoffeeList(), coffee -> {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra("coffee", coffee);
            startActivity(intent);
        });
        rvCoffee.setAdapter(adapter);
    }
}
