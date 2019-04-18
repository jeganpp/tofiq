package com.toufic.myshows.db.model;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.toufic.myshows.db.Episode;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class EpisodeToStringTypeConverter {

    private static final Gson mGson = new Gson();

    @TypeConverter
    public static List<Episode> StringToEpisodeList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Episode>>() {
        }.getType();

        return mGson.fromJson(data, listType);
    }

    @TypeConverter
    public static String EpisodeListToEpisode(List<Episode> data) {
        return mGson.toJson(data);
    }
}
