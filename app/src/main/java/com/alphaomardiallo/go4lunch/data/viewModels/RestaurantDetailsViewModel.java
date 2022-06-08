package com.alphaomardiallo.go4lunch.data.viewModels;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantDetailsViewModel extends ViewModel {

    private final PlacesAPIRepository placesAPIRepository;
    private final LocationRepository locationRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private MutableLiveData<List<User>> userDiningCurrentRestaurant = new MutableLiveData<>();

    @Inject
    public RestaurantDetailsViewModel(PlacesAPIRepository placesAPIRepository, LocationRepository locationRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.placesAPIRepository = placesAPIRepository;
        this.locationRepository = locationRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Result> getAllDetails(String placeID) {
        placesAPIRepository.fetchAllDetails(placeID);
        return placesAPIRepository.getDetails();
    }

    public LiveData<Location> getLocation(Context context, Activity activity) {
        locationRepository.startLocationRequest(context, activity);
        return locationRepository.getCurrentLocation();
    }

    public Location getOfficeLocation() {
        return locationRepository.getOfficeLocation();
    }

    /**
     * Firebase Firestore
     */

    // user

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public void getDataBaseInstanceUser() {
        userRepository.getDataBaseInstance();
    }

    public LiveData<List<User>> getAllUsers(){
        userRepository.getAllUsersFromDataBase();
        return userRepository.getAllUsers();
    }

    // booking

    public void getDatabaseInstanceBooking() {
        bookingRepository.getInstance();
    }

    public Booking checkIfUserHasBooking() {
        Booking hasBooking = null;
        List<Booking> bookings = bookingRepository.getAllBookings().getValue();
        String userID = userRepository.getCurrentUserID();

        for (Booking booking : bookings) {
            if (booking.getUserWhoBooked().equalsIgnoreCase(userID)) {
                hasBooking = booking;
                break;
            }
        }

        return hasBooking;
    }

    public void createBooking(Booking bookingToSave) {
        bookingRepository.createBookingAndAddInDatabase(bookingToSave);
    }

    public void updateBooking(String bookingID, String restaurantID, String restaurantName) {
        bookingRepository.updateBooking(bookingID, restaurantID, restaurantName);
    }

    public void addRestaurantToFavourite(String restaurantID) {
       userRepository.addFavouriteRestaurant(userRepository.getCurrentUserID(), restaurantID);
    }

    public void removeRestaurantFromFavourite(String restaurantID) {
        userRepository.removeFavouriteRestaurant(userRepository.getCurrentUserID(), restaurantID);
    }

    public void deleteBooking(String bookingID) {
        bookingRepository.deleteBookingInDatabase(bookingID);
    }

    public void observeBookingsFromDataBase() {
        bookingRepository.getAllBookingsFromDataBase();
    }

    public LiveData<List<Booking>> getAllBookings() {
        return bookingRepository.getAllBookings();
    }

    public void setListUserWhoBookedThisRestaurant (List<User> list) {
        userDiningCurrentRestaurant.setValue(list);
    }

    public LiveData<List<User>> getUserWhoBookedThatRestaurant() {
        return userDiningCurrentRestaurant;
    }

    public void getCurrentUserDataFromFireStore(String userID) {
        userRepository.getUserDataFromDataBase(userID);
    }

    public LiveData<User> observeCurrentUser() {
        return userRepository.observeCurrentUser();
    }
}
