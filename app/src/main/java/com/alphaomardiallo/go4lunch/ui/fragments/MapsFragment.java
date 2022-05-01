package com.alphaomardiallo.go4lunch.ui.fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.databinding.FragmentMapsBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_PERMISSIONS_LOCATION = 567;
    private static final PermissionUtils permission = new PermissionUtils();
    private final long defaultCameraZoomOverMap = 19;
    private FragmentMapsBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private PositionUtils positionUtils = new PositionUtils();

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.addMarker(new MarkerOptions()
                    .position(positionUtils.getOfficeLocation())
                    .title("Office"));
            if(permission.hasLocationPermissions(requireContext())) {
                enableMyLocation();
                getCurrentLocation();
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionUtils.getOfficeLocation(), defaultCameraZoomOverMap));
            }

            // Initialize the SDK
            Places.initialize(requireContext(), "com.google.android.geo.API_KEY");

            // Create a new PlacesClient instance
            PlacesClient placesClient = Places.createClient(requireContext());
            Log.e(TAG, "MAPS FRAGMENT onMapReady: " + placesClient, null);

            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

// Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
            if (permission.hasLocationPermissions(requireContext())) {
                @SuppressLint("MissingPermission") Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                placeResponse.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = task.getResult();
                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                    placeLikelihood.getPlace().getName(),
                                    placeLikelihood.getLikelihood()));
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "MAPS FRAGMENT Place not found: " + apiException.getStatusCode());
                        }
                    }
                });
            } else {
                requestPermission();
                Log.e(TAG, "MAPS FRAGMENT onViewCreated: permission not granted", null);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.chipGroup.setVisibility(View.GONE);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        requestPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        binding.fabMyLocation.setOnClickListener(view1 -> {
            if (permission.hasLocationPermissions(requireContext())) {
                getCurrentLocation();
            } else {
                Log.e(TAG, "MAPS FRAGMENT onClick: permission not granted", null);
                requestPermission();
            }
        });
    }

    /**
     * Location handling
     */

    @SuppressLint("MissingPermission")
    public void enableMyLocation() {
        map.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @SuppressWarnings("ConstantConditions")
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnSuccessListener(location -> {
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, defaultCameraZoomOverMap));
        });
    }

    /**
     * Permission handling
     */

    public void requestPermission() {
        if (permission.hasLocationPermissions(requireContext())) {
            return;
        }

        EasyPermissions.requestPermissions(
                this,
                "This app needs location permission to function properly",
                REQUEST_PERMISSIONS_LOCATION,
                ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        enableMyLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestPermission();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}