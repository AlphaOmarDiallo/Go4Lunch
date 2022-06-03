package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public interface UserRepository {

    UserRepository getInstance();

    @Nullable
    FirebaseUser getCurrentUser();

    @Nullable
    String getCurrentUserID();

    Task<Void> signOut(Context context);

    Task<Void> deleteUser(Context context);

    void getDataBaseInstance();

    CollectionReference getUserCollection();

    void createUser();

    Task<DocumentSnapshot> getUserData();

    void updateHasABooking(Boolean hasABooking);

    void deleteUserFromFirestore();


}
