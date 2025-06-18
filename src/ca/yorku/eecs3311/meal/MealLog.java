package ca.yorku.eecs3311.meal;

import java.time.LocalDateTime;

public class MealLog {
    private int            id;           // for DB PK (0 before save)
    private String         profileName;  // which user
    private MealType       mealType;
    private LocalDateTime  timestamp;
    private String         foodItem;
    private double         quantity;     // e.g. grams or servings

    // Constructor for new logs (id assigned by DB)
    public MealLog(String profileName,
                   MealType mealType,
                   LocalDateTime timestamp,
                   String foodItem,
                   double quantity) {
        this(0, profileName, mealType, timestamp, foodItem, quantity);
    }

    // Full constructor (for reading from DB)
    public MealLog(int id,
                   String profileName,
                   MealType mealType,
                   LocalDateTime timestamp,
                   String foodItem,
                   double quantity) {
        this.id = id;
        this.profileName = profileName;
        this.mealType    = mealType;
        this.timestamp   = timestamp;
        this.foodItem    = foodItem;
        this.quantity    = quantity;
    }

    // Getters (and setters if you like)
    public int getId()                  { return id; }
    public String getProfileName()      { return profileName; }
    public MealType getMealType()       { return mealType; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getFoodItem()         { return foodItem; }
    public double getQuantity()         { return quantity; }

    @Override
    public String toString() {
        return "[" + id + "] "
                + profileName + " | "
                + mealType + " @ " + timestamp + " → "
                + foodItem + ": " + quantity;
    }
}
