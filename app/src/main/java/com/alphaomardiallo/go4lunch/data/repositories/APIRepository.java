package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.lifecycle.LiveData;

public interface APIRepository {

    LiveData getNearBySearchListAsLiveData(String location);

    LiveData getDetailsListAsLiveData();

    LiveData getAutocompleteListAsLiveData();

}
