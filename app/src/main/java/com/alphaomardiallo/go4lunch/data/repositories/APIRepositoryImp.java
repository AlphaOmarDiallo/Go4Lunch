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
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitNearBySearchAPI;
import com.alphaomardiallo.go4lunch.domain.PositionUtils;

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

    private PositionUtils positionUtils = new PositionUtils();
    private int radius = positionUtils.getRadius();

    private String pageToken = null;

    private MutableLiveData<List<ResultsItem>> nearByRestaurantList = new MutableLiveData<>();
    private MutableLiveData<List<ResultsItem>> rankByList = new MutableLiveData<>();


    @Inject
    public APIRepositoryImp() {
    }

    /**
     * Retrofit Builder
     */

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RetrofitNearBySearchAPI retrofitNearBySearchAPI = retrofit.create(RetrofitNearBySearchAPI.class);

    /**
     * Getting NearBySearch results as a list using the radius parameter and managing the recall if there is a page token
     *
     * @param location
     * @return nearByRestaurantList
     */

    @Override
    public LiveData<List<ResultsItem>> getNearBySearchListRadiusMethod(String location) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);

        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                nearByRestaurantList.setValue(response.body().getResults());

                //TODO delete these once API works correctly
                System.out.println(nearByRestaurantList.getValue().size());
                Log.d("onResponse: Retrofit ", response.raw().request().url().toString());

                if (response.body().getNextPageToken() != null) {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNextResultsRadiusMethod(location, response.body().getNextPageToken());
                        }
                    }, HANDLING_TIME);

                }

            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);
            }
        });
        return nearByRestaurantList;
    }

    public void getNextResultsRadiusMethod(String location, String pageToken) {
        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);
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
                nearByRestaurantList.getValue().addAll(response.body().getResults());
                System.out.println(nearByRestaurantList.getValue().size());

                if (response.body().getNextPageToken() != null) {
                    Call<PlaceNearBy> call2 = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, response.body().getNextPageToken());

                    call2.enqueue(new Callback<PlaceNearBy>() {
                        @Override
                        public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                            if (!response.isSuccessful()) {
                                Log.e(TAG, "onResponse: failed " + response.message(), null);
                                return;
                            }

                            Log.d("onResponse: Retrofit ", response.raw().request().url().toString());
                            nearByRestaurantList.getValue().addAll(response.body().getResults());
                            System.out.println(nearByRestaurantList.getValue().size());
                        }

                        @Override
                        public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage(), t);
                        }
                    });
                    nearByRestaurantList.getValue().addAll(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), null);
            }
        });
    }

    /**
     * Getting NearBySearch results as a list using the rankBy parameter
     *
     * @return
     */

/*    public List<ResultsItem> getNearBySearchListRankByMethod(String location) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, rankBy, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);

        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }
                Log.d(" Retrofit Rank by ", response.raw().request().url().toString());
                rankByList.addAll(response.body().getResults());

                if (response.body().getNextPageToken() != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNextResultsRankByMethod(location, response.body().getNextPageToken());
                        }
                    }, HANDLING_TIME);
                }

            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), null);
            }
        });
        return rankByList.getValue();
    }

    private void getNextResultsRankByMethod(String location, String pageToken) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, rankBy, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, pageToken);

        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "onResponse: failed " + response.message(), null);
                    return;
                }

                Log.d("onResponse: Retrofit ", response.raw().request().url().toString());
                rankByList.addAll(response.body().getResults());
                System.out.println(rankByList.size());

                if (response.body().getNextPageToken() != null) {
                    Call<PlaceNearBy> call2 = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, rankBy, MAXPRICE, RESTAURANT, BuildConfig.PLACES_API_KEY, response.body().getNextPageToken());

                    call2.enqueue(new Callback<PlaceNearBy>() {
                        @Override
                        public void onResponse(Call<PlaceNearBy> call, Response<PlaceNearBy> response) {
                            if (!response.isSuccessful()) {
                                Log.e(TAG, "onResponse: failed " + response.message(), null);
                                return;
                            }

                            Log.d("onResponse: Retrofit ", response.raw().request().url().toString());
                            rankByList.addAll(response.body().getResults());
                            System.out.println(rankByList.size());
                        }

                        @Override
                        public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage(), null);
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<PlaceNearBy> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), null);
            }
        });
    }*/

    /**
     * Getting Details results for one restaurant
     * @return
     */

/*    @Override
    public void getDetailsListAsLiveData() {
        RetrofitDetailsAPI retrofitDetailsAPI = retrofit.create(RetrofitDetailsAPI.class);
    }

    @Override
    public void getAutocompleteListAsLiveData() {
        RetrofitAutocompleteAPI retrofitAutocompleteAPI = retrofit.create(RetrofitAutocompleteAPI.class);
    }*/


}
