package com.toufic.myshows.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tvshow")
public class TvShow {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("Title")
    private String title;
    @SerializedName("Year")
    private String year;
    @SerializedName("Rated")
    private String rating;
    @SerializedName("Poster")
    private String posterPath;
    @SerializedName("Plot")
    private String plot;
    @SerializedName("totalSeasons")
    private String seasonCount;
    @SerializedName("imdbID")
    private String seriesId;
    @ColumnInfo(name = "timestamp")
    private long cacheTimeStamp = System.currentTimeMillis();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(String seasonCount) {
        this.seasonCount = seasonCount;
    }

    public long getCacheTimeStamp() {
        return cacheTimeStamp;
    }

    public void setCacheTimeStamp(long cacheTimeStamp) {
        this.cacheTimeStamp = cacheTimeStamp;
    }

    @Override
    public String toString() {
        return "TvShow{" +
                "id=" + id +
                ", Title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", rating='" + rating + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", plot='" + plot + '\'' +
                ", seasonCount='" + seasonCount + '\'' +
                ", seriesID=" + seriesId +
                ", cacheTimeStamp=" + cacheTimeStamp +
                '}';
    }
}
