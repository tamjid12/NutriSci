
package ca.yorku.eecs3311.meal;
/**
 * Represents a single food item that is part of a MealEntry.
 * Each MealItem has an associated quantity and belongs to a specific MealEntry.
 * This class is used for both data storage and in-memory meal representation.
 */
public class MealItem {
    private int id;         // DB PK (0 before save)
    private int entryId;    // FK to MealEntry.id
    private String foodName;
    private double quantity;

    public MealItem(int entryId, String foodName, double quantity) {
        this(0, entryId, foodName, quantity);
    }

    public MealItem(int id, int entryId, String foodName, double quantity) {
        this.id = id;
        this.entryId = entryId;
        this.foodName = foodName;
        this.quantity = quantity;
    }

    // Getters
    public int getId()        { return id; }
    public int getEntryId()   { return entryId; }
    public String getFoodName(){ return foodName; }
    public double getQuantity(){ return quantity; }

    @Override
    public String toString() {
        return foodName + " (" + quantity + ")";
    }
}
