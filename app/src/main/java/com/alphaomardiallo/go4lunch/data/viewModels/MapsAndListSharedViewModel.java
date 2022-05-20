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

import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.repositories.APIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PermissionRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MapsAndListSharedViewModel extends ViewModel {

    private final APIRepository apiRepository;
    private final LocationRepository locationRepository;
    private final PermissionRepository permissionRepository;

    LiveData<List<ResultsItem>> restaurants;
    LiveData<List<ResultsItem>> checkList;
    Location savedLocation;

    @Inject
    public MapsAndListSharedViewModel(APIRepository apiRepository, LocationRepository locationRepository, PermissionRepository permissionRepository) {
        this.apiRepository = apiRepository;
        this.locationRepository = locationRepository;
        this.permissionRepository = permissionRepository;

        restaurants = apiRepository.getNearBySearchRestaurantList();
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
            apiRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());

        } else {
            if (savedLocation.distanceTo(location) > 51) {
                Location currentLocation = locationRepository.getCurrentLocation().getValue();

                String locationString = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                apiRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());
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
