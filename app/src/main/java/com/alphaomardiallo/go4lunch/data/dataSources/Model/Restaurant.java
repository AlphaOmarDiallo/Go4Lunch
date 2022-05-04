package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import android.graphics.Bitmap;

public class Restaurant {
    String restaurantName;
    long distance;
    Bitmap restaurantPhoto;
    String restaurantType;
    String restaurantAddress;
    long openingHours;
    double restaurantRating;
    long workmatesGoingToThisRestaurant;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public Bitmap getRestaurantPhoto() {
        return restaurantPhoto;
    }

    public void setRestaurantPhoto(Bitmap restaurantPhoto) {
        this.restaurantPhoto = restaurantPhoto;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public long getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(long openingHours) {
        this.openingHours = openingHours;
    }

    public double getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(double restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public long getWorkmatesGoingToThisRestaurant() {
        return workmatesGoingToThisRestaurant;
    }

    public void setWorkmatesGoingToThisRestaurant(long workmatesGoingToThisRestaurant) {
        this.workmatesGoingToThisRestaurant = workmatesGoingToThisRestaurant;
    }
}
