package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import javax.inject.Inject;

public class UserRepositoryImp implements UserRepository {

    private static final String FIELD_FAVOURITE_RESTAURANT = "favouriteRestaurants";
    private static final String COLLECTION_NAME = "users";
    private static final String HAS_A_BOOKING = "hasABooking";
    private static volatile UserRepository instance;
    private FirebaseFirestore database;
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();

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

    @Nullable
    public String getCurrentUserID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    @Override
    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    @Override
    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    /**
     * FIREBASE RELATED METHODS
     */

    public void getDataBaseInstance() {
        database = FirebaseFirestore.getInstance();
    }

    //Get the collection reference
    public CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Create user in Firestore
    public void createUser() {
        FirebaseUser currentUser = getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();
            String username = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            String urlPicture = (currentUser.getPhotoUrl() != null) ? currentUser.getPhotoUrl().toString() : null;

            User userToCreate = new User(userID, username, userEmail, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();

            userData.addOnSuccessListener(documentSnapshot -> this.getUserCollection().document(userID).set(userToCreate));
        }
    }

    //Get updated list of users
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        assert this.getCurrentUser() != null;
        String uid = this.getCurrentUser().getUid();
        return this.getUserCollection().document(uid).get();
    }

    //Get ll users from FireStore
    public void getAllUsersFromDataBase() {
        database.collection(COLLECTION_NAME)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Listen fail ", error);
                        return;
                    }

                    if (value != null) {
                        List<User> temList = value.toObjects(User.class);
                        allUsers.setValue(temList);
                    } else {
                        Log.i(TAG, "onEvent: all user is null");
                    }

                });
    }

    // Update User status about booking of the day
    public void updateHasABooking(Boolean hasABooking) {
        String userID = this.getCurrentUserID();
        if (userID != null) {
            this.getUserCollection().document(userID).update(HAS_A_BOOKING, hasABooking);
        }
    }

    //Delete the user from firestore
    public void deleteUserFromFirestore() {
        String userID = this.getCurrentUserID();
        if (userID != null) {
            this.getUserCollection().document(userID).delete();
        }
    }

    //Get user data
    public void getUserDataFromDataBase(String userID) {
        DocumentReference docRef = database.collection(COLLECTION_NAME).document(userID);

        docRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "onEvent: Listen failed", error);
                return;
            }

            if (value != null && value.exists()) {
                user.setValue(value.toObject(User.class));
            } else {
                Log.i(TAG, "onEvent: data is null");
                createUser();
            }

        });
    }

    public LiveData<User> observeCurrentUser() {
        return user;
    }

    public void addFavouriteRestaurant(String userID, String restaurantID) {
        database.collection(COLLECTION_NAME).document(userID)
                .update(FIELD_FAVOURITE_RESTAURANT, FieldValue.arrayUnion(restaurantID))
                .addOnFailureListener(e -> Log.e(TAG, "addFavouriteRestaurant: failed ", e));
    }

    public void removeFavouriteRestaurant(String userID, String restaurantID) {
        database.collection(COLLECTION_NAME).document(userID)
                .update(FIELD_FAVOURITE_RESTAURANT, FieldValue.arrayRemove(restaurantID))
                .addOnFailureListener(e -> Log.e(TAG, "removeFavouriteRestaurant: failed ", e));
    }
}
