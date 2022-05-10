package com.alphaomardiallo.go4lunch.data.dataSources.remoteData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitNearBySearchAPI {

    @GET("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
    Call<PlaceNearBy> getNearByPlaces(@Query("key") String key,
                                      @Query("location") String location,
                                      @Query("type") String type,
                                      @Query("radius") int radius);
}
