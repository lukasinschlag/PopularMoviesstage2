package com.inschlag.popularmovies_stage2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.inschlag.popularmovies_stage2.data.Constants;
import com.inschlag.popularmovies_stage2.data.FavoriteMoviesContract.FavoriteMovie;
import com.inschlag.popularmovies_stage2.data.model.Movie;
import com.inschlag.popularmovies_stage2.data.model.Review;
import com.inschlag.popularmovies_stage2.data.model.Trailer;
import com.inschlag.popularmovies_stage2.utils.JsonUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.onMovieClickListener {

    // https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys/34021467#34021467
    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;
    private static final String MOVIE_FILTER = "movie_filter";

    private ProgressBar mProgressBar;
    private MoviesAdapter mAdapter;
    private int mSelectedFilter = Constants.MOVIES_MOST_POPULAR;
    private SharedPreferences mSharedPrefs;
    private FavoriteMoviesObserver mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        mSharedPrefs = getPreferences(MODE_PRIVATE);
        mSelectedFilter = mSharedPrefs.getInt(MOVIE_FILTER, Constants.MOVIES_MOST_POPULAR);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        recyclerView.setLayoutManager(new GridLayoutManager(this, landscape ? 3 : 2));

        mProgressBar = findViewById(R.id.progressBar);

        mAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(mAdapter);

        Looper looper = Looper.getMainLooper();
        Handler handler = new Handler(looper);
        mObserver = new FavoriteMoviesObserver(handler);
        mObserver.deliverSelfNotifications();

        // store the movies to be reused on e.g. orientation change
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIE_KEY)) {
            mAdapter.setMovies((savedInstanceState.<Movie>getParcelableArrayList(Constants.MOVIE_KEY)));
        } else {
            loadMovies(mSelectedFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Constants.CONTENT_URI_FAVORITES,
                true, mObserver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constants.MOVIE_KEY, mAdapter.getMovies());
        outState.putInt(MOVIE_FILTER, mSelectedFilter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * Store the selection as shared preference.
         * Because with only the instance state, the user selection won't be stored when the
         * activity is paused and then resumed.
         */
        mSharedPrefs.edit().putInt(MOVIE_FILTER, mSelectedFilter).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister in onDestroy to allow for listing for changes while activity is only paused/stopped
        getContentResolver().unregisterContentObserver(mObserver);
    }

    /*
     * per Implementation Guide, regarding internet access:
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadMovies(int filter) {
        if (filter != Constants.MOVIES_FAVORITES && !isOnline()) {
            Toast.makeText(MovieListActivity.this, R.string.movie_err_inet, Toast.LENGTH_LONG).show();
            return;
        }
        LoadMovies loadMovies = new LoadMovies(MovieListActivity.this, filter);
        loadMovies.execute();
    }

    private void setMoviesForAdapter(ArrayList<Movie> movies){
        if (movies != null && movies.size() > 0) {
            mAdapter.setMovies(movies);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movielist, menu);

        int id;
        switch (mSelectedFilter) {
            case Constants.MOVIES_MOST_POPULAR:
                id = R.id.menu_sort_by_most_popular;
                break;
            case Constants.MOVIES_HIGHEST_RATED:
                id = R.id.menu_sort_by_highest_rated;
                break;
            case Constants.MOVIES_FAVORITES:
                id = R.id.menu_sort_by_favorites;
                break;
            default:
                id = R.id.menu_sort_by_most_popular;
        }
        menu.findItem(id).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_sort_by_most_popular:
                loadMovies((mSelectedFilter = Constants.MOVIES_MOST_POPULAR));
                return true;
            case R.id.menu_sort_by_highest_rated:
                loadMovies((mSelectedFilter = Constants.MOVIES_HIGHEST_RATED));
                return true;
            case R.id.menu_sort_by_favorites:
                loadMovies((mSelectedFilter = Constants.MOVIES_FAVORITES));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setProgressState(boolean active) {
        mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onMovieClick(Movie movie) {
        final Bundle movieDetailArgs = new Bundle();
        movieDetailArgs.putParcelable(Constants.MOVIE_KEY, movie);

        Intent detailActivity = new Intent(this, MovieDetailActivity.class);
        detailActivity.putExtras(movieDetailArgs);
        startActivity(detailActivity);
    }

    /**
     * Resource: https://developer.android.com/reference/android/os/AsyncTask.html
     * note: static to prevent leaks
     */
    static class LoadMovies extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private String mUrl;
        private WeakReference<MovieListActivity> activityReference;

        LoadMovies(MovieListActivity context, int rating) {
            this.activityReference = new WeakReference<>(context);
            switch (rating) {
                case Constants.MOVIES_MOST_POPULAR:
                    mUrl = Constants.REQUEST_MOST_POPULAR;
                    break;
                case Constants.MOVIES_HIGHEST_RATED:
                    mUrl = Constants.REQUEST_HIGHEST_RATED;
                    break;
                case Constants.MOVIES_FAVORITES:
                    return;
            }
            mUrl = mUrl.concat(API_KEY);
        }

        /*
         * Activate\Deactivate the ProgressBar
         */
        private void postState(boolean active) {
            MovieListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.setProgressState(active);
        }

        @Override
        protected void onPreExecute() {
            postState(true);
        }

        /*
         * Resources:
         * https://developer.android.com/reference/java/net/HttpURLConnection.html
         * https://docs.oracle.com/javase/tutorial/essential/regex/bounds.html
         */
        @Override
        protected ArrayList<Movie> doInBackground(Void... voids) {
            ArrayList<Movie> movies = null;
            InputStream in = null;
            try {
                if (TextUtils.isEmpty(mUrl)) {
                    movies = loadFavoriteMovies();
                } else {
                    URL url = new URL(mUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    in = new BufferedInputStream(urlConnection.getInputStream());

                    Scanner scanner = new Scanner(in).useDelimiter("\\A");

                    if (scanner.hasNext()) {
                        movies = JsonUtils.parseMoviesJson(scanner.next());
                    }
                }
                if (movies != null && isOnline()) {
                    // Load the trailers & reviews
                    for (Movie m : movies) {
                        m.setTrailers(getTrailers(m.getId()));
                        m.setReviews(getReviews(m.getId()));
                    }
                }
            } catch (MalformedURLException e) {
                Log.d(LoadMovies.class.getCanonicalName(), "Error while parsing url: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(LoadMovies.class.getCanonicalName(), "Error while opening connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.d(LoadMovies.class.getCanonicalName(), "Error while closing InputStream: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            return movies;
        }

        private boolean isOnline(){
            ConnectivityManager cm =
                    (ConnectivityManager) activityReference.get().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        /*
         * Go through the locally stored favorite movies, provided by the content provider
         */
        private ArrayList<Movie> loadFavoriteMovies() {
            ArrayList<Movie> favMovies = new ArrayList<>();
            Cursor c = activityReference.get().getContentResolver().query(Constants.CONTENT_URI_FAVORITES, null, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        favMovies.add(new Movie(c.getInt(c.getColumnIndex(FavoriteMovie.COLUMN_ID)),
                                c.getString(c.getColumnIndex(FavoriteMovie.COLUMN_TITLE)),
                                c.getString(c.getColumnIndex(FavoriteMovie.COLUMN_POSTER)),
                                c.getString(c.getColumnIndex(FavoriteMovie.COLUMN_BACKDROP)),
                                c.getString(c.getColumnIndex(FavoriteMovie.COLUMN_SYNOPSIS)),
                                c.getFloat(c.getColumnIndex(FavoriteMovie.COLUMN_RATING)),
                                c.getString(c.getColumnIndex(FavoriteMovie.COLUMN_DATE))));
                    } while (c.moveToNext());
                }
                c.close();
            }
            return favMovies;
        }

        private ArrayList<Trailer> getTrailers(int id) throws IOException {
            ArrayList<Trailer> trailers = null;
            InputStream in;
            @SuppressLint("DefaultLocale")
            URL trailerUrl = new URL(String.format(Constants.REQUEST_TRAILERS, id, API_KEY));
            HttpURLConnection urlConnection = (HttpURLConnection) trailerUrl.openConnection();

            in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(in).useDelimiter("\\A");
            if (scanner.hasNext()) {
                trailers = JsonUtils.parseTrailerJson(scanner.next());
            }
            return trailers;
        }

        private ArrayList<Review> getReviews(int id) throws IOException {
            ArrayList<Review> reviews = null;
            InputStream in;
            @SuppressLint("DefaultLocale")
            URL reviewUrl = new URL(String.format(Constants.REQUEST_REVIEWS, id, API_KEY));
            HttpURLConnection urlConnection = (HttpURLConnection) reviewUrl.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(in).useDelimiter("\\A");
            if (scanner.hasNext()) {
                reviews = JsonUtils.parseReviewJson(scanner.next());
            }
            return reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            postState(false);
            MovieListActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.setMoviesForAdapter(movies);
        }
    }
    
    /*
     * Used to monitor changes to the favorite movies stored
     */
    class FavoriteMoviesObserver extends ContentObserver {

        /**
         * Creates a content mObserver.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        FavoriteMoviesObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            // check if currently the favorite movies are display, if reload them
            if(mSelectedFilter == Constants.MOVIES_FAVORITES) {
                loadMovies(mSelectedFilter);
            }
        }
    }
}
