package kr.joseph.wheelchair.gps;

/**
 * Created by Joseph on 2016-05-30.
 */
public class DistanceTo {

    /**
     * 주어진 도(degree) 값을 라디언으로 변환
     */
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    /**
     * 주어진 라디언(radian) 값을 도(degree) 값으로 변환
     */
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }

    /**
     * 두점 사이의 거리 구하기
     */
    public double distance(double from_lat, double from_lon, double to_lat, double to_lon){
        double theta, dist;
        theta = from_lon - to_lon;

        dist = Math.sin(deg2rad(from_lat)) * Math.sin(deg2rad(to_lat)) + Math.cos(deg2rad(from_lat)) * Math.cos(deg2rad(to_lat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;

        return dist;
    }
}
