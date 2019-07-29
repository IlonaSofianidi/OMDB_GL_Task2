package com.example.mdb.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mdb.entity.MovieInfo;
import com.example.mdb.repositories.MoviesRepository;

import java.util.List;

public class MoviesListViewModel extends AndroidViewModel {


    private MoviesRepository repository;

    List<MovieInfo> movies;


    public MoviesListViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance(application);
    }

    public LiveData<List<MovieInfo>> getMoviesByTitle(String title, int page) {
        return repository.getMoviesByName(title, page);
    }

    public List<MovieInfo> getMovies() {
        return movies;
    }

    public int getTotalResults() {
        return repository.getTotalResults();
    }

    public void refreshMovies(String title, int page) {
        repository.refreshFromAPi(title, page);
    }

}
