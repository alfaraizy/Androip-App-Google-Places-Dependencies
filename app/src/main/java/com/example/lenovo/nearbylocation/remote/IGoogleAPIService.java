package com.example.lenovo.nearbylocation.remote;

import com.example.lenovo.nearbylocation.model.MyPlaces;
import com.example.lenovo.nearbylocation.model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by LENOVO on 29/05/2018.
 */

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);
}
