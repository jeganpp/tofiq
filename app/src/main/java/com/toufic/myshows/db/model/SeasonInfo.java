package com.toufic.myshows.db.model;

import com.google.gson.annotations.SerializedName;

public class SeasonInfo {
    @SerializedName("imdbID")
    private String episodeId;

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    @Override
    public String toString() {
        return "SeasonInfo{" +
                "episodeId='" + episodeId + '\'' +
                '}';
    }
}
