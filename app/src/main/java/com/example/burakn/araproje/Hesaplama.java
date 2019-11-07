package com.example.burakn.araproje;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.burakn.araproje.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

public class Hesaplama extends AppCompatActivity {
    int i = 0;
    int userid;
    TextView bilgi;
    TextView yuzde;
    ArrayList<Checkins> gecmis_checkin = new ArrayList<Checkins>();
    ArrayList<Checkins> gelecek_checkin = new ArrayList<Checkins>();
    ArrayList<Locations> gecmis_checkin_koordinat = new ArrayList<Locations>();
    ArrayList<Locations> gelecek_checkin_koordinat = new ArrayList<Locations>();
    HashMap<Integer, Tahmin> tahminler = new HashMap<Integer, Tahmin>();
    ArrayList<Checkins> tahminler_checkin = new ArrayList<Checkins>();
    ArrayList<Locations> tahminler_checkin_koordinat = new ArrayList<Locations>();
    ArrayList<Locations> tahminler_checkin_koordinat2 = new ArrayList<Locations>();
    ArrayList<Locations> dogru_checkin_koordinat = new ArrayList<Locations>();
    ArrayList<UserInfo> arkadaslar = new ArrayList<UserInfo>();
    ArrayList<Kategori> kategoriler = new ArrayList<Kategori>();
    ArrayList<Integer> siralama = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hesaplama);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        userid = extras.getInt( "USERID" );
        bilgi = (TextView)findViewById(R.id.bilgi);
        yuzde = (TextView)findViewById(R.id.yuzde);
        ArrayList<Checkins> array = new ArrayList<Checkins>();
        Veritabani veritabani = null;
        SQLiteDatabase db;
        Cursor satirlar;
        try{
            // Veritabani baglantisi
            veritabani = new Veritabani(getApplicationContext());
            veritabani.createDatabase();
            // Gecmis checkinleri aliniyor.
            array = veritabani.gecmis_checkin_bul( userid );
            // Checkinleri gecmis ve gelecek diye ikiye ayriliyor.
            checkin_ayir( array );
            // Kullanicinin gecmis ve gelecek checkinlerin koordinatlari belirleniyor.
            gecmis_checkin_koordinat = veritabani.koordinatlari_belirle( gecmis_checkin );
            gelecek_checkin_koordinat = veritabani.koordinatlari_belirle ( gelecek_checkin );
            // tahminleri kayıt altina alacak hashmap temizleniyor.
            tahminler.clear();
            // Kullanicilarin gecmis konumlari incelenerek etrafindaki mekanlar tahmin listesine ekleniyor.
            tahminler = veritabani.gecmis_konumlardan_tahmin(tahminler, gecmis_checkin_koordinat);
            gecmis_konum_bonus(veritabani, tahminler);
            /*
            1. Arkadaslar belirleniyor.
            2. Arkadaslarin gittigi yerler oneriliyor.
             */
            arkadaslar = veritabani.arkadaslari_belirle( userid );
            arkadas_konumlari_incele( veritabani, arkadaslar );
            // Benzer kullanicilara gore bonuslar.
            tahminler = veritabani.benzer_kullanicilari_incele(tahminler, gecmis_checkin, userid);
            /*
            1. Veritabanindaki kategori listesi aliniyor.
            2. Checkinlerin kategoriye gore dagilimi belirleniyor.
             */
            kategoriler = veritabani.kategorileri_getir();
            kategori_analiz(kategoriler, gecmis_checkin_koordinat);
            /*
            Kullanicinin checkinlerinin %25'dan fazlasi olan kategorilere ait populer
            mekanlar listeye ekleniyor.
             */
            populer_mekanlar_incele(veritabani, kategoriler);
            /*
            Yapilan tahminlerin puani kullanicinin daha once ziyaret ettigi kategorilerin orani kadar arttiriliyor.
             */
            kategori_bonusu( kategoriler, tahminler, veritabani);

            /**/
            ArrayList<Tahmin> tahminArrayList = hashmap_to_arraylist( tahminler );
            ArrayList<Tahmin> tahminArrayList2 = new ArrayList<Tahmin>();
            Collections.sort(tahminArrayList, new Sortbyroll().reversed() );
            int sayac = 0;
            int gecmis = 0;
            int i = 0;
            while(sayac<100 && i < tahminArrayList.size() ){
                if( tahminArrayList.get(i).getPuan() != gecmis ){
                    sayac++;
                }
                gecmis = tahminArrayList.get(i).getPuan();
                tahminArrayList2.add( tahminArrayList.get(i));
                siralama.add(sayac);
                i++;
            }
            tahminler_checkin.clear();
            tahminler_checkin_koordinat = veritabani.tahmin_koordinatlari_belirle( tahminArrayList2 );
        }catch(Exception e){
            Log.e("DB_LOG",e.getMessage());
        } finally {
            veritabani.close();
        }
        dogru_checkin_koordinat.clear();
        int sayac = 0;
        double genel_ort=0;
        int size = gelecek_checkin.size();
        for(int i=0;i<tahminler_checkin_koordinat.size();i++){
            for(int j=0;j<gelecek_checkin_koordinat.size();j++){
                if( tahminler_checkin_koordinat.get(i).getLat() ==  gelecek_checkin_koordinat.get(j).getLat() && tahminler_checkin_koordinat.get(i).getLng() == gelecek_checkin_koordinat.get(j).getLng() ){
                    dogru_checkin_koordinat.add( tahminler_checkin_koordinat.get(i) );
                    dogru_checkin_koordinat.get(sayac).setOran( 100.0 - siralama.get(i) );
                    sayac++;
                    genel_ort += (100 - siralama.get(i));
                    Log.i("DB_LOG ORAN", Integer.toString(j) + " > %" + Integer.toString( 100 - siralama.get(i) ));
                }

            }
        }
        Log.i("DB_LOG SONUCLAR", Integer.toString(size) + " - " + Integer.toString(sayac));
        Log.i("DB_LOG GENEL ORT", "%"+Double.toString(genel_ort / gelecek_checkin_koordinat.size()));
        double ort = genel_ort / gelecek_checkin_koordinat.size();
        Log.i("DB_LOG", "BITTI");
        // haritaya ilgili verilerin gonderilmesi...
        for(i=0;i<50;i++){
            tahminler_checkin_koordinat2.add( tahminler_checkin_koordinat.get(i) );
        }
        Log.i("DB_LOG KOR", Integer.toString(gelecek_checkin_koordinat.size()) +" "+ Integer.toString(dogru_checkin_koordinat.size()));
        Intent harita = new Intent (Hesaplama.this, Harita.class);
        harita.putExtra("gecmis_koordinat", gecmis_checkin_koordinat);
        harita.putExtra("gelecek_koordinat", gelecek_checkin_koordinat);
        harita.putExtra("tahminler_koordinat", tahminler_checkin_koordinat2);
        harita.putExtra("dogru_koordinat", dogru_checkin_koordinat);
        startActivity(harita);
    }
    /*
    Kullanicilarin sik gittigi kategorilere ait oneriler yapiliyor.
     */
    public void populer_mekanlar_incele(Veritabani veritabani, ArrayList<Kategori> kategoriler){
        int i, j, k;
        for(i=0; i<kategoriler.size(); i++){
            if( kategoriler.get(i).getOran() >= 25 ){

                ArrayList<Tahmin> populer_mekanlar = veritabani.kategori_populer_mekanlar_getir( kategoriler.get(i).getId() );
                for( j=0; j<populer_mekanlar.size(); j++ ){
                    Integer placeid = populer_mekanlar.get(j).getPlaceid() ;
                    Tahmin tahmin = populer_mekanlar.get(j);
                    if( tahminler.containsKey(placeid) == false ){
                        tahminler.put(placeid, tahmin);
                    } else {
                        Tahmin tahmin_temp = tahminler.get( placeid );
                        tahmin_temp.puanArttir( tahmin.getPuan() );
                        tahminler.replace( placeid, tahmin_temp );
                    }
                }
                populer_mekanlar.clear();
            }
        }
    }
    /*
    Kullanicinin arkadaslarinin konumlarini tavsiye listesine ekliyor...
     */
    public void arkadas_konumlari_incele( Veritabani veritabani, ArrayList<UserInfo> arkadaslar ){
        int i, j, k;
        for(i=0; i<arkadaslar.size(); i++){
            ArrayList<Checkins> arkadas_checkins = new ArrayList<Checkins>();
            arkadas_checkins = veritabani.gecmis_checkin_bul( arkadaslar.get(i).getId() );
            for( j=0; j<arkadas_checkins.size(); j++ ){
                Integer placeid = arkadas_checkins.get(j).getPlaceid() ;
                Tahmin tahmin = new Tahmin(placeid);
                if( tahminler.containsKey(placeid) == false ){
                    tahmin.puanArttir(10);
                    tahminler.put(placeid, tahmin);
                } else {
                    tahmin = tahminler.get( placeid );
                    tahmin.puanArttir( 20 );
                    tahminler.replace( placeid, tahmin );
                }
            }
            arkadas_checkins.clear();
        }
    }
    /*
    Kullanicinin checkinlerini train ve test verilerine ayiriyor...
     */
    public void checkin_ayir( ArrayList<Checkins> checkins ){
        int checkin_sayisi = checkins.size();
        int gecmis_checkin_sayisi = ( checkin_sayisi * 90 ) / 100;//verilerin %90 'ı train için ayrılıyor
        gecmis_checkin.clear();
        gelecek_checkin.clear();
        i=0;
        while (i<gecmis_checkin_sayisi){
            gecmis_checkin.add(checkins.get(i));
            i++;
        }
        i=gecmis_checkin_sayisi;
        while (i<checkin_sayisi){
            gelecek_checkin.add( checkins.get(i) );
            i++;
        }
    }

    /*
    Hashmap arraylisye donusuyor.
     */
    public ArrayList<Tahmin> hashmap_to_arraylist(HashMap<Integer, Tahmin> tahminler){
        ArrayList<Tahmin> array = new ArrayList<Tahmin>();

        Set<Integer> anahtarlar = tahminler.keySet();
        for (Integer anahtar : anahtarlar) {
            Tahmin tahmin = new Tahmin();
            tahmin.setPlaceid( tahminler.get(anahtar).getPlaceid() );
            tahmin.setPuan( tahminler.get(anahtar).getPuan() );
            array.add(tahmin);
        }
        return array;
    }
    /*
    Kullanicinin hangi kategorilerde ne kadar mekana gittiginin oranini belirliyor.
     */
    public void kategori_analiz(ArrayList<Kategori> kategoriler, ArrayList<Locations> gecmis_checkins_koordinat){
        // Gecmis checkinlere gore kategori analizi yapiliyor...
        for( int i =0; i < gecmis_checkins_koordinat.size(); i++ ){
            int id = gecmis_checkins_koordinat.get(i).getCategories();

            for(int j=0; j<kategoriler.size() ; j++ ){
                if( kategoriler.get(j).getId() == id ){
                    kategoriler.get(j).checkin_miktar_arttir();
                }
            }

        }
        // oran hesabi
        int toplam_miktar = gecmis_checkins_koordinat.size();
        for(int i=0; i<kategoriler.size(); i++){
            kategoriler.get(i).setOran( 100.0*kategoriler.get(i).getCheckin_miktari() / toplam_miktar );
        }
    }
    /*
    Bir onceki methodda belirlenen oranlar dogrultusunda, tahmin listesindeki mekanlara
    ilgili kategori bonusunu uyguluyor.
     */
    public void kategori_bonusu(ArrayList<Kategori> kategoriler, HashMap<Integer, Tahmin> tahminler, Veritabani veritabani){
        Set<Integer> anahtarlar = tahminler.keySet();
        for (Integer anahtar : anahtarlar) {
            Tahmin tahmin = tahminler.get(anahtar);
            int kategoriID = veritabani.kategori_id_bul( tahmin.getPlaceid() );
            for(int j = 0; j<kategoriler.size(); j++){
                if (kategoriler.get(j).getId() == kategoriID ){
                    tahmin.puanArttir( (int)(tahmin.getPuan()*kategoriler.get(j).getOran()/100 ) );
                }
            }
            //tahminler.replace( placeid, tahmin_temp );
        }
    }
    /*
    Kullanicinin gecmiste gittigi mekanlar bir tahmin olarak tahmin listesine ekleniyor.
     */
    public void gecmis_konum_bonus(Veritabani veritabani, HashMap<Integer, Tahmin> tahminler){
        ArrayList<Checkins> gecmis = veritabani.gecmis_checkin_bul(userid);
        ArrayList<Integer> tekrar = new ArrayList<Integer>();
        for(int i = 0; i<gecmis.size(); i++){
            int konum_id = gecmis.get(i).getPlaceid();
            if( tekrar.contains(konum_id) == false ) {
                Tahmin tahmin = new Tahmin();
                tahmin.setPuan(50);
                tahmin.setPlaceid( gecmis.get(i).getPlaceid() );
                if( tahminler.containsKey(konum_id) == true ) {
                    Tahmin tahmin_temp = tahminler.get( tahmin.getPlaceid() );
                    tahmin_temp.puanArttir( 40 );
                    tahminler.replace( tahmin.getPlaceid(), tahmin_temp );
                } else {
                    tahminler.put(tahmin.getPlaceid(), tahmin);
                }
                tekrar.add(tahmin.getPlaceid());
            }

        }
    }
    // Dizi siralama islemi yapiliyor.
    class Sortbyroll implements Comparator<Tahmin>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Tahmin a, Tahmin b)
        {
            return a.getPuan() - b.getPuan();
        }
    }
}
