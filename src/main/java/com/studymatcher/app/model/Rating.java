package com.studymatcher.app.model;

/** Rating model for the partner rating system. */
public class Rating {
    public String ratingId;
    public String raterId;
    public String rateeId;
    public String matchId;
    public int    stars;       // 1–5
    public String reviewText;  // max 150 chars
    public long   createdAt;

    public Rating() {}
}
