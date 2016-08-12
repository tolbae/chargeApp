package kr.joseph.wheelchair.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.joseph.wheelchair.Preferences.WPreferences;
import kr.joseph.wheelchair.R;
import kr.joseph.wheelchair.ServerAddress;
import kr.joseph.wheelchair.adapter.SearchAdapter;
import kr.joseph.wheelchair.adapter.SearchItem;
import kr.joseph.wheelchair.detail.SearchDetail;
import kr.joseph.wheelchair.gps.GpsInfo;
import kr.joseph.wheelchair.main.SearchActivity;

/**
 * Created by Joseph on 2016-05-25.
 * 거리 순으로 정렬된 Fragment 리스트
 */
@SuppressWarnings("ALL")
public class SearchFragLeft  extends Fragment implements AdapterView.OnItemClickListener{

    ListView list;
    SearchAdapter adapter;
    ArrayList<SearchItem> mListItem = new ArrayList<SearchItem>();

    GpsInfo gps;
    String latitude, longitude;
    double myLatitude, myLongitude;

    ServerAddress sa;
    private final String MAIN_ADDRESS = sa.MAIN_ADDRESS;
    private final String LOAD_DATA = sa.LOAD_DATA;
    String result_txt;
    ProgressDialog dialog;

    String wc_name, wc_address, wc_location, wc_callnum, wc_day, wc_openTime, wc_closeTime, wc_memo;
    double wc_latitude, wc_longitude, wc_distance;
    int wc_category, wc_img_rotate, wc_permit;
    String wc_image, wc_image2, wc_image3;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.frag_listview, container, false);
        View footer = inflater.inflate(R.layout.copyright, null, false);

        dialog = new ProgressDialog(getContext());
        dialog.setCanceledOnTouchOutside(false);

        gps = new GpsInfo(getContext());
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            myLatitude = gps.getLatitude();
            myLongitude = gps.getLongitude();
            latitude = Double.toString(myLatitude);
            longitude = Double.toString(myLongitude);
        } else {
            myLatitude = 37.566650;
            myLongitude = 126.978432;
            // 35.871306, 128.601345 : 대구 시청 => 현재 서울시청//
            latitude = Double.toString(myLatitude);
            longitude = Double.toString(myLongitude);
        }

        list = (ListView) v.findViewById(R.id.listView);
        list.addFooterView(footer);
        list.setOnItemClickListener(this);

        GetDataJSON g = new GetDataJSON();
        g.execute(new String[]{MAIN_ADDRESS});

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(getContext(), "Item Position : "+position, Toast.LENGTH_SHORT).show();

        // 두가지 방법 모두 사용가능하다.
        SearchItem mData = (SearchItem) parent.getItemAtPosition(position);
