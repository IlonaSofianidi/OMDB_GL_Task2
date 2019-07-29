package com.example.mdb.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mdb.entity.MovieInfo;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("Select * FROM movies")
    List<MovieInfo> loadAllMovies();

    @Query("SELECT * FROM movies WHERE movie_name LIKE '%' || :title || '%' ")
    LiveData<List<MovieInfo>> loadAllMoviesByTitle(String title);

    @Query("SELECT * FROM movies WHERE movie_name LIKE '%' || :title || '%' ")
    List<MovieInfo> hasUsersByTitle(String title);

    @Query("SELECT * FROM movies WHERE movie_name=:title")
    MovieInfo getMovie(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieInfo movieInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListMovies(List<MovieInfo> movieInfos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieInfo movieInfo);

    @Delete
    void deleteMovie(MovieInfo movieInfo);

    @Query("DELETE FROM movies")
    void deleteAll();
}
