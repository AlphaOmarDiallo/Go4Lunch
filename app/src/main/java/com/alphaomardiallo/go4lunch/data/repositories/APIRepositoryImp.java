package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitAutocompleteAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitDetailsAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitNearBySearchAPI;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;

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
    private static final int MAXPRICE = 2;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private PositionUtils positionUtils = new PositionUtils();
    private int radius = positionUtils.getRadius();
    private String rankBy = "distance";
    private String pageToken = null;

    private MutableLiveData<List<ResultsItem>> nearByRestaurantListObserved = new MutableLiveData<>();
    private List<ResultsItem> nearByRestaurantList = new ArrayList();


    @Inject
    public APIRepositoryImp() {
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitNearBySearchAPI retrofitNearBySearchAPI = retrofit.create(RetrofitNearBySearchAPI.class);

    @Override
    public LiveData getNearBySearchListRankByMethod(String location) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, rankBy, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);

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
    public MutableLiveData<List<ResultsItem>> getNearBySearchListRadiusMethod(String location) {

        RetrofitNearBySearchAPI retrofitNearBySearchAPI = retrofit.create(RetrofitNearBySearchAPI.class);
        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, MAXPRICE, radius, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);

        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                Log.i(TAG, "onResponse: check " + response.raw().request().url());
                nearByRestaurantList.addAll(response.body().getResults());

                nearByRestaurantListObserved.setValue(nearByRestaurantList);

                System.out.println(nearByRestaurantListObserved.getValue().toArray().length);

                if (nearByRestaurantList.size() == 20) {
                    pageToken = response.body().getNextPageToken();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNextResults(location, pageToken);
                        }
                    }, 3000);

                }

            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
            }
        });
        return nearByRestaurantListObserved;
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

    public void getNextResults(String location, String pageToken) {
        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, MAXPRICE, radius, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);
        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }
                Log.i(TAG, "onResponse: check " + response.raw().request().url());
                List<ResultsItem> thisList = response.body().getResults();
                System.out.println(thisList.toArray().length);
                nearByRestaurantList.addAll(thisList);
                System.out.println(nearByRestaurantList.toArray().length);
                nearByRestaurantListObserved.setValue(nearByRestaurantList);

                if(nearByRestaurantList.size() == 40) {
                    String newPageToken = response.body().getNextPageToken();
                    getNextResults(location, newPageToken);
                    nearByRestaurantList.addAll(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), null);
            }
        });
    }

}
