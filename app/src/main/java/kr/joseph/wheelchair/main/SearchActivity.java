package kr.joseph.wheelchair.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.joseph.wheelchair.R;
import kr.joseph.wheelchair.fragment.SearchFragLeft;
import kr.joseph.wheelchair.fragment.SearchFragRight;
import kr.joseph.wheelchair.fragment.SerachFragWaiting;
import kr.joseph.wheelchair.gps.GpsInfo;

/**
 * Created by Joseph on 2016-05-24.
 * 충전소 정보를 받아오는 Fragment 메인 ACTIVITY
 */
public class SearchActivity extends FragmentActivity implements View.OnClickListener {

    Button bt_back, bt_left, bt_right, bt_waiting;
    RelativeLayout rl_left, rl_right, rl_waiting, frag;
    TextView txt_left, txt_right, txt_waiting;

    int img;
    String name, address, distance;

    int mCurrentFragmentIndex;
    public final static int FRAGMENT_ONE = 0;
    public final static int FRAGMENT_TWO = 1;
    public final static int FRAGMENT_THREE = 2;

    // GPSTracker class
    private GpsInfo gps;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

//        getGPSInfo();

        frag = (RelativeLayout) findViewById(R.id.frag);
        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_waiting = (RelativeLayout) findViewById(R.id.rl_waiting);
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_left = (Button) findViewById(R.id.bt_left);
        bt_right = (Button) findViewById(R.id.bt_right);
        bt_waiting = (Button) findViewById(R.id.bt_waiting);
        txt_left = (TextView) findViewById(R.id.txt_left);
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_waiting = (TextView) findViewById(R.id.txt_waiting);

        bt_back.setOnClickListener(this);
        bt_left.setOnClickListener(this);
        bt_right.setOnClickListener(this);
        bt_waiting.setOnClickListener(this);

        mCurrentFragmentIndex = FRAGMENT_ONE;
        fragmentReplace(mCurrentFragmentIndex);
    }

    public void fragmentReplace(int reqNewFragmentIndex) {

        Fragment newFragment = null;

        Log.d("MainActivity", "fragmentReplace " + reqNewFragmentIndex);

        newFragment = getFragment(reqNewFragmentIndex);

        // replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.frag, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    private Fragment getFragment(int idx) {
        Fragment newFragment = null;

        switch (idx) {
            case FRAGMENT_ONE:
                newFragment = new SearchFragLeft();
                break;
            case FRAGMENT_TWO:
                newFragment = new SearchFragRight();
                break;
            case FRAGMENT_THREE:
                newFragment = new SerachFragWaiting();
                break;
            default:
                Log.d("MainActivity", "Unhandle case");
                break;
        }
        return newFragment;
    }

    public void getGPSInfo(){
        gps = new GpsInfo(this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

//            Toast.makeText(getApplicationContext(), "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude, Toast.LENGTH_LONG).show();
            Log.e("Location", "Latitude : "+latitude+", Longitude : "+longitude);

        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                break;
            case R.id.bt_left:
                rl_left.setBackgroundResource(R.color.search_button_bg_click);
                rl_right.setBackgroundResource(R.color.search_button_bg_default);
                rl_waiting.setBackgroundResource(R.color.search_button_bg_default);
                txt_left.setTextColor(getResources().getColor(R.color.text_white));
                txt_right.setTextColor(getResources().getColor(R.color.text_black));
                txt_waiting.setTextColor(getResources().getColor(R.color.text_black));

                mCurrentFragmentIndex = FRAGMENT_ONE;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.bt_right:
                rl_left.setBackgroundResource(R.color.search_button_bg_default);
                rl_right.setBackgroundResource(R.color.search_button_bg_click);
                rl_waiting.setBackgroundResource(R.color.search_button_bg_default);
                txt_left.setTextColor(getResources().getColor(R.color.text_black));
                txt_right.setTextColor(getResources().getColor(R.color.text_white));
                txt_waiting.setTextColor(getResources().getColor(R.color.text_black));

                mCurrentFragmentIndex = FRAGMENT_TWO;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.bt_waiting:
                rl_left.setBackgroundResource(R.color.search_button_bg_default);
                rl_right.setBackgroundResource(R.color.search_button_bg_default);
                rl_waiting.setBackgroundResource(R.color.search_button_bg_click);
                txt_left.setTextColor(getResources().getColor(R.color.text_black));
                txt_right.setTextColor(getResources().getColor(R.color.text_black));
                txt_waiting.setTextColor(getResources().getColor(R.color.text_white));

                mCurrentFragmentIndex = FRAGMENT_THREE;
                fragmentReplace(mCurrentFragmentIndex);
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
