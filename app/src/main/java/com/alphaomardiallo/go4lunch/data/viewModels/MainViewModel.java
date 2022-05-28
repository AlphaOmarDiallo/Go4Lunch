package com.alphaomardiallo.go4lunch.data.viewModels;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final UserRepositoryImp userRepositoryImp;
    private final LocationRepository locationRepository;
    private final PlacesAPIRepository placesAPIRepository;


    @Inject
    public MainViewModel(UserRepositoryImp userRepositoryImp, LocationRepository locationRepository, PlacesAPIRepository placesAPIRepository) {
        this.userRepositoryImp = userRepositoryImp;
        this.locationRepository = locationRepository;
        this.placesAPIRepository = placesAPIRepository;
    }

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

        if(location != null){
            return placesAPIRepository.autoCompleteSearch(query,location, locationRepository.getRadius());
        }

        return null;
    }

    public LiveData<List<ResultsItem>> getRestaurantList() {
        return placesAPIRepository.getNearBySearchRestaurantList();
    }

}
