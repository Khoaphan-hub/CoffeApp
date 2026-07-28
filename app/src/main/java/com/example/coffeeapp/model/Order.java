package com.example.coffeeapp.model;

public class Order {
    private int id;
    private String date;
    private double totalPrice;
    private String itemsSummary;
    private String status; // Ongoing, History
    private String userName;   // MỚI THÊM
    private int imageResId;    // MỚI THÊM

    // Cập nhật Constructor
    public Order(int id, String date, double totalPrice, String itemsSummary, String status, String userName, int imageResId) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
        this.itemsSummary = itemsSummary;
        this.status = status;
        this.userName = userName;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public double getTotalPrice() { return totalPrice; }
    public String getItemsSummary() { return itemsSummary; }
    public String getStatus() { return status; }
    public String getUserName() { return userName; } // MỚI THÊM
    public int getImageResId() { return imageResId; } // MỚI THÊM
}