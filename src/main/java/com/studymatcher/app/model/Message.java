package com.studymatcher.app.model;

/** Chat message model — stored in Firebase Realtime Database. */
public class Message {
    public String messageId;
    public String senderId;
    public String receiverId;
    public String text;
    public long   timestamp;
    public String status;   // SENT, DELIVERED, READ
    public boolean deleted;

    public Message() {}

    public Message(String senderId, String receiverId, String text) {
        this.senderId   = senderId;
        this.receiverId = receiverId;
        this.text       = text;
        this.timestamp  = System.currentTimeMillis();
        this.status     = "SENT";
        this.deleted    = false;
    }

    public enum Status { SENT, DELIVERED, READ }
}
