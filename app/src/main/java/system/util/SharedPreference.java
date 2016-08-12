package system.util;


import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreference {
    private static SharedPreference _instance = null;
    private Context context = null;

    public static SharedPreference getInstance(Context context) {
        if (_instance == null)
            _instance = new SharedPreference(context);
        return _instance;
    }

    private SharedPreference(Context context) {
        this.context = context;
    }

    public void setLastWritename(String nickname) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("nickname", nickname);
        ed.commit();
    }

    public void setLastGcmIdx(String idx) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("lastidx", idx);
        ed.commit();
    }

    public void setPrevent(boolean prevent) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("prevent", prevent);
        ed.commit();
    }

    public void setLocation(String location) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("location", location);
        ed.commit();
    }

    public void setWeatherJson(String weather) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("weather", weather);
        ed.commit();
    }

    public void setVersionName(String versioname) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("versionname", versioname);
        ed.commit();
    }

    public void setCurrentVersionCode(int versioncode) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt("currentversioncode", versioncode);
        ed.commit();
    }

    public void setVersionCode(int versioncode) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt("versioncode", versioncode);
        ed.commit();
    }

    public void setLastupdate(String lastupdate) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("lastupdate", lastupdate);
        ed.commit();
    }

    public void setContact(String contactemail) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("contactemail", contactemail);
        ed.commit();
    }

    public void setIsfirst(boolean first) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("first", first);
        ed.commit();
    }

    public void setGcm(boolean gcm) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("gcm", gcm);
        ed.commit();
    }

    public void setGoogleLogin(String email) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("email", email);
        ed.commit();
    }

    public void setAutoDelete(String bookmark) {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("bookmark", bookmark);
        ed.commit();
    }

    public void setRealscreen(int width, int height) {
        SharedPreferences prefs = context
                .getSharedPreferences("screeninfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt("realwidth", width);
        ed.putInt("realheight", height);
        ed.commit();
    }

    public void setGcmcode(String code) {
        SharedPreferences prefs = context.getSharedPreferences("userinfor", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("gcmcode", code);
        ed.commit();
    }

    public String getLastWritename() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String lastname = prefs.getString("nickname", "");
        return lastname;
    }

    public String getLastGcmIdx() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String lastidx = prefs.getString("lastidx", "0");
        return lastidx;
    }

    public boolean getPrevent() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        boolean prevent = prefs.getBoolean("prevent", false);
        return prevent;
    }

    public String getLocation() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String location = prefs.getString("location", "daegu");
        return location;
    }

    public String getWeatherJson() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String weather = prefs.getString("weather", "");
        return weather;
    }

    public String getVersionName() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String version = prefs.getString("versionname", "0");
        return version;
    }

    public int getVersionCode() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        int version = prefs.getInt("versioncode", 0);
        return version;
    }

    public int getCurrentVersionCode() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        int version = prefs.getInt("currentversioncode", 0);
        return version;
    }

    public String getLastupdate() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String lastupdate = prefs.getString("lastupdate", "");
        return lastupdate;
    }

    public String getContact() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String email = prefs.getString("contactemail", "webmaster@wizware.kr");
        return email;
    }

    public boolean getIsfirst() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        boolean first = prefs.getBoolean("first", true);
        return first;
    }

    public boolean getGcm() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        boolean gcm = prefs.getBoolean("gcm", true);
        return gcm;
    }

    public boolean getAutoDelete() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        boolean gcm = prefs.getBoolean("bookmark", true);
        return gcm;
    }

    public String getGoogleLogin() {
        SharedPreferences prefs = context.getSharedPreferences("appinfor", 0);
        String email = prefs.getString("email", "");
        return email;
    }

    public String getGcmcode() {
        SharedPreferences prefs = context.getSharedPreferences("userinfor", 0);
        String gcmcode = prefs.getString("gcmcode", "");
        return gcmcode;
    }

    public int getRealwidth() {
        SharedPreferences prefs = context
                .getSharedPreferences("screeninfor", 0);
        int width = prefs.getInt("realwidth", 0);
        return width;
    }

    public int getRealheight() {
        SharedPreferences prefs = context
                .getSharedPreferences("screeninfor", 0);
        int height = prefs.getInt("realheight", 0);
        return height;
    }
}
