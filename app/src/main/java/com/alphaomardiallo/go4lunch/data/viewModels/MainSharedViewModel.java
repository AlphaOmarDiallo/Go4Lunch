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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.repositories.AutocompleteRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PermissionRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainSharedViewModel extends ViewModel {

    private final UserRepositoryImp userRepositoryImp;
    private final LocationRepository locationRepository;
    private final PlacesAPIRepository placesAPIRepository;
    private final PermissionRepository permissionRepository;
    private final AutocompleteRepository autocompleteRepository;

    LiveData<List<ResultsItem>> restaurants;
    LiveData<List<ResultsItem>> checkList;
    MutableLiveData<PredictionsItem> selectedRestaurant = new MutableLiveData<>();
    Location savedLocation;
    MutableLiveData<String> restaurantToFocusOn = new MutableLiveData<>();

    @Inject
    public MainSharedViewModel(UserRepositoryImp userRepositoryImp, LocationRepository locationRepository, PlacesAPIRepository placesAPIRepository, PermissionRepository permissionRepository, AutocompleteRepository autocompleteRepository) {
        this.userRepositoryImp = userRepositoryImp;
        this.locationRepository = locationRepository;
        this.placesAPIRepository = placesAPIRepository;
        this.permissionRepository = permissionRepository;
        this.autocompleteRepository = autocompleteRepository;

        restaurants = placesAPIRepository.getNearBySearchRestaurantList();
        checkList = null;
    }

    /**
     * Main Activity Methods
     */

    public UserRepository getInstance() {
        return userRepositoryImp.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return userRepositoryImp.getCurrentUser();
    }

    public Boolean isCurrentUserNotLoggedIn() {
        return (this.getCurrentUser() == null);
    }

    public Task<Void> signOut(Context context) {
        return userRepositoryImp.signOut(context);
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

    public LiveData<List<PredictionsItem>> searchAutoComplete(String query, String location) {

        if (location != null) {
            return placesAPIRepository.autoCompleteSearch(query, location, locationRepository.getRadius());
        }

        return null;
    }

    public LiveData<List<ResultsItem>> getRestaurantList() {
        return placesAPIRepository.getNearBySearchRestaurantList();
    }

    /**
     * Others
     */

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
            placesAPIRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());

        } else {
            if (savedLocation.distanceTo(location) > 51) {
                Location currentLocation = locationRepository.getCurrentLocation().getValue();

                String locationString = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                placesAPIRepository.fetchNearBySearchPlaces(locationString, locationRepository.getRadius());
                Log.e(TAG, "getAllRestaurantList: distance condition met", null);
            }
        }
    }

    public Location getOfficeLocation() {
        return locationRepository.getOfficeLocation();
    }

    public LiveData<PredictionsItem> getSelectedRestaurant() {
        //return autocompleteRepository.getSelectedRestaurant();
        Log.e(TAG, "getSelectedRestaurant: " + selectedRestaurant, null);
        return selectedRestaurant;
    }

    public LiveData<Result> getSelectedRestaurantDetails() {
        return placesAPIRepository.getSelectedRestaurantDetails();
    }

    public void getSelectedRestaurantDetail(String placeID) {
        placesAPIRepository.fetchDetails(placeID);
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

    //****************************************

    public LiveData<List<PredictionsItem>> getPredictionList() {
        return autocompleteRepository.searchPrediction();
    }

    public void setRestaurantToFocusOn(PredictionsItem predictionsItem) {
        //autocompleteRepository.setPlaceToFocusOn(predictionsItem);
        selectedRestaurant.setValue(predictionsItem);
        Log.e(TAG, "setRestaurantToFocusOn: " + predictionsItem.getPlaceId(), null);
    }

    public void getIdRestaurantToFocusOn(String id) {
        restaurantToFocusOn.setValue(id);
    }

    public LiveData<String> getRestaurantToFocusOn() {
        return restaurantToFocusOn;
    }


}
