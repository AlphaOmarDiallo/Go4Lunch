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
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MapsAndListSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentMapsBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class MapsFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_PERMISSIONS_LOCATION = 567;
    private static final PermissionUtils permission = new PermissionUtils();
    private final long defaultCameraZoomOverMap = 19;
    private FragmentMapsBinding binding;
    private MapsAndListSharedViewModel viewModel;
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
                    .title("Office")
                    .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(),R.drawable.office_marker)))
            );
            if (permission.hasLocationPermissions(requireContext())) {
                enableMyLocation();
                getCurrentLocation();
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionUtils.getOfficeLocation(), defaultCameraZoomOverMap));
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MapsAndListSharedViewModel.class);
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

        binding.chipGroup.setVisibility(View.GONE);

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

        fetchAndObserveData();

    }

    /**
     * NearBy Restaurants handling
     */

    private void fetchAndObserveData() {
        viewModel.getAllRestaurantList(requireContext());
        viewModel.getRestaurants().observe(requireActivity(), this::updateMapWithRestaurants);
    }

    private void updateMapWithRestaurants (List<ResultsItem> resultsItemList) {
        Log.e(TAG, "updateMapWithRestaurants: new list " + resultsItemList.toString(), null);

        for (ResultsItem resultsItem : resultsItemList) {

            LatLng coordinates = new LatLng(resultsItem.getGeometry().getLocation().getLat(), resultsItem.getGeometry().getLocation().getLng());

            map.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(resultsItem.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker))
            );
        }
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