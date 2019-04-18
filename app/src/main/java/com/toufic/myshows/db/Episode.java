package com.toufic.myshows.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "episode")
public class Episode {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("imdbID")
    private String episodeId;
    @SerializedName("Title")
    private String title;
    @SerializedName("Director")
    private String director;
    @SerializedName("Writer")
    private String writer;
    @SerializedName("Plot")
    private String plot;
    @SerializedName("Poster")
    private String posterPath;
    @SerializedName("imdbRating")
    private String rating;
    @SerializedName("Episode")
    private String episodeNumber;
    @SerializedName("seriesID")
    private String seriesID;
    @SerializedName("Season")
    private String seasonNumber;
    @ColumnInfo(name = "timestamp")
    private long cacheTimeStamp = System.currentTimeMillis();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(String episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public long getCacheTimeStamp() {
        return cacheTimeStamp;
    }

    public void setCacheTimeStamp(long cacheTimeStamp) {
        this.cacheTimeStamp = cacheTimeStamp;
    }

    public String getSeriesID() {
        return seriesID;
    }

    public void setSeriesID(String seriesID) {
        this.seriesID = seriesID;
    }

    public String getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(String seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "id=" + id +
                ", episodeId='" + episodeId + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", writer='" + writer + '\'' +
                ", plot='" + plot + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", rating='" + rating + '\'' +
                ", episodeNumber='" + episodeId + '\'' +
                ", seriesId='" + seriesID + '\'' +
                ", seasonNumber='" + seasonNumber + '\'' +
                ", cachedTimeStamp='" + cacheTimeStamp + '\'' +
                '}';
    }
}
