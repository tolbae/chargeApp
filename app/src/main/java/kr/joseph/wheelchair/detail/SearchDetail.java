package kr.joseph.wheelchair.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import kr.joseph.wheelchair.BackPressCloseHandler;
import kr.joseph.wheelchair.R;
import kr.joseph.wheelchair.ServerAddress;
import kr.joseph.wheelchair.gps.GpsInfo;
import kr.joseph.wheelchair.main.SearchActivity;

/**
 * Created by Joseph on 2016-05-26.
 * 리스트뷰 아이템 클릭시 나타나는 ACTIVITY
 */
public class SearchDetail extends FragmentActivity implements View.OnClickListener, MapView.POIItemEventListener, MapView.MapViewEventListener {

    Button back, myLocation, moveLocation, bt_rotate;
    ImageView mIconImg;
    TextView mName, mAddress, mNumber, mMaterial, mLocation, mTime, mMemo;
    View mapInclude;
    ScrollView mScroll;


    int img, category, rotate;
    String image, image2, image3;
    String name, address, callnum, location, day, time, memo;

    private MapView mapView;
    private MapPOIItem mDefaultMarker;
    private MapPOIItem mCustomMarker;

    ///////// ImageSlide 관련 ///////////
    ViewPager viewPager;
    ImageView pagerImgView;
    ArrayList<String> imgArray = new ArrayList<>();
    CustomPagerAdapter mCustomAdapter;

    //////// Daum Map 관련 변수 //////////
    ViewGroup mapViewContainer;
    private final String API_KEY = "d83e84a17b2614d29f78a1b58d6ed409";

    BackPressCloseHandler handler;

    private static MapPoint MARKER_POINT, MY_MARKER_POINT;
//    private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.4020737, 127.1086766);
    /////////////////////////////////////

    GpsInfo gps;
    double myLatitude, myLongitude;
    double latitude, longitude;
    private static int LOCATION_STATE = 0;

