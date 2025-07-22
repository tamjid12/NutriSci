
package ca.yorku.eecs3311.meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single meal entry made by a user.
 * Each meal entry is linked to a specific profile and includes the type of meal,
 * date and time of consumption, and a list of food items.
 * This class is used both for in-memory manipulation and database interaction.
 */

public class MealEntry {
    private int id;                        // DB PK (0 before save)
    private String profileName;
    private MealType mealType;
    private LocalDate date;
    private LocalTime time;
    private List<MealItem> items = new ArrayList<>();

    // Constructor for new entries
    public MealEntry(String profileName,
                     MealType mealType,
                     LocalDate date,
                     LocalTime time) {
        this(0, profileName, mealType, date, time, new ArrayList<>());
    }

    // Full constructor for loading from DB
    public MealEntry(int id,
                     String profileName,
                     MealType mealType,
                     LocalDate date,
                     LocalTime time,
                     List<MealItem> items) {
        this.id = id;
        this.profileName = profileName;
        this.mealType    = mealType;
        this.date        = date;
        this.time        = time;
        this.items       = items;
    }

    // Getters / setters
    public int getId()               { return id; }
    public String getProfileName()   { return profileName; }
    public MealType getMealType()    { return mealType; }
    public LocalDate getDate()       { return date; }
    public LocalTime getTime()       { return time; }
    public List<MealItem> getItems() { return items; }
    public void setItems(List<MealItem> items) {
        this.items = items;
    }

    //Generates a string summary of the meal for display/logging.
    @Override
    public String toString() {
        return "[" + id + "] " + profileName
                + " | " + mealType
                + " | " + date + " " + time
                + " | items=" + items.size();
    }
}


