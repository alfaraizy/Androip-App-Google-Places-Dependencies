package com.example.lenovo.nearbylocation;

import com.example.lenovo.nearbylocation.model.MyPlaces;
import com.example.lenovo.nearbylocation.model.Results;
import com.example.lenovo.nearbylocation.remote.IGoogleAPIService;
import com.example.lenovo.nearbylocation.remote.RetrofitClient;

import javax.xml.transform.Result;

import retrofit2.Retrofit;

/**
 * Created by LENOVO on 29/05/2018.
 */

public class Common {

    public static Results currentResult;

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService(){
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
