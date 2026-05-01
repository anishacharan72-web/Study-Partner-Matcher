package com.studymatcher.app.model;

import com.google.firebase.database.Exclude;
import java.util.List;

/**
 * User model — maps to Firebase Realtime DB + MySQL users table.
 */
public class User {
    public String userId;
    public String name;
    public String email;
    public String institution;
    public String academicLevel;   // HIGH_SCHOOL, UG, PG, PHD
    public String studyGoal;       // EXAM_PREP, ASSIGNMENTS, GENERAL
    public String modePreference;  // ONLINE, IN_PERSON, BOTH
    public String bio;
    public String profilePhotoUrl;
    public double ratingAvg;
    public int    ratingCount;
    public long   createdAt;
    public long   availabilityUpdatedAt;
    public List<String> subjectIds;

    public User() { /* Required for Firebase deserialization */ }

    public User(String userId, String name, String email, String institution) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.institution = institution;
        this.ratingAvg = 0.0;
        this.ratingCount = 0;
        this.createdAt = System.currentTimeMillis();
    }

    @Exclude
    public boolean isProfileComplete() {
        return academicLevel != null && studyGoal != null && modePreference != null
                && subjectIds != null && !subjectIds.isEmpty();
    }

    @Exclude
    public boolean isAvailabilityStale() {
        long sevenDaysMs = 7L * 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - availabilityUpdatedAt > sevenDaysMs;
    }
}
