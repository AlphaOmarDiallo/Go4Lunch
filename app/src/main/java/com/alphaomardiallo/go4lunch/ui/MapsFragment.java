package com.alphaomardiallo.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.databinding.FragmentMapsBinding;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;

public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private FragmentMapsBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private final long defaultCameraZoomOverMap = 19;
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
            LatLng office = new LatLng(48.86501071160738, 2.3467211059168793);

            //map.setMyLocationEnabled(enableMyLocation());
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.addMarker(new MarkerOptions()
                    .position(office)
                    .title("Office"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(office, defaultCameraZoomOverMap));
            enableMyLocation();

            // Initialize the SDK
            Places.initialize(requireContext(), "${MAPS_API_KEY}");

            // Create a new PlacesClient instance
            PlacesClient placesClient = Places.createClient(requireContext());
            Log.e(TAG, "onMapReady: " + placesClient, null);
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
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        binding.fabMyLocation.setOnClickListener(view1 -> getCurrentLocation());
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }
        Snackbar.make(binding.map, "Location permission is not granted", Snackbar.LENGTH_LONG)
                .setAction(R.string.activate, view -> Log.e(TAG, "onClick: Activate localization", null))
                .setAnchorView(binding.fabMyLocation)
                .show();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                Log.e(TAG, "onCanceledRequested: no no",null );
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                Log.e(TAG, "isCancellationRequested: no", null);
                return false;
            }
        }) .addOnSuccessListener(location -> {
            Log.e(TAG, "onSuccess: yes " + location, null);
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, defaultCameraZoomOverMap));
        });
    }

}