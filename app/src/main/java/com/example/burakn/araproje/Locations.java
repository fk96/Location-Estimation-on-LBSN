package com.example.burakn.araproje;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Locations implements Serializable{
    private int id, checkins_count, categories;
    private String created_at;
    private double lng, lat, oran;

    public Locations(int id, int checkins_count, int categories, String created_at, double lng, double lat) {
        this.id = id;
        this.checkins_count = checkins_count;
        this.categories = categories;
        this.created_at = created_at;
        this.lng = lng;
        this.lat = lat;
        this.oran = 0.0;
    }
    public Locations(){ this.oran = 0.0; };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckins_count() {
        return checkins_count;
    }

    public void setCheckins_count(int checkins_count) {
        this.checkins_count = checkins_count;
    }

    public int getCategories() {
        return categories;
    }

    public void setCategories(int categories) {
        this.categories = categories;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getOran() {
        return oran;
    }

    public void setOran(double oran) {
        this.oran = oran;
    }
}
