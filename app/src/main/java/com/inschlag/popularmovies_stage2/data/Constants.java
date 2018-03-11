package com.inschlag.popularmovies_stage2.data;

import android.net.Uri;

public final class Constants {

    public static final int MOVIES_MOST_POPULAR = 0;
    public static final int MOVIES_HIGHEST_RATED = 1;
    public static final int MOVIES_FAVORITES = 2;
    public static final String MOVIE_KEY = "movie_key";
    public static final String MOVIE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String MOVIE_BACKDROP_URL = "http://image.tmdb.org/t/p/w500/";
    public static final String REQUEST_MOST_POPULAR = "http://api.themoviedb.org/3/movie/popular?api_key=";
    public static final String REQUEST_HIGHEST_RATED = "http://api.themoviedb.org/3/movie/top_rated?api_key=";
    public static final String REQUEST_REVIEWS = "http://api.themoviedb.org/3/movie/%d/reviews?api_key=%s";
    public static final String REQUEST_TRAILERS = "http://api.themoviedb.org/3/movie/%d/trailers?api_key=%s";
    public static final String REQUEST_YOUTUBE_VIDEO = "https://www.youtube.com/watch?v=";
    public static final String REQUEST_YOUTUBE_PREVIEW = "http://img.youtube.com/vi/%s/1.jpg";
    public static final String FIELD_RESULTS = "results";
    public static final String FIELD_YOUTUBE = "youtube";
    public static final String FIELD_ID = "id";
    public static final String FIELD_RATING = "vote_average";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_POSTER = "poster_path";
    public static final String FIELD_BACKDROP = "backdrop_path";
    public static final String FIELD_SYNOPSIS = "overview";
    public static final String FIELD_RELEASED = "release_date";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SOURCE = "source";

    public static final String CONTENT_AUTHORITY = "com.inschlag.popularmovies";
    public static final String CONTENT_PATH_FAVORITES = "favorites";
    public static final String CONTENT_PATH_FAVORITE_ID = "favorites/#";
    public static final String CONTENT_PATH_FAVORITE_ID_R = "favorites/%d";
    public static final Uri CONTENT_URI_FAVORITES =
            Uri.parse("content://" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_FAVORITES);
    public static final String CONTENT_URI_FAVORITES_ID =
            "content://" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_FAVORITE_ID_R;
    public static final int CONTENT_FAVORITES = 1;
    public static final int CONTENT_FAVORITES_ID = 2;
}
