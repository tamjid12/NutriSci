package ca.yorku.eecs3311.profile;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {
    private List<UserProfile> profiles = new ArrayList<>();

    // Simulates saving the profile
    public boolean saveProfile(UserProfile profile) {
        profiles.add(profile);
        return true; // Always "success" for D1
    }

    // To get saved profiles (optional)
    public List<UserProfile> getProfiles() {
        return profiles;
    }
}
