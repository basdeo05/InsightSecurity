package com.example.googlelogin;

// Users: User Class that defines how users will be added to the database. Name, Email, notified, userName, notfication token

public class Users {

    String userEmail;
    Boolean NoiseEvent = false;
    String notificationToken;
    Boolean notified = false;
    String userName;

    public  Users(){

    }

    public Users(String userEmail, Boolean noiseEvent, String notificationToken, Boolean notified, String userName) {
        this.userEmail = userEmail;
        NoiseEvent = noiseEvent;
        this.notificationToken = notificationToken;
        this.notified = notified;
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Boolean getNoiseEvent() {
        return NoiseEvent;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public Boolean getNotified() {
        return notified;
    }

    public String getUserName() {
        return userName;
    }
}
