package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;

import java.util.List;

import javax.inject.Inject;

public class AutocompleteRepositoryImp implements AutocompleteRepository {

    final MutableLiveData<List<PredictionsItem>> predictionsList = new MutableLiveData<>();
    final MutableLiveData<PredictionsItem> selectedRestaurant = new MutableLiveData<>();

    @Inject
    public AutocompleteRepositoryImp() {
    }

    @Override
    public void updatePredictionList(List<PredictionsItem> list) {
        predictionsList.setValue(list);
    }

    @Override
    public LiveData<List<PredictionsItem>> searchPredictionResults() {
        return predictionsList;
    }

    public void setPlaceToFocusOn(PredictionsItem restaurant) {
        selectedRestaurant.setValue(restaurant);
    }

}
