package com.alphaomardiallo.go4lunch.data.dataSources.remoteData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo.PlaceDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitDetailsAPI {

    @GET("details/json?")
    Call<PlaceDetails> getPlaceDetails(
            @Query("fields") String fields,
            @Query("place_id") String placeId,
            @Query("key") String key
    );
}
