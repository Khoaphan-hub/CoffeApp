package com.example.coffeeapp.utils;

public class CalorieEngine {
    
    public static int calculateCalories(int baseCalories, String size, String shot) {
        double multiplier = 1.0;
        switch (size) {
            case "Medium": multiplier = 1.5; break;
            case "Large": multiplier = 2.0; break;
        }
        
        int extraShotCalories = shot.equals("Double") ? 5 : 0;
        
        return (int) (baseCalories * multiplier) + extraShotCalories;
    }

    public static double calculatePrice(double basePrice, String size, String shot) {
        double extraSize = 0.0;
        switch (size) {
            case "Medium": extraSize = 0.50; break;
            case "Large": extraSize = 1.00; break;
        }
        
        double extraShot = shot.equals("Double") ? 1.00 : 0.0;
        
        return basePrice + extraSize + extraShot;
    }
}
