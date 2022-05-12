package com.alphaomardiallo.go4lunch.domain;

import com.google.android.gms.maps.model.LatLng;

public class PositionUtils {

    public LatLng getOfficeLocation() {
        return new LatLng(48.86501071160738, 2.3467211059168793);
    }

    public int getRadius() {
        return 800;
    }
}