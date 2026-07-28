package com.example.coffeeapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite helper for local persistence of cart items and order history.
 * Manages table creation, versioning, and CRUD operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CoffeeCup.db";
    private static final int DATABASE_VERSION = 6;

    // Cart Table
    public static final String TABLE_CART = "cart";
    public static final String COLUMN_CART_ID = "id";
    public static final String COLUMN_COFFEE_ID = "coffee_id";
    public static final String COLUMN_COFFEE_NAME = "coffee_name";
    public static final String COLUMN_SHOT = "shot";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_ICE = "ice";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_TOTAL_CALORIES = "total_calories";
    public static final String COLUMN_QUANTITY = "quantity";

    // Favorites Table
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_FAVORITE_ID = "id";
    public static final String COLUMN_FAV_COFFEE_ID = "coffee_id";
    public static final String COLUMN_FAV_COFFEE_NAME = "coffee_name";
    public static final String COLUMN_FAV_SHOT = "shot";
    public static final String COLUMN_FAV_SIZE = "size";
    public static final String COLUMN_FAV_ICE = "ice";
    public static final String COLUMN_FAV_TOTAL_PRICE = "total_price";
    public static final String COLUMN_FAV_TOTAL_CALORIES = "total_calories";

    // Orders Table
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_PRICE = "total_price";
    public static final String COLUMN_ORDER_CALORIES = "total_calories"; // Cột mới thêm
    public static final String COLUMN_ORDER_ITEMS = "items_summary";
    public static final String COLUMN_ORDER_STATUS = "status"; // Ongoing, History

    // Points History Table
    public static final String TABLE_POINTS_HISTORY = "points_history";
    public static final String COLUMN_POINT_ID = "id";
    public static final String COLUMN_POINT_DATE = "date";
    public static final String COLUMN_POINT_AMOUNT = "amount";
    public static final String COLUMN_POINT_SOURCE = "source";

    // Gift History Table
    public static final String TABLE_GIFT = "gift_history";
    public static final String COLUMN_GIFT_ID = "id";
    public static final String COLUMN_GIFT_DRINK = "drink_name";
    public static final String COLUMN_GIFT_TABLE = "table_number";
    public static final String COLUMN_GIFT_MESSAGE = "message";
    public static final String COLUMN_GIFT_DATE = "date";

    public static final String COLUMN_ORDER_USER_NAME = "user_name";
    public static final String COLUMN_ORDER_IMAGE = "image_res_id";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COFFEE_ID + " INTEGER,"
                + COLUMN_COFFEE_NAME + " TEXT,"
                + COLUMN_SHOT + " TEXT,"
                + COLUMN_SIZE + " TEXT,"
                + COLUMN_ICE + " TEXT,"
                + COLUMN_TOTAL_PRICE + " REAL,"
                + COLUMN_TOTAL_CALORIES + " INTEGER,"
                + COLUMN_QUANTITY + " INTEGER" + ")";
        db.execSQL(CREATE_CART_TABLE);

        // ĐÃ SỬA: Thêm COLUMN_ORDER_CALORIES vào lệnh tạo bảng Orders
        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_DATE + " TEXT,"
                + COLUMN_ORDER_PRICE + " REAL,"
                + COLUMN_ORDER_CALORIES + " INTEGER,"
                + COLUMN_ORDER_ITEMS + " TEXT,"
                + COLUMN_ORDER_USER_NAME + " TEXT,"     // Thêm cột này
                + COLUMN_ORDER_IMAGE + " INTEGER,"      // Thêm cột này
                + COLUMN_ORDER_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_ORDERS_TABLE);

        String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS_HISTORY + "("
                + COLUMN_POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POINT_DATE + " TEXT,"
                + COLUMN_POINT_AMOUNT + " INTEGER,"
                + COLUMN_POINT_SOURCE + " TEXT" + ")";
        db.execSQL(CREATE_POINTS_TABLE);

        String CREATE_GIFT_TABLE = "CREATE TABLE " + TABLE_GIFT + "("
                + COLUMN_GIFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GIFT_DRINK + " TEXT,"
                + COLUMN_GIFT_TABLE + " TEXT,"
                + COLUMN_GIFT_MESSAGE + " TEXT,"
                + COLUMN_GIFT_DATE + " TEXT" + ")";
        db.execSQL(CREATE_GIFT_TABLE);

        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FAV_COFFEE_ID + " INTEGER,"
                + COLUMN_FAV_COFFEE_NAME + " TEXT,"
                + COLUMN_FAV_SHOT + " TEXT,"
                + COLUMN_FAV_SIZE + " TEXT,"
                + COLUMN_FAV_ICE + " TEXT,"
                + COLUMN_FAV_TOTAL_PRICE + " REAL,"
                + COLUMN_FAV_TOTAL_CALORIES + " INTEGER" + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // ĐÃ CHUẨN HÓA LOGIC CẬP NHẬT TỪNG PHIÊN BẢN (Không bị xóa nhầm data cũ)
        if (oldVersion < 2) {
            String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS_HISTORY + "("
                    + COLUMN_POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_POINT_DATE + " TEXT,"
                    + COLUMN_POINT_AMOUNT + " INTEGER,"
                    + COLUMN_POINT_SOURCE + " TEXT" + ")";
            db.execSQL(CREATE_POINTS_TABLE);
        }

        if (oldVersion < 3) {
            String CREATE_GIFT_TABLE = "CREATE TABLE " + TABLE_GIFT + "("
                    + COLUMN_GIFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_GIFT_DRINK + " TEXT,"
                    + COLUMN_GIFT_TABLE + " TEXT,"
                    + COLUMN_GIFT_MESSAGE + " TEXT,"
                    + COLUMN_GIFT_DATE + " TEXT" + ")";
            db.execSQL(CREATE_GIFT_TABLE);
        }

        if (oldVersion < 4) {
            try {
                // Tiêm thêm cột total_calories vào bảng đã có
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_CALORIES + " INTEGER DEFAULT 0");
            } catch (android.database.SQLException e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_USER_NAME + " TEXT DEFAULT 'Guest'");
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_IMAGE + " INTEGER DEFAULT 0");
            } catch (android.database.SQLException e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 6) {
            String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                    + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FAV_COFFEE_ID + " INTEGER,"
                    + COLUMN_FAV_COFFEE_NAME + " TEXT,"
                    + COLUMN_FAV_SHOT + " TEXT,"
                    + COLUMN_FAV_SIZE + " TEXT,"
                    + COLUMN_FAV_ICE + " TEXT,"
                    + COLUMN_FAV_TOTAL_PRICE + " REAL,"
                    + COLUMN_FAV_TOTAL_CALORIES + " INTEGER" + ")";
            db.execSQL(CREATE_FAVORITES_TABLE);
        }
    }

    // Cart Operations
    public long addToCart(int coffeeId, String name, String shot, String size, String ice, double price, int calories, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COFFEE_ID, coffeeId);
        values.put(COLUMN_COFFEE_NAME, name);
        values.put(COLUMN_SHOT, shot);
        values.put(COLUMN_SIZE, size);
        values.put(COLUMN_ICE, ice);
        values.put(COLUMN_TOTAL_PRICE, price);
        values.put(COLUMN_TOTAL_CALORIES, calories);
        values.put(COLUMN_QUANTITY, quantity);
        return db.insert(TABLE_CART, null, values);
    }

    public Cursor getCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CART, null, null, null, null, null, null);
    }

    public void deleteCartItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_CART_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }

    // ĐÃ SỬA: Hàm placeOrder nhận thêm tham số int totalCalories
    public long placeOrder(String date, double totalPrice, int totalCalories, String itemsSummary, String userName, int imageResId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_DATE, date);
        values.put(COLUMN_ORDER_PRICE, totalPrice);
        values.put(COLUMN_ORDER_CALORIES, totalCalories);
        values.put(COLUMN_ORDER_ITEMS, itemsSummary);
        values.put(COLUMN_ORDER_USER_NAME, userName);
        values.put(COLUMN_ORDER_IMAGE, imageResId);
        values.put(COLUMN_ORDER_STATUS, "Ongoing");
        return db.insert(TABLE_ORDERS, null, values);
    }

    public Cursor getOrders(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ORDERS, null, COLUMN_ORDER_STATUS + "=?", new String[]{status}, null, null, COLUMN_ORDER_ID + " DESC");
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_STATUS, newStatus);
        db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
    }

    public void deleteOrdersByStatus(String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ORDERS, COLUMN_ORDER_STATUS + "=?", new String[]{status});
    }

    // ĐÃ THÊM: Lấy toàn bộ đơn hàng để phục vụ cho Dashboard
    public Cursor getAllOrdersForStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ORDERS, null, null, null, null, null, COLUMN_ORDER_ID + " DESC");
    }

    // Points History Operations
    public void addPointTransaction(String date, int amount, String source) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POINT_DATE, date);
        values.put(COLUMN_POINT_AMOUNT, amount);
        values.put(COLUMN_POINT_SOURCE, source);
        db.insert(TABLE_POINTS_HISTORY, null, values);
    }

    public Cursor getPointsHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_POINTS_HISTORY, null, null, null, null, null, COLUMN_POINT_ID + " DESC");
    }

    public void clearPointsHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POINTS_HISTORY, null, null);
    }

    // Gift Operations
    public void addGift(String drink, String table, String message, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GIFT_DRINK, drink);
        values.put(COLUMN_GIFT_TABLE, table);
        values.put(COLUMN_GIFT_MESSAGE, message);
        values.put(COLUMN_GIFT_DATE, date);
        db.insert(TABLE_GIFT, null, values);
    }

    // Favorites Operations
    public long addFavorite(int coffeeId, String name, String shot, String size, String ice, double price, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAV_COFFEE_ID, coffeeId);
        values.put(COLUMN_FAV_COFFEE_NAME, name);
        values.put(COLUMN_FAV_SHOT, shot);
        values.put(COLUMN_FAV_SIZE, size);
        values.put(COLUMN_FAV_ICE, ice);
        values.put(COLUMN_FAV_TOTAL_PRICE, price);
        values.put(COLUMN_FAV_TOTAL_CALORIES, calories);
        return db.insert(TABLE_FAVORITES, null, values);
    }

    public Cursor getFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FAVORITES, null, null, null, null, null, COLUMN_FAVORITE_ID + " DESC");
    }

    public void removeFavorite(int favoriteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_FAVORITE_ID + "=?", new String[]{String.valueOf(favoriteId)});
    }

    public void removeFavoriteByConfig(int coffeeId, String shot, String size, String ice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_FAV_COFFEE_ID + "=? AND " + COLUMN_FAV_SHOT + "=? AND " +
                COLUMN_FAV_SIZE + "=? AND " + COLUMN_FAV_ICE + "=?",
                new String[]{String.valueOf(coffeeId), shot, size, ice});
    }

    public boolean isFavorite(int coffeeId, String shot, String size, String ice) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_FAV_COFFEE_ID + "=? AND " + COLUMN_FAV_SHOT + "=? AND " +
                COLUMN_FAV_SIZE + "=? AND " + COLUMN_FAV_ICE + "=?";
        String[] selectionArgs = {String.valueOf(coffeeId), shot, size, ice};
        Cursor cursor = db.query(TABLE_FAVORITES, null, selection, selectionArgs, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }
}
