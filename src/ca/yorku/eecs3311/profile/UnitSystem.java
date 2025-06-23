package ca.yorku.eecs3311.profile;
/**
 * Represents the system of measurement for user profiles.
 * Supports both metric (cm, kg) and imperial (in, lb) units.
 */
public enum UnitSystem {
    METRIC("cm", "kg"),
    IMPERIAL("in", "lb");

    private final String heightUnit;
    private final String weightUnit;

    UnitSystem(String heightUnit, String weightUnit) {
        this.heightUnit = heightUnit;
        this.weightUnit = weightUnit;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public String getWeightUnit() {
        return weightUnit;
    }
}

