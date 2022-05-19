package com.alphaomardiallo.go4lunch.ui.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alphaomardiallo.go4lunch.R;
import com.alphaomardiallo.go4lunch.data.viewModels.MapsAndListSharedViewModel;
import com.alphaomardiallo.go4lunch.databinding.FragmentWorkmatesBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;
    MapsAndListSharedViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MapsAndListSharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        return binding.getRoot();
    }

    /**
     * LyfeCycle
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.stopTrackingLocation();
        viewModel.getRestaurants().removeObservers(this);
        Log.e(TAG, "onDestroyView: Destroy", null);
    }
}