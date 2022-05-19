package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

public class LocationRepositoryImp implements LocationRepository {

    private static final int LOCATION_REQUEST_PROVIDER_IN_MS = 60000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 2;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Inject
    public LocationRepositoryImp() {
    }

    @Override
    public int getRadius() {
        return 500;
    }

    public LiveData<Location> getCurrentLocation() {
        return locationMutableLiveData;
    }

    @Override
    public Location getOfficeLocation() {
        Location officeLocation = new Location("Office");
        officeLocation.setLongitude(2.3467211059168793);
        officeLocation.setLatitude(48.86501071160738);
        return officeLocation;
    }

    @SuppressLint("MissingPermission")
    public void startLocationRequest(Context context, Activity activity) {

        Log.e(TAG, "startLocationRequest: Started", null);
        instantiateFusedLocationProviderClient(context);

        getLastKnownLocation(activity);

        setupALocationRequest();

        createLocationCallback();

        startLocationUpdates();
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void instantiateFusedLocationProviderClient(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation(Activity activity) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        Log.e(TAG, "onSuccess: we got the last location", null);
                    }
                });
    }

    private void setupALocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_REQUEST_PROVIDER_IN_MS);
        locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    locationMutableLiveData.setValue(location);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        Log.e(TAG, "startLocationUpdates: updating", null);
    }

}
