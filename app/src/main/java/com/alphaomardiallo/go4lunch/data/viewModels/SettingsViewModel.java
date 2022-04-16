package com.alphaomardiallo.go4lunch.data.viewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.repositories.UserRepositoryImp;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private final UserRepositoryImp userRepositoryImp;

    @Inject
    public SettingsViewModel(UserRepositoryImp userRepositoryImp) {
        this.userRepositoryImp = userRepositoryImp;
    }

    public FirebaseUser getCurrentUser() {
        return userRepositoryImp.getCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return userRepositoryImp.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return userRepositoryImp.deleteUser(context);
    }
}