//        SearchItem mData = mListItem.get(position);

        Intent intent = new Intent(getContext(), SearchDetail.class);

        //넘겨야 하는 데이터가 추가되면 putExtra 추가. 로컬서버
        intent.putExtra("img", mData.getwImg());
        intent.putExtra("img2", mData.getwImg2());
        intent.putExtra("img3", mData.getwImg3());
        intent.putExtra("rotate", mData.getwRotate());
        intent.putExtra("name", mData.getwName());
        intent.putExtra("address", mData.getwAddress());
        intent.putExtra("location", mData.getwLocation());
        intent.putExtra("callnum", mData.getwCallnum());
        intent.putExtra("latitude", mData.getwLatitude());
        intent.putExtra("longitude", mData.getwLongitude());
        intent.putExtra("category", mData.getwCategory());
        intent.putExtra("day", mData.getwDay());
        intent.putExtra("time", mData.getwOpenTime()+" ~ "+mData.getwCloseTime());
        intent.putExtra("memo", mData.getwMemo());

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        getActivity().finish();
    }

    private class GetDataJSON extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {
            dialog.setMessage("Sending Data.... ");
            dialog.show();   ////우선수행

        }
        @Override
        protected Boolean doInBackground(String... params) {

            String uri = params[0];

            try {
                Log.e("좌표확인", "Latitude====>"+latitude+", Longitude====>"+longitude);
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("myLat", latitude));
                pairs.add(new BasicNameValuePair("myLon", longitude));

                HttpClient client = new DefaultHttpClient();

                // 서버 GET //
                URI u = URIUtils.createURI("http", uri, 80, LOAD_DATA, URLEncodedUtils.format(pairs, "UTF-8"), null);
                HttpGet post = new HttpGet(u);

                // 로컬 POST //
//                HttpPost post = new HttpPost(uri+LOAD_DATA);
//                post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));

                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);
                Log.e("Response Value", result_txt);

                doJSONParser();

                return true;

            }catch(Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result == true) {
//                Toast.makeText(getContext(), "Insert Success",Toast.LENGTH_LONG).show();
                Log.e("SearchFragLeft", "Insert Success");

                adapter = new SearchAdapter(getContext(), mListItem);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }else{
//                Toast.makeText(getContext(), "Error",Toast.LENGTH_LONG).show();
                Log.e("SearchFragLeft", "Error");
            }
        }
    }

    public void doJSONParser() {

        Log.e("doJSONParser","Enter Function");
        if(result_txt.equals("Not Found")){
            wc_name = "충전소 데이터가 없습니다.";
        } else {
            try {
                JSONArray ja = new JSONArray(result_txt);
                JSONArray jaArray = sortJsonArray(ja);
                Log.e("Parser JSONArray Length", Integer.toString(jaArray.length()));
                for(int i = 0; i < jaArray.length(); i++){
                    JSONObject jsonObj = jaArray.getJSONObject(i);
                    wc_name = jsonObj.getString("wc_name");
                    wc_address = jsonObj.getString("wc_address");
                    wc_location = jsonObj.getString("wc_location");
                    wc_callnum = jsonObj.getString("wc_callnum");
                    wc_latitude = jsonObj.getDouble("wc_latitude");
                    wc_longitude = jsonObj.getDouble("wc_longitude");
                    wc_category = Integer.parseInt(jsonObj.getString("wc_category"));
                    wc_day = jsonObj.getString("wc_day");
                    wc_openTime = jsonObj.getString("wc_open_time");
                    wc_closeTime = jsonObj.getString("wc_close_time");
                    wc_memo = jsonObj.getString("wc_memo");
                    wc_image = jsonObj.getString("wc_image");
                    wc_image2 = jsonObj.getString("wc_image2");
                    wc_image3 = jsonObj.getString("wc_image3");
                    wc_img_rotate = Integer.parseInt(jsonObj.getString("wc_img_rotate"));
                    wc_permit = Integer.parseInt(jsonObj.getString("wc_permit"));
                    wc_distance = jsonObj.getDouble("wc_distance");

                    Log.e("JSONArray Data", wc_permit+", "+wc_image+", "+wc_image2+", "+wc_image3+", "+wc_img_rotate+", "+wc_name+", "+wc_address+", "+wc_callnum+", "+wc_location+", "+wc_latitude+", "+wc_longitude+", "+wc_category+", "+
                            wc_day+", "+wc_openTime+", "+wc_closeTime+", "+wc_memo+", "+wc_distance);
                    if(wc_permit == 1) {
                        mListItem.add(new SearchItem(wc_image, wc_image2, wc_image3, wc_img_rotate, wc_name, wc_address, wc_location, wc_callnum, wc_latitude, wc_longitude, wc_category,
                                wc_day, wc_openTime, wc_closeTime, wc_memo, wc_distance));
                    }
                }

            } catch (JSONException e) {
                Log.e("JSONException", "Exception!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 거리 순으로 정렬
     * @param array
     * @return
     * @throws JSONException
     */
    public static JSONArray sortJsonArray(JSONArray array) throws JSONException {
        ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                double lid = 0;
                double rid = 0;
                try {
                    lid = lhs.getDouble("wc_distance");
                    rid = rhs.getDouble("wc_distance");
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(lid < rid){
                    return -1;
                } else if(lid > rid){
                    return 1;
                } else {
                    return 0;
                }
                // Here you could parse string id to integer and then compare.
            }
        });
        return new JSONArray(jsons);
    }
}
