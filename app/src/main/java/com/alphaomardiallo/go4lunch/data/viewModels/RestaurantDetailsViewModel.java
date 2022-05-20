package com.alphaomardiallo.go4lunch.data.viewModels;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantDetailsViewModel extends ViewModel {

    private final PlacesAPIRepository placesAPIRepository;
    private final LocationRepository locationRepository;

    @Inject
    public RestaurantDetailsViewModel(PlacesAPIRepository placesAPIRepository, LocationRepository locationRepository) {
        this.placesAPIRepository = placesAPIRepository;
        this.locationRepository = locationRepository;
    }

    public LiveData<Result> getDetails(String placeID) {
        placesAPIRepository.fetchDetails(placeID);
        return placesAPIRepository.getDetails();
    }

    public LiveData<Location> getLocation(Context context, Activity activity){
        locationRepository.startLocationRequest(context, activity);
        return locationRepository.getCurrentLocation();
    }

    public Location getOfficeLocation() {
        return locationRepository.getOfficeLocation();
    }

}