    ServerAddress sa;
    private final String FOLDER = sa.IMAGE_FOLDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail);

        handler = new BackPressCloseHandler(this);

        gps = new GpsInfo(SearchDetail.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            myLatitude = gps.getLatitude();
            myLongitude = gps.getLongitude();
        } else {
            myLatitude = 37.566650;
            myLongitude = 126.978432;
        }

        Intent intent = getIntent();
        image = intent.getStringExtra("img");
        image2 = intent.getStringExtra("img2");
        image3 = intent.getStringExtra("img3");
        rotate = intent.getIntExtra("rotate", 0);
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        location = intent.getStringExtra("location");
        callnum = intent.getStringExtra("callnum");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        category = intent.getIntExtra("category", 4);
        day = intent.getStringExtra("day");
        time = intent.getStringExtra("time");
        memo = intent.getStringExtra("memo");

        MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        MY_MARKER_POINT = MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude);

        back = (Button) findViewById(R.id.detail_bt_back);
        mIconImg = (ImageView) findViewById(R.id.detail_icon_img);
        viewPager = (ViewPager) findViewById(R.id.detail_img);
        mName = (TextView) findViewById(R.id.detail_name);
        mAddress = (TextView) findViewById(R.id.detail_address);
        mNumber = (TextView) findViewById(R.id.detail_number);
        mMaterial = (TextView) findViewById(R.id.detail_material);
        mLocation = (TextView) findViewById(R.id.detail_location);
        mTime = (TextView) findViewById(R.id.detail_time);
        mMemo = (TextView) findViewById(R.id.detail_memo);
        mScroll = (ScrollView) findViewById(R.id.scroll);
        bt_rotate = (Button) findViewById(R.id.bt_rotate);

        if(image.equals(null) || image.equals("")){
        } else { imgArray.add(image); }
        if (image2.equals(null) || image2.equals("")){
        } else { imgArray.add(image2); }
        if (image3.equals(null) || image3.equals("")){
        } else { imgArray.add(image3); }
        if(imgArray.size() == 0){
            imgArray.add("");
        }
        Log.e("ImgArray Size", Integer.toString(imgArray.size()));

        include();

        back.setOnClickListener(this);
        bt_rotate.setOnClickListener(this);
        mNumber.setOnClickListener(this);

        Log.e("Image Null Check", image+", "+image2+", "+image3);

        mIconImg.setImageResource(img);
        mName.setText(name);
        mAddress.setText(address);
        mLocation.setText(location);
        mNumber.setText(phonFomatter(callnum));
        mTime.setText("("+day+") "+time);
        if(category == 1){
            mMaterial.setText("행정기관");
        } else if(category == 2){
            mMaterial.setText("복지관");
        } else if(category == 3){
            mMaterial.setText("지하철");
        } else if(category == 4){
            mMaterial.setText("기타");
        }
        mMemo.setText(memo);


        mCustomAdapter = new CustomPagerAdapter(this);
        viewPager.setAdapter(mCustomAdapter);
    }


    /**
     * 전화번호 폼
     */
    @SuppressWarnings("SpellCheckingInspection")
    public String phonFomatter(String callnum){

//        String pat1 = "^01(?:0|1|[6-9])(\\d{3,4})(\\d{4})";  //핸드폰 번호
        String pat2 = "(^02)(\\d{3,4})(\\d{4})";              //지역번호 02
        String pat3 = "(\\d{3})(\\d{3,4})(\\d{4})";         //나머지 전화번호

//        if(Pattern.matches(pat1, callnum)) {
//            Log.e("Pattern1", callnum.replaceAll(pat1, "$1-$2-$3"));
//            return callnum.replaceAll(pat1, "$1-$2-$3"); }
        if(Pattern.matches(pat2, callnum)) {
            Log.e("Pattern2", callnum.replaceAll(pat2, "$1-$2-$3"));
            return callnum.replaceAll(pat2, "$1-$2-$3");
        } else if(Pattern.matches(pat3, callnum)) {
            Log.e("Pattern3", callnum.replaceAll(pat3, "$1-$2-$3"));
            return callnum.replaceAll(pat3, "$1-$2-$3");
        } else {
            return callnum;
        }
    }

    /**
     * 등록지점으로 지도 이동
     */
    public void include() {
        mapInclude = (View) findViewById(R.id.mapInclude);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(API_KEY);

        mapViewContainer = (ViewGroup) mapInclude.findViewById(R.id.map_view);
        myLocation = (Button) mapInclude.findViewById(R.id.my_location);
        moveLocation = (Button) mapInclude.findViewById(R.id.move_location);
        myLocation.setOnClickListener(this);
        moveLocation.setOnClickListener(this);

        mapViewContainer.addView(mapView);

        mapView.setPOIItemEventListener(this); // 마커 클릭시 나타나는 말풍선 클릭 이벤트
        mapView.setMapViewEventListener(this);

        mDefaultMarker = new MapPOIItem();
        mCustomMarker = new MapPOIItem();

        moveLocation();
     }

    /**
     * 충전소 위치로 지도이동
     */
    private void moveLocation() {
        gps = new GpsInfo(SearchDetail.this);
        LOCATION_STATE = 0;

        mapView.setMapCenterPoint(MARKER_POINT, true); // 중심점 변경
        mapView.setZoomLevel(0, true); // 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(CUSTOM_MARKER_POINT, 9, true); // 중심점 변경 + 줌 레벨 변경
        mapView.zoomIn(true); // 줌 인
        mapView.zoomOut(true); // 줌 아웃

        createDefaultMarker(mapView);
    }

    /**
     * 내 위치로 지도 이동
     */
    private void myLocation() {
        LOCATION_STATE = 1;

        mapView.setMapCenterPoint(MY_MARKER_POINT, true); // 중심점 변경
        mapView.setZoomLevel(0, true); // 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(CUSTOM_MARKER_POINT, 9, true); // 중심점 변경 + 줌 레벨 변경
        mapView.zoomIn(true); // 줌 인
        mapView.zoomOut(true); // 줌 아웃

        createCustomMarker(mapView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_bt_back:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                break;
            case R.id.my_location:
                myLocation();
                break;
            case R.id.move_location:
                moveLocation();
                break;
            case R.id.bt_rotate:
                break;
            case R.id.detail_number:
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:"+mNumber.getText().toString()));
                startActivity(call);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createDefaultMarker(MapView mapView) {

        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(MARKER_POINT);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.removePOIItem(mDefaultMarker);
        mapView.addPOIItem(mDefaultMarker);
        mapView.selectPOIItem(mDefaultMarker, true);
        mapView.setMapCenterPoint(MARKER_POINT, false);
    }

    private void createCustomMarker(MapView mapView) {

        mCustomMarker.setItemName("내 위치");
        mCustomMarker.setTag(1);
        mCustomMarker.setMapPoint(MY_MARKER_POINT);

        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

        //커스텀 마커 설정부분
//        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

//        mCustomMarker.setCustomImageResourceId(R.drawable.map_pin_yellow);
//        mCustomMarker.setCustomImageAutoscale(false);
//        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.removePOIItem(mCustomMarker);
        mapView.addPOIItem(mCustomMarker);
        mapView.selectPOIItem(mCustomMarker, true);
        mapView.setMapCenterPoint(MY_MARKER_POINT, false);

    }

    //Balloon 클릭이벤트//
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        if(mapPOIItem.getMapPoint() == MARKER_POINT){
            LOCATION_STATE = 0;
        } else if (mapPOIItem.getMapPoint() == MY_MARKER_POINT){
            LOCATION_STATE = 1;
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        try {
            Intent url = new Intent();
            url.setAction(Intent.ACTION_VIEW);
            if(LOCATION_STATE == 0){
                url.setData(Uri.parse("daummaps://look?p=" + latitude + "," + longitude));
            } else if(LOCATION_STATE == 1) {
                url.setData(Uri.parse("daummaps://look?p=" + myLatitude + "," + myLongitude));
            }
            startActivity(url);
        } catch (Exception e) {
            Intent url = new Intent();
            url.setAction(Intent.ACTION_VIEW);
            url.setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map"));
            startActivity(url);
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }
    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        mScroll.requestDisallowInterceptTouchEvent(true);
    }


    /**
     * 이미지가 회전되어 있는지 확인
     * @param exifOrientation
     * @return
     */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * 회전되어 있던 이미지를 다시 회전시킴
     * @param bitmap
     * @param degrees
     * @return
     */
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    /**
     * image url을 받아서 bitmap을 생성하고 리턴합니다
     * @param url 얻고자 하는 image url
     * @return 생성된 bitmap
     */
    private Bitmap getBitmap(String url) {
        URL imgUrl = null;
        HttpURLConnection connection = null;
        InputStream is = null;

        Bitmap retBitmap = null;

        try{
            imgUrl = new URL(url);
            connection = (HttpURLConnection) imgUrl.openConnection();
            connection.setDoInput(true); //url로 input받는 flag 허용
            connection.connect(); //연결
            is = connection.getInputStream(); // get inputstream
            retBitmap = BitmapFactory.decodeStream(is);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(connection!=null) {
                connection.disconnect();
            }
            return retBitmap;
        }
    }


    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        private DisplayImageOptions options;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.splash_circle)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }

        @Override
        public int getCount() {
            return imgArray.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.img_fragment, container, false);
            Log.e("Pager Position", position+"");
            Log.e("Image Position", imgArray.get(position));

            ImageLoader imageLoader = ImageLoader.getInstance();

            pagerImgView = (ImageView) itemView.findViewById(R.id.imgView);
            pagerImgView.setScaleType(ImageView.ScaleType.FIT_XY);
            if(imgArray.get(0).equals("") || imgArray.get(0).equals(null)){

            } else {
                imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
                imageLoader.displayImage(FOLDER + imgArray.get(position), pagerImgView);
            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

}