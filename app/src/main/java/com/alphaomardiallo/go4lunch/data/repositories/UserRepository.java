package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface UserRepository {

    UserRepository getInstance();

    @Nullable
    FirebaseUser getCurrentUser();

    @Nullable
    String getCurrentUserID();

    Task<Void> signOut(Context context);

    Task<Void> deleteUser(Context context);

    LiveData<List<User>> getAllUsers();

    void getDataBaseInstance();

    CollectionReference getUserCollection();

    void createUser();

    Task<DocumentSnapshot> getUserData();

    void updateHasABooking(Boolean hasABooking);

    void deleteUserFromFirestore();

    void getAllUsersFromDataBase();


}
