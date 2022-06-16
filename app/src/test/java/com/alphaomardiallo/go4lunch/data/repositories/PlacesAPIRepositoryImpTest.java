package com.alphaomardiallo.go4lunch.data.repositories;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.alphaomardiallo.go4lunch.BuildConfig;
import com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo.PlaceNearBy;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitAutocompleteAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitDetailsAPI;
import com.alphaomardiallo.go4lunch.data.dataSources.remoteData.RetrofitNearBySearchAPI;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public class PlacesAPIRepositoryImpTest {

    String location = "48.86501071160738,2.3467211059168793";
    int radius = 500;
    int maxPrice = 2;
    String RESTAURANT = "restaurant";
    String key = BuildConfig.PLACES_API_KEY;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RetrofitAutocompleteAPI retrofitAutocompleteAPI;

    @Mock
    private RetrofitNearBySearchAPI retrofitNearBySearchAPI;

    @Mock
    private RetrofitDetailsAPI retrofitDetailsAPI;

    @Mock
    private PlacesAPIRepositoryImp placesAPIRepositoryImp;

    @Mock
    Call<PlaceNearBy> mockedNearBySearchCall;

    @Before
    public void init(){
        retrofitNearBySearchAPI.getNearByPlacesRadiusMethod(location, radius, maxPrice, RESTAURANT, key, null);
    }

    @Test
    public void getNearBySearchRestaurantList() {

    }

    @Test
    public void fetchNearBySearchPlaces() {
    }

    @Test
    public void getNextResultsRadiusMethod() {
    }

    @Test
    public void getDetails() {
    }

    @Test
    public void getSelectedRestaurantDetails() {
    }

    @Test
    public void fetchOneNearByRestaurantDetail() {
    }

    @Test
    public void fetchAllDetails() {
    }

    @Test
    public void fetchDetailsForFavourite() {
    }

    @Test
    public void autoCompleteSearch() {
    }

    @Test
    public void observeListFavouriteRestaurant() {
    }

}