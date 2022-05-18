package com.alphaomardiallo.go4lunch.data.repositories;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

public interface LocationRepository {

    LiveData<Location> getLocationLiveData();

    void startLocationRequest(Context context, Activity activity);

    int getRadius();

    LatLng getOfficeAddressLatLngFormat();

    String getLocationStringFormat(Context context);

}
