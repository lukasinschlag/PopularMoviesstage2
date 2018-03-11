package com.inschlag.popularmovies_stage2.data;

import android.provider.BaseColumns;

public final class FavoriteMoviesContract {

    private FavoriteMoviesContract() {}

    public static class FavoriteMovie implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_DATE = "releasedate";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKDROP = "backdrop";
    }
}
