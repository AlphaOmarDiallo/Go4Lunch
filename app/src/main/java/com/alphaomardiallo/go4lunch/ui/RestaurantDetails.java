package com.alphaomardiallo.go4lunch.ui;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.viewModels.RestaurantDetailsViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.bumptech.glide.Glide;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetails extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel viewModel;
    private Location location;

    private String restaurantID;
    private String restaurantPhoneNumber;
    private String restaurantWebsite;
    private double restaurantLatitude;
    private double restaurantLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(RestaurantDetailsViewModel.class);
        setContentView(view);

        location = viewModel.getOfficeLocation();

        startTrackingLocation();

        retrieveInformation();

    }

    /**
     * Getting location and restaurant DATA
     */

    private void startTrackingLocation() {
        viewModel.getLocation(getBaseContext(), this).observe(this, this::updateLocation);
    }

    private void updateLocation(Location location) {
        this.location = location;
        Location restaurantLocationUpdate = new Location("Restaurant Location");
        restaurantLocationUpdate.setLongitude(restaurantLongitude);
        restaurantLocationUpdate.setLatitude(restaurantLatitude);
        binding.tvDistanceRestaurantDetails.setText(String.format("%sm", Math.round(this.location.distanceTo(restaurantLocationUpdate))));
    }

    private void retrieveInformation() {
        Intent intent = getIntent();
        viewModel.getAllDetails(intent.getStringExtra("id")).observe(this, this::setupViews);
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
            String restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.PLACES_API_KEY;
            String restaurantName = restaurant.getName();
            double restaurantRating = restaurant.getRating();
            String restaurantAddress = restaurant.getVicinity();
            boolean restaurantIsOpenNow = restaurant.getOpeningHours().isOpenNow();
            restaurantLatitude = restaurant.getGeometry().getLocation().getLat();
            restaurantLongitude = restaurant.getGeometry().getLocation().getLng();

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

            Location restaurantLocation = new Location("Restaurant Location");
            restaurantLocation.setLongitude(restaurantLongitude);
            restaurantLocation.setLatitude(restaurantLatitude);
            binding.tvDistanceRestaurantDetails.setText(String.format("%sm", Math.round(location.distanceTo(restaurantLocation))));

            Glide.with(binding.ivEatingAloneDetail)
                    .asGif()
                    .load("https://media.giphy.com/media/p1NqIBmDgA2P8Kwz8E/giphy.gif")
                    .into(binding.ivEatingAloneDetail);

            //Phone number
            if (restaurant.getInternationalPhoneNumber() != null) {

                restaurantPhoneNumber = restaurant.getInternationalPhoneNumber();
                binding.ibCallDetail.setVisibility(View.VISIBLE);
                binding.tvCallDetails.setVisibility(View.VISIBLE);
                binding.ibCallDetail.setOnClickListener(view -> {
                    String phoneFormatted = String.format("tel:%s", restaurantPhoneNumber);
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
     * LifeCycle related
     */

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.getDetails(restaurantID).removeObservers(this);
        viewModel.getLocation(getBaseContext(), this).removeObservers(this);
    }
}