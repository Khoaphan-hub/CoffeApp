package com.example.coffeeapp.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeeapp.R;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.Coffee;
import com.example.coffeeapp.ui.cart.CartActivity;
import com.example.coffeeapp.utils.CalorieEngine;

import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private Coffee coffee;
    private TextView tvProductName, tvTotalPrice, tvCaloriesValue, tvSugarValue, tvFatValue, tvQuantity;
    private RadioGroup rgShot, rgSize, rgIce;
    private Button btnAddToCart;
    private ImageButton btnBack;
    private ImageView ivProduct, btnFavorite;
    private TextView btnIncrease, btnDecrease;
    private int quantity = 1;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        coffee = (Coffee) getIntent().getSerializableExtra("coffee");
        dbHelper = new DatabaseHelper(this);

        // Fix UI overlap with status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailsRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        displayProductInfo();
        setupListeners();
        updateDynamicValues();
    }

    private void initViews() {
        tvProductName = findViewById(R.id.tvProductName);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvCaloriesValue = findViewById(R.id.tvCaloriesValue);
        tvSugarValue = findViewById(R.id.tvSugarValue);
        tvFatValue = findViewById(R.id.tvFatValue);
        rgShot = findViewById(R.id.rgShot);
        rgSize = findViewById(R.id.rgSize);
        rgIce = findViewById(R.id.rgIce);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack = findViewById(R.id.btnBack);
        // Ánh xạ các UI của bộ đếm số lượng
        tvQuantity = findViewById(R.id.tvQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnBack.setZ(10f); // Force high Z-index
        findViewById(R.id.btnCartPreview).setZ(10f); // Force high Z-index
        ivProduct = findViewById(R.id.ivProduct);
        btnFavorite = findViewById(R.id.btnFavorite);
    }

    private void displayProductInfo() {
        if (coffee != null) {
            tvProductName.setText(coffee.getName());
            ivProduct.setImageResource(coffee.getImageResId());
            tvQuantity.setText(String.valueOf(quantity));
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.btnCartPreview).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        rgShot.setOnCheckedChangeListener((group, checkedId) -> updateDynamicValues());
        rgSize.setOnCheckedChangeListener((group, checkedId) -> updateDynamicValues());
        rgIce.setOnCheckedChangeListener((group, checkedId) -> updateDynamicValues());
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateDynamicValues();
            }
        });
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateDynamicValues();
        });
        btnAddToCart.setOnClickListener(v -> addToCart());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void updateDynamicValues() {
        String shot = rgShot.getCheckedRadioButtonId() == R.id.rbDoubleShot ? "Double" : "Single";
        String size = "Small";
        int sizeId = rgSize.getCheckedRadioButtonId();
        if (sizeId == R.id.rbMedium) size = "Medium";
        else if (sizeId == R.id.rbLarge) size = "Large";

        double price = CalorieEngine.calculatePrice(coffee.getBasePrice(), size, shot) * quantity ;
        int calories = CalorieEngine.calculateCalories(coffee.getBaseCalories(), size, shot) * quantity;

        tvTotalPrice.setText(String.format(Locale.getDefault(), "$%.2f", price));
        tvCaloriesValue.setText(String.valueOf(calories));
        // Mock values for sugar and fat based on base calories
        tvSugarValue.setText(String.format(Locale.getDefault(), "%dg", (int)(calories * 0.1)));
        tvFatValue.setText(String.format(Locale.getDefault(), "%dg", (int)(calories * 0.05)));

        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        String shot = rgShot.getCheckedRadioButtonId() == R.id.rbDoubleShot ? "Double" : "Single";
        String size = "Small";
        int sizeId = rgSize.getCheckedRadioButtonId();
        if (sizeId == R.id.rbMedium) size = "Medium";
        else if (sizeId == R.id.rbLarge) size = "Large";

        String ice = "Full";
        int iceId = rgIce.getCheckedRadioButtonId();
        if (iceId == R.id.rbNoIce) ice = "None";
        else if (iceId == R.id.rbLessIce) ice = "Less";

        boolean isFav = dbHelper.isFavorite(coffee.getId(), shot, size, ice);
        if (isFav) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void toggleFavorite() {
        String shot = rgShot.getCheckedRadioButtonId() == R.id.rbDoubleShot ? "Double" : "Single";
        String size = "Small";
        int sizeId = rgSize.getCheckedRadioButtonId();
        if (sizeId == R.id.rbMedium) size = "Medium";
        else if (sizeId == R.id.rbLarge) size = "Large";

        String ice = "Full";
        int iceId = rgIce.getCheckedRadioButtonId();
        if (iceId == R.id.rbNoIce) ice = "None";
        else if (iceId == R.id.rbLessIce) ice = "Less";

        boolean isFav = dbHelper.isFavorite(coffee.getId(), shot, size, ice);

        if (isFav) {
            dbHelper.removeFavoriteByConfig(coffee.getId(), shot, size, ice);
            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            double price = CalorieEngine.calculatePrice(coffee.getBasePrice(), size, shot);
            int calories = CalorieEngine.calculateCalories(coffee.getBaseCalories(), size, shot);
            dbHelper.addFavorite(coffee.getId(), coffee.getName(), shot, size, ice, price, calories);
            Toast.makeText(this, "Added to Favorites!", Toast.LENGTH_SHORT).show();
        }
        updateFavoriteIcon();
    }

    private void addToCart() {
        String shot = rgShot.getCheckedRadioButtonId() == R.id.rbDoubleShot ? "Double" : "Single";
        String size = "Small";
        int sizeId = rgSize.getCheckedRadioButtonId();
        if (sizeId == R.id.rbMedium) size = "Medium";
        else if (sizeId == R.id.rbLarge) size = "Large";

        String ice = "Full";
        int iceId = rgIce.getCheckedRadioButtonId();
        if (iceId == R.id.rbNoIce) ice = "None";
        else if (iceId == R.id.rbLessIce) ice = "Less";

        double price = CalorieEngine.calculatePrice(coffee.getBasePrice(), size, shot) * quantity;
        int calories = CalorieEngine.calculateCalories(coffee.getBaseCalories(), size, shot) * quantity;

        long result = dbHelper.addToCart(coffee.getId(), coffee.getName(), shot, size, ice, price, calories, quantity);
        if (result != -1) {
            Toast.makeText(this, "Added " + quantity + " items to cart!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailsActivity.this, CartActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }
}
