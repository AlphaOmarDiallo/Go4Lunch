package com.alphaomardiallo.go4lunch.ui;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.viewModels.RestaurantDetailsViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.alphaomardiallo.go4lunch.ui.adapters.WorkmatesAdapter;
import com.alphaomardiallo.go4lunch.ui.adapters.WorkmatesJoiningAdapter;
import com.bumptech.glide.Glide;

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
    private double restaurantLatitude;
    private double restaurantLongitude;
    private List<Booking> allBookings;
    private Intent intent;
    private WorkmatesJoiningAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(RestaurantDetailsViewModel.class);
        setContentView(view);

        recyclerViewSetup();

        observeLocation();

        retrieveInformationFromIntent();

        observeAllBookings();

        setupFAB();
    }

    /**
     * RecycleView settings
     */

    private void recyclerViewSetup() {
        adapter = new WorkmatesJoiningAdapter(new WorkmatesAdapter.ListDiff(), this);
        binding.rvWorkmatesDetails.setHasFixedSize(true);
        binding.rvWorkmatesDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvWorkmatesDetails.setAdapter(adapter);
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
        viewModel.getAllDetails(intent.getStringExtra(KEY_RESTAURANT_PLACE_ID)).observe(this, this::setupViews);
    }

    /**
     * Booking related methods
     */

    private void observeAllBookings() {
        viewModel.getDatabaseInstanceBooking();
        viewModel.observeBookingsFromDataBase();
        viewModel.getAllBookings().observe(this, this::updateBookingList);
    }

    private void updateBookingList(List<Booking> allBookings){
        this.allBookings = allBookings;
    }

    private void createBooking(Booking bookingToCreate) {
        viewModel.createBooking(bookingToCreate);
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
            restaurantID = restaurant.getPlaceId();
            String restaurantPhoto = getString(R.string.restaurantPlaceHolder);
            restaurantName = restaurant.getName();
            double restaurantRating = restaurant.getRating();
            String restaurantAddress = restaurant.getVicinity();
            boolean restaurantIsOpenNow = restaurant.getOpeningHours().isOpenNow();
            restaurantLatitude = restaurant.getGeometry().getLocation().getLat();
            restaurantLongitude = restaurant.getGeometry().getLocation().getLng();

            try {
                restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.PLACES_API_KEY;
            } catch (Exception e) {
                System.out.println(e.getMessage());
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
        binding.fabSelectRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Booking booking = bookingToCreate();
                createBooking(booking);
            }
        });
    }

    private void setupFABColor() {
        //TODO FAB color
    }

    private Booking bookingToCreate() {
        return new Booking(intent.getStringExtra(KEY_RESTAURANT_PLACE_ID), restaurantName, viewModel.getCurrentUser().getUid());
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