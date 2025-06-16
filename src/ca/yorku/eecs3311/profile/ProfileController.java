package ca.yorku.eecs3311.profile;

import java.util.List;

public class ProfileController {
    private ProfileDAO dao = new ProfileDAO();

    /**
     * Save the given profile to the database.
     * @return true if saved successfully, false otherwise
     */
    public boolean saveProfile(UserProfile profile) {
        return dao.save(profile);
    }

    /**
     * Retrieve all saved profiles from the database.
     */
    public List<UserProfile> getProfiles() {
        return dao.findAll();
    }
}
