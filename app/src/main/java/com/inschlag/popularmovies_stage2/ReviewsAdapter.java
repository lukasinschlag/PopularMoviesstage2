package com.inschlag.popularmovies_stage2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inschlag.popularmovies_stage2.data.model.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviews = new ArrayList<>();

    public ReviewsAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView author, content;

        ReviewViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.review_author);
            content = itemView.findViewById(R.id.review_content);
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View reviewView = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        final Review review = reviews.get(position);

        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
