package com.example.coffeeapp.model;

public class Order {
    private int id;
    private String date;
    private double totalPrice;
    private String itemsSummary;
    private String status; // Ongoing, History

    public Order(int id, String date, double totalPrice, String itemsSummary, String status) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
        this.itemsSummary = itemsSummary;
        this.status = status;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public double getTotalPrice() { return totalPrice; }
    public String getItemsSummary() { return itemsSummary; }
    public String getStatus() { return status; }
}
