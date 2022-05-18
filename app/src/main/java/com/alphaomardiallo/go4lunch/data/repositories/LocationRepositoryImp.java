package com.alphaomardiallo.go4lunch.data.repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

public class LocationRepositoryImp implements LocationRepository {

    private static final String OFFICE_LOCATION_STRING = "48.86501071160738, 2.3467211059168793";
    private static final int LOCATION_REQUEST_PROVIDER_IN_MS = 60000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 50;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private LocationCallback locationCallback;

    @Inject
    public LocationRepositoryImp() {
    }

    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
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
            return OFFICE_LOCATION_STRING;
        }
    }

    @SuppressLint("MissingPermission")
    public void startLocationRequest(Context context) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();

                    locationMutableLiveData.setValue(location);
                }
            };
        }

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                        .setInterval(LOCATION_REQUEST_PROVIDER_IN_MS),
                locationCallback,
                Looper.getMainLooper()
        );
    }
}
