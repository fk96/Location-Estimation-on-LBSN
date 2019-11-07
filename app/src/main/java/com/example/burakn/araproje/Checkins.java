package com.example.burakn.araproje;

public class Checkins {
    private int userid, placeid;
    private String datetime;
    public Checkins(int userid, int placeid, String datetime) {
        this.userid = userid;
        this.placeid = placeid;
        this.datetime = datetime;
    }
    public Checkins(){ }
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getPlaceid() {
        return placeid;
    }

    public void setPlaceid(int placeid) {
        this.placeid = placeid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

}
