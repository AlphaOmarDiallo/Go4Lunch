package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.repositories.BookingRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class YourLunchViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private LiveData<List<Booking>> bookings;
    private MutableLiveData<Booking> userBooking = new MutableLiveData<>();

    @Inject
    public YourLunchViewModel(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        bookings = bookingRepository.getAllBookings();
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

    public LiveData<Booking> getUserBooking(String userID){
        bookingRepository.getInstance();
        bookingRepository.getAllBookingsFromDataBase();

        if (bookings.getValue() != null){
            for (Booking booking : bookings.getValue()) {
                if(booking.getUserWhoBooked().equalsIgnoreCase(userID)){
                    userBooking.setValue(booking);
                    break;
                }
            }
        }

        return userBooking;

    }
}
