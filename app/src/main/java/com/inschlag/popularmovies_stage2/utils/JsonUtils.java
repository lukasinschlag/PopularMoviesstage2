package com.inschlag.popularmovies_stage2.utils;

import android.util.Log;

import com.inschlag.popularmovies_stage2.data.Constants;
import com.inschlag.popularmovies_stage2.data.model.Movie;
import com.inschlag.popularmovies_stage2.data.model.Review;
import com.inschlag.popularmovies_stage2.data.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static ArrayList<Movie> parseMoviesJson(String json) {

        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray arr = jObject.optJSONArray(Constants.FIELD_RESULTS);

            if (arr != null) {
                //Go through the movie results
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject mObj = arr.getJSONObject(i);

                    float rating = 0;
                    String ratingS = mObj.getString(Constants.FIELD_RATING);
                    if (ratingS != null) {
                        rating = Float.parseFloat(ratingS);
                    }

                    movies.add(new Movie(
                            mObj.getInt(Constants.FIELD_ID),
                            mObj.getString(Constants.FIELD_TITLE),
                            Constants.MOVIE_POSTER_URL + mObj.getString(Constants.FIELD_POSTER),
                            Constants.MOVIE_BACKDROP_URL + mObj.getString(Constants.FIELD_BACKDROP),
                            mObj.getString(Constants.FIELD_SYNOPSIS),
                            rating,
                            mObj.getString(Constants.FIELD_RELEASED)
                    ));
                }
            }
        } catch (JSONException e) {
            // Err while parsing
            Log.d(JsonUtils.class.getCanonicalName(), e.getMessage());
        }

        return movies;
    }

    public static ArrayList<Review> parseReviewJson(String json) {
        ArrayList<Review> reviews = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray arr = jObject.optJSONArray(Constants.FIELD_RESULTS);

            if (arr != null) {
                //Go through the review results
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject mObj = arr.getJSONObject(i);

                    reviews.add(new Review(
                            mObj.getString(Constants.FIELD_ID),
                            mObj.getString(Constants.FIELD_AUTHOR),
                            mObj.getString(Constants.FIELD_CONTENT)
                    ));
                }
            }
        } catch (JSONException e) {
            // Err while parsing
            Log.d(JsonUtils.class.getCanonicalName(), e.getMessage());
        }
        return reviews;
    }

    public static ArrayList<Trailer> parseTrailerJson(String json) {
        ArrayList<Trailer> trailers = new ArrayList<>();

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray arr = jObject.optJSONArray(Constants.FIELD_YOUTUBE);

            if (arr != null) {
                //Go through the review results
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject mObj = arr.getJSONObject(i);

                    trailers.add(new Trailer(
                            mObj.getString(Constants.FIELD_SOURCE),
                            mObj.getString(Constants.FIELD_NAME)
                    ));
                }
            }
        } catch (JSONException e) {
            // Err while parsing
            Log.d(JsonUtils.class.getCanonicalName(), e.getMessage());
        }

        return trailers;
    }
}
