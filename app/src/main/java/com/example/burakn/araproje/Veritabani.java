package com.example.burakn.araproje;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Veritabani extends SQLiteOpenHelper {
    private Context myContext;
    private static String DB_NAME = "AraProje.db";
    private static String DB_PATH = "";
    private static int DATABASE_VERSION = 1;
    public SQLiteDatabase myDatabase;
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i > i1)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            deleteDatabase();
        }
    }
    public Veritabani(Context context) throws IOException {
        super(context,DB_NAME,null,DATABASE_VERSION);
        this.myContext = context;
        boolean dbExists = checkDatabase();
        DB_PATH = "/data/data/"+context.getPackageName()+"/databases/";

        if(dbExists){
            Log.d("DB_LOG","Database bulundu !");
        }else{
            try{
                if(createDatabase()==true){
                    Log.d("DB_LOG","Database oluşturuldu !");
                }else{
                    Log.d("DB_LOG","Database oluşturulamadı !");
                }
            }catch (Exception e){
                Log.d("DB_LOG","Database oluşturulamadı !");
            }
        }

    }
    public boolean createDatabase() throws IOException{
        boolean dbExists = checkDatabase();
        // checkDatabase metodu ile database varmı/yokmu kontrolü yap
        if(dbExists){ //database varsa
            return true;
        }else{ // database yoksa
            this.getReadableDatabase();
            try {
                this.close();
                copyDatabase();
            }catch (IOException e){
                throw  new Error("Database kopyalanma hatası");
            }
            return false;
        }
    }

    public boolean checkDatabase(){
        boolean checkdb = false;
        try{
            String dosyaKonumu = DB_PATH + DB_NAME;
            File dbFile = new File(dosyaKonumu);
            checkdb = dbFile.exists();
        }catch (SQLiteException e){
            Log.d("DB_LOG","Database bulunamadı");
        }
        return checkdb;
    }

    private void copyDatabase() throws IOException{
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void openDatabase(){
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void deleteDatabase()
    {
        File file = new File(DB_PATH + DB_NAME);
        if(file.exists())
        {
            file.delete();
            if(file.delete()==true){
                Log.d("DB_LOG","Database file deleted on apk in database file");
            }else{
                Log.d("DB_LOG","Database file do not deleted !");
            }
        }
    }

    public synchronized void close(){
        if (myDatabase != null){
            myDatabase.close();
        }
        super.close();
    }

    /*
    Kullanicinin gecmis checkinlerini geri dondurur.
     */
    public ArrayList<Checkins> gecmis_checkin_bul(int userid){

        ArrayList<Checkins> array = new ArrayList<Checkins>();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor satirlar = db.rawQuery("SELECT * FROM newyork_checkins WHERE userid = '" + Integer.toString( userid ) + "'", null);
        Cursor satirlar = null;
        // Kategoriler cekildi...
        try {
            satirlar = db.rawQuery("SELECT * FROM newyork_checkins WHERE userid = '" + Integer.toString( userid ) + "'", null);


            if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
                do{
                    Checkins iliski = new Checkins();
                    iliski.setUserid(satirlar.getInt(satirlar.getColumnIndex("userid")));
                    iliski.setPlaceid((satirlar.getInt(satirlar.getColumnIndex("placeid"))));
                    iliski.setDatetime(satirlar.getString(satirlar.getColumnIndex("datetime")));

                    array.add(iliski);
                }while(satirlar.moveToNext());
            }
            satirlar.close();
        } finally {
            satirlar.close();
        }
        db.close();
        return array;

    }
    /*
    Parametre olarak gelen checkin e ait, Location nesnesi dondurur.
     */
    public Locations koordinat_belirle( Checkins checkins ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor satirlar = db.rawQuery("SELECT * FROM newyork_locations WHERE id = '" + Integer.toString( checkins.getPlaceid() ) + "'", null);
        satirlar.moveToFirst();

        Locations location = new Locations();

        location.setId( satirlar.getInt( satirlar.getColumnIndex("id") ) );
        location.setCreated_at( satirlar.getString( satirlar.getColumnIndex("created_at") ) );
        location.setLng( satirlar.getDouble( satirlar.getColumnIndex("lng") ) );
        location.setLat( satirlar.getDouble( satirlar.getColumnIndex("lat") ) );
        location.setCheckins_count( satirlar.getInt( satirlar.getColumnIndex("checkins_count") ) );
        location.setCategories( satirlar.getInt( satirlar.getColumnIndex("categories") ) );
        db.close();
        return location;
    }
    /*
    ID'si verilen kullanicinin arkadaslarini dizi olarak dondurur.
     */
    public ArrayList<UserInfo> arkadaslari_belirle( int userid ){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<UserInfo> arkadaslar = new ArrayList<UserInfo>();

        Cursor satirlar = db.rawQuery("SELECT * FROM newyork_friendship WHERE userid1 = '" + Integer.toString( userid ) + "'", null);


        if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
            do{
                UserInfo kisi = new UserInfo();
                kisi.setId(satirlar.getInt( satirlar.getColumnIndex("userid2") ) );
                arkadaslar.add(kisi);
            }while(satirlar.moveToNext());
        }
        db.close();
        return arkadaslar;
    }
    /*
    Kullanicinin sistemde kayitli olup olmadiginin kontrolu
     */
    public boolean kullanici_kontrol(String userid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor satirlar = null;
        try {
            satirlar = db.rawQuery("SELECT id FROM newyork_userinfo", null);
            if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
                do{
                    String id = satirlar.getString( satirlar.getColumnIndex("id") );
                    if ( id.compareTo( userid ) == 0 ){
                        return true;
                    }
                }while(satirlar.moveToNext());
            }
            satirlar.close();
        } finally {
            satirlar.close();
        }
        db.close();
        return false;
    }
    /*parametre olarak gelen placeId'nin ait olduğu kategorinin id'sini döndürür*/
    public int kategori_id_bul(int placeID){
        SQLiteDatabase db = this.getReadableDatabase();
        int kategoriID = 0;
        Cursor satirlar = null;
        satirlar = db.rawQuery("SELECT categories FROM newyork_locations WHERE id = '" + placeID + "'", null);
        if ((satirlar != null) && (satirlar.moveToFirst()) && (satirlar.getCount() > 0)) {
            kategoriID = satirlar.getInt(satirlar.getColumnIndex("categories"));
        }
        satirlar.close();
        db.close();
        return kategoriID;
    }
    /*
        Kullanicinin gecmiste gittigi her mekanin etrafindaki 1km yaricaptaki tum mekanlari onerilenler
        listesine ekler. Son mekanin ise kapsami 10km ye cikarilir.
     */
    public HashMap<Integer, Tahmin>  gecmis_konumlardan_tahmin(HashMap<Integer, Tahmin> tahminler, ArrayList<Locations> gecmis_checkin_koordinat){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor satirlar = null;
        try {
            satirlar = db.rawQuery("SELECT id, lng, lat FROM newyork_locations", null);
            for(int i=0; i<gecmis_checkin_koordinat.size(); i++){
                if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
                    do{
                        Locations mekan = new Locations();

                        mekan.setId(satirlar.getInt(satirlar.getColumnIndex("id")));
                        mekan.setLng((satirlar.getDouble(satirlar.getColumnIndex("lng"))));
                        mekan.setLat(satirlar.getDouble(satirlar.getColumnIndex("lat")));

                        Double mesafe = distance(gecmis_checkin_koordinat.get(i).getLat(), gecmis_checkin_koordinat.get(i).getLng(), mekan.getLat(), mekan.getLng() );

                        if ( mesafe < 1000f ){
                            Tahmin yeni_mekan = new Tahmin(mekan.getId());
                            yeni_mekan.puanArttir(20);
                            tahminler.put( yeni_mekan.getPlaceid(), yeni_mekan );
                        }

                    }while(satirlar.moveToNext());
                }
            }
            /*
            Kullanicinin son checkin konumunda olacagi varsayildigi icin son checkin'e ait konuma gore kapsam biraz daha genisletilir.
             */
            if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
                do{
                    Locations mekan = new Locations();

                    mekan.setId(satirlar.getInt(satirlar.getColumnIndex("id")));
                    mekan.setLng((satirlar.getDouble(satirlar.getColumnIndex("lng"))));
                    mekan.setLat(satirlar.getDouble(satirlar.getColumnIndex("lat")));

                    Double mesafe = distance(gecmis_checkin_koordinat.get(gecmis_checkin_koordinat.size()-1).getLat(), gecmis_checkin_koordinat.get(gecmis_checkin_koordinat.size()-1).getLng(), mekan.getLat(), mekan.getLng() );

                    if ( mesafe < 10000f ){
                        Tahmin yeni_mekan = new Tahmin(mekan.getId());
                        if( mesafe < 5000f ){
                            yeni_mekan.puanArttir(10);
                            if( mesafe < 2500f ){
                                yeni_mekan.puanArttir(10);
                            }
                        }
                        tahminler.put( yeni_mekan.getPlaceid(), yeni_mekan );
                    }

                }while(satirlar.moveToNext());
            }
            satirlar.close();
        } finally {
            satirlar.close();
        }
        db.close();
        return tahminler;
    }
    /*
    Kendisine gelen Checkins dizisini Locations dizisine cevirir.
     */
    public ArrayList<Locations> koordinatlari_belirle( ArrayList<Checkins> checkins ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Locations> koordinatlar = new ArrayList<Locations>();
        int i;
        Cursor satirlar;
        for(i=0; i<checkins.size(); i++){
            satirlar = null;
            try {
                satirlar = db.rawQuery("SELECT * FROM newyork_locations WHERE id = '" + Integer.toString(checkins.get(i).getPlaceid()) + "'", null);
                satirlar.moveToFirst();
                Locations location = new Locations();
                location.setId( satirlar.getInt( satirlar.getColumnIndex("id") ) );
                location.setCreated_at( satirlar.getString( satirlar.getColumnIndex("created_at") ) );
                location.setLng( satirlar.getDouble( satirlar.getColumnIndex("lng") ) );
                location.setLat( satirlar.getDouble( satirlar.getColumnIndex("lat") ) );
                location.setCheckins_count( satirlar.getInt( satirlar.getColumnIndex("checkins_count") ) );
                location.setCategories( satirlar.getInt( satirlar.getColumnIndex("categories") ) );
                koordinatlar.add(location);
                satirlar.close();
            } finally {
                satirlar.close();
            }
        }
        db.close();
        return koordinatlar;
    }
    /*
    Kendisine gelen tahmin listesinin koordinatlarini belirler.
     */
    public ArrayList<Locations> tahmin_koordinatlari_belirle( ArrayList<Tahmin> tahminler ){


        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Locations> koordinatlar = new ArrayList<Locations>();


        int i;
        for(i=0; i<tahminler.size(); i++){
            Cursor satirlar = null;
            try {
                satirlar = db.rawQuery("SELECT * FROM newyork_locations WHERE id = '" + Integer.toString( tahminler.get(i).getPlaceid() ) + "'", null);
                satirlar.moveToFirst();

                Locations location = new Locations();

                location.setId( satirlar.getInt( satirlar.getColumnIndex("id") ) );
                location.setCreated_at( satirlar.getString( satirlar.getColumnIndex("created_at") ) );
                location.setLng( satirlar.getDouble( satirlar.getColumnIndex("lng") ) );
                location.setLat( satirlar.getDouble( satirlar.getColumnIndex("lat") ) );
                location.setCheckins_count( satirlar.getInt( satirlar.getColumnIndex("checkins_count") ) );
                location.setCategories( satirlar.getInt( satirlar.getColumnIndex("categories") ) );

                koordinatlar.add(location);
                satirlar.close();
            } finally {
                satirlar.close();
            }


        }
        db.close();
        return koordinatlar;
    }


    /*
    Benzer kullanicilari inceler
     */
    public HashMap<Integer, Tahmin> benzer_kullanicilari_incele(HashMap<Integer, Tahmin> tahminler, ArrayList<Checkins> gecmis_checkin, int userid){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> kullanicilar = new ArrayList<Integer>();
        ArrayList<Checkins> kullanicilar_gecmis = new ArrayList<Checkins>();
        ArrayList<Double> benzerlik_oran = new ArrayList<Double>();
        ArrayList<Integer> benzer_mekan = new ArrayList<Integer>();
        HashMap<Integer, Integer> gecmis = new HashMap<Integer, Integer>();
        for(int i=0; i<gecmis_checkin.size(); i++) {
            gecmis.put( gecmis_checkin.get(i).getPlaceid(), i);
        }
        Cursor satirlar = db.rawQuery("select * from jacard_similarity where kullanici1 = " + Integer.toString(userid) + " and oran > 40", null);
        if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
            do{
                kullanicilar.add( satirlar.getInt( satirlar.getColumnIndex("kullanici2") ) );
                benzerlik_oran.add( satirlar.getDouble( satirlar.getColumnIndex("oran") ) );
            }while(satirlar.moveToNext());
        }
        for(int i=0; i<kullanicilar.size(); i++) {
            kullanicilar_gecmis = gecmis_checkin_bul( kullanicilar.get(i) );
            for(int j=0; j<kullanicilar_gecmis.size(); j++) {
                int mekan = kullanicilar_gecmis.get(j).getPlaceid();
                if( gecmis.containsKey( mekan ) == false && benzer_mekan.contains( mekan ) == false ) {
                    Tahmin tahmin = new Tahmin();
                    tahmin.setPuan(50);
                    tahmin.setPlaceid( mekan );
                    if( tahminler.containsKey(mekan) == true ) {
                        Tahmin tahmin_temp = tahminler.get( tahmin.getPlaceid() );
                        tahmin_temp.puanArttir( 40 );
                        tahminler.replace( tahmin.getPlaceid(), tahmin_temp );
                    } else {
                        tahminler.put(tahmin.getPlaceid(), tahmin);
                    }

                    benzer_mekan.add(mekan);
                }
            }
            benzer_mekan.clear();
        }
        db.close();
        return tahminler;
    }
    /*

     */
    public ArrayList<Kategori> kategorileri_getir(  ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Kategori> kategoriler = new ArrayList<Kategori>();
        Cursor satirlar = null;
        // Kategoriler cekildi...
        try {
            satirlar = db.rawQuery("SELECT * FROM categories", null);
            if ( ( satirlar != null ) && ( satirlar.moveToFirst() ) && ( satirlar.getCount() > 0 ) ) {
                do{
                    Kategori kategori = new Kategori();
                    kategori.setId(satirlar.getInt(satirlar.getColumnIndex("id")));
                    kategori.setIsim((satirlar.getString(satirlar.getColumnIndex("name"))));
                    kategoriler.add(kategori);
                }while(satirlar.moveToNext());
            }
            satirlar.close();
        } finally {
            satirlar.close();
        }
        db.close();
        return kategoriler;
    }
    /*

     */
    public ArrayList<Tahmin> kategori_populer_mekanlar_getir(int kategoriID){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Tahmin> tahminler = new ArrayList<Tahmin>();
        Cursor satirlar = null;
        try {
            satirlar = db.rawQuery("SELECT id, checkins_count FROM newyork_locations WHERE categories = " + kategoriID + " ORDER BY checkins_count DESC LIMIT 3", null);
            if ((satirlar != null) && (satirlar.moveToFirst()) && (satirlar.getCount() > 0)) {
                do {
                    Tahmin tahmin = new Tahmin();

                    tahmin.setPlaceid(satirlar.getInt(satirlar.getColumnIndex("id")));
                    tahmin.setPuan( satirlar.getInt( satirlar.getColumnIndex("checkins_count") ) / 10 );

                    tahminler.add(tahmin);

                } while (satirlar.moveToNext());
            }
            satirlar.close();
        } finally {
            satirlar.close();
        }
        db.close();
        return tahminler;
    }

    /*
    Sonraki üç method koordinatlar arası mesafe ölçümü içindir...
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344 * 1000;
        return (dist);
    }
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
