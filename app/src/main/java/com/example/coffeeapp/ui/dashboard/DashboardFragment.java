package com.example.coffeeapp.ui.dashboard;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coffeeapp.R;
import com.example.coffeeapp.database.DatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvTotalSpent, tvTotalOrdersCount, tvAvgSpent, tvTotalCalories;
    private BarChart barChartSpending;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        dbHelper = new DatabaseHelper(getContext());

        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvTotalOrdersCount = view.findViewById(R.id.tvTotalOrdersCount);
        tvAvgSpent = view.findViewById(R.id.tvAvgSpent);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        barChartSpending = view.findViewById(R.id.barChartSpending);

        loadDashboardData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        Cursor cursor = dbHelper.getAllOrdersForStats();

        double totalSpent = 0;
        int totalCalories = 0;
        int totalOrders = 0;

        // Chuẩn bị mảng chứa dữ liệu của 7 ngày
        float[] dailySpending = new float[7];
        String[] daysOfWeek = new String[7];

        // Lấy nhãn trục X (Thứ mấy) cho 7 ngày qua
        Calendar now = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        for (int i = 6; i >= 0; i--) {
            daysOfWeek[i] = dayFormat.format(now.getTime());
            now.add(Calendar.DAY_OF_YEAR, -1);
        }

        // Xác định giới hạn thời gian (Đầu ngày của 7 ngày trước -> Cuối ngày hôm nay)
        now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        long endOfToday = now.getTimeInMillis();

        now.add(Calendar.DAY_OF_YEAR, -6);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        long startOf7DaysAgo = now.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM | hh:mm a", Locale.getDefault());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_DATE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PRICE));
                int calories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_CALORIES));

                try {
                    Date orderDate = sdf.parse(dateStr);
                    Calendar orderCal = Calendar.getInstance();
                    orderCal.setTime(orderDate);
                    orderCal.set(Calendar.YEAR, currentYear); // Bổ sung năm để so sánh chính xác

                    long orderTime = orderCal.getTimeInMillis();

                    // Nếu đơn hàng nằm trong 7 ngày qua
                    if (orderTime >= startOf7DaysAgo && orderTime <= endOfToday) {
                        totalSpent += price;
                        totalCalories += calories;
                        totalOrders++;

                        // Tính xem đơn hàng này rơi vào cột ngày thứ mấy (0 -> 6)
                        long diffMillis = orderTime - startOf7DaysAgo;
                        int dayIndex = (int) (diffMillis / (1000 * 60 * 60 * 24));
                        if(dayIndex >= 0 && dayIndex < 7) {
                            dailySpending[dayIndex] += (float) price;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
            cursor.close();
        }

        double avgSpent = totalOrders > 0 ? (totalSpent / totalOrders) : 0;

        tvTotalSpent.setText(String.format(Locale.getDefault(), "$%.2f", totalSpent));
        tvTotalOrdersCount.setText(String.valueOf(totalOrders));
        tvAvgSpent.setText(String.format(Locale.getDefault(), "$%.2f", avgSpent));
        tvTotalCalories.setText(String.format(Locale.getDefault(), "%,d kcal", totalCalories));

        // Kích hoạt hàm vẽ biểu đồ
        setupChart(dailySpending, daysOfWeek);
    }

    private void setupChart(float[] dailySpending, String[] daysOfWeek) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dailySpending.length; i++) {
            // Chỉ hiển thị cột nếu hôm đó có chi tiêu, nếu bằng 0 sẽ để trống cho đẹp
            entries.add(new BarEntry(i, dailySpending[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Spending ($)");
        dataSet.setColor(Color.parseColor("#324A59"));
        dataSet.setValueTextColor(Color.parseColor("#757575"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChartSpending.setData(barData);

        // Tùy chỉnh trục X (hiển thị nhãn Thứ)
        XAxis xAxis = barChartSpending.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Làm mượt và tinh giản biểu đồ
        barChartSpending.getAxisLeft().setDrawGridLines(false);
        barChartSpending.getAxisRight().setEnabled(false);
        barChartSpending.getDescription().setEnabled(false);
        barChartSpending.animateY(1000);

        barChartSpending.invalidate();
    }
}