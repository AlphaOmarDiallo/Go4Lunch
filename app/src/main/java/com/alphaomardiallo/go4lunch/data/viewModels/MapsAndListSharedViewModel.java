package com.alphaomardiallo.go4lunch.data.viewModels;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.repositories.AutocompleteRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PermissionRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapsAndListSharedViewModel extends ViewModel {

    private final PlacesAPIRepository placesApiRepository;
    private final LocationRepository locationRepository;
    private final PermissionRepository permissionRepository;
    private final AutocompleteRepository autocompleteRepository;

    LiveData<List<ResultsItem>> restaurants;
    LiveData<List<ResultsItem>> checkList;
    Location savedLocation;

    @Inject
    public MapsAndListSharedViewModel(PlacesAPIRepository placesApiRepository, LocationRepository locationRepository, PermissionRepository permissionRepository, AutocompleteRepository autocompleteRepository) {
        this.placesApiRepository = placesApiRepository;
        this.locationRepository = locationRepository;
        this.permissionRepository = permissionRepository;
        this.autocompleteRepository = autocompleteRepository;

        restaurants = placesApiRepository.getNearBySearchRestaurantList();
        checkList = null;
    }

    public LiveData<List<ResultsItem>> getRestaurants() {
        return restaurants;
    }

    public void getAllRestaurantList(Context context, Location location) {
        if (savedLocation == null) {
            if (permissionRepository.hasLocationPermissions(context)) {
                savedLocation = location;
            } else {
                savedLocation = locationRepository.getOfficeLocation();
            }

            Location currentLocation = locationRepository.getCurrentLocation().getValue();

            String locationString = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            placesApiRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());

        } else {
            if (savedLocation.distanceTo(location) > 51) {
                Location currentLocation = locationRepository.getCurrentLocation().getValue();

                String locationString = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                placesApiRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());
                Log.e(TAG, "getAllRestaurantList: distance condition met", null);
            }
        }
    }

    public void startTrackingLocation(Context context, Activity activity) {
        locationRepository.startLocationRequest(context, activity);
    }

    public void stopTrackingLocation() {
        locationRepository.stopLocationUpdates();
    }

    public LiveData<Location> getLocation() {
        return locationRepository.getCurrentLocation();
    }

    public Location getOfficeLocation() {
        return locationRepository.getOfficeLocation();
    }

    public LiveData<PredictionsItem> getSelectedRestaurant () {
        return autocompleteRepository.getSelectedRestaurant();
    }

    public LiveData<Result> getSelectedRestaurantDetails() {
        return placesApiRepository.getSelectedRestaurantDetails();
    }

    public void getSelectedRestaurantDetail(String placeID){
        placesApiRepository.fetchDetails(placeID);
    }

    public Boolean hasPermission(Context context) {
        return permissionRepository.hasLocationPermissions(context);
    }

    public Bitmap resizeMarker(Resources resources, int drawable) {
        int height = 120;
        int width = 100;
        Bitmap icon = BitmapFactory.decodeResource(resources, drawable);
        return Bitmap.createScaledBitmap(icon, width, height, false);
    }
}
