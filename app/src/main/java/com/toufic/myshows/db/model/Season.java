package com.toufic.myshows.db.model;

import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Season {
    @SerializedName("Title")
    private String title;
    @SerializedName("Season")
    private String season;
    @SerializedName("Episodes")
    @TypeConverters(EpisodeToStringTypeConverter.class)
    private List<SeasonInfo> episodesId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public List<SeasonInfo> getEpisodesId() {
        return episodesId;
    }

    public void setEpisodesId(List<SeasonInfo> episodesId) {
        this.episodesId = episodesId;
    }

    @Override
    public String toString() {
        return "Season{" +
                "title='" + title + '\'' +
                ", season='" + season + '\'' +
                ", episodesId=" + episodesId +
                '}';
    }
}
