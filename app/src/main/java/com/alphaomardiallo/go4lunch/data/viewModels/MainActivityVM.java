package com.alphaomardiallo.go4lunch.data.viewModels;

import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.repositories.UserRepository;
import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityVM extends ViewModel {

    private final UserRepositoryImp userRepositoryImp;

    @Inject
    public MainActivityVM(UserRepositoryImp userRepositoryImp) {
        this.userRepositoryImp = userRepositoryImp;
    }

    public UserRepository getInstance() {
        return userRepositoryImp.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return userRepositoryImp.getCurrentUser();
    }

    public Boolean isCurrentUserNotLoggedIn(){
        return (this.getCurrentUser() == null);
    }
}
