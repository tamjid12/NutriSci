package ca.yorku.eecs3311.nutrient;

/**
 * Represents a single nutrient value for a food item,
 * including its symbol (e.g., KCAL, PROT), the amount,
 * and the measurement unit (e.g., g, mg, kcal).
 */
public class NutrientInfo {
    private final String symbol;
    private final double amount;
    private final String unit;

    // Constructs a new NutrientInfo object.
    public NutrientInfo(String symbol, double amount, String unit) {
        this.symbol = symbol;
        this.amount = amount;
        this.unit   = unit;
    }
    public String getSymbol() { return symbol; }
    public double getAmount() { return amount; }
    public String getUnit()   { return unit; }
}
