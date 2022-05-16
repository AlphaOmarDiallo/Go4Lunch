package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

public class LocationRepositoryImp implements LocationRepository {

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
    public String getLocationStringFormat(Context context) {
        PermissionUtils permissionUtils = new PermissionUtils();
        if (permissionUtils.hasLocationPermissions(context)) {
            return "48.86501071160738, 2.3467211059168793";
        } else {
            return "48.86501071160738, 2.3467211059168793";
        }
    }
}
