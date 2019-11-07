package com.example.burakn.araproje;

public class Tahmin {
    private int placeid, puan;
    public Tahmin(int placeid) {
        this.placeid = placeid;
        this.puan = 10;
    }
    public Tahmin(){ this.puan = 10; }
    public int getPlaceid() {
        return placeid;
    }

    public void setPlaceid(int placeid) {
        this.placeid = placeid;
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
    }
    public void puanArttir(int deger){
        puan += deger;
    }
}
