package com.alphaomardiallo.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.viewModels.YourLunchViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityYourLunchBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class YourLunchActivity extends AppCompatActivity {

    private static final String USER_ID = "userID";
    private ActivityYourLunchBinding binding;
    private YourLunchViewModel viewModel;
    private String userID;
    private User currentUser;
    private Booking userBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYourLunchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(YourLunchViewModel.class);
        setContentView(view);

        gettingIntent();

        gettingUserData();

    }

    /**
     * Getting data from intent
     */

    private void gettingIntent(){
        Intent intent = getIntent();
        userID = intent.getStringExtra(USER_ID);
    }

    /**
     * User data
     */

    private void gettingUserData(){
        viewModel.getDataBaseInstanceUser();
        viewModel.getCurrentUserDataFromFireStore(userID);
        viewModel.observeCurrentUser().observe(this, this::updateUser);
    }

    private void updateUser(User user){
        currentUser = user;
        getUserBooking();
    }

    /**
     * Booking Data
     */

    private void getUserBooking() {
        viewModel.getUserBooking(userID).observe(this, this::updateUserBooking);
    }

    private void updateUserBooking(Booking booking){
        userBooking = booking;
        System.out.println(booking);

        if (booking != null) {
            binding.tvRestaurantNameYourLunch.setText(String.format(getString(R.string.yes_booking), booking.getBookedRestaurantName()));
        } else {
            binding.tvRestaurantNameYourLunch.setText(getString(R.string.no_booking));
        }
    }
}