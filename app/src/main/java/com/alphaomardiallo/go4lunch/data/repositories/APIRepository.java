package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;

import java.util.List;

public interface APIRepository {

    LiveData<List<ResultsItem>> getNearBySearchList(String location, int radius);

    //void getDetailsListAsLiveData();

    //void getAutocompleteListAsLiveData();

}
