package com.example.googlelogin;

public class Users {

    String databaseID;
    String authenicationID;
    String userName;
    String userEmail;
    Boolean NoiseEvent = false;
    String notificationToken;

    public  Users(){

    }

    //parametrize constructor

    public Users(String databaseID, String authenicationID, String userName, String userEmail, Boolean noiseEvent, String notificationToken) {
        this.databaseID = databaseID;
        this.authenicationID = authenicationID;
        this.userName = userName;
        this.userEmail = userEmail;
        NoiseEvent = noiseEvent;
        this.notificationToken = notificationToken;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public String getAuthenicationID() {
        return authenicationID;
    }

    public String getUserName() {
        return userName;
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
}
