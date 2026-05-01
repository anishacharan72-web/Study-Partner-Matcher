package com.studymatcher.app.model;

/** Study session — scheduled by matched partners via chat. */
public class Session {
    public String sessionId;
    public String matchId;
    public long   scheduledAt;       // epoch ms
    public int    durationMinutes;
    public String status;            // SCHEDULED, COMPLETED, CANCELLED
    public String locationNote;      // optional free-text
    public boolean ratingPromptSent;

    public Session() {}
}
