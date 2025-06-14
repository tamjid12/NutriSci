package ca.yorku.eecs3311.profile;

public class UserProfileFactory {
    public static UserProfile createUserProfile(String name, String sex, int age) {
        return new UserProfile(name, sex, age);
    }
}
