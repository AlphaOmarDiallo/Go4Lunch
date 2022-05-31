package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.repositories.AutocompleteRepository;
import com.alphaomardiallo.go4lunch.data.repositories.LocationRepository;
import com.alphaomardiallo.go4lunch.data.repositories.PlacesAPIRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SearchViewModel extends ViewModel {

    private final AutocompleteRepository autocompleteRepository;
    private final PlacesAPIRepository placesAPIRepository;
    private final LocationRepository locationRepository;

    @Inject
    public SearchViewModel(AutocompleteRepository autocompleteRepository, PlacesAPIRepository placesAPIRepository, LocationRepository locationRepository) {
        this.autocompleteRepository = autocompleteRepository;
        this.placesAPIRepository = placesAPIRepository;
        this.locationRepository = locationRepository;
    }

    public LiveData<List<PredictionsItem>> getPredictionList() {
        return autocompleteRepository.searchPrediction();
    }

    public LiveData<List<PredictionsItem>> searchAutoComplete(String query, String location) {
        autocompleteRepository.updatePredictionList(placesAPIRepository.autoCompleteSearch(query, location, locationRepository.getRadius()).getValue());
        return placesAPIRepository.autoCompleteSearch(query, location, locationRepository.getRadius());
    }

    public void setRestaurantToFocusOn(PredictionsItem predictionsItem) {
        autocompleteRepository.setPlaceToFocusOn(predictionsItem);
    }
}