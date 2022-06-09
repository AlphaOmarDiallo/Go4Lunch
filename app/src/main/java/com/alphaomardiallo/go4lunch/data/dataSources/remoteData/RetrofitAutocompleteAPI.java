package com.alphaomardiallo.go4lunch.data.dataSources.remoteData;

import com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo.PlaceAutoComplete;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAutocompleteAPI {

    @GET("autocomplete/json?")
    Call<PlaceAutoComplete> getPlaceAutocomplete(
            @Query("input") String input,
            @Query("offset") int offset,
            @Query("origin") String origin,
            @Query("location") String location,
            @Query("types") String types,
            @Query("radius") int radius,
            @Query("key") String key
    );
}
