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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.viewModels.MapsAndListSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentListViewBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;
import com.alphaomardiallo.go4lunch.ui.adapters.ListViewAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment {

    private static final String TAG = "ListViewFragment";
    private final Handler handler = new Handler(Looper.getMainLooper());

    private FragmentListViewBinding binding;
    public MapsAndListSharedViewModel viewModel;
    private PermissionUtils permissionUtils = new PermissionUtils();
    private PositionUtils positionUtils = new PositionUtils();
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
        viewModel.getAllRestaurantList("48.86501071160738, 2.3467211059168793").observe(requireActivity(), new Observer<List<ResultsItem>>() {
            @Override
            public void onChanged(List<ResultsItem> resultsItems) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(resultsItems != null) {
                            System.out.println("List change " + resultsItems.size());
                            viewModel.getAllRestaurantList("48.86501071160738, 2.3467211059168793").observe(requireActivity(), adapter::submitList);
                        }
                        System.out.println(resultsItems.toString());
                    }
                }, 1000);

            }
        });
    }


}