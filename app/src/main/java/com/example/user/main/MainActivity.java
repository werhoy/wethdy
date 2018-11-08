package com.example.user.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import org.ankit.gpslibrary.MyTracker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Gota.OnRequestPermissionsBack {
    TextView locationText;
    TextView tempratureText;
    TextView dateText;
    ImageView charactersImage;
    final String TAG = MainActivity.class.getSimpleName();
    String locationInfo;
    String temperatureString="";
    int temperatureValue=0;
    int weathrtId=0;
    String weather="";
    boolean raining=false;
    String week="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText=(TextView)findViewById(R.id.locationText);
        tempratureText = (TextView)findViewById(R.id.tempratureText);
        dateText = (TextView)findViewById(R.id.dateText);
        charactersImage = (ImageView)findViewById(R.id.charactersimage);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //권한을 거절하면 재 요청을 하는 함수
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_CHECKIN_PROPERTIES},1);
            }
        }

        new Gota.Builder(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .requestId(1)
                .setListener(this)
                .check();
        Context mContext = this;
        Address finalA = null;
        MyTracker tracker = new MyTracker(mContext);
        Geocoder mGeo = new Geocoder(mContext, Locale.ENGLISH);
        Geocoder mGeok = new Geocoder(mContext, Locale.KOREA);
        List<Address> addr = null;
        List<Address> addrk = null;
        Address a = null;   Address ak = null;

        try {
            addr = mGeo.getFromLocation(tracker.getLatitude(),tracker.getLongitude(),1);
            addrk = mGeok.getFromLocation(tracker.getLatitude(),tracker.getLongitude(),1);
            a = addr.get(0);
            ak = addrk.get(0);
            finalA = a;
            locationInfo = a.getSubLocality();
            Log.e("Location",locationInfo);
            locationText.setText(ak.getSubLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getWeatherInfo();

        SimpleDateFormat df = new SimpleDateFormat("MM/dd",Locale.KOREA);
        String Date = df.format(new Date());
        getDate();
        dateText.setText(Date+"("+week+")");
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

    private void getWeatherInfo() {
        MainActivity2 mainActivity2 = MainActivity2.retrofit.create(MainActivity2.class);
        Call<WeatherItem> call = mainActivity2.weatherItem(locationInfo,"438222bbe95d52ebb79660d82adaa30a");
        call.enqueue(new Callback<WeatherItem>() {
            @Override
            public void onResponse(Call<WeatherItem> call, Response<WeatherItem> response) {
                try {
                    Log.i(TAG, "onResponse: "+ (((int)response.body().mains.temp) - 273));
                    temperatureValue = (((int)response.body().mains.temp) - 273);
                    temperatureString = ((Integer)temperatureValue).toString();
                    tempratureText.setText(temperatureString);

                    Log.i(TAG, "rain: "+ response.body().weathers.main);
                    weather = response.body().weathers.main;
                    if(weather.equals("rain")) raining = true;

                    Log.i(TAG,"weather"+response.body().weathers.id);


                    imageChange();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WeatherItem> call, Throwable t) {
                Log.i(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }

    private void getDate(){
        Calendar cal = Calendar.getInstance();
        int dayweek = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayweek){
            case 1:
                week="일";
                break;
            case 2:
                week= "월";
                break;
            case 3:
                week= "화";
                break;
            case 4:
                week= "수";
                break;
            case 5:
                week= "목";
                break;
            case 6:
                week= "금";
                break;
            case 7:
                week= "토";
                break;
        }
    }

    private void imageChange(){
        if(temperatureValue<5){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr5);
                return;
            }
            charactersImage.setImageResource(R.drawable.c5);
        }else if(temperatureValue<10){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr6_9);
                return;
            }
            charactersImage.setImageResource(R.drawable.c6_9);
        }else if(temperatureValue<12){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr10_11);
                return;
            }
            charactersImage.setImageResource(R.drawable.c10_11);
        }else if(temperatureValue<17){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr12_16);
                return;
            }
            charactersImage.setImageResource(R.drawable.c12_16);
        }else if(temperatureValue<20){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr17_19);
                return;
            }
            charactersImage.setImageResource(R.drawable.c17_19);
        }else if(temperatureValue<23){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr20_22);
                return;
            }
            charactersImage.setImageResource(R.drawable.c20_22);
        }else if(temperatureValue<27){
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr23_26);
                return;
            }
            charactersImage.setImageResource(R.drawable.c23_26);
        }else{
            if(raining) {
                charactersImage.setImageResource(R.drawable.cr27);
                return;
            }
            charactersImage.setImageResource(R.drawable.c27);
        }
    }


}
