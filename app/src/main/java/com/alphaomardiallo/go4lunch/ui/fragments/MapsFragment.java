package com.alphaomardiallo.go4lunch.ui.fragments;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
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
import com.alphaomardiallo.go4lunch.RestaurantDetails;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MapsAndListSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentMapsBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class MapsFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_PERMISSIONS_LOCATION = 567;
    private static final PermissionUtils permission = new PermissionUtils();
    private final long defaultCameraZoomOverMap = 19;
    private CameraPosition cameraPosition;
    private FragmentMapsBinding binding;
    private MapsAndListSharedViewModel viewModel;
    private List<ResultsItem> restaurantList;
    private GoogleMap map;
    private LatLng location;
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

            Log.e(TAG, "onMapReady: called", null);

            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                requireContext(), R.raw.map_styling));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }

            map = googleMap;

            setMap(googleMap);

            addOfficeMarkerToMap(googleMap);

            setCameraListener(googleMap);

            binding.fabMyLocation.setOnClickListener(view1 -> {
                if (permission.hasLocationPermissions(requireContext())) {
                    getCurrentLocation(googleMap);
                } else {
                    Log.e(TAG, "MAPS FRAGMENT onClick: permission not granted", null);
                    requestPermission();
                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        Log.e(TAG, "onCreateView: called", null);
        viewModel = new ViewModelProvider(requireActivity()).get(MapsAndListSharedViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated: called", null);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        binding.chipGroup.setVisibility(View.GONE);

        requestPermission();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        fetchAndObserveData();

    }

    /**
     * Getting data from viewModel
     */

    private void fetchAndObserveData() {
        viewModel.getAllRestaurantList(requireContext());
        viewModel.getLocation(requireContext()).observe(requireActivity(), this::updateLocation);
        viewModel.getRestaurants().observe(requireActivity(), this::updateMapWithRestaurants);
    }

    private void updateMapWithRestaurants(List<ResultsItem> resultsItemList) {
        Log.e(TAG, "updateMapWithRestaurants: new list " + resultsItemList.toString(), null);
        restaurantList = resultsItemList;

        addRestaurantMarkersToMap(resultsItemList, map);
    }

    private void updateLocation(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        this.location = current;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.location, defaultCameraZoomOverMap));
    }

    /**
     * Map handling
     */

    private void setMap(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (permission.hasLocationPermissions(requireContext())) {
            enableMyLocation(googleMap);
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionUtils.getOfficeLocation(), defaultCameraZoomOverMap));
        }
    }

    private void addOfficeMarkerToMap(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(positionUtils.getOfficeLocation())
                .title("Office")
                .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(), R.drawable.office_marker)))
        );
    }

    private void addRestaurantMarkersToMap(List<ResultsItem> list, GoogleMap googleMap) {
        for (ResultsItem resultsItem : list) {

            LatLng coordinates = new LatLng(resultsItem.getGeometry().getLocation().getLat(), resultsItem.getGeometry().getLocation().getLng());

            googleMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(resultsItem.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(), R.drawable.restaurant)))
            );
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                List<ResultsItem> list = viewModel.getRestaurants().getValue();

                assert list != null;
                for (ResultsItem resultsItem : list) {
                    if (Objects.requireNonNull(marker.getTitle()).equalsIgnoreCase(resultsItem.getName())) {
                        openDetailActivity(resultsItem);
                    }
                }
            }
        });
    }

    private void setCameraListener(GoogleMap googleMap) {
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                cameraPosition = googleMap.getCameraPosition();
            }
        });
    }

    private void openDetailActivity(ResultsItem restaurant) {
        Intent intent = new Intent(requireContext(), RestaurantDetails.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", restaurant.getPlaceId());
        bundle.putString("photo", restaurant.getPhotos().get(0).getPhotoReference());
        bundle.putString("name", restaurant.getName());
        bundle.putDouble("rating", restaurant.getRating());
        bundle.putString("address", restaurant.getVicinity());
        bundle.putBoolean("openNow", restaurant.getOpeningHours().isOpenNow());
        bundle.putDouble("latitude", restaurant.getGeometry().getLocation().getLat());
        bundle.putDouble("longitude", restaurant.getGeometry().getLocation().getLng());
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    /**
     * Location handling
     */

    @SuppressLint("MissingPermission")
    public void enableMyLocation(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(GoogleMap googleMap) {
        /*fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
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
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, defaultCameraZoomOverMap));
        });*/
        LatLng current = new LatLng(location.latitude, location.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, defaultCameraZoomOverMap));
        Log.e(TAG, "getCurrentLocation: test", null);
        Log.e(TAG, "onMapReady: location gotten", null);
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
                Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        enableMyLocation(map);
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

