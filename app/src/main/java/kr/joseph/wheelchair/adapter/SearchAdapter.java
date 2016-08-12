package kr.joseph.wheelchair.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.joseph.wheelchair.Preferences.WPreferences;
import kr.joseph.wheelchair.R;
import kr.joseph.wheelchair.gps.DistanceTo;

/**
 * Created by Joseph on 2016-05-25.
 */
public class SearchAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<SearchItem> mData;
    private double distance;
    private String strDist;

    public SearchAdapter(Context context, ArrayList<SearchItem> listItem){
        super();
        this.mContext = context;
        this.mData = listItem;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder vh = new ViewHolder();

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.search_list_item, null);

            vh.img = (ImageView) v.findViewById(R.id.icon_img);
            vh.name = (TextView) v.findViewById(R.id.name);
            vh.address = (TextView) v.findViewById(R.id.address);
            vh.distance = (TextView) v.findViewById(R.id.distance);

            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        vh.mItem = mData.get(position);
        if(vh.mItem != null){

            vh.img.setBackgroundResource(R.drawable.icon_building);
            vh.name.setText(vh.mItem.getwName());
            vh.address.setText(vh.mItem.getwAddress());
            distance = vh.mItem.getwDistance();
            if(distance <= 0){
                strDist = Double.toString(Math.round(distance * 1000));
                vh.distance.setText(strDist+"m");
            } else {
                vh.distance.setText(distance+"km");
            }

        }

        return v;
    }

    public class ViewHolder{
        private ImageView img;
        private TextView name, address, distance;
        private SearchItem mItem;
    }

    public void add(SearchItem item){
        this.mData.add(item);
    }
}
