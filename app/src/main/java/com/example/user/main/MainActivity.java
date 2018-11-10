package com.example.user.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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
    TextView scriptText;
    ImageView charactersImage;
    ImageView weatherImage;
    final String TAG = MainActivity.class.getSimpleName();
    String locationInfo;
    String temperatureString="";
    int temperatureValue=0;
    String weathericon="";
    String week="";
    boolean raining = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText=(TextView)findViewById(R.id.locationText);
        tempratureText = (TextView)findViewById(R.id.tempratureText);
        dateText = (TextView)findViewById(R.id.dateText);
        scriptText = (TextView)findViewById(R.id.scriptText);
        charactersImage = (ImageView)findViewById(R.id.charactersimage);
        weatherImage = (ImageView)findViewById(R.id.weatherImage);

//        new Gota.Builder(this)
//                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
//                .requestId(1)
//                .setListener(this)
//                .check();
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    } //OnCreate

    private void init(){
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
            locationInfo = a.getSubLocality();
            Log.e("Location",locationInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mGeo = new Geocoder(mContext, Locale.KOREA);
        addr = null;
        a = null;

        try {
            addr = mGeo.getFromLocation(tracker.getLatitude(),tracker.getLongitude(),1);
            a = addr.get(0);
            finalA = a;
            locationText.setText(a.getSubLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleDateFormat df = new SimpleDateFormat("MM/dd",Locale.KOREA);
        String Date = df.format(new Date());
        getDate(); //날짜
        dateText.setText(Date+"("+week+")");

        getWeatherInfo(); //날씨
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

        }
    }

    private void getWeatherInfo() {
        MainActivity2 mainActivity2 = MainActivity2.retrofit.create(MainActivity2.class);
        Call<WeatherItem> call = mainActivity2.weatherItem(locationInfo,"438222bbe95d52ebb79660d82adaa30a");
        call.enqueue(new Callback<WeatherItem>() {
            @Override
            public void onResponse(Call<WeatherItem> call, Response<WeatherItem> response) {
                try {
                    //위치
                    Log.i(TAG, "onResponse: "+ (((int)response.body().mains.temp) - 273));
//                    Log.i(TAG, "onResponse weather : "+response.body().weather.size());
                    temperatureValue = (((int)response.body().mains.temp) - 273);
                    temperatureString = ((Integer)temperatureValue).toString();
                    tempratureText.setText(temperatureString);

                    //날씨
                    weathericon = response.body().weathers.get(0).icon;

                    if(weathericon.equals("01d")){
                        weatherImage.setImageResource(R.drawable.sun);
                    }else if(weathericon.equals("01n")){
                        weatherImage.setImageResource(R.drawable.moon);
                    }else if(weathericon.equals("02d")){
                        weatherImage.setImageResource(R.drawable.clouds_sun);
                    }else if(weathericon.equals("02n")){
                        weatherImage.setImageResource(R.drawable.clouds_moon);
                    }else if(weathericon.equals("03d") || weathericon.equals("03n")){
                        weatherImage.setImageResource(R.drawable.clouds);
                    }else if(weathericon.equals("04d") || weathericon.equals("04n")){
                        weatherImage.setImageResource(R.drawable.broken_clouds);
                    }else if(weathericon.equals("09d") || weathericon.equals("09n")){
                        weatherImage.setImageResource(R.drawable.shower_rain);
                        raining = true;
                    }else if(weathericon.equals("10d")){
                        weatherImage.setImageResource(R.drawable.rain_sun);
                        raining = true;
                    }else if(weathericon.equals("10n")){
                        weatherImage.setImageResource(R.drawable.rain_moon);
                        raining = true;
                    }else if(weathericon.equals("11d") || weathericon.equals("11n")){
                        weatherImage.setImageResource(R.drawable.thunder);
                    }else if(weathericon.equals("13d") || weathericon.equals("13n")){
                        weatherImage.setImageResource(R.drawable.snow);
                    }else if(weathericon.equals("50d") || weatherImage.equals("50n")){
                        weatherImage.setImageResource(R.drawable.mist);
                    }

                    if (temperatureValue < 5) {
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr5);
                            return;
                        }
                        scriptText.setText("읏추!! 감기 걸리겠어요");
                        charactersImage.setImageResource(R.drawable.c5);

                    } else if (temperatureValue < 10) { //6~9도
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr6_9);
                            return;
                        }
                        scriptText.setText("핫도그 먹기 좋은 날씨에요:0");
                        charactersImage.setImageResource(R.drawable.c6_9);

                    } else if (temperatureValue < 12) { //10~11도
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr10_11);
                            return;
                        }
                        scriptText.setText("으슬으슬! 뜨숩게 입고 나가요");
                        charactersImage.setImageResource(R.drawable.c10_11);

                    } else if (temperatureValue < 17) { //12~16도
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr12_16);
                            return;
                        }
                        scriptText.setText("겉옷 꼭 챙겨서 나가요!"); //겉옷필수
                        charactersImage.setImageResource(R.drawable.c12_16);

                    } else if (temperatureValue < 20) { //17~19도
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr17_19);
                            return;
                        }
                        scriptText.setText("자전거 타러갈까요?:)");
                        charactersImage.setImageResource(R.drawable.c17_19);

                    } else if (temperatureValue < 23) { //20~22도
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr20_22);
                            return;
                        }
                        scriptText.setText("솜사탕들고 나들이갈 날씨에요:0");
                        charactersImage.setImageResource(R.drawable.c20_22);

                    } else if (temperatureValue < 27) { //23~26도
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr23_26);
                            return;
                        }
                        scriptText.setText("더워! 물 자주 마시세요");
                        charactersImage.setImageResource(R.drawable.c23_26);

                    } else { //27도 이상
                        if(raining){
                            scriptText.setText("비가 와요! 우산을 챙기세요");
                            charactersImage.setImageResource(R.drawable.cr27);
                            return;
                        }
                        scriptText.setText("아이스크림처럼 녹아버리겠어요:(");
                        charactersImage.setImageResource(R.drawable.c27);

                    }

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
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            init();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "권한이 거부되었습니다" , Toast.LENGTH_SHORT).show();
            finish();
        }


    };

}
