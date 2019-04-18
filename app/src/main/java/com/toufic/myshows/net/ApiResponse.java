package com.toufic.myshows.net;

import com.toufic.myshows.db.Episode;
import com.toufic.myshows.db.TvShow;

import java.util.List;

/**
 * A class representing an api response holder which could include
 * a list of shows or a list of episodes and a status {@link ApiStatus}
 */
public class ApiResponse {
    public ApiStatus status;
    public List<TvShow> tvshows;
    public List<Episode> episodes;
}
