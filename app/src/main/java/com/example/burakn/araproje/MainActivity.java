package com.example.burakn.araproje;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burakn.araproje.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 25;
    double toplam_oran;
    int sayac;
    int i = 0;
    ArrayList<Integer> kullanicilar = new ArrayList<Integer>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText input = (EditText)findViewById(R.id.userid);
        Button basla = (Button)findViewById(R.id.basla);
        basla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!input.getText().toString().matches("")){
                    Veritabani veritabani = null;
                    try{
                        veritabani = new Veritabani(getApplicationContext());
                        veritabani.createDatabase();
                        if( veritabani.kullanici_kontrol( input.getText().toString() ) ){
                            int userid = Integer.parseInt( input.getText().toString() );
                            Intent hesaplama = new Intent (MainActivity.this, Hesaplama.class);
                            hesaplama.putExtra("USERID", userid);
                            startActivity(hesaplama);
                        } else {
                            Toast.makeText(getApplicationContext(), "Kayıtsız ID!", Toast.LENGTH_SHORT).show();
                        }
                        veritabani.close();
                    }catch(Exception e){
                        Log.e("DB_LOG",e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "ID Alanını Boş Bırakmayınız!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
