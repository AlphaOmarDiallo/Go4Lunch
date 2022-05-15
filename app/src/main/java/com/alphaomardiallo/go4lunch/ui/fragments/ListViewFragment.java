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

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements OnClickItemListener {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private FragmentListViewBinding binding;
    public MapsAndListSharedViewModel viewModel;
    private List<ResultsItem> restaurantList;
    private final ListViewAdapter adapter = new ListViewAdapter(new ListViewAdapter.ListDiff(), restaurantList, this);

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
        loadingGIFSetup();
        getNearByRestaurants();

    }

    public void getNearByRestaurants() {
        viewModel.getAllRestaurantList(viewModel.getOfficeLocationAsString(), viewModel.getRadius()).observe(requireActivity(), resultsItems -> handler.postDelayed(() -> {
            if(resultsItems != null /*&& (restaurantList == null || !restaurantList.equals(resultsItems))*/) {
                System.out.println("List change " + resultsItems.size());
                restaurantList = resultsItems;
                viewModel.getAllRestaurantList(viewModel.getOfficeLocationAsString(), viewModel.getRadius()).observe(requireActivity(), adapter::submitList);
                viewModel.getRestaurants().observe(getViewLifecycleOwner(), this::updateRestaurantList);
                loadingGIFSetup();
            }
        }, 1000));
    }

    private void loadingGIFSetup(){
        // https://tenor.com/view/ice-family-bear-food-hungry-gif-15132050648417346282
        if (restaurantList == null) {
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .load("https://media.giphy.com/media/0KiLnOipDAnfk5Jgsf/giphy.gif")
                    .into(binding.ivLoadingGIF);
        } else if (restaurantList != null && restaurantList.size() == 0){
            binding.tvLoadingMessage.setText("No results in your area");
            Glide.with(binding.ivLoadingGIF)
                    .asGif()
                    .override(100, 100)
                    .load("https://tenor.com/view/shermans-night-in-midnight-snack-hungry-shopping-the-fridge-empty-fridge-gif-14466290")
                    .into(binding.ivLoadingGIF);
        } else {
            binding.tvLoadingMessage.setVisibility(View.INVISIBLE);
            binding.ivLoadingGIF.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onClickItem(int position) {
        ResultsItem restaurant = viewModel.getRestaurants().getValue().get(position);
        Intent intent = new Intent(requireContext(), RestaurantDetails.class);
        intent.putExtra("id", restaurant.getPlaceId());
        startActivity(intent);
    }

    public void updateRestaurantList(List<ResultsItem> list) {
        restaurantList = list;
    }
}