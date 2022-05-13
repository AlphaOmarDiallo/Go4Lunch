package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;

import java.util.List;

public interface APIRepository {

    LiveData<List<ResultsItem>> getNearBySearchListRadiusMethod(String location);

    //List<ResultsItem> getNearBySearchListRankByMethod(String location);

    //void getDetailsListAsLiveData();

    //void getAutocompleteListAsLiveData();

}
