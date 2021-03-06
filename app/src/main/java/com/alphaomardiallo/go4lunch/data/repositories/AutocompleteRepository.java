package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;

import java.util.List;

public interface AutocompleteRepository {

    void updatePredictionList(List<PredictionsItem> list);

    LiveData<List<PredictionsItem>> searchPredictionResults();

    void setPlaceToFocusOn(PredictionsItem restaurant);

}
