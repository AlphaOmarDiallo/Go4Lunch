package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitNearBySearchAPI;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRepositoryImp implements APIRepository {

    private static final String RESTAURANT = "restaurant";
    private static final String rankBy = "distance";
    private static final int MAXPRICE = 2;
    private static final int HANDLING_TIME = 2000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<List<ResultsItem>> restaurantListLiveData = new MutableLiveData<>();
    private final List<ResultsItem> restaurantList = new ArrayList<>();

    @Inject
    public APIRepositoryImp() {
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitNearBySearchAPI retrofitNearBySearchAPI = retrofit.create(RetrofitNearBySearchAPI.class);

    @Override
    public LiveData<List<ResultsItem>> getNearBySearchRestaurantList() {
        return restaurantListLiveData;
    }

    /**
     * Getting NearBySearch results as a list using the radius parameter and managing the recall if there is a page token
     */

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
        } );

        Log.i(TAG, "fetchNearBySearchPlaces: API called", null);
    }

    /**
     * PageToken recall if it is found
     */

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

    /**
     * API called when API with Radius method does not return anything
     */

    private void noRestaurantInRadius(String location) {
        Call<PlaceNearBy> call3 = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, rankBy, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, null);

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

    /**
     * populate the list to avoid double results
     */

    private void populateList(List<ResultsItem> list) {
        restaurantList.addAll(list);
        restaurantListLiveData.setValue(restaurantList);
    }

}
