package ca.yorku.eecs3311.profile;

import java.time.LocalDate;
/**
 * Represents a user profile.
 * Stores personal information including name, sex, date of birth,
 * physical measurements, and preferred unit system.
 */

public class UserProfile {
    private String     name;
    private String     sex;
    private LocalDate  dob;
    private double     height;
    private double     weight;
    private UnitSystem units;

    public UserProfile(
            String name,
            String sex,
            LocalDate dob,
            double height,
            double weight,
            UnitSystem units
    ) {
        this.name   = name;
        this.sex    = sex;
        this.dob    = dob;
        this.height = height;
        this.weight = weight;
        this.units  = units;
    }

    // Getters
    public String     getName()   { return name; }
    public String     getSex()    { return sex; }
    public LocalDate  getDob()    { return dob; }
    public double     getHeight() { return height; }
    public double     getWeight() { return weight; }
    public UnitSystem getUnits()  { return units; }

    @Override
    public String toString() {
        return name
                + " | " + sex
                + " | DOB: " + dob
                + " | Height: " + height
                + " | Weight: " + weight
                + " | Units: " + units;
    }
}
