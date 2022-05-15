package com.alphaomardiallo.go4lunch.ui.fragments;

import android.annotation.SuppressLint;
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
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MapsAndListSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentListViewBinding;
import com.alphaomardiallo.go4lunch.ui.adapters.ListViewAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private FragmentListViewBinding binding;
    public MapsAndListSharedViewModel viewModel;
    private List<ResultsItem> restaurantList;
    private final ListViewAdapter adapter = new ListViewAdapter(new ListViewAdapter.ListDiff(), restaurantList);

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
        getNearByRestaurants();

    }

    public void getNearByRestaurants() {
        viewModel.getAllRestaurantList(viewModel.getOfficeLocationAsString(), viewModel.getRadius()).observe(requireActivity(), resultsItems -> handler.postDelayed(() -> {
            if(resultsItems != null /*&& (restaurantList == null || !restaurantList.equals(resultsItems))*/) {
                System.out.println("List change " + resultsItems.size());
                restaurantList = resultsItems;
                viewModel.getAllRestaurantList(viewModel.getOfficeLocationAsString(), viewModel.getRadius()).observe(requireActivity(), adapter::submitList);
            }
        }, 1000));
    }

}