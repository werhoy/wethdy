package com.example.user.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.MapView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import org.ankit.gpslibrary.MyTracker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends AppCompatActivity implements Gota.OnRequestPermissionsBack {
    TextView locationText;
    TextView tempratureText;
    TextView dateText;
    TextView scriptText;
    ImageView charactersImage;
    ImageView weatherImage;
    ImageButton imageButton;

    final String TAG = MainActivity.class.getSimpleName();
    String locationInfo;
    String temperatureString="";
    int temperatureValue=0;
    String weathericon="";
    String week="";
    boolean raining = false;

    static boolean check=false;//다른위치를 검색하였는가
    TextView textView;
    String otherLocationInfo;

    Intent intent;

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
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        textView = (TextView)findViewById(R.id.textView);


        new Gota.Builder(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                .requestId(1)
                .setListener(this)
                .check();

        //TedPermission.with(this)
        //        .setPermissionListener(permissionlistener)
        //        .setPermissions(Manifest.permission.INTERNET)
        //        .check();


        if (!netWork()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("네트워크 연결상태를 확인해주세요")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialogshow = dialog.create();    // 알림창 객체 생성
            dialogshow.show();    // 알림창 띄우기
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String > item = new ArrayList<>();
                item.add("현재위치날씨보기");
                item.add("다른위치날씨보기");
                final CharSequence[] items = item.toArray(new String[item.size()]);

                AlertDialog.Builder dialong = new AlertDialog.Builder(MainActivity.this);
                dialong.setCancelable(false)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        check=false;
                                        ini
                                        break;
                                    case 1:
                                        check=true;
                                        startActivity(new Intent(MainActivity.this,OtherLocationActivity.class));
                                        finish();
                                        break;
                                }

                            }
                        });
                dialong.show();

            }
        });


    } //OnCreate

    private boolean netWork(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ninfo = cm.getActiveNetworkInfo();
        if(ninfo == null){
            return false;
        }
        return true;
    }

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

    }//init-----------------------------------------------------------------------------------------

    void init(String otherLocationInfo){

        SimpleDateFormat df = new SimpleDateFormat("MM/dd",Locale.KOREA);
        String Date = df.format(new Date());
        getDate(); //날짜
        dateText.setText(Date+"("+week+")");

        getWeatherInfo(otherLocationInfo); //날씨

    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if(gotaResponse.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            intent = getIntent();
            otherLocationInfo = intent.getStringExtra("location");
            Log.i("gecorderinputother",otherLocationInfo);
            if(check && otherLocationInfo != null) {
                init(otherLocationInfo);
            }else{
                init();
            }
        }
    }

    private void getWeatherInfo() {
        MainActivity2 mainActivity2 = MainActivity2.retrofit.create(MainActivity2.class);
        Call<WeatherItem> call = mainActivity2.weatherItem(locationInfo,"438222bbe95d52ebb79660d82adaa30a");
        call.enqueue(new Callback<WeatherItem>() {
            @Override
            public void onResponse(Call<WeatherItem> call, Response<WeatherItem> response) {
                try {
                    //온도
//                  Log.i(TAG, "onResponse weather : "+response.body().weather.size());
                    temperatureValue = (((int)response.body().mains.temp) - 273);
                    temperatureString = ((Integer)temperatureValue).toString();
                    tempratureText.setText(temperatureString);

                    //날씨
                    weathericon = response.body().weathers.get(0).icon;

                    if(weathericon.equals("01d")){
                        weatherImage.setImageResource(R.drawable.w_sun);
                    }else if(weathericon.equals("01n")){
                        weatherImage.setImageResource(R.drawable.w_moon);
                    }else if(weathericon.equals("02d")){
                        weatherImage.setImageResource(R.drawable.w_clouds_sun);
                    }else if(weathericon.equals("02n")){
                        weatherImage.setImageResource(R.drawable.w_clouds_moon);
                    }else if(weathericon.equals("03d") || weathericon.equals("03n")){
                        weatherImage.setImageResource(R.drawable.w_clouds);
                    }else if(weathericon.equals("04d") || weathericon.equals("04n")){
                        weatherImage.setImageResource(R.drawable.w_broken_clouds);
                    }else if(weathericon.equals("09d") || weathericon.equals("09n")){
                        weatherImage.setImageResource(R.drawable.w_shower_rain);
                        raining = true;
                    }else if(weathericon.equals("10d")){
                        weatherImage.setImageResource(R.drawable.w_rain_sun);
                        raining = true;
                    }else if(weathericon.equals("10n")){
                        weatherImage.setImageResource(R.drawable.w_rain_moon);
                        raining = true;
                    }else if(weathericon.equals("11d") || weathericon.equals("11n")){
                        weatherImage.setImageResource(R.drawable.w_thunder);
                    }else if(weathericon.equals("13d") || weathericon.equals("13n")){
                        weatherImage.setImageResource(R.drawable.w_snow);
                    }else {
                        weatherImage.setImageResource(R.drawable.w_mist);
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

    private void getWeatherInfo(String otherLocationInfo) {
        MainActivity2 mainActivity2 = MainActivity2.retrofit.create(MainActivity2.class);
        Call<WeatherItem> call = mainActivity2.weatherItem(otherLocationInfo,"438222bbe95d52ebb79660d82adaa30a");
        call.enqueue(new Callback<WeatherItem>() {
            @Override
            public void onResponse(Call<WeatherItem> call, Response<WeatherItem> response) {
                try {
                    //온도
//                  Log.i(TAG, "onResponse weather : "+response.body().weather.size());
                    temperatureValue = (((int)response.body().mains.temp) - 273);
                    temperatureString = ((Integer)temperatureValue).toString();
                    tempratureText.setText(temperatureString);

                    //날씨
                    weathericon = response.body().weathers.get(0).icon;

                    if(weathericon.equals("01d")){
                        weatherImage.setImageResource(R.drawable.w_sun);
                    }else if(weathericon.equals("01n")){
                        weatherImage.setImageResource(R.drawable.w_moon);
                    }else if(weathericon.equals("02d")){
                        weatherImage.setImageResource(R.drawable.w_clouds_sun);
                    }else if(weathericon.equals("02n")){
                        weatherImage.setImageResource(R.drawable.w_clouds_moon);
                    }else if(weathericon.equals("03d") || weathericon.equals("03n")){
                        weatherImage.setImageResource(R.drawable.w_clouds);
                    }else if(weathericon.equals("04d") || weathericon.equals("04n")){
                        weatherImage.setImageResource(R.drawable.w_broken_clouds);
                    }else if(weathericon.equals("09d") || weathericon.equals("09n")){
                        weatherImage.setImageResource(R.drawable.w_shower_rain);
                        raining = true;
                    }else if(weathericon.equals("10d")){
                        weatherImage.setImageResource(R.drawable.w_rain_sun);
                        raining = true;
                    }else if(weathericon.equals("10n")){
                        weatherImage.setImageResource(R.drawable.w_rain_moon);
                        raining = true;
                    }else if(weathericon.equals("11d") || weathericon.equals("11n")){
                        weatherImage.setImageResource(R.drawable.w_thunder);
                    }else if(weathericon.equals("13d") || weathericon.equals("13n")){
                        weatherImage.setImageResource(R.drawable.w_snow);
                    }else {
                        weatherImage.setImageResource(R.drawable.w_mist);
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



    //PermissionListener permissionlistener = new PermissionListener() {
    //    @Override
    //    public void onPermissionGranted() {
    //        Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
    //        init();
    //    }

     //   @Override
     //   public void onPermissionDenied(List<String> deniedPermissions) {
     //       Toast.makeText(MainActivity.this, "와이파이 연결을 해주세요" , Toast.LENGTH_SHORT).show();
     //       finish();
     //   }

    //};

}
