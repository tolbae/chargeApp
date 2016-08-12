package kr.joseph.wheelchair.adapter;

/**
 * Created by Joseph on 2016-05-25.
 */
public class SearchItem {

    private String wImg;
    private String wImg2;
    private String wImg3;
    private int wRotate;
    private String wName;
    private String wAddress;
    private String wLocation;
    private String wCallnum;
    private double wLatitude;
    private double wLongitude;
    private int wCategory;
    private String wDay;
    private String wOpenTime;
    private String wCloseTime;
    private String wMemo;
    private double wDistance;

    public SearchItem(String img, String img2, String img3, int rotate, String name, String address, String location, String callnum, double latitude, double longitude, int category, String day,
                      String openTime, String closeTime, String memo, double distance){
        this.wImg = img;
        this.wImg2 = img2;
        this.wImg3 = img3;
        this.wRotate = rotate;
        this.wName = name;
        this.wAddress = address;
        this.wLocation = location;
        this.wCallnum = callnum;
        this.wLatitude = latitude;
        this.wLongitude = longitude;
        this.wCategory = category;
        this.wDay = day;
        this.wOpenTime = openTime;
        this.wCloseTime = closeTime;
        this.wMemo = memo;
        this.wDistance = distance;
    }

    public String getwImg(){
        return wImg;
    }
    public String getwImg2(){
        return wImg2;
    }
    public String getwImg3(){
        return wImg3;
    }
    public int getwRotate(){
        return wRotate;
    };
    public String getwName(){
        return wName;
    }
    public String getwAddress(){
        return wAddress;
    }
    public String getwLocation(){
        return wLocation;
    }
    public String getwCallnum(){
        return wCallnum;
    }
    public double getwLatitude(){
        return wLatitude;
    }
    public double getwLongitude(){
        return wLongitude;
    }
    public int getwCategory(){
        return wCategory;
    }
    public String getwDay(){
        return wDay;
    }
    public String getwOpenTime(){
        return wOpenTime;
    }
    public String getwCloseTime(){
        return wCloseTime;
    }
    public String getwMemo(){
        return wMemo;
    }
    public double getwDistance(){
        return wDistance;
    }

}
