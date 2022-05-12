package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitAutocompleteAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitDetailsAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitNearBySearchAPI;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRepositoryImp implements APIRepository {

    String typeRestaurant = "restaurant";
    PositionUtils positionUtils = new PositionUtils();
    int radius = positionUtils.getRadius();

    @Inject
    public APIRepositoryImp() {
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    public LiveData getNearBySearchListAsLiveData(String location) {
        RetrofitNearBySearchAPI retrofitNearBySearchAPI = retrofit.create(RetrofitNearBySearchAPI.class);

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlaces(BuildConfig.PLACES_API_KEY, location, typeRestaurant, radius);

        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                Log.i(TAG, "onResponse: check " + response.raw().request().url());
            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
            }
        });
        return null;
    }

    @Override
    public LiveData getDetailsListAsLiveData() {
        RetrofitDetailsAPI retrofitDetailsAPI = retrofit.create(RetrofitDetailsAPI.class);
        return null;
    }

    @Override
    public LiveData getAutocompleteListAsLiveData() {
        RetrofitAutocompleteAPI retrofitAutocompleteAPI = retrofit.create(RetrofitAutocompleteAPI.class);
        return null;
    }
}
