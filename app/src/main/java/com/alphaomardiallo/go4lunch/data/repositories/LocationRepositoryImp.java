package com.alphaomardiallo.go4lunch.data.repositories;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

public class LocationRepositoryImp implements LocationRepository{

    @Inject
    public LocationRepositoryImp() {
    }

    @Override
    public int getRadius() {
        return 500;
    }

    @Override
    public LatLng getOfficeAddressLatLngFormat() {
        return new LatLng(48.86501071160738, 2.3467211059168793);
    }

    @Override
    public String getOfficeAddressStringFormat() {
        return "48.86501071160738, 2.3467211059168793";
    }
}
