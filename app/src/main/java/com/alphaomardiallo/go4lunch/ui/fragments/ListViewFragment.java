package com.alphaomardiallo.go4lunch.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.RestaurantDetails;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MapsAndListSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentListViewBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickItemListener;
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
    public MapsAndListSharedViewModel viewModel;
    private List<ResultsItem> restaurantList = new ArrayList<>();
    private final ListViewAdapter adapter = new ListViewAdapter(new ListViewAdapter.ListDiff(), this);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MapsAndListSharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoaders();
        getNearByRestaurants();
    }

    /**
     * Methods getting API's to populate recyclerView
     */
    public void getNearByRestaurants() {
        viewModel.getRestaurants().observe(requireActivity(), this::setLoadersAfterAPICalls);
        viewModel.getAllRestaurantList(requireContext());
        viewModel.getRestaurants().observe(requireActivity(), adapter::submitList);
    }

    /**
     * Method overriding onCLickItemListener interface to manage clicks on recyclerView
     */
    @Override
    public void onClickItem(int position) {
        ResultsItem restaurant = Objects.requireNonNull(viewModel.getRestaurants().getValue()).get(position);
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
     * Managing the idling time with a gif
     */
    private void setLoaders() {
        binding.tvLoadingMessage.setVisibility(View.VISIBLE);
        binding.ivLoadingGIF.setVisibility(View.VISIBLE);
        if (restaurantList == null || restaurantList.isEmpty()) {
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load("https://media.giphy.com/media/0KiLnOipDAnfk5Jgsf/giphy.gif")
                    .into(binding.ivLoadingGIF);
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
}