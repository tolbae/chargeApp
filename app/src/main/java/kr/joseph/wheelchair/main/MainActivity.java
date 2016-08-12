package kr.joseph.wheelchair.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;

import kr.joseph.wheelchair.BackPressCloseHandler;
import kr.joseph.wheelchair.Preferences.WPreferences;
import kr.joseph.wheelchair.R;
import kr.joseph.wheelchair.gps.GpsInfo;

/**
 * Created by Joseph on 2016-05-24.
 * 메인 화면
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    ImageView left, right;

    BackPressCloseHandler mHandler;

    // GPSTracker class
    private GpsInfo gps;
    WPreferences pref;

    double latitude;
    double longitude;

    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getGPSInfo();

        mHandler = new BackPressCloseHandler(this);

        left = (ImageView) findViewById(R.id.c_find);
        right = (ImageView) findViewById(R.id.c_register);

        left.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    public void getGPSInfo(){
        pref = new WPreferences(this);
        gps = new GpsInfo(this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

//            pref.putString("latitude", Double.toString(latitude));    //위도 저장
//            pref.putString("longitude",Double.toString(longitude));   //경도 저장

//            Toast.makeText(getApplicationContext(), "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_LONG).show();
            Log.e("Location", "Latitude : "+latitude+", Longitude : "+longitude);

        } else {
            // GPS 를 사용할수 없으므로
            latitude = 37.566650;
            longitude = 126.978432;
            // 35.871306, 128.601345 : 대구 시청 //

//            Toast.makeText(getApplicationContext(), "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_LONG).show();
            Log.e("Location", "Latitude : "+latitude+", Longitude : "+longitude);
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){
            case R.id.c_find:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
//                Toast.makeText(getApplicationContext(), "Find Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.c_register:
                gps = new GpsInfo(this);
                if (gps.isGetLocation()) {
                    intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    showSettingsAlert();
                }

//                intent = new Intent(this, RegisterActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
//                Toast.makeText(getApplicationContext(), "Register Button", Toast.LENGTH_SHORT).show();

                // 좌표를 이용하여 주소 찾기 //
//                String API_KEY = "d83e84a17b2614d29f78a1b58d6ed409";
//                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
//                MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder(API_KEY, mapPoint, this, this);
//                reverseGeoCoder.startFindingAddress();
//
//                Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        mHandler.onBackPressed();
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS 설정");
        alertDialog.setMessage("충전소 등록은 GPS 설정이 켜져있어야만 가능합니다.\n설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        address = s;
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    // 설문참여하기 클릭 : 설문페이지 열기
    public void onClick_survey(View v) {
        startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse( "https://docs.google.com/forms/d/e/1FAIpQLScVuc4K0ssJGNw3elydCiC_STM9WXCeuLhRyytW16NOpdN-Kg/viewform"  )));
    }


}