package com.testask.letsfly.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbudyak on 21.03.17.
 */

public class City implements Parcelable {
    private String fullName;
    private Location location;
    private List<String> iata;
    private String city;
    private int id;

    public City(Location location, List<String> iata) {
        this.location = location;
        this.iata = iata;
    }

    private City(Parcel in) {
        readFromParcel(in);
    }

    public String getFullName() {
        return fullName;
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getIata() {
        return iata;
    }

    public String getCity() {
        return city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(iata);
        dest.writeParcelable(location, flags);
        dest.writeString(fullName);
        dest.writeString(city);
        dest.writeInt(id);
    }

    private void readFromParcel(Parcel in) {
        iata = in.createStringArrayList();
        location = in.readParcelable(Location.class.getClassLoader());
        fullName = in.readString();
        city = in.readString();
        id = in.readInt();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

}
