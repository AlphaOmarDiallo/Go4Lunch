package com.alphaomardiallo.go4lunch;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
    private String restaurantPhoto;
    private String restaurantName;
    private double restaurantRating;
    private String restaurantAddress;
    private Boolean restaurantIsOpenNow;
    private double restaurantLatitude;
    private double restaurantLongitude;
    private String restaurantPhoneNumber;
    private String restaurantWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(RestaurantDetailsViewModel.class);
        setContentView(view);

        location = viewModel.getOfficeLocation();

        //View setup
        startTrackingLocation();

        retrieveInformation();

    }

    private void startTrackingLocation(){
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

        if (!intent.hasExtra("bundle")) {
            //TODO API CALL
            viewModel.getAllDetails(intent.getStringExtra("id"));
            Log.i(TAG, "onCreate: Empty bundle, API call needed");
        } else {
            Bundle bundle = intent.getBundleExtra("bundle");
            restaurantID = bundle.getString("id");
            restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + bundle.getString("photo", null) + "&key=" + BuildConfig.PLACES_API_KEY;
            restaurantName = bundle.getString("name");
            restaurantRating = bundle.getDouble("rating");
            restaurantAddress = bundle.getString("address");
            restaurantIsOpenNow = bundle.getBoolean("openNow");
            restaurantLatitude = bundle.getDouble("latitude");
            restaurantLongitude = bundle.getDouble("longitude");

            //TODO make API call for what is left

            setupViews();
        }
    }

    private void setupViews() {

        binding.ibWebSiteDetails.setVisibility(View.INVISIBLE);
        binding.ibCallDetail.setVisibility(View.INVISIBLE);
        binding.tvCallDetails.setVisibility(View.INVISIBLE);
        binding.tvWebsiteDetails.setVisibility(View.INVISIBLE);

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

        //TODO setup Missing information

        Glide.with(binding.ivEatingAloneDetail)
                .asGif()
                .load("https://media.giphy.com/media/p1NqIBmDgA2P8Kwz8E/giphy.gif")
                .into(binding.ivEatingAloneDetail);

        viewModel.getDetails(restaurantID).observe(this, this::setupContact);
    }

    private void setupContact(Result result) {

        Log.e(TAG, "setupContact: works ", null);

        //Phone number
        if (result.getInternationalPhoneNumber() != null){

            restaurantPhoneNumber = result.getInternationalPhoneNumber();
            binding.ibCallDetail.setVisibility(View.VISIBLE);
            binding.tvCallDetails.setVisibility(View.VISIBLE);
            binding.ibCallDetail.setOnClickListener(view -> {
                String phoneFormatted = String.format("tel:%s", restaurantPhoneNumber);
                Intent callIntent = new Intent (Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse(phoneFormatted));
                startActivity(callIntent);
            });
        }

        // Website settings
        if (result.getWebsite() != null){

            restaurantWebsite = result.getWebsite();
            binding.ibWebSiteDetails.setVisibility(View.VISIBLE);
            binding.tvWebsiteDetails.setVisibility(View.VISIBLE);
            binding.ibWebSiteDetails.setOnClickListener(view -> {
                Uri uri = Uri.parse(restaurantWebsite);
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(websiteIntent);
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.getDetails(restaurantID).removeObservers(this);
        viewModel.getLocation(getBaseContext(), this).removeObservers(this);
    }
}