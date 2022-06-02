package com.alphaomardiallo.go4lunch.data.repositories;

import android.content.Context;

import androidx.annotation.Nullable;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class UserRepositoryImp implements UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String HAS_A_BOOKING = "hasABooking";
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

            userData.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains(HAS_A_BOOKING)) {
                    userToCreate.setBookingOfTheDay((Boolean)documentSnapshot.get(HAS_A_BOOKING));
                }
                this.getUserCollection().document(userID).set(userToCreate);
            });
        }
    }


    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        assert this.getCurrentUser() != null;
        String uid = this.getCurrentUser().getUid();
        return this.getUserCollection().document(uid).get();
    }

    // Update User status about booking of the day
    //TODO setup how this information will be updated
    public void updateHasABooking(Boolean hasABooking) {
        String userID = this.getCurrentUserID();
        if(userID != null) {
            this.getUserCollection().document(userID).update(HAS_A_BOOKING, hasABooking);
        }
    }

    //Delete the user from firestore
    public void deleteUserFromFirestore() {
        String userID = this.getCurrentUserID();
        if(userID != null) {
            this.getUserCollection().document(userID).delete();
        }
    }
}
