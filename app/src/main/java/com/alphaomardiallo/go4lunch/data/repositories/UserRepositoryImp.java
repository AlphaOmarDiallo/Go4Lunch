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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;

public class UserRepositoryImp implements UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String HAS_A_BOOKING = "hasABooking";
    private static volatile UserRepository instance;
    private FirebaseFirestore database;
    private MutableLiveData<List<User>> allUsers = new MutableLiveData<>();

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
        Log.i(TAG, "getInstance: FireBase " + database);
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

            userData.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains(HAS_A_BOOKING)) {
                    userToCreate.setBookingOfTheDay((Boolean)documentSnapshot.get(HAS_A_BOOKING));
                }
                this.getUserCollection().document(userID).set(userToCreate);
            });
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
        Log.e(TAG, "getUserData: " + this.getCurrentUserID(), null);
        return this.getUserCollection().document(uid).get();
    }

    //Get ll users from FireStore
    public void getAllUsersFromDataBase() {
        /*database.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> tempList = task.getResult().toObjects(User.class);
                            allUsers.setValue(tempList);
                            Log.d(TAG, "onComplete: full list " + tempList);
                        } else {
                            Log.e(TAG, "onComplete: Error getting document " + task.getException(), null);
                        }
                    }
                });*/

        database.collection(COLLECTION_NAME)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen fail ",  error);
                            return;
                        }

                        if (value != null){
                            List<User> temList = value.toObjects(User.class);
                            allUsers.setValue(temList);
                            Log.d(TAG, "onEvent: all user " + allUsers.getValue());
                        } else {
                            Log.d(TAG, "onEvent: all user is null");
                        }

                    }
                });
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
