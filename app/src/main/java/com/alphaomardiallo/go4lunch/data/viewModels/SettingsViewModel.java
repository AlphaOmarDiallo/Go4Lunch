package com.alphaomardiallo.go4lunch.data.viewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
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

    public void getDataBaseInstanceUser() {
        userRepositoryImp.getDataBaseInstance();
    }

    public void getCurrentUserDataFromFireStore(String userID) {
        userRepositoryImp.getUserDataFromDataBase(userID);
    }

    public LiveData<User> observeCurrentUserFromFireStore() {
        return userRepositoryImp.observeCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return userRepositoryImp.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return userRepositoryImp.deleteUser(context);
    }

    public void deleteUserFromFirestore() {
        userRepositoryImp.deleteUserFromFirestore();
    }
}
