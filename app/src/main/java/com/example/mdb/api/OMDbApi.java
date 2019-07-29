package com.example.mdb.api;

import com.example.mdb.entity.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OMDbApi {
    @GET("/")
    Call<SearchResponse> getMovieByTitle(@Query("s") String title,
                                         @Query("apikey") String api_key,
                                         @Query("page") int pageIndex);
}
