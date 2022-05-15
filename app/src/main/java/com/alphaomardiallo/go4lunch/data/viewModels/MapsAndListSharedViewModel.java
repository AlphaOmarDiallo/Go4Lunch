package com.alphaomardiallo.go4lunch.data.viewModels;

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
    LiveData<List<ResultsItem>> checkList = null;

    @Inject
    public MapsAndListSharedViewModel(APIRepository apiRepository, LocationRepository locationRepository) {
        this.apiRepository = apiRepository;
        this.locationRepository = locationRepository;
    }

    public LiveData<List<ResultsItem>> getAllRestaurantList(String location, int radius) {
        if (checkList == null) {
            checkList = apiRepository.getNearBySearchList(location, radius);
            return checkList;
        } else {
            return checkList;
        }
    }

    public int getRadius(){
        return locationRepository.getRadius();
    }

    public String getOfficeLocationAsString(){
        return locationRepository.getOfficeAddressStringFormat();
    }
}
