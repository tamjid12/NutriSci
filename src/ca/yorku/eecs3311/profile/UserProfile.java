package ca.yorku.eecs3311.profile;

public class UserProfile {
    private String name;
    private String sex;
    private int age;

    public UserProfile(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
    // Getters
    public String getName() { return name; }
    public String getSex() { return sex; }
    public int getAge() { return age; }
}
