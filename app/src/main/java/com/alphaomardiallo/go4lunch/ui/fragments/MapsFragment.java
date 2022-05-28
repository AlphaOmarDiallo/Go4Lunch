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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MainSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentMapsBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;
import com.alphaomardiallo.go4lunch.ui.RestaurantDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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
    //private MapsAndListSharedViewModel viewModel;
    public MainSharedViewModel viewModel;
    private List<ResultsItem> restaurantList;
    private GoogleMap map;
    private Location location;
    private final PositionUtils positionUtils = new PositionUtils();

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

            if (location != null) {
                getCurrentLocation(googleMap);
            }

            if (restaurantList != null) {
                addRestaurantMarkersToMap(restaurantList, googleMap);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: called", null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        Log.e(TAG, "onCreateView: called", null);

        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);

        this.onAttach(requireContext());
        Log.e(TAG, "attached ", null);

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

        checkIfRestaurantHasBeenSelected();

    }

    /**
     * Getting data from viewModel
     */

    private void fetchAndObserveData() {
        if (viewModel.hasPermission(requireContext())) {
            viewModel.startTrackingLocation(requireContext(), getActivity());
            viewModel.getLocation().observe(requireActivity(), this::updateLocation);
            //viewModel.getSelectedRestaurant().observe(requireActivity(), this::focusOnSelectedRestaurant);
            viewModel.getRestaurantToFocusOn().observe(requireActivity(), this::focusOnSelectedRestaurant);
        }
    }

    private void updateMapWithRestaurants(List<ResultsItem> resultsItemList) {
        Log.e(TAG, "updateMapWithRestaurants: new list " + resultsItemList.toString(), null);
        restaurantList = resultsItemList;
        addRestaurantMarkersToMap(resultsItemList, map);
    }

    private void updateLocation(Location location) {

        if (this.isAdded()) {
            this.location = location;
            getCurrentLocation(map);

            //API call
            viewModel.getAllRestaurantList(requireContext(), this.location);
            viewModel.getRestaurants().observe(requireActivity(), this::updateMapWithRestaurants);
            Log.e(TAG, "updateLocation: updated", null);
        }

    }

    private void checkIfRestaurantHasBeenSelected() {
        //viewModel.getSelectedRestaurant().observe(requireActivity(), this::focusOnSelectedRestaurant);
        Log.e(TAG, "checkIfRestaurantHasBeenSelected: accessed", null);
    }

    private void focusOnSelectedRestaurant(String id){
        //PredictionsItem selectedRestaurant = viewModel.getSelectedRestaurant().getValue();
/*        if (selectedRestaurant != null) {
            Log.e(TAG, "focusOnSelectedRestaurant: CALLED", null);
            viewModel.getSelectedRestaurantDetail(selectedRestaurant.getPlaceId());
            Result restaurant = viewModel.getSelectedRestaurantDetails().getValue();
            LatLng restaurantLatLng = new LatLng(restaurant.getGeometry().getLocation().getLat(),restaurant.getGeometry().getLocation().getLng());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLatLng, defaultCameraZoomOverMap));
        }*/
        Log.e(TAG, "focusOnSelectedRestaurant: called " + id, null);
        Toast.makeText(requireContext(), id, Toast.LENGTH_LONG).show();
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

        googleMap.setOnInfoWindowClickListener(marker -> {
            List<ResultsItem> list1 = viewModel.getRestaurants().getValue();

            assert list1 != null;
            for (ResultsItem resultsItem : list1) {
                if (Objects.requireNonNull(marker.getTitle()).equalsIgnoreCase(resultsItem.getName())) {
                    openDetailActivity(resultsItem);
                }
            }
        });
    }

    private void setCameraListener(GoogleMap googleMap) {
        googleMap.setOnCameraMoveListener(() -> cameraPosition = googleMap.getCameraPosition());
    }

    private void openDetailActivity(ResultsItem restaurant) {
        Intent intent = new Intent(requireContext(), RestaurantDetails.class);
        intent.putExtra("id", restaurant.getPlaceId());
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
        if (location != null) {
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, defaultCameraZoomOverMap));
        }
    }

    /**
     * Permission handling
     */

    public void requestPermission() {
        if (permission.hasLocationPermissions(requireContext())) {
            fetchAndObserveData();
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
        fetchAndObserveData();
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

    @Override
    public void onPause() {
        super.onPause();
        viewModel.stopTrackingLocation();
        Log.e(TAG, "onPause: called", null);
    }

    /**
     * LifeCycle
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel.hasPermission(requireContext())) {
            viewModel.getRestaurants().removeObservers(this);
            binding = null;
        }
        setCameraListener(map);
        Log.e(TAG, "onDestroyView: Destroy", null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraPosition != null) {
            Log.e(TAG, "onResume: called here", null);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, defaultCameraZoomOverMap));
        }
    }

}

