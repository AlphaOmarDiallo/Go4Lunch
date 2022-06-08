package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class YourLunchViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PlacesAPIRepository placesAPIRepository;

    @Inject
    public YourLunchViewModel(UserRepository userRepository, BookingRepository bookingRepository, PlacesAPIRepository placesAPIRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.placesAPIRepository = placesAPIRepository;
    }

    /**
     * User
     */

    public void getDataBaseInstanceUser() {
        userRepository.getDataBaseInstance();
    }

    public void getCurrentUserDataFromFireStore(String userID) {
        userRepository.getUserDataFromDataBase(userID);
    }

    public LiveData<User> observeCurrentUser() {
        return userRepository.observeCurrentUser();
    }

    /**
     * Booking Data
     */

    public LiveData<List<Booking>> getAllBookings() {
        bookingRepository.getInstance();
        bookingRepository.getAllBookingsFromDataBase();
        return bookingRepository.getAllBookings();
    }

    /**
     * Favourite
     */

    public void setListOfFavourites(List<String> favRestaurantID) {

        if (favRestaurantID != null) {
            placesAPIRepository.fetchDetailsForFavourite(favRestaurantID);
        }
    }

    public LiveData<List<Result>> observeFavList() {
        return placesAPIRepository.observeListFavouriteRestaurant();
    }
}
