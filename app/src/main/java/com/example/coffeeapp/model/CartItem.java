package com.example.coffeeapp.model;

public class CartItem {
    private int id;
    private int coffeeId;
    private String coffeeName;
    private String shot; // Single, Double
    private String size; // Small, Medium, Large
    private String ice;  // None, Less, Full
    private double totalPrice;
    private int totalCalories;
    private int quantity;

    public CartItem(int id, int coffeeId, String coffeeName, String shot, String size, String ice, double totalPrice, int totalCalories, int quantity) {
        this.id = id;
        this.coffeeId = coffeeId;
        this.coffeeName = coffeeName;
        this.shot = shot;
        this.size = size;
        this.ice = ice;
        this.totalPrice = totalPrice;
        this.totalCalories = totalCalories;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getCoffeeId() { return coffeeId; }
    public String getCoffeeName() { return coffeeName; }
    public String getShot() { return shot; }
    public String getSize() { return size; }
    public String getIce() { return ice; }
    public double getTotalPrice() { return totalPrice; }
    public int getTotalCalories() { return totalCalories; }
    public int getQuantity() { return quantity; }
}
