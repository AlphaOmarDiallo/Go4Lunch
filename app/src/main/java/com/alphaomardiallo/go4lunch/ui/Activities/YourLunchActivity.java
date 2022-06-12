package com.alphaomardiallo.go4lunch.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.viewModels.YourLunchViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityYourLunchBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickRestaurantListener;
import com.alphaomardiallo.go4lunch.ui.adapters.FavListRestaurantAdapter;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class YourLunchActivity extends AppCompatActivity implements OnClickRestaurantListener {

    private static final String USER_ID = "userID";
    private static final String ID = "id";
    private ActivityYourLunchBinding binding;
    private YourLunchViewModel viewModel;
    private String userID;
    private User currentUser;
    private Booking userBooking;
    private FavListRestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYourLunchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(YourLunchViewModel.class);
        setContentView(view);

        setRecyclerView();

        gettingIntent();

        gettingUserData();

    }

    /**
     * Set recyclerView
     */
    private void setRecyclerView() {
        binding.rvFavouritesYourLunch.setHasFixedSize(true);
        binding.rvFavouritesYourLunch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new FavListRestaurantAdapter(new FavListRestaurantAdapter.ListDiff(), this);
        binding.rvFavouritesYourLunch.setAdapter(adapter);
    }

    /**
     * Getting data from intent
     */

    private void gettingIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra(USER_ID);
    }

    /**
     * User data
     */

    private void gettingUserData() {
        viewModel.getDataBaseInstanceUser();
        viewModel.getCurrentUserDataFromFireStore(userID);
        viewModel.observeCurrentUser().observe(this, this::updateUser);
    }

    private void updateUser(User user) {
        currentUser = user;
        getAllBooking();
        setListFav(currentUser.getFavouriteRestaurants());
    }

    /**
     * Booking Data
     */

    private void getAllBooking() {
        viewModel.getAllBookings().observe(this, this::updateUserBooking);
    }

    private void updateUserBooking(List<Booking> list) {

        for (Booking booking : list) {
            if (booking.getUserWhoBooked().equalsIgnoreCase(userID)) {
                userBooking = booking;
                break;
            }
        }

        if (userBooking != null) {
            binding.tvRestaurantNameYourLunch.setText(String.format(getString(R.string.yes_booking), userBooking.getBookedRestaurantName()));
            binding.cardViewYourLunch.setOnClickListener(view -> openDetailActivity(userBooking.getBookedRestaurantID()));
        } else {
            binding.tvRestaurantNameYourLunch.setText(getString(R.string.no_booking));
        }
    }

    /**
     * FavList settings
     */

    private void setListFav(List<String> favList) {
        viewModel.setListOfFavourites(favList);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            binding.progressBar.setVisibility(View.INVISIBLE);
            getListFav();
        }, 3000);
    }

    private void getListFav() {
        viewModel.observeFavList().observe(this, adapter::submitList);
    }

    /**
     * Detail activity
     */

    private void openDetailActivity(String restaurantID) {
        Intent intent = new Intent(this, RestaurantDetails.class);
        intent.putExtra(ID, restaurantID);
        startActivity(intent);
    }

    /**
     * RV onclick
     */

    @Override
    public void onClickRestaurant(int position) {
        openDetailActivity(Objects.requireNonNull(currentUser.getFavouriteRestaurants()).get(position));
    }

}