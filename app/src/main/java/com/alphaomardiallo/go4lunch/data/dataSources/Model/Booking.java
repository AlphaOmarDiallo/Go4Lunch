package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import java.util.Date;

public class Booking {

    private long bookingID;
    private Date bookingDate;
    private String bookedRestaurantID;
    private User userWhoBooked;

    public Booking(long bookingID, Date bookingDate, String bookedRestaurantID, User userWhoBooked) {
        this.bookingID = bookingID;
        this.bookingDate = bookingDate;
        this.bookedRestaurantID = bookedRestaurantID;
        this.userWhoBooked = userWhoBooked;
    }

    //Getters

    public long getBookingID() {
        return bookingID;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public String getBookedRestaurantID() {
        return bookedRestaurantID;
    }

    public User getUserWhoBooked() {
        return userWhoBooked;
    }

    //Setters

    public void setBookingID(long bookingID) {
        this.bookingID = bookingID;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setBookedRestaurantID(String bookedRestaurantID) {
        this.bookedRestaurantID = bookedRestaurantID;
    }

    public void setUserWhoBooked(User userWhoBooked) {
        this.userWhoBooked = userWhoBooked;
    }
}
