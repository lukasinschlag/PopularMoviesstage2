package com.inschlag.popularmovies_stage2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private int id = 0;
    private String author;
    private String content;

    public Review(int id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    private Review(Parcel source) {
        this.id = source.readInt();
        this.author = source.readString();
        this.content = source.readString();
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.author);
        dest.writeString(this.content);
    }
}
