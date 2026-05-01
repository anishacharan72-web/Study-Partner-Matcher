package com.studymatcher.app.model;

/** Match model — composite scoring result from Spring Boot API. */
public class Match {
    public String matchId;
    public String userAId;
    public String userBId;
    public int    score;       // 0–100
    public String status;      // PENDING, ACCEPTED, DECLINED
    public long   createdAt;

    // Populated when fetching match suggestions (joined partner data)
    public User   partner;

    public Match() {}

    public enum Status {
        PENDING, ACCEPTED, DECLINED
    }
}
