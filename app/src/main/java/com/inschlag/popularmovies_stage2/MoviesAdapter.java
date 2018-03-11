package com.inschlag.popularmovies_stage2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inschlag.popularmovies_stage2.data.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private MovieListActivity movieListActivity;
    private ArrayList<Movie> movies = new ArrayList<>();

    MoviesAdapter(MovieListActivity movieListActivity) {
        this.movieListActivity = movieListActivity;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView title;

        MovieViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.movie_img);
            title = itemView.findViewById(R.id.movie_title);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Movie movie = movies.get(position);
        Picasso.with(movieListActivity).load(movie.getImg()).into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieListActivity.onMovieClick(movie);
            }
        });
        holder.title.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    ArrayList<Movie> getMovies() {
        return movies;
    }

    interface onMovieClickListener {
        void onMovieClick(Movie movie);
    }
}
