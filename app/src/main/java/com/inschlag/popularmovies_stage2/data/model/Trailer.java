package com.inschlag.popularmovies_stage2.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.inschlag.popularmovies_stage2.data.Constants;

public class Trailer implements Parcelable {

    private String source;
    private String name;

    public Trailer(String source, String name) {
        this.source = source;
        this.name = name;
    }

    private Trailer(Parcel source){
        this.source = source.readString();
        this.name = source.readString();
    }

    public String getKey() {
        return source;
    }

    public String getImg(){
        return String.format(Constants.REQUEST_YOUTUBE_PREVIEW, this.source);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>(){
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.source);
        dest.writeString(this.name);
    }
}
