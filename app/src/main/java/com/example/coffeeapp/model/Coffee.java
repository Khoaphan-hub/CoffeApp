package com.example.coffeeapp.model;

import java.io.Serializable;

public class Coffee implements Serializable {
    private int id;
    private String name;
    private double basePrice;
    private String description;
    private int imageResId;
    private int baseCalories;

    public Coffee(int id, String name, double basePrice, String description, int imageResId, int baseCalories) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.imageResId = imageResId;
        this.baseCalories = baseCalories;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public int getBaseCalories() { return baseCalories; }
}
