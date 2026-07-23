package com.example.coffeeapp.model;

public class PointTransaction {
    private int id;
    private String date;
    private int amount;
    private String source;

    public PointTransaction(int id, String date, int amount, String source) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.source = source;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public int getAmount() { return amount; }
    public String getSource() { return source; }
}
