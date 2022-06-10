package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;

import java.util.List;

public interface BookingRepository {

    void getInstance();

    void createBookingAndAddInDatabase(@NonNull Booking bookingToSave, Context context);

    void deleteBookingInDatabase(String bookingID);

    void updateBooking(String bookingID, String restaurantID, String restaurantName);

    void getAllBookingsFromDataBase();

    LiveData<List<Booking>> getAllBookings();

    void deleteBookingsFromPreviousDays();

}
