package com.example.mdb.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.mdb.ExecutorProvider;
import com.example.mdb.api.OMDbApi;
import com.example.mdb.api.service.NetworkService;
import com.example.mdb.database.AppDatabase;
import com.example.mdb.database.dao.MovieDao;
import com.example.mdb.entity.MovieInfo;
import com.example.mdb.entity.SearchResponse;

import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {
    private static final String TAG = MoviesRepository.class.getSimpleName();
    private static MoviesRepository instance;
    private final OMDbApi omDbApi;
    private final MovieDao movieDao;
    private final Executor executor;
    private int totalResults;


    private MoviesRepository(Context context) {
        movieDao = AppDatabase.getInstance(context.getApplicationContext()).movieDao();
        omDbApi = NetworkService.getInstance().getOMDbApi();
        executor = ExecutorProvider.getExecutorProvider().singleExecutor;
        totalResults = 0;
    }

    public static MoviesRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MoviesRepository(context);
            Log.d(TAG, "Creating new repository instance");
        }
        return instance;
    }

    public LiveData<List<MovieInfo>> getMoviesByName(String title, int page) {
        Log.d(TAG, "getMoviesByName " + title + page);
        refreshFromAPi(title, page);
        return movieDao.loadAllMoviesByTitle(title);
    }

    public void refreshFromAPi(String title, int page) {
        callApi(title, page);
    }


    private void callApi(String title, int page) {
        Log.d(TAG, "Refresh movies by title " + title);
        omDbApi.getMovieByTitle(title, NetworkService.API_KEY, page).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {


                if (response.body().getMovies() != null) {
                    totalResults = Integer.parseInt(response.body().getTotalResults());
                    Log.i(TAG, "Data refreshed from network ! Total Results " + totalResults);
                    List<MovieInfo> movies = response.body().getMovies();

                    executor.execute(() -> {movieDao.insertListMovies(movies);});
                } else {
                    totalResults = 0;
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public int getTotalResults() {
        return totalResults;
    }

}