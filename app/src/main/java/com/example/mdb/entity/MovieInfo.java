package com.example.mdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "movies")
public class MovieInfo {
    @PrimaryKey
    @SerializedName("imdbID")
    @NonNull
    @Expose
    private String id;

    @ColumnInfo(name = "movie_name")
    @SerializedName("Title")
    @Expose
    private String movieName;

    @ColumnInfo(name = "release")
    @SerializedName("Year")
    @Expose
    private String release;

    @ColumnInfo(name = "poster")
    @SerializedName("Poster")
    @Expose
    private String posterPath;

    @Ignore
    public MovieInfo(String movieName, String release, String posterPath) {
        this.movieName = movieName;
        this.release = release;
        this.posterPath = posterPath;
    }

    public MovieInfo(@NonNull String id, String movieName, String release, String posterPath) {
        this.id = id;
        this.movieName = movieName;
        this.release = release;
        this.posterPath = posterPath;
    }

    @Ignore
    public MovieInfo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "id=" + id +
                ", movieName='" + movieName + '\'' +
                ", release='" + release + '\'' +
                ", posterPath='" + posterPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieInfo movieInfo = (MovieInfo) o;
        return Objects.equals(id, movieInfo.id) &&
                Objects.equals(movieName, movieInfo.movieName) &&
                Objects.equals(release, movieInfo.release) &&
                Objects.equals(posterPath, movieInfo.posterPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, movieName);
    }
}
