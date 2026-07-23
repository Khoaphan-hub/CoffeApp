package com.example.coffeeapp.ui.rewards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.RedeemAdapter;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.Coffee;
import com.example.coffeeapp.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RedeemActivity extends AppCompatActivity {

    private RecyclerView rvRedeem;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_redeem);

        // Fix UI overlap with status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.redeemRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        initViews();
        setupRecyclerView();
    }

    private void initViews() {
        rvRedeem = findViewById(R.id.rvRedeem);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setZ(10f);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvRedeem.setLayoutManager(new LinearLayoutManager(this));
        List<Coffee> coffeeList = Constants.getCoffeeList();

        // Sử dụng RedeemAdapter để hiển thị đúng giao diện từng dòng item_redeem
        RedeemAdapter adapter = new RedeemAdapter(coffeeList, coffee -> {
            int pointsNeeded = (int) (coffee.getBasePrice() * 300); // Công thức tính điểm quy đổi
            int currentPoints = prefs.getInt(Constants.KEY_POINTS, 0);

            if (currentPoints >= pointsNeeded) {
                prefs.edit().putInt(Constants.KEY_POINTS, currentPoints - pointsNeeded).apply();

                // Add to points history
                String dateString = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
                dbHelper.addPointTransaction(dateString, -pointsNeeded, "Redeem " + coffee.getName());

                dbHelper.addToCart(coffee.getId(), coffee.getName() + " (Free)", "Single", "Small", "Full", 0.0, coffee.getBaseCalories(), 1);

                Toast.makeText(this, "Redeemed successfully! Added to cart.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Not enough points to redeem this item!", Toast.LENGTH_SHORT).show();
            }
        });

        rvRedeem.setAdapter(adapter);
    }
}