package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;

import java.util.List;

import javax.inject.Inject;

public class AutocompleteRepositoryImp implements AutocompleteRepository {

    MutableLiveData<List<PredictionsItem>> predictionsList = new MutableLiveData<>();
    MutableLiveData<PredictionsItem> selectedRestaurant = new MutableLiveData<>();

    @Inject
    public AutocompleteRepositoryImp() {

    }

    @Override
    public void updatePredictionList(List<PredictionsItem> list) {
        predictionsList.setValue(list);
    }

    @Override
    public LiveData<List<PredictionsItem>> searchPrediction() {
        return predictionsList;
    }

    public void setPlaceToFocusOn(PredictionsItem restaurant) {
        selectedRestaurant.setValue(restaurant);
        System.out.println("done");
    }

    public LiveData<PredictionsItem> getSelectedRestaurant() {
        return selectedRestaurant;
    }

    public void deleteSelectedRestaurant() {
        selectedRestaurant.setValue(null);
    }
}
