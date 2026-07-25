package com.example.coffeeapp.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.coffeeapp.R;
import com.example.coffeeapp.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvPhone, tvEmail, tvAddress;
    private ImageButton btnEditName, btnEditPhone, btnEditEmail, btnEditAddress;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        initViews(view);
        setupInsets(view);
        loadUserData();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tvProfileName);
        tvPhone = view.findViewById(R.id.tvProfilePhone);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvAddress = view.findViewById(R.id.tvProfileAddress);

        btnEditName = view.findViewById(R.id.btnEditName);
        btnEditPhone = view.findViewById(R.id.btnEditPhone);
        btnEditEmail = view.findViewById(R.id.btnEditEmail);
        btnEditAddress = view.findViewById(R.id.btnEditAddress);
    }

    private void setupInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.profileRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserData() {
        tvName.setText(prefs.getString(Constants.KEY_USER_NAME, "Anderson"));
        tvPhone.setText(prefs.getString(Constants.KEY_USER_PHONE, "+60134589525"));
        tvEmail.setText(prefs.getString(Constants.KEY_USER_EMAIL, "Anderson@email.com"));
        tvAddress.setText(prefs.getString(Constants.KEY_USER_ADDRESS, "3 Addersion Court Chino Hills, HO56824, United State"));
    }

    private void setupListeners() {
        btnEditName.setOnClickListener(v -> showEditDialog("Full name", Constants.KEY_USER_NAME, tvName));
        btnEditPhone.setOnClickListener(v -> showEditDialog("Phone number", Constants.KEY_USER_PHONE, tvPhone));
        btnEditEmail.setOnClickListener(v -> showEditDialog("Email", Constants.KEY_USER_EMAIL, tvEmail));
        btnEditAddress.setOnClickListener(v -> showEditDialog("Address", Constants.KEY_USER_ADDRESS, tvAddress));
    }

    private void showEditDialog(String title, String key, TextView targetView) {
        EditText editText = new EditText(getContext());
        editText.setText(targetView.getText());
        
        // Add padding to the EditText inside the dialog
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        editText.setPadding(padding, padding, padding, padding);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit " + title)
                .setView(editText)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newValue = editText.getText().toString().trim();
                    if (!newValue.isEmpty()) {
                        prefs.edit().putString(key, newValue).apply();
                        targetView.setText(newValue);
                        Toast.makeText(getContext(), title + " updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
