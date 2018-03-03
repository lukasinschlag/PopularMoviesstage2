package com.inschlag.popularmovies_stage2.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.inschlag.popularmovies_stage2.data.FavoriteMoviesContract.FavoriteMovie;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FavoriteMovies.db";

    private static final String SQL_CREATE = "CREATE TABLE " + FavoriteMovie.TABLE_NAME + " (" +
            FavoriteMovie.COLUMN_ID + " INTEGER PRIMARY KEY," +
            FavoriteMovie.COLUMN_TITLE + " TEXT)";

    private static final String SQL_DROP = "DROP TABLE IF EXISTS" + FavoriteMovie.TABLE_NAME;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }
}
