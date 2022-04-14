package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class UserRepositoryImp implements UserRepository {

    private static volatile UserRepository instance;

    @Inject
    public UserRepositoryImp() {
    }

    @Override
    public UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepositoryImp();
                }
            }
            return instance;
    }

    @Nullable
    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}
