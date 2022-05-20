package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantDetailsViewModel extends ViewModel {

    private final PlacesAPIRepository placesAPIRepository;

    @Inject
    public RestaurantDetailsViewModel(PlacesAPIRepository placesAPIRepository) {
        this.placesAPIRepository = placesAPIRepository;
    }

    public LiveData<Result> getDetails(String placeID) {
        placesAPIRepository.fetchDetails(placeID);
        return placesAPIRepository.getDetails();
    }

}
