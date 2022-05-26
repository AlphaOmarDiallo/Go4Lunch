package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;

import java.util.List;

public interface PlacesAPIRepository {

    LiveData<List<ResultsItem>> getNearBySearchRestaurantList();

    void fetchNearBySearchPlaces(String location, int radius);

    LiveData<Result> getDetails();

    LiveData<Result> getSelectedRestaurantDetails();

    void fetchDetails(String placeID);

    void fetchAllDetails (String placeID);

    LiveData<List<PredictionsItem>> autoCompleteSearch(String Query, String location, int radius);

}
