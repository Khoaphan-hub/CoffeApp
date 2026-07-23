package com.example.coffeeapp.ui.orders;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.OrderAdapter;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.Order;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private TabLayout tabLayout;
    private TextView btnClearHistory;
    private DatabaseHelper dbHelper;
    private List<Order> orderList;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        dbHelper = new DatabaseHelper(getContext());
        rvOrders = view.findViewById(R.id.rvOrders);
        tabLayout = view.findViewById(R.id.tabLayout);
        btnClearHistory = view.findViewById(R.id.btnClearHistory);
        
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        
        setupTabs();
        setupClearHistory();
        loadOrders("Ongoing");
        
        return view;
    }

    private void setupClearHistory() {
        btnClearHistory.setOnClickListener(v -> {
            dbHelper.deleteOrdersByStatus("History");
            loadOrders("History");
        });
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = tab.getPosition() == 0 ? "Ongoing" : "History";
                btnClearHistory.setVisibility(status.equals("History") ? View.VISIBLE : View.GONE);
                loadOrders(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadOrders(String status) {
        orderList = new ArrayList<>();
        Cursor cursor = dbHelper.getOrders(status);
        
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_DATE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PRICE));
                String summary = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEMS));
                String ordStatus = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_STATUS));
                
                orderList.add(new Order(id, date, price, summary, ordStatus));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        adapter = new OrderAdapter(orderList, order -> {
            dbHelper.updateOrderStatus(order.getId(), "History");
            loadOrders("Ongoing");
        });
        rvOrders.setAdapter(adapter);
    }
}
