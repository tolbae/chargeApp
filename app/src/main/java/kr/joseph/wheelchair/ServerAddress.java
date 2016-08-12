package kr.joseph.wheelchair;

/**
 * Created by Joseph on 2016-06-02.
 * 서버 연결에 필요한 주소들
 */
public class ServerAddress {

    /**
     * 로컬 연결
     */
//    public static final String MAIN_ADDRESS = "http://192.168.0.29/wheelchair";
//    public static final String LOAD_DATA = "/location_data_load.php";                       // 데이터 로드 //
//    public static final String LOCATION_INSERT = MAIN_ADDRESS+"/location_data_insert.php";  // 입력한 데이터를 DB로 보냄 //
//    public static final String IMAGE_UPLOAD = MAIN_ADDRESS+"/image_upload.php";             // 이미지 서버로 보냄 //
//    public static final String IMAGE_FOLDER = MAIN_ADDRESS+"/image/";                       // 서버 이미지 저장 경로 //

    /**
     * 서버 연결
     */
    public static final String MAIN_ADDRESS = "charge.peoplestudio.kr";
    public static final String LOAD_DATA = "/getLoc.php";                                         // 데이터 로드 //
    public static final String LOCATION_INSERT = "http://"+MAIN_ADDRESS+"/askLoc.php";            // 입력한 데이터를 DB로 보냄 //
    public static final String IMAGE_UPLOAD = "http://"+MAIN_ADDRESS+"/askImage.php";             // 이미지 서버로 보냄 //
    public static final String IMAGE_FOLDER = "http://"+MAIN_ADDRESS+"/image/";                   // 서버 이미지 저장 경로 //
}
