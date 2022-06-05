package com.alphaomardiallo.go4lunch.ui.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
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
import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.alphaomardiallo.go4lunch.data.viewModels.MainSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentWorkmatesBinding;
import com.alphaomardiallo.go4lunch.domain.OnClickItemListener;
import com.alphaomardiallo.go4lunch.ui.adapters.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment implements OnClickItemListener {

    FragmentWorkmatesBinding binding;
    MainSharedViewModel viewModel;
    private List<Booking> allBookings = new ArrayList<>();
    private WorkmatesAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        binding.rvWorkmates.setHasFixedSize(true);
        binding.rvWorkmates.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new WorkmatesAdapter(new WorkmatesAdapter.ListDiff(), this, allBookings, requireContext());
        binding.rvWorkmates.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observeUserList();

    }

    /**
     * Observing user list
     */

    private void observeUserList() {
        if (this.isAdded()) {
            viewModel.observeUserList().observe(requireActivity(), adapter::submitList);
        }
    }

    /**
     * LifeCycle
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (viewModel.hasPermission(requireContext())) {
            viewModel.stopTrackingLocation();
        }

        Log.e(TAG, "onDestroyView: Destroy", null);
    }

    @Override
    public void onClickItem(int position) {
        Log.d(TAG, "onClickItem: do something here");
    }
}