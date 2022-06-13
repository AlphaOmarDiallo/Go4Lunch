package com.alphaomardiallo.go4lunch.data.repositories;

import static android.content.ContentValues.TAG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PlaceAutoComplete;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PredictionsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.PlaceDetails;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.Result;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.ResultsItem;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitAutocompleteAPI;
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

    private static final String PLACES_API_KEY = BuildConfig.PLACES_API_KEY;
    private static final String RESTAURANT = "restaurant";
    private static final String RANK_BY = "distance";
    private static final String FIELDS_RESTAURANT_DETAIL_ACTIVITY = "place_id,name,geometry/location,photo,vicinity,opening_hours/open_now,rating";
    private static final String FIELDS_RESTAURANT_DETAIL_ACTIVITY_COMPLETE = "name,photo,vicinity,rating,geometry/location,international_phone_number,opening_hours/open_now,website";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final int MAXPRICE = 2;
    private static final int HANDLING_TIME = 2000;
    private static final int OFFSET = 2;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final MutableLiveData<List<ResultsItem>> restaurantListLiveData = new MutableLiveData<>();
    private final List<ResultsItem> restaurantList = new ArrayList<>();
    private final MutableLiveData<Result> restaurantDetails = new MutableLiveData<>();
    private final MutableLiveData<List<PredictionsItem>> predictionAutoComplete = new MutableLiveData<>();
    private final MutableLiveData<Result> selectedRestaurantDetails = new MutableLiveData<>();
    private List<Result> favRestaurant = new ArrayList<>();

    @Inject
    public PlacesAPIRepositoryImp() {
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
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

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, PLACES_API_KEY, null);
        call.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {

                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: no response", null);
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
                Log.e(TAG, "onFailure: API call failed", t);
            }
        });
    }

    //PageToken recall if it is found

    public void getNextResultsRadiusMethod(String location, int radius, String pageToken) {

        Call<PlaceNearBy> call = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, PLACES_API_KEY, pageToken);
        call.enqueue(new Callback<PlaceNearBy>() {

            @Override
            public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {

                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: no response", null);
                    return;
                }

                assert response.body() != null;
                populateList(response.body().getResults());

                if (response.body().getNextPageToken() != null) {

                    Call<PlaceNearBy> call2 = retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, MAXPRICE, RESTAURANT, PLACES_API_KEY, response.body().getNextPageToken());
                    call2.enqueue(new Callback<PlaceNearBy>() {

                        @Override
                        public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {
                            if (!response.isSuccessful()) {
                                Log.w(TAG, "onResponse: no response", null);
                                return;
                            }

                            assert response.body() != null;
                            populateList(response.body().getResults());
                        }

                        @Override
                        public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: API call failed", t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: API call failed", t);
            }
        });
    }

    //API called when API with Radius method does not return anything

    private void noRestaurantInRadius(String location) {
        Call<PlaceNearBy> call3 = retrofitNearBySearchAPI.getNearByPlacesRankByMethod(location, RANK_BY, MAXPRICE, RESTAURANT, PLACES_API_KEY, null);

        call3.enqueue(new Callback<PlaceNearBy>() {
            @Override
            public void onResponse(@NonNull Call<PlaceNearBy> call, @NonNull Response<PlaceNearBy> response) {

                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: no response", null);
                    return;
                }

                assert response.body() != null;
                populateList(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<PlaceNearBy> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: API call failed", t);
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
        restaurantListLiveData.setValue(restaurantList);
    }


    /**
     * Places Detail
     */

    RetrofitDetailsAPI retrofitDetailsAPI = retrofit.create(RetrofitDetailsAPI.class);

    @Override
    public LiveData<Result> getDetails() {
        return restaurantDetails;
    }

    public LiveData<Result> getSelectedRestaurantDetails() {
        return selectedRestaurantDetails;
    }

    @Override
    public void fetchOneNearByRestaurantDetail(String placeID) {
        Call<PlaceDetails> call5 = retrofitDetailsAPI.getPlaceDetails(FIELDS_RESTAURANT_DETAIL_ACTIVITY, placeID, PLACES_API_KEY);
        call5.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetails> call, @NonNull Response<PlaceDetails> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: no response", null);
                    return;
                }

                if (response.body() != null) {
                    selectedRestaurantDetails.setValue(response.body().getResult());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetails> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: API call failed", t);
            }
        });
    }

    @Override
    public void fetchAllDetails(String placeID) {

        Call<PlaceDetails> call5 = retrofitDetailsAPI.getPlaceDetails(FIELDS_RESTAURANT_DETAIL_ACTIVITY_COMPLETE, placeID, PLACES_API_KEY);
        call5.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(@NonNull Call<PlaceDetails> call, @NonNull Response<PlaceDetails> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: no response", null);
                    return;
                }

                if (response.body() != null) {
                    restaurantDetails.setValue(response.body().getResult());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceDetails> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: API call failed", t);
            }
        });

    }

    @Override
    public void fetchDetailsForFavourite(List<String> placeIDList) {

        List<Result> tempList = new ArrayList<>();

        for (String placeID : placeIDList) {

            Call<PlaceDetails> call6 = retrofitDetailsAPI.getPlaceDetails(FIELDS_RESTAURANT_DETAIL_ACTIVITY, placeID, PLACES_API_KEY);
            call6.enqueue(new Callback<PlaceDetails>() {
                @Override
                public void onResponse(@NonNull Call<PlaceDetails> call, @NonNull Response<PlaceDetails> response) {
                    if (!response.isSuccessful()) {
                        Log.w(TAG, "onResponse: no response", null);
                        return;
                    }

                    if (response.body() != null) {
                        tempList.add(response.body().getResult());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PlaceDetails> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: API call failed", t);
                }
            });
        }

        favRestaurant = tempList;
    }

    /**
     * Places AutoComplete
     */

    RetrofitAutocompleteAPI retrofitAutocompleteAPI = retrofit.create(RetrofitAutocompleteAPI.class);

    public LiveData<List<PredictionsItem>> autoCompleteSearch(String Query, String location, int radius) {

        Call<PlaceAutoComplete> call7 = retrofitAutocompleteAPI.getPlaceAutocomplete(Query, OFFSET, location, location, RESTAURANT, radius, PLACES_API_KEY);
        call7.enqueue(new Callback<PlaceAutoComplete>() {
            @Override
            public void onResponse(@NonNull Call<PlaceAutoComplete> call, @NonNull Response<PlaceAutoComplete> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: no response", null);
                    return;
                }

                if (response.body() != null) {
                    predictionAutoComplete.setValue(response.body().getPredictions());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlaceAutoComplete> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: API call failed", t);
            }
        });

        return predictionAutoComplete;
    }

    public LiveData<List<Result>> observeListFavouriteRestaurant() {
        MutableLiveData<List<Result>> listToReturn = new MutableLiveData<>();
        listToReturn.setValue(favRestaurant);
        return listToReturn;
    }
}
