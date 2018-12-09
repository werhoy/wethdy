package com.example.user.main;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OtherLocationActivity extends AppCompatActivity {
    final String TAG = MainActivity.class.getSimpleName();
    EditText locationInput;
    String otherlocation;
    String otherlocationinfo;
    Geocoder geocoder;
    Intent intent;

    double lat;
    double lon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        intent = new Intent(OtherLocationActivity.this, MainActivity.class);

        Log.i(TAG, "onGps: " + "OK");
        locationInput = (EditText) findViewById(R.id.input_search);
        locationInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(EditorInfo.IME_ACTION_SEARCH == actionId){
                    Log.i("gps",locationInput.getText().toString());
                    otherlocation = locationInput.getText().toString().trim();

                    geocoder = new Geocoder(OtherLocationActivity.this);
                    //입력받은걸 -> 위도 경도 -> 주소로 변경
                    List<Address> list = null;
                    Address a = null;

                    try {
                        list = geocoder.getFromLocationName(otherlocation,1);
                        if(list != null){
                            if(list.size() == 0){
                                intent.putExtra("notinfo",false);
                                startActivity(intent);
                                finish();
                            }else{
                                a = list.get(0);
                                lat = a.getLatitude();
                                lon = a.getLongitude();
                                Log.i("gecorderinputlat+lon",lat+" " +lon);
                            }
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        startActivity(intent);
                        finish();
                    }


                    geocoder = new Geocoder(OtherLocationActivity.this,Locale.ENGLISH);

                    try{
                        list = geocoder.getFromLocation(lat,lon,1);
                        if(list != null){
                            if(list.size()==0){
                                intent.putExtra("notinfo",false);
                                startActivity(intent);
                                finish();
                            }
                            otherlocationinfo = list.get(0).getSubLocality().toString();
                            Log.i("gecorderinputother",otherlocationinfo);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        startActivity(intent);
                        finish();
                    }

                    intent.putExtra("location",otherlocationinfo);
                    startActivity(intent);
                    finish();
                    return true;

                }else {
                    return false;
                }
            }
        });



    }//onCreate-------------------------------------------------------------------------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean[] checking = {false};
            intent.putExtra("check",checking);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, msg);
    }
}
