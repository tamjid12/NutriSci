// src/ca/yorku/eecs3311/nutrient/NutrientInfo.java
package ca.yorku.eecs3311.nutrient;

public class NutrientInfo {
    private final String symbol;
    private final double amount;
    private final String unit;

    public NutrientInfo(String symbol, double amount, String unit) {
        this.symbol = symbol;
        this.amount = amount;
        this.unit   = unit;
    }
    public String getSymbol() { return symbol; }
    public double getAmount() { return amount; }
    public String getUnit()   { return unit; }
}
