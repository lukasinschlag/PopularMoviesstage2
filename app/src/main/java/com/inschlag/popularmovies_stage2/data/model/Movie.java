package com.inschlag.popularmovies_stage2.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Movie implements Parcelable {

    private int id = 0;
    private String title;
    private String img;
    private String backdrop;
    private String plot;
    private float rating;
    private String date;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<Trailer> trailers = new ArrayList<>();

    public Movie(int id, String title, String img, String backdrop, String plot, float rating, String date) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.backdrop = backdrop;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
    }

    private Movie(Parcel source){
        this.id = source.readInt();
        this.title = source.readString();
        this.img = source.readString();
        this.backdrop = source.readString();
        this.plot = source.readString();
        this.rating = source.readFloat();
        this.date = source.readString();
        source.readTypedList(this.reviews, Review.CREATOR);
        source.readTypedList(this.trailers, Trailer.CREATOR);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public String getPlot() {
        return plot;
    }

    public float getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", backdrop='" + backdrop + '\'' +
                ", plot='" + plot + '\'' +
                ", rating=" + rating +
                ", date='" + date + '\'' +
                ", trailers='" + trailers.size() + '\'' +
                '}';
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.img);
        dest.writeString(this.backdrop);
        dest.writeString(this.plot);
        dest.writeFloat(this.rating);
        dest.writeString(this.date);
        dest.writeTypedList(this.reviews);
        dest.writeTypedList(this.trailers);
    }
}
