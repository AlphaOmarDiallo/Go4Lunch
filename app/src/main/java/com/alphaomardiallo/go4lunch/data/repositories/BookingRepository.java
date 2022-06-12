package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;

import java.util.List;

public interface BookingRepository {

    void getInstance();

    void createBookingInDatabase(@NonNull Booking bookingToSave, Context context);

    void deleteBookingInDatabase(String bookingID, Context context);

    void updateBookingInDataBase(String bookingID, String restaurantID, String restaurantName, Context context);

    void getAllBookingsFromDataBase();

    LiveData<List<Booking>> getAllBookingsAsList();

    void deleteBookingsFromPreviousDays(Context context);

}
