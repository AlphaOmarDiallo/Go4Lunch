package com.alphaomardiallo.go4lunch.data.viewModels;

import android.content.Context;

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

    public LiveData<List<ResultsItem>> getAllRestaurantList(Context context) {
        if (checkList == null) {
            checkList = apiRepository.fetchNearBySearchPlaces(locationRepository.getLocationStringFormat(context), locationRepository.getRadius());
            restaurants = checkList;
            return checkList;
        } else {
            return checkList;
        }
    }
}
