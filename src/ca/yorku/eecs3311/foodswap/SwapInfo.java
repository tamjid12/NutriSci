package ca.yorku.eecs3311.foodswap;

public class SwapInfo {
    private double quantity;
    private double calories;
    private double protein;

    public SwapInfo(double quantity, double calories, double protein) {
        this.quantity = quantity;
        this.calories = calories;
        this.protein = protein;
    }

    public double getQuantity() { return quantity; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
}
