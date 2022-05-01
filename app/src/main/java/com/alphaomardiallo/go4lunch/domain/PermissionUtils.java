package com.alphaomardiallo.go4lunch.domain;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtils {

    public boolean hasLocationPermissions(Context context) {
        Log.e(TAG, "hasLocationPermissions: result", null);
        return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
        );
    }
}
