package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

public interface APIRepository {

    LiveData getNearBySearchListRankByMethod(String location);

    LiveData getNearBySearchListRadiusMethod(String location);

    LiveData getDetailsListAsLiveData();

    LiveData getAutocompleteListAsLiveData();

}
