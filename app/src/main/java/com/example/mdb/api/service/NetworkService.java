package com.example.mdb.api.service;

import android.util.Log;

import com.example.mdb.api.OMDbApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    public static final String TAG = NetworkService.class.getSimpleName();
    public static final String API_KEY = "f4c619af";
    private static final String BASE_URL = "https://www.omdbapi.com";

    private static NetworkService myInstance;
    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createLoggingInterceptor())
                .build();
    }

    public static NetworkService getInstance() {
        if (myInstance == null) {
            myInstance = new NetworkService();
            Log.d(TAG, "New network service instance  created");
        }

        return myInstance;
    }

    private OkHttpClient createLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);
        return httpClient.build();

    }

    public OMDbApi getOMDbApi() {
        return mRetrofit.create(OMDbApi.class);
    }


}
