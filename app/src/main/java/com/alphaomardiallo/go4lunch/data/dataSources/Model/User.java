package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    @Nullable
    String bookingOfTheDay;

    public User(String userId, String userName, @Nullable String userPicture) {
        this.uid = userId;
        this.username = userName;
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

    @Nullable
    public String getBookingOfTheDay() {
        return bookingOfTheDay;
    }

    //SETTERS

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setBookingOfTheDay(@Nullable String bookingOfTheDay) {
        this.bookingOfTheDay = bookingOfTheDay;
    }
}
