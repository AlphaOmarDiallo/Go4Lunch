package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import androidx.annotation.Nullable;

import java.util.List;

public class User {

    private String uid;
    private String username;
    private String userEmail;
    @Nullable
    private String urlPicture;
    @Nullable
    List<String> favouriteRestaurants;

    public User() {
    }

    public User(String userId, String userName, String userEmail, @Nullable String userPicture) {
        this.uid = userId;
        this.username = userName;
        this.userEmail = userEmail;
        this.urlPicture = userPicture;
        this.favouriteRestaurants = null;
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
    public List<String> getFavouriteRestaurants() {
        return favouriteRestaurants;
    }

}
