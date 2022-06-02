package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    private String userEmail;
    @Nullable
    private String urlPicture;
    @Nullable
    Boolean bookingOfTheDay;

    public User(String userId, String userName, String userEmail, @Nullable String userPicture) {
        this.uid = userId;
        this.username = userName;
        this.userEmail = userEmail;
        this.urlPicture = userPicture;
        this.bookingOfTheDay = null;
    }

    // GETTERS

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public String getUserEmail() {
        return userEmail;
    }

    @Nullable
    public Boolean getBookingOfTheDay() {
        return bookingOfTheDay;
    }

    //SETTERS

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setBookingOfTheDay(@Nullable Boolean bookingOfTheDay) {
        this.bookingOfTheDay = bookingOfTheDay;
    }
}
