package com.alphaomardiallo.go4lunch.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MainSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentMapsBinding;
import com.alphaomardiallo.go4lunch.ui.RestaurantDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapsFragment extends Fragment {

    private static final int SNACK_BAR_LENGTH_LONG = 10000;
    private static final int ZERO = 0;
    private static final String RESTAURANT_ID_KEY = "id";
    private final long defaultCameraZoomOverMap = 19;
    private FragmentMapsBinding binding;
    private MainSharedViewModel viewModel;
    private List<ResultsItem> restaurantList;
    private List<Booking> bookingList;
    private GoogleMap map;
    private Location location;

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

            // Customise the styling of the base map using a JSON object defined in a raw resource file.
            try {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                requireContext(), R.raw.map_styling));

                if (!success) {
                    Toast.makeText(requireContext(), getString(R.string.style_parsing_failed), Toast.LENGTH_SHORT).show();
                }
            } catch (Resources.NotFoundException e) {
                Toast.makeText(requireContext(), getString(R.string.style_parsing_error), Toast.LENGTH_SHORT).show();
            }

            map = googleMap;

            setMap(googleMap);

            addOfficeMarkerToMap(googleMap);

            if (location != null) {
                getCurrentLocation(googleMap);
            }

            if (restaurantList != null) {
                addRestaurantMarkersToMap(restaurantList, googleMap);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        this.onAttach(requireContext());

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

        observePermission();
    }

    /**
     * Getting data from viewModel and update views accordingly
     */

    private void observeBookings() {
        viewModel.getAllBookings().observe(requireActivity(), this::setBookingList);
    }

    private void setBookingList(List<Booking> list) {
        bookingList = list;
    }

    private void observePermission() {
        viewModel.observePermissionState().observe(requireActivity(), this::observeData);
    }

    private void observeData(Boolean hasPermission) {
        if (hasPermission) {
            if (this.isAdded()) {
                observeBookings();
                viewModel.getCurrentLocation().observe(requireActivity(), this::updateLocation);
                viewModel.getRestaurantToFocusOn().observe(requireActivity(), this::focusOnSelectedRestaurant);
            }
        }
    }

    private void updateMapWithRestaurants(List<ResultsItem> resultsItemList) {
        restaurantList = resultsItemList;
        addRestaurantMarkersToMap(resultsItemList, map);
    }

    private void updateLocation(Location location) {
        if (this.isAdded()) {
            this.location = location;
            enableMyLocation(map);
            binding.fabMyLocation.setOnClickListener(view1 -> getCurrentLocation(map));
            getCurrentLocation(map);
            viewModel.getRestaurants().observe(requireActivity(), this::updateMapWithRestaurants);
        }
    }

    private void focusOnSelectedRestaurant(String id) {
        if (this.isAdded()) {
            if (id != null) {
                viewModel.fetchPartialRestaurantDetails(id);
                viewModel.getPartialRestaurantDetails().observe(requireActivity(), this::moveMapAndSetLocationOfSelectedRestaurant);
            }
        }
    }

    private void moveMapAndSetLocationOfSelectedRestaurant(Result restaurant) {
        boolean isAlreadyInTheList = false;

        LatLng restaurantLatLng = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLatLng, defaultCameraZoomOverMap));

        for (ResultsItem item : restaurantList) {
            if (item.getName().equalsIgnoreCase(restaurant.getName())) {
                isAlreadyInTheList = true;
                break;
            }
        }

        if (!isAlreadyInTheList) {
            map.addMarker(new MarkerOptions()
                    .title(restaurant.getName())
                    .position(restaurantLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(), R.drawable.restaurant))));
            String snackBarMessage = String.format(getString(R.string.get_info_selected_restaurant), restaurant.getName());
            Snackbar.make(binding.map, snackBarMessage, SNACK_BAR_LENGTH_LONG)
                    .setAction(getString(R.string.get_details), view -> openDetailActivity(restaurant.getPlaceId()))
                    .show();
        }
    }

    /**
     * Map handling
     */

    private void setMap(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        viewModel.observePermissionState().observe(requireActivity(), this::setCamera);

    }

    private void setCamera(boolean hasPermission) {
        if (hasPermission) {
            enableMyLocation(map);
            binding.fabMyLocation.setOnClickListener(view1 -> getCurrentLocation(map));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getOfficeLatLng(), defaultCameraZoomOverMap));
        }
    }

    private void addOfficeMarkerToMap(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(viewModel.getOfficeLatLng())
                .title(getString(R.string.office_name))
                .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(), R.drawable.office_marker)))
        );
    }

    private void addRestaurantMarkersToMap(List<ResultsItem> list, GoogleMap googleMap) {
        for (ResultsItem resultsItem : list) {

            boolean hasBooking = false;

            LatLng coordinates = new LatLng(resultsItem.getGeometry().getLocation().getLat(), resultsItem.getGeometry().getLocation().getLng());

            if (bookingList != null && bookingList.size() > ZERO) {


                for (Booking booking : bookingList) {
                    if (booking.getBookedRestaurantID().equalsIgnoreCase(resultsItem.getPlaceId())) {
                        hasBooking = true;
                        break;
                    }
                }
            }
            if (hasBooking) {
                googleMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(resultsItem.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(), R.drawable.booked_restaurant)))
                );
            } else {
                googleMap.addMarker(new MarkerOptions()
                        .position(coordinates)
                        .title(resultsItem.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(viewModel.resizeMarker(requireContext().getResources(), R.drawable.restaurant)))
                );
            }
        }

        googleMap.setOnInfoWindowClickListener(marker -> {
            List<ResultsItem> list1 = viewModel.getRestaurants().getValue();

            assert list1 != null;
            for (ResultsItem resultsItem : list1) {
                if (Objects.requireNonNull(marker.getTitle()).equalsIgnoreCase(resultsItem.getName())) {
                    openDetailActivity(resultsItem.getPlaceId());
                }
            }
        });
    }

    private void openDetailActivity(String restaurantID) {
        Intent intent = new Intent(requireContext(), RestaurantDetails.class);
        intent.putExtra(RESTAURANT_ID_KEY, restaurantID);
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
     * LifeCycle
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel.hasPermission(requireContext())) {
            viewModel.getRestaurants().removeObservers(this);
            viewModel.getPartialRestaurantDetails().removeObservers(this);
            viewModel.observePermissionState().removeObservers(this);
        }
    }

}