package com.alphaomardiallo.go4lunch.data.repositories;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionRepositoryImp implements PermissionRepository {

    final MutableLiveData<Boolean> hasPermissions = new MutableLiveData<>();

    @Inject
    public PermissionRepositoryImp() {
    }

    @Override
    public boolean hasLocationPermissions(Context context) {
        return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
        );
    }

    @Override
    public LiveData<Boolean> liveDataHasLocationPermission(Context context) {
        hasPermissions.setValue(EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION));
        return hasPermissions;
    }
}
