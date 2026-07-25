package com.example.coffeeapp.utils;

import com.example.coffeeapp.R;
import com.example.coffeeapp.model.Coffee;
import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static List<Coffee> getCoffeeList() {
        List<Coffee> list = new ArrayList<>();
        list.add(new Coffee(1, "Americano", 3.00, "Rich, full-bodied espresso with hot water.", R.drawable.img_americano, 100));
        list.add(new Coffee(2, "Cappuccino", 4.50, "Espresso with steamed milk and a thick layer of foam.", R.drawable.img_capuchino, 120));
        list.add(new Coffee(4, "Flat White", 4.25, "Rich espresso with velvety steamed milk.", R.drawable.img_flatwhite, 130));
        list.add(new Coffee(5, "Mocha", 5.00, "Espresso with bittersweet chocolate and steamed milk.", R.drawable.img_mocha, 250));
        return list;
    }

    public static final String PREF_NAME = "CoffeeAppPrefs";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_STAMPS = "loyaltyStamps";
    public static final String KEY_POINTS = "rewardPoints";
    public static final String KEY_USER_PHONE = "userPhone";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_ADDRESS = "userAddress";
}
