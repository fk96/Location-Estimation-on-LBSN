package com.example.burakn.araproje;

public class Kategori {
    int id;
    int checkin_miktari;
    double oran;
    String isim;
    public Kategori(int id, String isim){
        id = this.id;
        isim = this.isim;
        checkin_miktari = 0;
        oran = 0;
    }
    public Kategori(){
        checkin_miktari = 0;
        oran = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheckin_miktari() {
        return checkin_miktari;
    }

    public void setCheckin_miktari(int checkin_miktari) {
        this.checkin_miktari = checkin_miktari;
    }

    public double getOran() {
        return oran;
    }

    public void setOran(double oran) {
        this.oran = oran;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }
    public void checkin_miktar_arttir(){
        checkin_miktari++;
    }
}
