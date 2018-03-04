package com.inschlag.popularmovies_stage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inschlag.popularmovies_stage2.data.Constants;
import com.inschlag.popularmovies_stage2.data.model.Movie;
import com.inschlag.popularmovies_stage2.data.model.Trailer;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.OnTrailerClickListener{

    private Movie mMovie;

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

        Log.d("MDA", "trailers " + mMovie.getTrailers().size());

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

        rVTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rVReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rVTrailers.setAdapter(new TrailersAdapter(this, mMovie.getTrailers()));
        rVReviews.setAdapter(new ReviewsAdapter(mMovie.getReviews()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.MOVIE_KEY, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTrailerClick(Trailer trailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.REQUEST_YOUTUBE_VIDEO + trailer.getKey())));
    }
}
