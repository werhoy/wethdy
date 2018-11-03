package com.example.user.main;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import org.ankit.gpslibrary.MyTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Gota.OnRequestPermissionsBack {
    TextView location;
    final String TAG = MainActivity.class.getSimpleName();
    String locationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location=(TextView)findViewById(R.id.textView);
        new Gota.Builder(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .requestId(1)
                .setListener(this)
                .check();
        Context mContext = this;
        Address finalA = null;
        MyTracker tracker = new MyTracker(mContext);
        Geocoder mGeo = new Geocoder(mContext, Locale.ENGLISH);
        List<Address> addr = null;
        Address a = null;

        try {
            addr = mGeo.getFromLocation(tracker.getLatitude(),tracker.getLongitude(),1);
            a = addr.get(0);
            finalA = a;
            Log.e("Location",a.getSubLocality());
            locationInfo = a.getSubLocality();
            location.setText(a.getSubLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }

        getweatherinfo();
    }

    public class Weater {
        int lat;
        int ion;
        int temprature;
        int cloudy;
        String city;

        public void setLat(int lat) {this.lat = lat;}
        public void setIon(int ion){ this.ion = ion;}
        public void setTemprature(int t){ this.temprature = t;}
        public void setCloudy(int cloudy){ this.cloudy = cloudy;}
        public void setCity(String city){ this.city = city;}

        public int getLat(){ return lat;}
        public int getIon() { return ion;}
        public int getTemprature() { return temprature;}
        public int getCloudy() { return cloudy; }
        public String getCity() { return city; }
    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if(gotaResponse.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Your Code
        }
    }
    private void getweatherinfo() {
        MainActivity2 mainActivity2 = MainActivity2.retrofit.create(MainActivity2.class);
        Call<WeatherItem> call = mainActivity2.weatherItem(locationInfo,"438222bbe95d52ebb79660d82adaa30a");
        call.enqueue(new Callback<WeatherItem>() {
            @Override
            public void onResponse(Call<WeatherItem> call, Response<WeatherItem> response) {
                Log.i(TAG, "onResponse: "+ (((int)response.body().mains.temp) - 273));
            }

            @Override
            public void onFailure(Call<WeatherItem> call, Throwable t) {
                Log.i(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }
}
