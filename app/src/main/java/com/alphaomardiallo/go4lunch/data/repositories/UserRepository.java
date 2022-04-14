package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;

public interface UserRepository {

    UserRepository getInstance();

    @Nullable
    FirebaseUser getCurrentUser();

}
