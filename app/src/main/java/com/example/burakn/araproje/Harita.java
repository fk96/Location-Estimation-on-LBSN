package com.example.burakn.araproje;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.burakn.araproje.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class Harita extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ArrayList<Locations> gelecek_koordinatlar = new ArrayList<Locations>();
    ArrayList<Locations> gecmis_koordinatlar = new ArrayList<Locations>();
    ArrayList<Locations> tahmin_koordinatlar = new ArrayList<Locations>();
    ArrayList<Locations> dogru_koordinatlar = new ArrayList<Locations>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harita);

        gelecek_koordinatlar = (ArrayList<Locations>) getIntent().getSerializableExtra("gelecek_koordinat");
        gecmis_koordinatlar = (ArrayList<Locations>) getIntent().getSerializableExtra("gecmis_koordinat");
        tahmin_koordinatlar = (ArrayList<Locations>) getIntent().getSerializableExtra("tahminler_koordinat");
        dogru_koordinatlar = (ArrayList<Locations>) getIntent().getSerializableExtra("dogru_koordinat");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int i=0;
        while(i<tahmin_koordinatlar.size()){
            LatLng konum = new LatLng( tahmin_koordinatlar.get(i).getLat(), tahmin_koordinatlar.get(i).getLng() );
            mMap.addMarker(new MarkerOptions().position( konum )
                    .title(Integer.toString( tahmin_koordinatlar.get(i).getId() )).snippet( Integer.toString( tahmin_koordinatlar.get(i).getCheckins_count() ) )
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).alpha(0.8f));
            i++;
        }
        i=0;

        while(i<gecmis_koordinatlar.size()){
            LatLng konum = new LatLng( gecmis_koordinatlar.get(i).getLat(), gecmis_koordinatlar.get(i).getLng() );
            mMap.addMarker(new MarkerOptions().position( konum )
                    .title(Integer.toString( gecmis_koordinatlar.get(i).getId() ) ).snippet( Integer.toString( gecmis_koordinatlar.get(i).getCheckins_count() ) )
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).alpha(0.8f));
            i++;
        }
        i=0;
        while(i<gelecek_koordinatlar.size()){
            LatLng konum = new LatLng( gelecek_koordinatlar.get(i).getLat(), gelecek_koordinatlar.get(i).getLng() );
            mMap.addMarker(new MarkerOptions().position( konum )
                    .title(Integer.toString( gelecek_koordinatlar.get(i).getId() ) ).snippet( Integer.toString( gelecek_koordinatlar.get(i).getCheckins_count() ) )
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).alpha(0.8f));
            i++;
        }
        i=0;
        while(i<dogru_koordinatlar.size()){
            LatLng konum = new LatLng( dogru_koordinatlar.get(i).getLat(), dogru_koordinatlar.get(i).getLng() );
            mMap.addMarker(new MarkerOptions().position( konum )
                    .title(Integer.toString( dogru_koordinatlar.get(i).getId() )).snippet( Integer.toString( dogru_koordinatlar.get(i).getCheckins_count() ) + " - Başarı %" + Double.toString(dogru_koordinatlar.get(i).getOran()) )
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.8f));
            i++;
        }
        LatLng sonKonum = new LatLng(dogru_koordinatlar.get( dogru_koordinatlar.size()-1 ).getLat(), dogru_koordinatlar.get( dogru_koordinatlar.size()-1 ).getLng());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonKonum, 8));
    }
}
