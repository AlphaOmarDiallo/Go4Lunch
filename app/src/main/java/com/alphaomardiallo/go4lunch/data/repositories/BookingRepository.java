package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.annotation.NonNull;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;

public interface BookingRepository {

    void getInstance();

    void createBookingAndAddInDatabase(@NonNull Booking bookingToSave);

    void deleteBookingInDatabase(String bookingID);

    void getAllBookingsFromDataBase();

}
