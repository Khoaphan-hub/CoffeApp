package com.example.coffeeapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.CoffeeAdapter;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.Coffee;
import com.example.coffeeapp.ui.cart.CartActivity;
import com.example.coffeeapp.ui.details.DetailsActivity;
import com.example.coffeeapp.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView tvUserName, tvStampCount;
    private LinearLayout stampContainer;
    private RecyclerView rvCoffee;
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
        
        return view;
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
        int randomTable = new Random().nextInt(20) + 1;
        String[] mockMsgs = {
                "You look cool today! Enjoy this drink.",
                "Happy Monday! Have a coffee on me.",
                "Sharing is caring. Cheers!",
                "I ordered too many. Please help me drink one!"
        };

        tvDrink.setText(randomCoffee.getName());
        tvTable.setText("From Table " + String.format(Locale.getDefault(), "%02d", randomTable));
        tvMsg.setText("\"" + mockMsgs[new Random().nextInt(mockMsgs.length)] + "\"");

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create();

        btnClaim.setOnClickListener(v -> {
            handleClaimGift(randomTable);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleClaimGift(int tableNum) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int currentPoints = prefs.getInt(Constants.KEY_POINTS, 0);
        int bonus = 200;
        prefs.edit().putInt(Constants.KEY_POINTS, currentPoints + bonus).apply();

        String date = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
        dbHelper.addPointTransaction(date, bonus, "Gift from Table " + tableNum);

        hasPendingGift = false;
        viewGiftBadge.setVisibility(View.GONE);

        com.google.android.material.snackbar.Snackbar snackbar =
                com.google.android.material.snackbar.Snackbar.make(requireView(), "🎁 Claimed! 200 points added.", 3000);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.parseColor("#4CAF50"));
        snackbar.show();
        
        setupUserHeader();
    }

    private void showGiftDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_gift, null);
        Spinner spinner = dialogView.findViewById(R.id.spinnerDrinks);
        EditText etTable = dialogView.findViewById(R.id.etTableNumber);
        EditText etMsg = dialogView.findViewById(R.id.etGiftMessage);

        List<String> drinkNames = new ArrayList<>();
        for (Coffee c : Constants.getCoffeeList()) {
            drinkNames.add(c.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, drinkNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Send Gift", (dialog, which) -> {
                    String selectedDrink = spinner.getSelectedItem().toString();
                    String tableNum = etTable.getText().toString();
                    String message = etMsg.getText().toString();

                    if (tableNum.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a table number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    handleGiftTransaction(selectedDrink, tableNum, message);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleGiftTransaction(String drink, String table, String message) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int currentPoints = prefs.getInt(Constants.KEY_POINTS, 0);
        int giftCost = 500;

        if (currentPoints < giftCost) {
            Toast.makeText(getContext(), "Not enough points! (Need 500 pts)", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putInt(Constants.KEY_POINTS, currentPoints - giftCost).apply();

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
