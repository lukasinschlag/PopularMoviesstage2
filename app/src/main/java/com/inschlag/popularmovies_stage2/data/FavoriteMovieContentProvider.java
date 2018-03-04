package com.inschlag.popularmovies_stage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.text.TextUtils;

import com.inschlag.popularmovies_stage2.data.FavoriteMoviesContract.FavoriteMovie;

public class FavoriteMovieContentProvider extends ContentProvider {

    private FavoriteMoviesDbHelper mDbHelper;

    // Matcher to decide wether to load whole table or just one row
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Constants.CONTENT_AUTHORITY,
                Constants.CONTENT_PATH_FAVORITES,
                Constants.CONTENT_FAVORITES);
        sUriMatcher.addURI(Constants.CONTENT_AUTHORITY,
                Constants.CONTENT_PATH_FAVORITE_ID,
                Constants.CONTENT_FAVORITES_ID);
    }

    public FavoriteMovieContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numDeletes = 0;
        switch (sUriMatcher.match(uri)){
            case Constants.CONTENT_FAVORITES:
                numDeletes = mDbHelper.getWritableDatabase().delete(FavoriteMovie.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case Constants.CONTENT_FAVORITES_ID:
                if(!TextUtils.isEmpty(selection)){
                    selection += " AND ";
                } else {
                    selection = "";
                }
                selection += FavoriteMovie.COLUMN_ID + " = " + uri.getLastPathSegment();
                numDeletes = mDbHelper.getWritableDatabase().delete(FavoriteMovie.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default: // err: Uri not matched
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        // notify about change
        getContext().getContentResolver().notifyChange(uri, null);
        return numDeletes;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case Constants.CONTENT_FAVORITES:
                return "vnd.android.cursor.dir/vnd." + Constants.CONTENT_AUTHORITY + "."
                        + FavoriteMovie.TABLE_NAME;
            case Constants.CONTENT_FAVORITES_ID:
                return "vnd.android.cursor.item/vnd." + Constants.CONTENT_AUTHORITY + "."
                        + FavoriteMovie.TABLE_NAME;
            default: // err: Uri not matched
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = mDbHelper.getWritableDatabase()
                .insert(FavoriteMovie.TABLE_NAME, "", values);

        if(rowID > 0){
            Uri result = ContentUris.withAppendedId(Constants.CONTENT_URI_FAVORITES, rowID);
            // notify about change
            getContext().getContentResolver().notifyChange(result, null);
            return result;
        }
        throw new SQLException("Failed to insert record " + uri);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)){
            case Constants.CONTENT_FAVORITES:
                return mDbHelper.getReadableDatabase().query(FavoriteMovie.TABLE_NAME,
                        projection, selection, selectionArgs,null, null, sortOrder);
            case Constants.CONTENT_FAVORITES_ID:
                if(!TextUtils.isEmpty(selection)){
                    selection += " AND ";
                } else {
                    selection = "";
                }
                selection += FavoriteMovie.COLUMN_ID + " = " + uri.getLastPathSegment();
                return mDbHelper.getReadableDatabase().query(FavoriteMovie.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
            default: // err: Uri not matched
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
