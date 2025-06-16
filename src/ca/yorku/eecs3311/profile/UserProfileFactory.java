package ca.yorku.eecs3311.profile;

import java.time.LocalDate;

public class UserProfileFactory {
    /**
     * Create a UserProfile with all required fields.
     *
     * @param name    the user’s name
     * @param sex     “Male” or “Female”
     * @param dob     date of birth as LocalDate
     * @param height  numeric height (m or in)
     * @param weight  numeric weight (kg or lb)
     * @param units   UnitSystem.METRIC or UnitSystem.IMPERIAL
     * @return a fully-populated UserProfile
     */
    public static UserProfile createUserProfile(
            String name,
            String sex,
            LocalDate dob,
            double height,
            double weight,
            UnitSystem units
    ) {
        return new UserProfile(name, sex, dob, height, weight, units);
    }
}
