package com.example.coffeeapp.ui.rewards;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.PointHistoryAdapter;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.PointTransaction;
import com.example.coffeeapp.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RewardsFragment extends Fragment {

    private TextView tvTotalPoints, tvStampsProgress, btnResetPoints;
    private Button btnRedeem;
    private RecyclerView rvPointsHistory;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        
        dbHelper = new DatabaseHelper(getContext());
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvStampsProgress = view.findViewById(R.id.tvStampsProgress);
        btnRedeem = view.findViewById(R.id.btnRedeem);
        btnResetPoints = view.findViewById(R.id.btnResetPoints);
        rvPointsHistory = view.findViewById(R.id.rvPointsHistory);
        
        rvPointsHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPointsHistory.setNestedScrollingEnabled(false);
        
        setupUI();
        setupListeners();
        
        return view;
    }

    private void setupListeners() {
        btnRedeem.setOnClickListener(v -> startActivity(new Intent(getActivity(), RedeemActivity.class)));
        
        btnResetPoints.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putInt(Constants.KEY_POINTS, 0).apply();
            
            dbHelper.clearPointsHistory();
            
            String date = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
            dbHelper.addPointTransaction(date, 0, "System Reset");
            
            setupUI();
            Toast.makeText(getContext(), "Points and history reset", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUI();
        checkLuckyWheelTrigger();
    }

    private void setupUI() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int points = prefs.getInt(Constants.KEY_POINTS, 0);
        int stamps = prefs.getInt(Constants.KEY_STAMPS, 0);
        
        tvTotalPoints.setText(String.format(Locale.getDefault(), "%,d", points));
        tvStampsProgress.setText(String.format(Locale.getDefault(), "%d of 8 stamps collected", stamps));
        
        loadPointHistory();
    }

    private void loadPointHistory() {
        List<PointTransaction> history = new ArrayList<>();
        android.database.Cursor cursor = dbHelper.getPointsHistory();
        
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POINT_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POINT_DATE));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POINT_AMOUNT));
                String source = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_POINT_SOURCE));
                
                history.add(new PointTransaction(id, date, amount, source));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        PointHistoryAdapter adapter = new PointHistoryAdapter(history);
        rvPointsHistory.setAdapter(adapter);
    }

    private void checkLuckyWheelTrigger() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int stamps = prefs.getInt(Constants.KEY_STAMPS, 0);
        if (stamps >= 8) {
            startActivity(new Intent(getActivity(), LuckyWheelActivity.class));
        }
    }
}
