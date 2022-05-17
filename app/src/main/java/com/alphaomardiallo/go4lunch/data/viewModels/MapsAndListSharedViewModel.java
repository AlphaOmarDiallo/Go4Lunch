package com.alphaomardiallo.go4lunch.data.viewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.repositories.APIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel

public class MapsAndListSharedViewModel extends ViewModel {

    private final APIRepository apiRepository;
    private final LocationRepository locationRepository;
    LiveData<List<ResultsItem>> restaurants;
    LiveData<List<ResultsItem>> checkList;
    String location;

    @Inject
    public MapsAndListSharedViewModel(APIRepository apiRepository, LocationRepository locationRepository) {
        this.apiRepository = apiRepository;
        this.locationRepository = locationRepository;
        restaurants = apiRepository.getNearBySearchRestaurantList();
        checkList = null;
    }
    public LiveData<List<ResultsItem>> getRestaurants() {
        return restaurants;
    }

    public void getAllRestaurantList(Context context) {
        if (location == null) {
            location = locationRepository.getLocationStringFormat(context);
            apiRepository.fetchNearBySearchPlaces(locationRepository.getLocationStringFormat(context), locationRepository.getRadius());
        }
    }
}
