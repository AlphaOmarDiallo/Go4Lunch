package com.alphaomardiallo.go4lunch.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alphaomardiallo.go4lunch.databinding.FragmentListViewBinding;
import com.alphaomardiallo.go4lunch.domain.PermissionUtils;

public class ListViewFragment extends Fragment {

    private static final String TAG = "ListViewFragment";
    private FragmentListViewBinding binding;
    private PermissionUtils permissionUtils = new PermissionUtils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}