package com.bob.pstation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/11/16.
 */
public class Petrol implements Parcelable{
    private String type;
    private String price;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(price);
    }

    public static final Parcelable.Creator<Petrol> CREATOR=new Parcelable.Creator<Petrol>(){
        @Override
        public Petrol createFromParcel(Parcel source) {
            Petrol p = new Petrol();
            p.type = source.readString();
            p.price = source.readString();
            return p;

        }

        @Override
        public Petrol[] newArray(int size) {
            return null;
        }
    };
}
