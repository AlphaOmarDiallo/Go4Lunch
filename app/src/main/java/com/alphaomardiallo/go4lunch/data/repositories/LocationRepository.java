package com.alphaomardiallo.go4lunch.data.repositories;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

public interface LocationRepository {

    LiveData<Location> getCurrentLocation();

    void startLocationRequest(Context context, Activity activity);

    int getRadius();

    Location getOfficeLocation();

    void stopLocationUpdates();

}
