package com.alphaomardiallo.go4lunch.data.dataSources.remoteData;

import androidx.annotation.Nullable;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitNearBySearchAPI {

    @GET("nearbysearch/json?")
    Call<PlaceNearBy> getNearByPlacesRadiusMethod(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("maxprice") int maxPrice,
            @Query("type") String type,
            @Query("key") String key,
            @Nullable @Query("pagetoken") String pageToken
    );

    @GET("nearbysearch/json?")
    Call<PlaceNearBy> getNearByPlacesRankByMethod(
            @Query("location") String location,
            @Query("rankby") String rankBy,
            @Query("maxprice") int maxPrice,
            @Query("type") String type,
            @Query("key") String key,
            @Nullable @Query("pagetoken") String pageToken
    );

}
