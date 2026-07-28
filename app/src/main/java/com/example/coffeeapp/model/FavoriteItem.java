package com.example.coffeeapp.model;

import java.io.Serializable;

public class FavoriteItem implements Serializable {
    private int id;
    private int coffeeId;
    private String name;
    private String shot;
    private String size;
    private String ice;
    private double totalPrice;
    private int totalCalories;

    public FavoriteItem(int id, int coffeeId, String name, String shot, String size, String ice, double totalPrice, int totalCalories) {
        this.id = id;
        this.coffeeId = coffeeId;
        this.name = name;
        this.shot = shot;
        this.size = size;
        this.ice = ice;
        this.totalPrice = totalPrice;
        this.totalCalories = totalCalories;
    }

    public int getId() { return id; }
    public int getCoffeeId() { return coffeeId; }
    public String getName() { return name; }
    public String getShot() { return shot; }
    public String getSize() { return size; }
    public String getIce() { return ice; }
    public double getTotalPrice() { return totalPrice; }
    public int getTotalCalories() { return totalCalories; }
}
