package com.alphaomardiallo.go4lunch.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MainSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentListViewBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickRestaurantListener;
import com.alphaomardiallo.go4lunch.ui.Activities.RestaurantDetails;
import com.alphaomardiallo.go4lunch.ui.adapters.ListViewAdapter;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements OnClickRestaurantListener {

    private static final String KEY_RESTAURANT_ID = "id";
    private static final int SNACK_BAR_LENGTH_LONG = 10000;
    private Location location = null;
    private FragmentListViewBinding binding;
    private MainSharedViewModel viewModel;
    private List<ResultsItem> restaurantList = new ArrayList<>();
    private List<Booking> bookingList = new ArrayList<>();
    private ListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));

        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLocationDefaultLocation();

        setLoaders(restaurantList);

        observePermission();
    }

    /**
     * Methods getting API's to populate recyclerView
     */

    private void getLocationDefaultLocation() {
        location = viewModel.getOfficeLocation();
        setAdapter(location);
    }

    private void observePermission() {
        viewModel.observePermissionState().observe(requireActivity(), this::observeData);
    }

    private void observeData(boolean hasPermission) {
        if (hasPermission) {
            if (this.isAdded()) {
                viewModel.getAllBookings().observe(requireActivity(), this::updateBookingList);
            }
        }
    }

    private void updateBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
        viewModel.getCurrentLocation().observe(requireActivity(), this::updateLocation);
        viewModel.getRestaurantToFocusOn().observe(requireActivity(), this::getSearchResult);
    }

    private void updateLocation(Location location) {
        this.location = location;
        setAdapter(this.location);
        if (this.isAdded()) {
            viewModel.getCurrentLocation().observe(requireActivity(), this::updateViewsWithParametersLocation);
        }
    }

    private void updateViewsWithParametersLocation(Location location) {
        if (this.isAdded()) {
            viewModel.getRestaurants().observe(requireActivity(), this::setLoaders);
            setAdapter(location);
            viewModel.getRestaurants().observe(requireActivity(), adapter::submitList);
        }
    }

    /**
     * Using search result
     */

    private void getSearchResult(String restaurantID) {
        boolean isInList = false;
        int position;
        for (ResultsItem item : restaurantList) {
            if (item.getPlaceId().equalsIgnoreCase(restaurantID)) {
                position = restaurantList.indexOf(item);
                isInList = true;
                binding.recyclerView.scrollToPosition(position);
                break;
            }
        }

        if (!isInList) {
            Snackbar.make(binding.listViewFragment, getString(R.string.restaurant_not_in_list), SNACK_BAR_LENGTH_LONG)
                    .setAction(getString(R.string.get_details), view -> openDetailActivity(restaurantID)).show();
        }
    }

    /**
     * Adapter settings
     */

    private void setAdapter(Location location) {
        adapter = new ListViewAdapter(new ListViewAdapter.ListDiff(), this, location, bookingList);
        binding.recyclerView.setAdapter(adapter);
    }

    /**
     * Method overriding onCLickItemListener interface to manage clicks on recyclerView
     */
    @Override
    public void onClickRestaurant(int position) {
        ResultsItem restaurant = Objects.requireNonNull(viewModel.getRestaurants().getValue()).get(position);
        openDetailActivity(restaurant.getPlaceId());
    }

    /**
     * Managing the idling time with a gif
     */
    private void setLoaders(List<ResultsItem> list) {
        restaurantList = list;

        if (!viewModel.hasPermission(requireContext())) {
            binding.tvLoadingMessage.setText(R.string.location_missing);
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load(getString(R.string.we_need_your_location_gif))
                    .into(binding.ivLoadingGIF);
            return;
        }

        if (restaurantList == null || restaurantList.isEmpty()) {
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load(getString(R.string.fetching_list_gif))
                    .into(binding.ivLoadingGIF);

            binding.tvLoadingMessage.setVisibility(View.VISIBLE);
            binding.ivLoadingGIF.setVisibility(View.VISIBLE);
        } else {
            binding.tvLoadingMessage.setVisibility(View.INVISIBLE);
            binding.ivLoadingGIF.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Open details
     */

    private void openDetailActivity(String restaurantID) {
        Intent intent = new Intent(requireContext(), RestaurantDetails.class);
        intent.putExtra(KEY_RESTAURANT_ID, restaurantID);
        startActivity(intent);
    }

    /**
     * LifeCycle
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (viewModel.hasPermission(requireContext())) {
            viewModel.getRestaurants().removeObservers(this);
            viewModel.getCurrentLocation().removeObservers(this);
            viewModel.observePermissionState().removeObservers(this);
        }
    }
}