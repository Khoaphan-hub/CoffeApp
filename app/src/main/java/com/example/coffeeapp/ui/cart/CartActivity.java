package com.example.coffeeapp.ui.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeeapp.R;
import com.example.coffeeapp.adapter.CartAdapter;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.model.CartItem;
import com.example.coffeeapp.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvTotalCartPrice;
    private Button btnCheckout;
    private ImageButton btnBack;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Fix UI overlap with status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadCartItems();
        setupListeners();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        tvTotalCartPrice = findViewById(R.id.tvTotalCartPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCartItems() {
        cartItems = new ArrayList<>();
        Cursor cursor = dbHelper.getCartItems();
        double total = 0;

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ID));
                int coffeeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COFFEE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COFFEE_NAME));
                String shot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHOT));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SIZE));
                String ice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ICE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL_PRICE));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL_CALORIES));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));

                cartItems.add(new CartItem(id, coffeeId, name, shot, size, ice, price, calories, qty));
                total += price;
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new CartAdapter(cartItems, item -> {});
        rvCart.setAdapter(adapter);
        tvTotalCartPrice.setText(String.format(Locale.getDefault(), "$%.2f", total));
        
        setupSwipeToDelete();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CartItem item = cartItems.get(position);
                dbHelper.deleteCartItem(item.getId());
                cartItems.remove(position);
                adapter.notifyItemRemoved(position);
                updateTotalPrice();
            }

            // THÊM MỚI: Xử lý hiệu ứng đồ họa khi người dùng vuốt item
            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                android.view.View itemView = viewHolder.itemView;
                // Tạo nền màu đỏ nhạt (mã màu giống trong ảnh mẫu)
                android.graphics.drawable.ColorDrawable background = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#FFEAEA"));

                // Sử dụng icon thùng rác mặc định của hệ thống Android
                android.graphics.drawable.Drawable trashIcon = androidx.core.content.ContextCompat.getDrawable(CartActivity.this, android.R.drawable.ic_menu_delete);
                if (trashIcon != null) {
                    // Tô màu đỏ đậm cho icon thùng rác
                    trashIcon.setTint(android.graphics.Color.parseColor("#FF4C4C"));
                }

                if (dX < 0) { // Nếu đang vuốt sang trái
                    // Tính toán và vẽ khối nền màu đỏ
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    background.draw(c);

                    // Tính toán vị trí để vẽ icon thùng rác nằm giữa khối màu đỏ
                    if (trashIcon != null) {
                        int iconMargin = (itemView.getHeight() - trashIcon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + trashIcon.getIntrinsicHeight();
                        int iconLeft = itemView.getRight() - iconMargin - trashIcon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;

                        // Chỉ cho phép icon hiện ra khi đã vuốt đủ khoảng cách
                        if (itemView.getRight() + (int) dX < iconLeft) {
                            trashIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            trashIcon.draw(c);
                        }
                    }
                } else {
                    // Trả lại trạng thái mặc định nếu không vuốt
                    background.setBounds(0, 0, 0, 0);
                }
            }
        }).attachToRecyclerView(rvCart);
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        tvTotalCartPrice.setText(String.format(Locale.getDefault(), "$%.2f", total));
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        btnCheckout.setEnabled(false);
        StringBuilder summary = new StringBuilder();
        double total = 0;
        int totalCups = 0;
        for (CartItem item : cartItems) {
            summary.append(item.getCoffeeName()).append(", ");
            total += item.getTotalPrice() ;
            totalCups += item.getQuantity();
        }
        if (summary.length() > 0) summary.setLength(summary.length() - 2);

        String date = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
        long orderId = dbHelper.placeOrder(date, total, summary.toString());

        if (orderId != -1) {
            // Xóa giỏ hàng trước
            dbHelper.clearCart();

            // Gọi hàm cập nhật điểm. Hàm này giờ sẽ tự chịu trách nhiệm đợi 2 giây rồi mới chuyển trang
            updateLoyaltyData(total, totalCups);

            // ĐÃ XÓA startActivity() và finish() ở đây!
        } else {
            Toast.makeText(this, "Checkout failed", Toast.LENGTH_SHORT).show();
            btnCheckout.setEnabled(true);
        }
    }

    private void updateLoyaltyData(double totalAmount, int cupCount) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int currentStamps = prefs.getInt(Constants.KEY_STAMPS, 0);
        int currentPoints = prefs.getInt(Constants.KEY_POINTS, 0);

        // Logic: 1 stamp per cup, 50 points per $1.00
        int newStamps = currentStamps + cupCount;
        int pointsEarned = (int) (totalAmount * 50);
        int newPoints = currentPoints + pointsEarned;

        prefs.edit()
                .putInt(Constants.KEY_STAMPS, newStamps)
                .putInt(Constants.KEY_POINTS, newPoints)
                .apply();

        // Add to points history
        String dateString = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
        dbHelper.addPointTransaction(dateString, pointsEarned, "Purchase");

        String message = "🎉 Awesome! You earned " + cupCount + " stamps and " + pointsEarned + " points!";

        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                message,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        );

        android.view.View snackbarView = snackbar.getView();

        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(24f);
        shape.setColor(android.graphics.Color.parseColor("#324A59"));

        snackbarView.setBackground(shape);

        android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = android.view.Gravity.TOP;
        params.setMargins(40, 120, 40, 0);
        snackbarView.setLayoutParams(params);

        android.widget.TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16f);
        textView.setMaxLines(3);
        textView.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);

        // Đã thêm lệnh đổi màu chữ thành TRẮNG
        textView.setTextColor(android.graphics.Color.WHITE);

        // Hiển thị thông báo
        snackbar.show();

        // Dùng Handler tạo độ trễ 2000ms (2 giây) rồi mới chuyển sang màn hình OrderSuccess
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, OrderSuccessActivity.class));
            finish();
        }, 2000);
    }
}
