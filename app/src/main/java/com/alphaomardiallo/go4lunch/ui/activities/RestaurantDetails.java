package com.alphaomardiallo.go4lunch.ui.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.viewModels.RestaurantDetailsViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.alphaomardiallo.go4lunch.ui.adapters.WorkmatesJoiningAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetails extends AppCompatActivity {

    private static final String KEY_RESTAURANT_PLACE_ID = "id";
    private ActivityRestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel viewModel;
    private Location location;
    private String restaurantID;
    private String restaurantPhoneNumber;
    private String restaurantWebsite;
    private String restaurantName;
    private String restaurantAddress;
    private double restaurantLatitude;
    private double restaurantLongitude;
    private List<Booking> allBookings;
    private Intent intent;
    private final WorkmatesJoiningAdapter adapter = new WorkmatesJoiningAdapter(new WorkmatesJoiningAdapter.ListDiff(), this);
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(RestaurantDetailsViewModel.class);
        setContentView(view);

        retrieveInformationFromIntent();

        recyclerViewSetup();

        observeLocation();

        observeAllBookings();

        setupFAB();

        observeUsersDiningIn();

        getCurrentUser();

        setupLikeButton();

    }


    /**
     * Getting location and restaurant DATA
     */

    private void observeLocation() {
        location = viewModel.getOfficeLocation();
        viewModel.getLocation(getBaseContext(), this).observe(this, this::updateCurrentLocationAndDistance);
    }

    private void updateCurrentLocationAndDistance(Location location) {
        this.location = location;
        Location restaurantLocationUpdate = new Location(getString(R.string.restaurant_location));
        restaurantLocationUpdate.setLongitude(restaurantLongitude);
        restaurantLocationUpdate.setLatitude(restaurantLatitude);
        binding.tvDistanceRestaurantDetails.setText(String.format(getString(R.string.distance_in_meters_d), Math.round(this.location.distanceTo(restaurantLocationUpdate))));
    }

    private void retrieveInformationFromIntent() {
        intent = getIntent();
        restaurantID = intent.getStringExtra(KEY_RESTAURANT_PLACE_ID);
        viewModel.getAllDetails(restaurantID).observe(this, this::setupViews);
    }

    /**
     * Booking related methods
     */

    private void observeAllBookings() {
        viewModel.getDatabaseInstanceBooking();
        viewModel.observeBookingsFromDataBase();
        viewModel.getAllBookings().observe(this, this::updateBookingList);
    }

    private void updateBookingList(List<Booking> allBookings) {
        this.allBookings = allBookings;
        viewModel.getDataBaseInstanceUser();
        viewModel.getAllUsers().observe(this, this::setUserJoiningList);
        setupFABColor();
    }

    private void createBooking(Booking bookingToCreate, Context context) {
        viewModel.createBooking(bookingToCreate, context);
        Toast.makeText(this, R.string.created_booking, Toast.LENGTH_SHORT).show();
    }

    private void saveRestaurantIDInSharePreferences() {
        Context context = this;
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                getString(R.string.preferences_main_file), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_restaurant_ID), restaurantID);
        editor.putString(getString(R.string.shared_pref_restaurant_Name), restaurantName);
        editor.putString(getString(R.string.shared_pref_restaurant_Address), restaurantAddress);
        editor.apply();
    }

    /**
     * View settings
     */

    private void setupViews(@Nullable Result restaurant) {
        binding.ibWebSiteDetails.setVisibility(View.INVISIBLE);
        binding.ibCallDetail.setVisibility(View.INVISIBLE);
        binding.tvCallDetails.setVisibility(View.INVISIBLE);
        binding.tvWebsiteDetails.setVisibility(View.INVISIBLE);

        if (restaurant != null) {
            String restaurantPhoto = getString(R.string.restaurantPlaceHolder);
            restaurantName = restaurant.getName();
            double restaurantRating = restaurant.getRating();
            restaurantAddress = restaurant.getVicinity();
            boolean restaurantIsOpenNow = restaurant.getOpeningHours().isOpenNow();
            restaurantLatitude = restaurant.getGeometry().getLocation().getLat();
            restaurantLongitude = restaurant.getGeometry().getLocation().getLng();

            try {
                restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.PLACES_API_KEY;
            } catch (Exception e) {
                Log.e(TAG, "setupViews: Failed ", e);
            }

            Glide.with(binding.ivRestaurantPhotoDetail)
                    .load(restaurantPhoto)
                    .into(binding.ivRestaurantPhotoDetail);

            binding.tvRestaurantNameDetail.setText(restaurantName);

            binding.ratingBarRestaurantDetails.setRating((float) restaurantRating);

            binding.tvAddressRestaurantDetails.setText(restaurantAddress);

            if (restaurantIsOpenNow) {
                binding.tvIsOpenDetails.setText(R.string.open_now);
            } else {
                binding.tvIsOpenDetails.setText(R.string.closed);
            }

            Location restaurantLocation = new Location(getString(R.string.restaurant_location));
            restaurantLocation.setLongitude(restaurantLongitude);
            restaurantLocation.setLatitude(restaurantLatitude);
            binding.tvDistanceRestaurantDetails.setText(String.format(getString(R.string.distance_in_meters_d), Math.round(location.distanceTo(restaurantLocation))));

            Glide.with(binding.ivEatingAloneDetail)
                    .asGif()
                    .load(getString(R.string.gif_eating_alone))
                    .into(binding.ivEatingAloneDetail);

            //Phone number
            if (restaurant.getInternationalPhoneNumber() != null) {
                restaurantPhoneNumber = restaurant.getInternationalPhoneNumber();
                binding.ibCallDetail.setVisibility(View.VISIBLE);
                binding.tvCallDetails.setVisibility(View.VISIBLE);
                binding.ibCallDetail.setOnClickListener(view -> {
                    String phoneFormatted = String.format(getString(R.string.dial_phone_number), restaurantPhoneNumber);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(phoneFormatted));
                    startActivity(callIntent);
                });
            }

            // Website settings
            if (restaurant.getWebsite() != null) {
                restaurantWebsite = restaurant.getWebsite();
                binding.ibWebSiteDetails.setVisibility(View.VISIBLE);
                binding.tvWebsiteDetails.setVisibility(View.VISIBLE);
                binding.ibWebSiteDetails.setOnClickListener(view -> {
                    Uri uri = Uri.parse(restaurantWebsite);
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(websiteIntent);
                });
            }
        }
    }

    /**
     * Booking FAB setup
     */

    private void setupFAB() {

        binding.fabSelectRestaurant.setOnClickListener(view -> {
            Booking bookingToCheck = viewModel.checkIfUserHasBooking();

            if (bookingToCheck == null) {
                Booking booking = bookingToCreate();
                saveRestaurantIDInSharePreferences();
                createBooking(booking, RestaurantDetails.this);
            } else {
                if (bookingToCheck.getBookedRestaurantID().equalsIgnoreCase(restaurantID)) {
                    deleteBookingAlertDialog(bookingToCheck.getBookingID(), RestaurantDetails.this);
                } else {
                    saveRestaurantIDInSharePreferences();
                    updateBookingAlertDialog(bookingToCheck.getBookingID(), restaurantID, restaurantName);
                }
            }

            setupFABColor();

        });
    }

    private void setupFABColor() {
        Booking hasBooking = viewModel.checkIfUserHasBooking();
        if (hasBooking == null) {
            binding.fabSelectRestaurant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            if (hasBooking.getBookedRestaurantID().equalsIgnoreCase(restaurantID)) {
                binding.fabSelectRestaurant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.teal_700)));
            } else {
                binding.fabSelectRestaurant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }
        }
    }

    private Booking bookingToCreate() {
        return new Booking(intent.getStringExtra(KEY_RESTAURANT_PLACE_ID), restaurantName, viewModel.getCurrentUser().getUid());
    }

    private void updateBookingAlertDialog(String bookingID, String restaurantID, String restaurantName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantDetails.this);

        builder.setPositiveButton(R.string.OK, (dialog, id) -> {
            viewModel.updateBooking(bookingID, restaurantID, restaurantName, this);
            Toast.makeText(this, R.string.updated_booking, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        builder.setCancelable(true);
        builder.setMessage(R.string.update_booking)
                .setTitle(R.string.update_booking_title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBookingAlertDialog(String bookingID, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantDetails.this);

        builder.setPositiveButton(R.string.OK, (dialog, id) -> {
            viewModel.deleteBooking(bookingID, context);
            Toast.makeText(this, R.string.deleted_booking, Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        builder.setCancelable(true);
        builder.setMessage(R.string.delete_booking)
                .setTitle(R.string.delete_booking_title);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Favourite button settings
     */

    private void addRestaurantToFavourite(String restaurantID) {
        viewModel.addRestaurantToFavourite(restaurantID);
    }

    private void setupLikeButton() {
        binding.ibLikeDetail.setOnClickListener(view -> {
            boolean isInList = false;

            if (user != null) {
                if (user.getFavouriteRestaurants() != null) {
                    for (String rID : user.getFavouriteRestaurants()) {
                        if (rID.equalsIgnoreCase(restaurantID)) {
                            isInList = true;
                            break;
                        }
                    }
                }
            }

            if (isInList) {
                viewModel.removeRestaurantFromFavourite(restaurantID);
            } else {
                addRestaurantToFavourite(restaurantID);
            }
            setupLikeButtonAppearance();

        });
    }

    private void setupLikeButtonAppearance() {
        if (user != null) {
            if (user.getFavouriteRestaurants() != null) {
                if (user.getFavouriteRestaurants().contains(restaurantID)) {
                    binding.tvLLikeDetails.setText(getString(R.string.unlike));
                } else {
                    binding.tvLLikeDetails.setText(getString(R.string.like));
                }
            }
        }

    }


    /**
     * RecycleView settings
     */

    private void recyclerViewSetup() {
        binding.rvWorkmatesDetails.setHasFixedSize(true);
        binding.rvWorkmatesDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvWorkmatesDetails.setAdapter(adapter);
    }

    private void setUserJoiningList(List<User> list) {
        List<User> userJoining = new ArrayList<>();

        for (User user : list) {
            for (Booking booking : allBookings) {
                if (booking.getUserWhoBooked().equalsIgnoreCase(user.getUid())
                        && booking.getBookedRestaurantID().equalsIgnoreCase(restaurantID)
                        && !user.getUid().equalsIgnoreCase(viewModel.getCurrentUser().getUid()
                )) {
                    userJoining.add(user);
                    break;
                }
            }
        }

        viewModel.setListUserWhoBookedThisRestaurant(userJoining);
    }

    private void observeUsersDiningIn() {
        viewModel.getUserWhoBookedThatRestaurant().observe(this, adapter::submitList);
        viewModel.getUserWhoBookedThatRestaurant().observe(this, this::setupLoader);
    }

    private void setupLoader(List<User> list) {
        if (list.size() == 0) {
            binding.ivEatingAloneDetail.setVisibility(View.VISIBLE);
        } else {
            binding.ivEatingAloneDetail.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Get current user
     */
    private void getCurrentUser() {
        viewModel.getDataBaseInstanceUser();
        viewModel.getCurrentUserDataFromFireStore(viewModel.getCurrentUser().getUid());
        viewModel.observeCurrentUser().observe(this, this::updateUser);
    }

    private void updateUser(User user) {
        this.user = user;
        setupLikeButtonAppearance();
    }


    /**
     * LifeCycle related
     */

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.getAllDetails(restaurantID).removeObservers(this);
        viewModel.getLocation(getBaseContext(), this).removeObservers(this);
    }
}