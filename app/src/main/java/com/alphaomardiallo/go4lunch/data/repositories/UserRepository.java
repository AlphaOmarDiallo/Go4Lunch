package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public interface UserRepository {

    UserRepository getInstance();

    @Nullable
    FirebaseUser getCurrentUser();

    Task<Void> signOut(Context context);

    Task<Void> deleteUser(Context context);



}
