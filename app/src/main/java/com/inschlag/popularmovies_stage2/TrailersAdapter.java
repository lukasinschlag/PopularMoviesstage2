package com.inschlag.popularmovies_stage2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inschlag.popularmovies_stage2.data.model.Movie;
import com.inschlag.popularmovies_stage2.data.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    private MovieDetailActivity movieDetailActivity;
    private ArrayList<Trailer> trailers = new ArrayList<>();

    public TrailersAdapter(MovieDetailActivity movieDetailActivity, ArrayList<Trailer> trailers) {
        this.movieDetailActivity = movieDetailActivity;
        this.trailers = trailers;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        TrailerViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.trailer_img);
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View trailerView = inflater.inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        final Trailer trailer = trailers.get(position);
        Picasso.with(movieDetailActivity).load(trailer.getImg()).into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieDetailActivity.onTrailerClick(trailer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    interface OnTrailerClickListener {
        void onTrailerClick(Trailer trailer);
    }
}
