package com.example.coffeeapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.CoffeeAdapter;
import com.example.coffeeapp.adapter.FavoriteAdapter;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.Coffee;
import com.example.coffeeapp.model.FavoriteItem;
import com.example.coffeeapp.ui.cart.CartActivity;
import com.example.coffeeapp.ui.details.DetailsActivity;
import com.example.coffeeapp.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView tvUserName, tvStampCount;
    private LinearLayout stampContainer;
    private RecyclerView rvCoffee, rvFavorites;
    private TabLayout tabLayoutHome;
    private DatabaseHelper dbHelper;
    private View viewGiftBadge;
    private FrameLayout layoutGiftBox;
    private boolean hasPendingGift = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        dbHelper = new DatabaseHelper(getContext());
        tvUserName = view.findViewById(R.id.tvUserName);
        tvStampCount = view.findViewById(R.id.tvStampCount);
        stampContainer = view.findViewById(R.id.stampContainer);
        rvCoffee = view.findViewById(R.id.rvCoffee);
        rvFavorites = view.findViewById(R.id.rvFavorites);
        tabLayoutHome = view.findViewById(R.id.tabLayoutHome);
        layoutGiftBox = view.findViewById(R.id.layoutGiftBox);
        viewGiftBadge = view.findViewById(R.id.viewGiftBadge);
        ImageButton btnGoToCart = view.findViewById(R.id.btnGoToCart);
        
        btnGoToCart.setOnClickListener(v -> startActivity(new Intent(getActivity(), CartActivity.class)));
        view.findViewById(R.id.fabGift).setOnClickListener(v -> showGiftDialog());
        
        layoutGiftBox.setOnClickListener(v -> {
            if (hasPendingGift) {
                showReceiveGiftDialog();
            } else {
                Toast.makeText(getContext(), "No new gifts yet. Try again later!", Toast.LENGTH_SHORT).show();
            }
        });

        setupUserHeader();
        setupRecyclerView();
        setupTabs();
        loadFavorites();

        return view;
    }

    private void setupTabs() {
        tabLayoutHome.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rvCoffee.setVisibility(View.VISIBLE);
                    rvFavorites.setVisibility(View.GONE);
                } else {
                    rvCoffee.setVisibility(View.GONE);
                    rvFavorites.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void simulateIncomingGift() {
        if (hasPendingGift) return;

        // Force gift for testing
        hasPendingGift = true;
        viewGiftBadge.setVisibility(View.VISIBLE);
        android.util.Log.d("HomeFragment", "Simulated gift triggered");
    }

    private void showReceiveGiftDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_receive_gift, null);
        TextView tvDrink = dialogView.findViewById(R.id.tvGiftDrinkName);
        TextView tvTable = dialogView.findViewById(R.id.tvGiftFromTable);
        TextView tvMsg = dialogView.findViewById(R.id.tvGiftMsg);
        android.widget.Button btnClaim = dialogView.findViewById(R.id.btnClaimGift);

        List<Coffee> coffees = Constants.getCoffeeList();
        Coffee randomCoffee = coffees.get(new Random().nextInt(coffees.size()));
        
        String[] shopMsgs = {
                "Surprise! A small treat from our cafe to brighten your day.",
                "Congratulations! You've been selected for a random surprise.",
                "Enjoy this complimentary drink on us!",
                "We love having you here. Enjoy this coffee!"
        };

        tvDrink.setText(randomCoffee.getName());
        tvTable.setText("Surprise from Cafe");
        tvMsg.setText("\"" + shopMsgs[new Random().nextInt(shopMsgs.length)] + "\"");

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        btnClaim.setOnClickListener(v -> {
            handleClaimGift(randomCoffee);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleClaimGift(Coffee coffee) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int currentStamps = prefs.getInt(Constants.KEY_STAMPS, 0);

        // Add to Cart with $0 price
        dbHelper.addToCart(coffee.getId(), coffee.getName(), "Single", "Small", "Full", 0.0, 0, 1);
        
        // Award 1 stamp
        prefs.edit().putInt(Constants.KEY_STAMPS, currentStamps + 1).apply();

        String date = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
        dbHelper.addPointTransaction(date, 0, "Surprise Gift: " + coffee.getName());

        hasPendingGift = false;
        viewGiftBadge.setVisibility(View.GONE);

        com.google.android.material.snackbar.Snackbar snackbar =
                com.google.android.material.snackbar.Snackbar.make(requireView(), "🎁 Gift added to Cart! (+1 Stamp)", 3000);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#4CAF50"));
        snackbar.show();
        
        setupUserHeader();
    }

    private void showGiftDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_gift, null);

        // 1. Khai báo View theo giao diện XML mới (Exposed Dropdown Menu & TextInputEditText)
        android.widget.AutoCompleteTextView spinnerDrinks = dialogView.findViewById(R.id.spinnerDrinks);
        com.google.android.material.textfield.TextInputEditText etTable = dialogView.findViewById(R.id.etTableNumber);
        com.google.android.material.textfield.TextInputEditText etMsg = dialogView.findViewById(R.id.etGiftMessage);
        com.google.android.material.button.MaterialButton btnSendGift = dialogView.findViewById(R.id.btnSendGift);

        // 2. Nạp danh sách thức uống vào AutoCompleteTextView
        List<String> drinkNames = new ArrayList<>();
        for (Coffee c : Constants.getCoffeeList()) {
            drinkNames.add(c.getName());
        }
        // Lưu ý: Dùng simple_dropdown_item_1line thay vì simple_spinner_item
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, drinkNames);
        spinnerDrinks.setAdapter(adapter);

        // 3. Tạo Dialog (Bỏ các nút Positive/Negative mặc định vì ta đã có nút Send Gift trên XML)
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        // 4. Xử lý sự kiện khi bấm nút Send Gift của chúng ta
        btnSendGift.setOnClickListener(v -> {
            // Lấy dữ liệu (AutoCompleteTextView dùng getText thay vì getSelectedItem)
            String selectedDrink = spinnerDrinks.getText().toString();
            String tableNum = etTable.getText() != null ? etTable.getText().toString().trim() : "";
            String message = etMsg.getText() != null ? etMsg.getText().toString().trim() : "";

            // Validate dữ liệu đầu vào
            if (selectedDrink.isEmpty() || selectedDrink.equals("Select a Drink")) {
                Toast.makeText(getContext(), "Please select a drink", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tableNum.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a table number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm xử lý giao dịch và đóng dialog
            handleGiftTransaction(selectedDrink, tableNum, message);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleGiftTransaction(String drink, String table, String message) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int currentPoints = prefs.getInt(Constants.KEY_POINTS, 0);
        int currentStamps = prefs.getInt(Constants.KEY_STAMPS, 0);
        int giftCost = 1500;

        if (currentPoints < giftCost) {
            Toast.makeText(getContext(), "Not enough points! (Need 1500 pts)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deduct points and Add 1 stamp
        prefs.edit()
                .putInt(Constants.KEY_POINTS, currentPoints - giftCost)
                .putInt(Constants.KEY_STAMPS, currentStamps + 1)
                .apply();

        String date = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
        dbHelper.addGift(drink, table, message, date);
        dbHelper.addPointTransaction(date, -giftCost, "Gift for Table " + table);

        showSuccessSnackbar(drink, table);
        setupUserHeader();
    }

    private void showSuccessSnackbar(String drink, String table) {
        String msg = "🎁 Magic! Your " + drink + " is being prepared for Table " + table + "!";
        com.google.android.material.snackbar.Snackbar snackbar =
                com.google.android.material.snackbar.Snackbar.make(requireView(), msg, 5000);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#324A59"));
        TextView tv = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUserHeader();
        simulateIncomingGift();
        loadFavorites();
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
                iv.clearColorFilter();
            } else {
                iv.setImageResource(R.drawable.ic_stamp_empty);
                iv.clearColorFilter();
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

    private void loadFavorites() {
        Cursor cursor = dbHelper.getFavorites();
        List<FavoriteItem> favoriteList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE_ID));
                int coffeeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_COFFEE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_COFFEE_NAME));
                String shot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_SHOT));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_SIZE));
                String ice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_ICE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_TOTAL_PRICE));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAV_TOTAL_CALORIES));

                favoriteList.add(new FavoriteItem(id, coffeeId, name, shot, size, ice, price, calories));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (favoriteList.isEmpty()) {
            // Optional: Hide "Favorites" tab or show empty state
        }
        
        // Use GridLayoutManager (2 columns) for Favorites when selected, or keep standard list? 
        // User didn't specify, but for consistency with main menu:
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        FavoriteAdapter adapter = new FavoriteAdapter(favoriteList, new FavoriteAdapter.OnFavoriteClickListener() {
            @Override
            public void onQuickAdd(FavoriteItem item) {
                dbHelper.addToCart(item.getCoffeeId(), item.getName(), item.getShot(), item.getSize(), item.getIce(), item.getTotalPrice(), item.getTotalCalories(), 1);
                Toast.makeText(getContext(), "Added " + item.getName() + " to Cart!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRemove(FavoriteItem item) {
                dbHelper.removeFavorite(item.getId());
                loadFavorites(); // Refresh
            }
        });
        rvFavorites.setAdapter(adapter);
    }
}
