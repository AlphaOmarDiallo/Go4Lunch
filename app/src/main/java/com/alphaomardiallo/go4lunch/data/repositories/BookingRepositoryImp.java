package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.Booking;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class BookingRepositoryImp implements BookingRepository {

    private static final String COLLECTION_NAME = "booking";
    private FirebaseFirestore database;

    @Inject
    public BookingRepositoryImp() {
    }

    public void getInstance() {
        database = FirebaseFirestore.getInstance();
        Log.i(TAG, "getInstance: FireBase " + database);
    }

    public void createBookingAndAddInDatabase(@NonNull Booking bookingToSave) {
        //Create a booking
        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingID", bookingToSave.getBookingID());
        booking.put("bookingDate", bookingToSave.getBookingDate());
        booking.put("restaurantID", bookingToSave.getBookedRestaurantID());
        booking.put("userWhoBooked", bookingToSave.getUserWhoBooked());

        database.collection(COLLECTION_NAME)
                .add(booking)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: ocument added " + documentReference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Error adding document " + e, null);
                    }
                });
    }

    public void getAllBookingsFromDataBase() {
        database.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " " + document.getData());
                            }
                        } else {
                            Log.e(TAG, "onComplete: Error getting document " + task.getException(), null);
                        }
                    }
                });
    }


}
