package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.PlaceDetails;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitDetailsAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitNearBySearchAPI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesAPIRepositoryImp implements PlacesAPIRepository {

    private static final String RESTAURANT = "restaurant";
    private static final String RANK_BY = "distance";
    private static final String FIELDS_RESTAURANT_DETAIL_ACTIVITY = "international_phone_number,website";
    private static final String FIELDS_RESTAURANT_DETAIL_ACTIVITY_COMPLETE = "name,photo,vicinity,rating,geometry/location,international_phone_number,opening_hours/open_now,website";
    private static final int MAXPRICE = 2;
    private static final int HANDLING_TIME = 2000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<List<ResultsItem>> restaurantListLiveData = new MutableLiveData<>();
    private final List<ResultsItem> restaurantList = new ArrayList<>();
    private final MutableLiveData<Result> contact = new MutableLiveData<>();

    @Inject
    public PlacesAPIRepositoryImp() {
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitNearBySearchAPI retrofitNearBySearchAPI = retrofit.create(RetrofitNearBySearchAPI.class);

    /**
     * NearBySearch
     */

    @Override
    public LiveData<List<ResultsItem>> getNearBySearchRestaurantList() {
        return restaurantListLiveData;
    }

    //Getting NearBySearch results as a list using the radius parameter and managing the recall if there is a page token

    @Override
    public void fetchNearBySearchPlaces(String location, int radius) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, null);
        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {

                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                assert response.body() != null;

                populateList(response.body().getResults());

                if (response.body().getNextPageToken() != null) {

                    handler.postDelayed(() -> getNextResultsRadiusMethod(location, radius, response.body().getNextPageToken()), HANDLING_TIME);

                } else {

                    noRestaurantInRadius(location);

                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage(), t);

            }
        });

        Log.e(TAG, "fetchNearBySearchPlaces: API called", null);
    }

    //PageToken recall if it is found

    public void getNextResultsRadiusMethod(String location, int radius, String pageToken) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);
        call.enqueue(new Callback<PlaceNearBy>() {

            @Override
            public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {

                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                assert response.body() != null;
                populateList(response.body().getResults());

                if (response.body().getNextPageToken() != null) {

                    Call<PlaceNearBy> call2 = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, response.body().getNextPageToken());
                    call2.enqueue(new Callback<PlaceNearBy>() {

                        @Override
                        public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {
                            if (!response.isSuccessful()) {
                                Log.e(TAG, "onResponse: failed " + response.message(), null);
                                return;
                            }

                            assert response.body() != null;
                            populateList(response.body().getResults());

                        }

                        @Override
                        public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {

                            Log.e(TAG, "onFailure: " + t.getMessage(), t);

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage(), null);

            }
        });
    }

    //API called when API with Radius method does not return anything

    private void noRestaurantInRadius(String location) {
        Call<PlaceNearBy> call3 = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, RANK_BY, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, null);

        call3.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {

                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                assert response.body() != null;

                populateList(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), null);
            }
        });
    }

    //populate the list to avoid double results

    private void populateList(List<ResultsItem> list) {
        for (ResultsItem newItem : list) {
            boolean isNotInList = true;

            for (ResultsItem oldItem : restaurantList) {
                if (oldItem.getName().equalsIgnoreCase(newItem.getName())) {
                    isNotInList = false;
                    break;
                }
            }

            if (isNotInList) {
                restaurantList.add(newItem);
            }

        }

        //restaurantList.addAll(list);
        restaurantListLiveData.setValue(restaurantList);
    }

    /**
     * Places Detail
     */

    RetrofitDetailsAPI retrofitDetailsAPI = retrofit.create(RetrofitDetailsAPI.class);

    @Override
    public LiveData<Result> getDetails() {
        return contact;
    }

    @Override
    public void fetchDetails(String placeID){

        Call<PlaceDetails> call4 = retrofitDetailsAPI.getPlaceDetails(FIELDS_RESTAURANT_DETAIL_ACTIVITY, placeID, BuildConfig.PLACES_API_KEY);
        call4.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetails> call, @NonNull Response<PlaceDetails> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                if (response.body() != null) {
                    contact.setValue(response.body().getResult());
                }

            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetails> call, @NonNull Throwable t) {

            }
        });

        Log.e(TAG, "getDetails: PLACE DETAILS API CALLED", null);

    }

    @Override
    public void fetchAllDetails (String placeID){
        Call<PlaceDetails> call5 = retrofitDetailsAPI.getPlaceDetails(FIELDS_RESTAURANT_DETAIL_ACTIVITY_COMPLETE, placeID, BuildConfig.PLACES_API_KEY);
        call5.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetails> call, @NonNull Response<PlaceDetails> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                System.out.println(response.raw().request().url());

                if (response.body() != null) {
                    contact.setValue(response.body().getResult());
                }

            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetails> call, @NonNull Throwable t) {

            }
        });

        Log.e(TAG, "getDetails: PLACE DETAILS API CALLED", null);
    }

}
