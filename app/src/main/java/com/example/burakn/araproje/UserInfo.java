package com.example.burakn.araproje;
public class UserInfo {
    private int id, frieds_count, checkin_num, places_num;
    public UserInfo(int id, int friends_count, int checkin_num, int places_num){
        this.id = id;
        this.frieds_count = friends_count;
        this.checkin_num = checkin_num;
        this.places_num = places_num;
    }

    public UserInfo(){};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrieds_count() {
        return frieds_count;
    }

    public void setFrieds_count(int frieds_count) {
        this.frieds_count = frieds_count;
    }

    public int getCheckin_num() {
        return checkin_num;
    }

    public void setCheckin_num(int checkin_num) {
        this.checkin_num = checkin_num;
    }

    public int getPlaces_num() {
        return places_num;
    }

    public void setPlaces_num(int places_num) {
        this.places_num = places_num;
    }
}