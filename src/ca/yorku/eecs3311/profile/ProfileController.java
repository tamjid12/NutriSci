package ca.yorku.eecs3311.profile;

import java.util.List;
/**
 * Controller class responsible for managing user profiles.
 * Acts as a bridge between the UI and the database layer (DAO).
 */

public class ProfileController {
    private final ProfileDAO dao = new ProfileDAO();

    /** Create a new profile. */
    public boolean saveProfile(UserProfile p) {
        return dao.save(p);
    }

    /** List all profiles. */
    public List<UserProfile> getProfiles() {
        return dao.findAll();
    }

    /** Fetch one profile by name. */
    public UserProfile findByName(String name) {
        return dao.findByName(name);
    }

    /** Update an existing profile. */
    public boolean updateProfile(UserProfile p) {
        return dao.update(p);
    }

    /** Delete a profile (and its related data). */
    public boolean deleteProfile(String name) {
        return dao.delete(name);
    }
}
