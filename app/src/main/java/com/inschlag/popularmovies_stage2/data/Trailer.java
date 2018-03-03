package com.inschlag.popularmovies_stage2.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private int id = 0;
    private String key;

    public Trailer(int id, String key) {
        this.id = id;
        this.key = key;
    }

    private Trailer(Parcel source){
        this.id = source.readInt();
        this.key = source.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
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
        dest.writeInt(this.id);
        dest.writeString(this.key);
    }
}
