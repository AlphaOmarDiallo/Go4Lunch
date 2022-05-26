package com.alphaomardiallo.go4lunch.data.dataSources.Model;

import java.util.Date;

public class Booking {

    private long bookingID;
    private Date bookingDate;
    private String bookedRestaurantID;

    public Booking(long bookingID, Date bookingDate, String bookedRestaurantID) {
        this.bookingID = bookingID;
        this.bookingDate = bookingDate;
        this.bookedRestaurantID = bookedRestaurantID;
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
}
