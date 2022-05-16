package com.alphaomardiallo.go4lunch;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alphaomardiallo.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.alphaomardiallo.go4lunch.domain.DistanceCalculatorUtils;
import com.bumptech.glide.Glide;

public class RestaurantDetails extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;
    private String location;
    private String restaurantID;
    private String restaurantPhoto;
    private String restaurantName;
    private double restaurantRating;
    private String restaurantAddress;
    private Boolean restaurantIsOpenNow;
    private double restaurantLatitude;
    private double restaurantLongitude;
    private String restaurantPhoneNumber;
    private Boolean restaurantIsInMyFavourite;
    private String restaurantWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(view);

        /**
         * Setting information retrieved via Bundle if available or getting the information via and API call
         */

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");

        if (bundle.isEmpty()){
            //TODO API CALL
            Log.i(TAG, "onCreate: Empty bundle, API call needed");
        } else {
            restaurantID = bundle.getString("id");
            restaurantPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + bundle.getString("photo", null) + "&key=" + BuildConfig.PLACES_API_KEY;
            restaurantName = bundle.getString("name");
            restaurantRating = bundle.getDouble("rating");
            restaurantAddress = bundle.getString("address");
            restaurantIsOpenNow = bundle.getBoolean("openNow");
            restaurantLatitude = bundle.getDouble("latitude");
            restaurantLongitude = bundle.getDouble("longitude");

            //TODO make API call for what is left
        }

        setupViews();
    }

    private String getLocation() {
        return location = "48.86501071160738, 2.3467211059168793";
        //TODO setup correctLocation
    }

    private void setupViews() {
        DistanceCalculatorUtils calculatorUtils = new DistanceCalculatorUtils();

        Glide.with(binding.ivRestaurantPhotoDetail)
                .load(restaurantPhoto)
                .into(binding.ivRestaurantPhotoDetail);

        binding.tvRestaurantNameDetail.setText(restaurantName);

        binding.ratingBarRestaurantDetails.setRating((float) restaurantRating);

        binding.tvAddressRestaurantDetails.setText(restaurantAddress);

        if (restaurantIsOpenNow) {
            binding.tvIsOpenDetails.setText("Open now");
        } else {
            binding.tvIsOpenDetails.setText("Closed");
        }
        getLocation();
        int distance = Math.round(calculatorUtils.getDistance(location, restaurantLatitude, restaurantLongitude));
        binding.tvDistanceRestaurantDetails.setText(String.format("%sm", distance));

        //TODO setup Missing informations

        Glide.with(binding.ivEatingAloneDetail)
                .asGif()
                .load("https://media.giphy.com/media/p1NqIBmDgA2P8Kwz8E/giphy.gif")
                .into(binding.ivEatingAloneDetail);
    }
}