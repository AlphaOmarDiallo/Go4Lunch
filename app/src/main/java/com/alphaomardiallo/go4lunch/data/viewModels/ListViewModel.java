package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.repositories.APIRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel

public class ListViewModel extends ViewModel {

    private final APIRepository apiRepository;

    @Inject
    public ListViewModel(APIRepository apiRepository) {
        this.apiRepository = apiRepository;
    }
}
