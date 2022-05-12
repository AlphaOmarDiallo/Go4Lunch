package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.repositories.APIRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel

public class MapsAndListSharedViewModel extends ViewModel {

    private final APIRepository apiRepository;
    private final LocationRepository locationRepository;

    @Inject
    public MapsAndListSharedViewModel(APIRepository apiRepository, LocationRepository locationRepository) {
        this.apiRepository = apiRepository;
        this.locationRepository = locationRepository;
    }

    public LiveData getNearBySearchListRankBy(String location) {
        return apiRepository.getNearBySearchListRankByMethod(location);
    }

    public LiveData getNearBySearchListRadius(String location) {
        return apiRepository.getNearBySearchListRadiusMethod(location);
    }

}
