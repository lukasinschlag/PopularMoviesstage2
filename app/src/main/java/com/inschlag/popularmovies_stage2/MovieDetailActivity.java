package com.inschlag.popularmovies_stage2;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inschlag.popularmovies_stage2.data.Constants;
import com.inschlag.popularmovies_stage2.data.FavoriteMoviesContract;
import com.inschlag.popularmovies_stage2.data.model.Movie;
import com.inschlag.popularmovies_stage2.data.model.Trailer;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.OnTrailerClickListener{

    private Movie mMovie;
    private boolean isFav = false;
    private ContentResolver mCR;
    private Intent shareTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar;
        if((actionBar = getSupportActionBar()) != null){
            actionBar.setBackgroundDrawable(null);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.MOVIE_KEY)){
            mMovie = savedInstanceState.getParcelable(Constants.MOVIE_KEY);
        } else {
            Bundle args = getIntent().getExtras();
            if (args != null && args.containsKey(Constants.MOVIE_KEY)) {
                mMovie = args.getParcelable(Constants.MOVIE_KEY);
            }
        }

        if(mMovie == null){
            Toast.makeText(MovieDetailActivity.this, R.string.movie_err, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find if this a favorite movie
        mCR = getContentResolver();
        @SuppressLint("DefaultLocale")
        Cursor cursor = mCR.query(Uri.parse(String.format(Constants.CONTENT_URI_FAVORITES_ID, mMovie.getId())),
                null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            isFav = true;
            cursor.close();
        }

        TextView title = findViewById(R.id.movie_title),
                releaseDate = findViewById(R.id.movie_release_date),
                rating = findViewById(R.id.movie_rating),
                plot = findViewById(R.id.movie_plot);
        ImageView img = findViewById(R.id.movie_img);
        ImageView backdrop = findViewById(R.id.movie_backdrop);

        title.setText(mMovie.getTitle());
        releaseDate.setText(mMovie.getDate());
        rating.setText(String.format(getResources().getString(R.string.movie_rating), mMovie.getRating()));
        plot.setText(mMovie.getPlot());

        Picasso.with(MovieDetailActivity.this).load(mMovie.getImg()).into(img);
        Picasso.with(MovieDetailActivity.this).load(mMovie.getBackdrop()).into(backdrop);

        RecyclerView rVTrailers = findViewById(R.id.rVTrailers);
        RecyclerView rVReviews = findViewById(R.id.rVReviews);

        ViewCompat.setNestedScrollingEnabled(rVTrailers, false);
        ViewCompat.setNestedScrollingEnabled(rVReviews, false);

        rVTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rVReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rVTrailers.setAdapter(new TrailersAdapter(this, mMovie.getTrailers()));
        rVReviews.setAdapter(new ReviewsAdapter(mMovie.getReviews()));

        if(mMovie.getReviews().size() == 0){
            findViewById(R.id.movie_reviews).setVisibility(View.GONE);
        }

        if(mMovie.getTrailers().size() > 0) {
            shareTrailer = new Intent(Intent.ACTION_SEND);
            shareTrailer.setType("text/plain");
            shareTrailer.putExtra(Intent.EXTRA_SUBJECT, String.format(getResources().getString(R.string.trailer_share_desc), mMovie.getTitle()));
            shareTrailer.putExtra(Intent.EXTRA_TEXT, Constants.REQUEST_YOUTUBE_VIDEO + mMovie.getTrailers().get(0).getKey());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.MOVIE_KEY, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_moviedetail, menu);

        if(shareTrailer == null){
            menu.findItem(R.id.menu_share).setVisible(false);
        }

        if(isFav){
            menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_favorite_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:
                item.setIcon(favoriteState() ? R.drawable.ic_favorite_24dp : R.drawable.ic_favorite_no_24dp);
                return true;
            case R.id.menu_share:
                startActivity(Intent.createChooser(shareTrailer, shareTrailer.getStringExtra(Intent.EXTRA_SUBJECT)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("DefaultLocale")
    private boolean favoriteState(){
        if(isFav){
            mCR.delete(Uri.parse(String.format(Constants.CONTENT_URI_FAVORITES_ID, mMovie.getId())), null, null);
        } else {
            ContentValues values = new ContentValues();
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_ID, mMovie.getId());
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_TITLE, mMovie.getTitle());
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_POSTER, mMovie.getImg());
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_BACKDROP, mMovie.getBackdrop());
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_SYNOPSIS, mMovie.getPlot());
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_RATING, mMovie.getRating());
            values.put(FavoriteMoviesContract.FavoriteMovie.COLUMN_DATE, mMovie.getDate());
            mCR.insert(Constants.CONTENT_URI_FAVORITES, values);
        }
        return (isFav = !isFav);
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.REQUEST_YOUTUBE_VIDEO + trailer.getKey())));
    }
}
