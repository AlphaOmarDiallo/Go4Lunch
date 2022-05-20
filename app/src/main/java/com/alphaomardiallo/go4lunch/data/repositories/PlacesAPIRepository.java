package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;

import java.util.List;

public interface PlacesAPIRepository {

    LiveData<List<ResultsItem>> getNearBySearchRestaurantList();

    void fetchNearBySearchPlaces(String location, int radius);

    LiveData<Result> getDetails();

    void fetchDetails(String placeID);

}
