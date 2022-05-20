package com.alphaomardiallo.go4lunch.domain;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class PositionUtils {

    public LatLng getOfficeLocation() {
        return new LatLng(48.86501071160738, 2.3467211059168793);
    }

    public Location getOfficeLocationFormat() {
        Location officeLocation = new Location("Office");
        officeLocation.setLongitude(2.3467211059168793);
        officeLocation.setLatitude(48.86501071160738);
        return officeLocation;
    }

    public int getRadius() {
        return 500;
    }
}