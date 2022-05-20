package com.alphaomardiallo.go4lunch;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.viewModels.RestaurantDetailsViewModel;
import com.alphaomardiallo.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.alphaomardiallo.go4lunch.domain.DistanceCalculatorUtils;
import com.bumptech.glide.Glide;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetails extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;
    private RestaurantDetailsViewModel viewModel;

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
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(RestaurantDetailsViewModel.class);
        setContentView(view);

        //View setup
        intent = getIntent();
        retrieveInformation();

    }

    private String getLocation() {
        return "48.86501071160738, 2.3467211059168793";
    }

    private void retrieveInformation() {
        if (!intent.hasExtra("bundle")) {
            //TODO API CALL
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
        DistanceCalculatorUtils calculatorUtils = new DistanceCalculatorUtils();

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
        getLocation();
        int distance = Math.round(calculatorUtils.getDistance(getLocation(), restaurantLatitude, restaurantLongitude));
        binding.tvDistanceRestaurantDetails.setText(String.format("%sm", distance));

        //TODO setup Missing information

        Glide.with(binding.ivEatingAloneDetail)
                .asGif()
                .load("https://media.giphy.com/media/p1NqIBmDgA2P8Kwz8E/giphy.gif")
                .into(binding.ivEatingAloneDetail);

        viewModel.getDetails(restaurantID).observe(this, this::setupContact);
    }

    private void setupContact(Result result) {

        Log.e(TAG, "setupContact: works ", null);

        restaurantPhoneNumber = result.getInternationalPhoneNumber();

        // Website settings
        restaurantWebsite = result.getWebsite();
        binding.ibWebSiteDetails.setColorFilter(R.color.teal_700);
        binding.ibWebSiteDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(restaurantWebsite);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.getDetails(restaurantID).removeObservers(this);
    }
}