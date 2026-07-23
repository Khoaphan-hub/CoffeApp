package com.example.coffeeapp.ui.rewards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeeapp.R;
import com.example.coffeeapp.database.DatabaseHelper;
import com.example.coffeeapp.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LuckyWheelActivity extends AppCompatActivity {

    private ImageView ivWheel;
    private Button btnSpin;
    private boolean isSpinning = false;
    private final Random random = new Random();
    private DatabaseHelper dbHelper;

    // 1. Mảng phần thưởng xếp chuẩn theo chiều kim đồng hồ từ góc 12h của bức ảnh
    private final int[] possibleBonuses = {1200, 2000, 3000, 500, 600, 800, 900, 1000};
    private int currentBonus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lucky_wheel);

        dbHelper = new DatabaseHelper(this);

        // Fix UI overlap with status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.luckyWheelRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivWheel = findViewById(R.id.ivWheel);
        btnSpin = findViewById(R.id.btnSpin);

        btnSpin.setOnClickListener(v -> {
            if (!isSpinning) {
                spinWheel();
            }
        });
    }

    private void spinWheel() {
        isSpinning = true;

        // 1. CHỌN TRƯỚC KẾT QUẢ
        int winningIndex = random.nextInt(possibleBonuses.length);
        currentBonus = possibleBonuses[winningIndex];

        // 2. TÍNH TOÁN GÓC QUAY CHUẨN XÁC
        float degreesPerSlice = 360f / possibleBonuses.length; // 360 / 8 = 45 độ

        // Tính góc để mũi tên (ở góc 12h) chỉ vào CHÍNH GIỮA ô trúng thưởng
        float targetAngle = 360f - (winningIndex * degreesPerSlice) - (degreesPerSlice / 2);

        // Thêm 4 đến 8 vòng quay "ảo" (360 độ x số vòng) để tạo kịch tính
        int extraRotations = (4 + random.nextInt(5)) * 360;

        // Tổng số độ bánh xe phải xoay
        float totalDegrees = extraRotations + targetAngle;

        // 3. THỰC THI ANIMATION
        RotateAnimation rotate = new RotateAnimation(0, totalDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(3500); // Kéo dài ra 3.5s
        rotate.setFillAfter(true); // Giữ nguyên vị trí dừng, không giật về 0
        rotate.setInterpolator(new DecelerateInterpolator(1.5f)); // Hiệu ứng hãm phanh mượt

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                isSpinning = false;
                showReward(); // Gọi hàm hiển thị kết quả
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        ivWheel.startAnimation(rotate);
    }

    private void showReward() {
        // Cập nhật điểm và reset tem vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        int currentPoints = prefs.getInt(Constants.KEY_POINTS, 0);

        prefs.edit()
                .putInt(Constants.KEY_POINTS, currentPoints + currentBonus)
                .putInt(Constants.KEY_STAMPS, 0)
                .apply();

        // Ghi lại lịch sử trúng thưởng vào Database SQLite
        String dateString = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault()).format(new Date());
        dbHelper.addPointTransaction(dateString, currentBonus, "Lucky Wheel");

        // Bắn thông báo Snackbar nổi bật
        String message = "🎁 JACKPOT! You won " + currentBonus + " bonus points!";
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
        textView.setTextColor(android.graphics.Color.WHITE);
        textView.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);

        snackbar.show();

        // Delay 2.5 giây để người dùng đọc thông báo rồi mới thoát
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            finish();
        }, 2000);
    }
}