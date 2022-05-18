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

import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.inject.Inject;

public class LocationRepositoryImp implements LocationRepository {

    private static final String OFFICE_LOCATION_STRING = "48.86501071160738, 2.3467211059168793";
    private static final int LOCATION_REQUEST_PROVIDER_IN_MS = 60000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 50;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

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
            Location location = getLocationLiveData().getValue();
            return OFFICE_LOCATION_STRING;
        } else {
            return OFFICE_LOCATION_STRING;
        }
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

    private void instantiateFusedLocationProviderClient(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation(Activity activity) {
        final Location[] lastKnownLocation = {null};
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.e(TAG, "onSuccess: we got the last location", null );
                            lastKnownLocation[0] = location;
                        }
                    }
                });
        return lastKnownLocation[0];
    }

    private void setupALocationRequest () {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);;
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
                    System.out.println(location.getLongitude());
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

    private void stopLocationUpdates () {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


}
