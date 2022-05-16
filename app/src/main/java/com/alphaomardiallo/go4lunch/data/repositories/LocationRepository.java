package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public interface LocationRepository {

    int getRadius();

    LatLng getOfficeAddressLatLngFormat();

    String getLocationStringFormat(Context context);

}
