package kr.joseph.wheelchair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import kr.joseph.wheelchair.main.MainActivity;
import system.util.SharedPreference;

/**
 * Created by Joseph on 2016-05-24.
 * 어플 실행시 나타나는 SPLASH 화면
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        checkScreen();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();

            }
        };
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkScreen() {
        int width = SharedPreference.getInstance(getApplicationContext()).getRealwidth();
        int height = SharedPreference.getInstance(getApplicationContext()).getRealheight();
        if (width == 0 || height == 0) {
            findViewById(R.id.splash_relative).post(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                        Rect rect = new Rect();
                        Window win = getWindow();
                        win.getDecorView().getWindowVisibleDisplayFrame(rect);
                        int statusHeight = rect.top;
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int width = size.x;
                        int height = size.y;
                        SharedPreference.getInstance(getApplicationContext()).setRealscreen(width, height - statusHeight);
                    } else {
                        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                                .getDefaultDisplay();
                        int calwidth = display.getWidth();
                        int calheight = display.getHeight();
                        double ratio = (double) calheight / (double) calwidth;
                        Rect rect = new Rect();
                        Window window = getWindow();
                        window.getDecorView().getWindowVisibleDisplayFrame(rect);
                        int statusBarHeight = rect.top;
                        SharedPreference.getInstance(getApplicationContext()).setRealscreen(
                                calwidth, calheight - statusBarHeight);
                    }
                }
            });
        }
    }
}
