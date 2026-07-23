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
    private static final int DATABASE_VERSION = 2;

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

    // Orders Table
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_ORDER_PRICE = "total_price";
    public static final String COLUMN_ORDER_ITEMS = "items_summary";
    public static final String COLUMN_ORDER_STATUS = "status"; // Ongoing, History

    // Points History Table
    public static final String TABLE_POINTS_HISTORY = "points_history";
    public static final String COLUMN_POINT_ID = "id";
    public static final String COLUMN_POINT_DATE = "date";
    public static final String COLUMN_POINT_AMOUNT = "amount";
    public static final String COLUMN_POINT_SOURCE = "source";

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

        String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ORDER_DATE + " TEXT,"
                + COLUMN_ORDER_PRICE + " REAL,"
                + COLUMN_ORDER_ITEMS + " TEXT,"
                + COLUMN_ORDER_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_ORDERS_TABLE);

        String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS_HISTORY + "("
                + COLUMN_POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POINT_DATE + " TEXT,"
                + COLUMN_POINT_AMOUNT + " INTEGER,"
                + COLUMN_POINT_SOURCE + " TEXT" + ")";
        db.execSQL(CREATE_POINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS_HISTORY + "("
                    + COLUMN_POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_POINT_DATE + " TEXT,"
                    + COLUMN_POINT_AMOUNT + " INTEGER,"
                    + COLUMN_POINT_SOURCE + " TEXT" + ")";
            db.execSQL(CREATE_POINTS_TABLE);
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS_HISTORY);
            onCreate(db);
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

    public long placeOrder(String date, double totalPrice, String itemsSummary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_DATE, date);
        values.put(COLUMN_ORDER_PRICE, totalPrice);
        values.put(COLUMN_ORDER_ITEMS, itemsSummary);
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
}
