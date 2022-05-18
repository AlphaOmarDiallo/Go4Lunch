package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

public interface LocationRepository {

    LiveData<Location> getLocationLiveData();

    public void startLocationRequest(Context context);

    int getRadius();

    LatLng getOfficeAddressLatLngFormat();

    String getLocationStringFormat(Context context);

}
