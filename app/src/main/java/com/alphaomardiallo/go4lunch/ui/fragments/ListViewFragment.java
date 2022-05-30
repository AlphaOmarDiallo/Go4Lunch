package com.alphaomardiallo.go4lunch.ui.fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MainSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentListViewBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickItemListener;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;
import com.alphaomardiallo.go4lunch.ui.RestaurantDetails;
import com.alphaomardiallo.go4lunch.ui.adapters.ListViewAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements OnClickItemListener {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private FragmentListViewBinding binding;
    public MainSharedViewModel viewModel;
    private List<ResultsItem> restaurantList = new ArrayList<>();
    private final PositionUtils positionUtils = new PositionUtils();
    private final Location location = positionUtils.getOfficeLocationFormat();
    private ListViewAdapter adapter = new ListViewAdapter(new ListViewAdapter.ListDiff(), this, location);

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
        setLoaders();

        if (viewModel.hasPermission(requireContext())) {
            observeData();
        }
    }

    /**
     * Methods getting API's to populate recyclerView
     */
    public void observeData() {
        if (this.isAdded()) {
            viewModel.getCurrentLocation().observe(requireActivity(), this::updateLocation);
            viewModel.getRestaurantToFocusOn().observe(requireActivity(), this::getSearchResult);
        }
    }

    public void updateViewsWithParametersLocation(Location location) {
        if (this.isAdded()) {
            viewModel.getRestaurants().observe(requireActivity(), this::setLoadersAfterAPICalls);
            viewModel.getRestaurants().observe(requireActivity(), adapter::submitList);
        }
    }

    private void updateLocation(Location location) {
        setAdapter(location);
        if (this.isAdded()) {
            viewModel.getCurrentLocation().observe(requireActivity(), this::updateViewsWithParametersLocation);
        }
    }

    private void getSearchResult(String restaurantID) {
        Log.e(TAG, "getSearchResult: called " + restaurantID, null);
    }

    /**
     * Adapter settings
     */

    private void setAdapter(Location location) {
        adapter = new ListViewAdapter(new ListViewAdapter.ListDiff(), this, location);
        binding.recyclerView.setAdapter(adapter);
    }

    /**
     * Method overriding onCLickItemListener interface to manage clicks on recyclerView
     */
    @Override
    public void onClickItem(int position) {
        ResultsItem restaurant = Objects.requireNonNull(viewModel.getRestaurants().getValue()).get(position);
        Intent intent = new Intent(requireContext(), RestaurantDetails.class);
        intent.putExtra("id", restaurant.getPlaceId());
        startActivity(intent);
    }

    /**
     * Managing the idling time with a gif
     */
    private void setLoaders() {

        if (!viewModel.hasPermission(requireContext())) {
            binding.tvLoadingMessage.setText(R.string.location_missing);
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load("https://media.giphy.com/media/Vh8zf1nIfQRRXksAm1/giphy.gif")
                    .into(binding.ivLoadingGIF);
            return;
        }

        if (restaurantList == null || restaurantList.isEmpty()) {
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load("https://media.giphy.com/media/0KiLnOipDAnfk5Jgsf/giphy.gif")
                    .into(binding.ivLoadingGIF);

            binding.tvLoadingMessage.setVisibility(View.VISIBLE);
            binding.ivLoadingGIF.setVisibility(View.VISIBLE);
        } else {
            binding.tvLoadingMessage.setVisibility(View.INVISIBLE);
            binding.ivLoadingGIF.setVisibility(View.INVISIBLE);
        }
    }

    public void setLoadersAfterAPICalls(List<ResultsItem> list) {
        restaurantList = list;
        if (list.isEmpty()) {
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load("https://media.giphy.com/media/0KiLnOipDAnfk5Jgsf/giphy.gif")
                    .into(binding.ivLoadingGIF);
        } else {
            binding.tvLoadingMessage.setVisibility(View.INVISIBLE);
            binding.ivLoadingGIF.setVisibility(View.INVISIBLE);
        }

        handler.postDelayed(() -> {
            binding.tvLoadingMessage.setText(R.string.no_restaurant_in_your_area);
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .override(100, 100)
                    .load("https://media.giphy.com/media/MdeHHwPzLpzbEkzl70/giphy.gif")
                    .into(binding.ivLoadingGIF);
        }, 20000);
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
            //viewModel.stopTrackingLocation();
        }

        Log.e(TAG, "onDestroyView: Destroy", null);
    }
}