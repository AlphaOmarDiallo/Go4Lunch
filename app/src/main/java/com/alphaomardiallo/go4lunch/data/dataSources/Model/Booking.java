package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import androidx.annotation.Nullable;

import java.util.Date;

public class Booking {

    @Nullable
    private String bookingID;
    private Date bookingDate;
    private String bookedRestaurantID;
    private String bookedRestaurantName;
    private String userWhoBooked;

    public Booking() {
    }

    public Booking(String bookedRestaurantID, String bookedRestaurantName, String userWhoBooked) {
        this.bookedRestaurantID = bookedRestaurantID;
        this.bookedRestaurantName = bookedRestaurantName;
        this.userWhoBooked = userWhoBooked;
    }

    //Getters

    public String getBookingID() {
        return bookingID;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public String getBookedRestaurantID() {
        return bookedRestaurantID;
    }

    public String getBookedRestaurantName() {
        return bookedRestaurantName;
    }

    public String getUserWhoBooked() {
        return userWhoBooked;
    }

    //Setters

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setBookedRestaurantID(String bookedRestaurantID) {
        this.bookedRestaurantID = bookedRestaurantID;
    }

    public void setBookedRestaurantName(String bookedRestaurantName) {
        this.bookedRestaurantName = bookedRestaurantName;
    }

    public void setUserWhoBooked(String userWhoBooked) {
        this.userWhoBooked = userWhoBooked;
    }
}
