package kr.joseph.wheelchair.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import kr.joseph.wheelchair.R;
import kr.joseph.wheelchair.ServerAddress;
import kr.joseph.wheelchair.gps.GpsInfo;

/**
 * Created by Joseph on 2016-05-31.
 * 충전소 등록을 위한 ACTIVITY
 */
@SuppressWarnings("deprecation")
public class RegisterActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener, RadioGroup.OnCheckedChangeListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private final String API_KEY = "d83e84a17b2614d29f78a1b58d6ed409";
    private MapPoint mapPoint;
    private MapReverseGeoCoder reverseGeoCoder;

    LinearLayout layout;

    ImageView picture_img, imgOne, imgTwo, imgThree;
    int imgState = 0;
    int delImg = 0;
    Button back, cancel, register, camera;
    EditText ed_name, ed_location, ed_callnum, ed_day, ed_memo;
    RadioGroup rg;
    RadioButton rb1, rb2, rb3, rb4;
    TextView txt_time_from, txt_time_to;

    TimePickerDialog dialog;

    int state, radioSelect = 0;

    // 카메라 찍은 후 저장될 파일 경로
    private static String TAG = "RegisterActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_ALBUM = 2;

    // 카메라 찍은 후 저장될 파일 경로
    private String filePath; // 이미지 저장을 위한 이미지 전체 경로
    private String folderPath;
    private String folderName = "WheelChair";// 폴더명
    private String fileName; // DB에 저장될 파일명
    private String imgsName;
    private String[] imgsPath;
    ArrayList<String> imgNameArray = new ArrayList<>();
    ArrayList<String> imgPathArray = new ArrayList<>();

    ServerAddress sa;
    private final String SERVER_ADDRESS = sa.LOCATION_INSERT;
    private final String upLoadServerUri = sa.IMAGE_UPLOAD;
    String result_txt;
    ProgressDialog mDialog;

    String name = null, address = null, location = null, callnum = null, writeID = null;
    String day = null, openTime = null, closeTime = null, memo = null, imgName = "";
    int category;
    int addressGetCount = 0;

    GpsInfo gps;
    double myLatitude, myLongitude;
    String latitude, longitude;

    /**********  File 전송 Path *************/
    int serverResponseCode = 0;
    String uploadFilePath; // filePath 정보를 담아 서버로 이미지 전송에 쓰이는 변수
    int rotate = 0;
    String responseImgName;

    Bitmap bm = null;

    long cTime;
    Date date;
    SimpleDateFormat currentTime;

    Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDialog = new ProgressDialog(RegisterActivity.this);

        gps = new GpsInfo(RegisterActivity.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            myLatitude = gps.getLatitude();
            myLongitude = gps.getLongitude();
        } else {
            showSettingsAlert();
        }

        layout = (LinearLayout) findViewById(R.id.registerLayout);

        back = (Button) findViewById(R.id.register_back);
        cancel = (Button) findViewById(R.id.cancel);
        register = (Button) findViewById(R.id.register);
        camera = (Button) findViewById(R.id.bt_camera);

        picture_img = (ImageView) findViewById(R.id.picture_img);
        imgOne = (ImageView) findViewById(R.id.img_one);
        imgTwo = (ImageView) findViewById(R.id.img_two);
        imgThree = (ImageView) findViewById(R.id.img_three);

        ed_name = (EditText) findViewById(R.id.edit_name);
        ed_location = (EditText) findViewById(R.id.edit_location);
        ed_callnum = (EditText) findViewById(R.id.edit_callnum);
        ed_day = (EditText) findViewById(R.id.edit_day);
        ed_memo = (EditText) findViewById(R.id.edit_memo);

        txt_time_from = (TextView) findViewById(R.id.time_from);
        txt_time_to = (TextView) findViewById(R.id.time_to);

        rg = (RadioGroup) findViewById(R.id.rg);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);

        rg.setOnCheckedChangeListener(this);

        layout.setOnClickListener(this);

        back.setOnClickListener(this);
        cancel.setOnClickListener(this);
        register.setOnClickListener(this);
        camera.setOnClickListener(this);

        imgOne.setOnClickListener(this);
        imgTwo.setOnClickListener(this);
        imgThree.setOnClickListener(this);

        txt_time_from.setOnClickListener(this);
        txt_time_to.setOnClickListener(this);

        ed_name.setOnFocusChangeListener(this);
        ed_location.setOnFocusChangeListener(this);
        ed_callnum.setOnFocusChangeListener(this);
        ed_day.setOnFocusChangeListener(this);
        ed_memo.setOnFocusChangeListener(this);

        ed_day.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_day.getWindowToken(), 0);
                    ed_day.clearFocus();
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.registerLayout:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ed_location.getWindowToken(), 0);
                ed_name.clearFocus();
                ed_location.clearFocus();
                ed_callnum.clearFocus();
                ed_day.clearFocus();
                ed_memo.clearFocus();
                break;
            case R.id.register_back:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                break;
            case R.id.cancel:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                break;
            case R.id.register:
                myLatitude = gps.getLatitude();
                myLongitude = gps.getLongitude();
                latitude = Double.toString(myLatitude);
                longitude = Double.toString(myLongitude);

                Log.e("Register Button Click", latitude+", "+longitude);
                registerData();
                break;
            case R.id.bt_camera:

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doTakePhoto();
                    }
                };
//                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        doTakeAlbum();
//                    }
//                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("최대 3개까지 등록가능\n가로를 길게 촬영하여주세요.")
                        .setPositiveButton("사진촬영", cameraListener)
//                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
                break;
            case R.id.time_from:
                state = 0;
                dialog = new TimePickerDialog(this, listener, 12, 00, true);
                dialog.show();
                break;
            case R.id.time_to:
                state = 1;
                dialog = new TimePickerDialog(this, listener, 12, 00, true);
                dialog.show();
                break;
            case R.id.img_one:
                if(imgNameArray.size() == 0){
                    break;
                } else {
                    delImg = 1;
                    deleteImgDialog();
                    break;
                }
            case R.id.img_two:
                if(imgNameArray.size() == 0 || imgNameArray.size() == 1){
                    break;
                } else {
                    delImg = 2;
                    deleteImgDialog();
                    break;
                }
            case R.id.img_three:
                if(imgNameArray.size() == 0 || imgNameArray.size() == 1 || imgNameArray.size() == 2){
                    break;
                } else {
                    delImg = 3;
                    deleteImgDialog();
                    break;
                }
        }
    }

    public void deleteImgDialog(){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(RegisterActivity.this);
        alert_confirm.setMessage("선택한 이미지를 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        deleteImg();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }
    public void deleteImg(){
        if(delImg == 1){
            imgOne.setImageBitmap(null);
            imgNameArray.remove(0);
            imgPathArray.remove(0);
            imgState = 0;
        } else if(delImg == 2){
            imgTwo.setImageBitmap(null);
            imgNameArray.remove(1);
            imgPathArray.remove(1);
            imgState = 1;
        } else if(delImg == 3){
            imgThree.setImageBitmap(null);
            imgNameArray.remove(2);
            imgPathArray.remove(2);
            imgState = 2;
        }
    }

    /**
     * 앨범에서 이미지 불러옴
     */
//    public void doTakeAlbum(){
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
//    }

    /**
     * 카메라를 이용하여 사진촬영
     */

    public void doTakePhoto(){
        Intent intent = new Intent();
        mCamera = Camera.open();
    	Camera.Parameters parameters = mCamera.getParameters();
    	List<Size> sizeList = parameters.getSupportedPictureSizes();
        // 원하는 최적화 사이즈를 1280x720 으로 설정
    	Size size =  getOptimalPictureSize(parameters.getSupportedPictureSizes(), 1280, 720);
    	Log.d(TAG, "Selected Optimal Size : (" + size.width + ", " + size.height + ")");
    	parameters.setPreviewSize(size.width,  size.height);
    	parameters.setPictureSize(size.width,  size.height);
        mCamera.setParameters(parameters);
        mCamera.release();

    	// 저장할 파일 설정
    	// 외부저장소 경로
    	String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 오늘 날짜와 시간으로 파일 이름 설정
        cTime = System.currentTimeMillis();
        date = new Date(cTime);
        currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        fileName = currentTime.format(date);
        imgName = fileName+".jpg";

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            File f = new File("folderPATH", "fileName");
//            Uri contentUri = Uri.fromFile(f);
//            mediaScanIntent.setData(contentUri);
//            appContext.sendBroadcast(mediaScanIntent);
//        } else {
//            appContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/" + "FOLDER_TO_REFRESH")));
//        }

//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        } else {
//            intent = new Intent();
//            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        }

    	// 폴더명 및 파일명
        folderPath = path + File.separator + folderName;
        filePath = path + File.separator + folderName + File.separator +  fileName + ".jpg";

        uploadFilePath = filePath;
        Log.e("uploadFilePath", uploadFilePath);

    	// 저장 폴더 지정 및 폴더 생성 및 저장
//    	File fileFolderPath = new File(folderPath);
//    	fileFolderPath.mkdir();

    	// 파일 이름 지정
    	File file = new File(filePath);

        // 폴더 작동유무 확인
        File tmpPath = new File(folderPath);
        if(!(tmpPath.exists() && tmpPath.isDirectory())){
            tmpPath.mkdir();
        }

    	Uri outputFileUri = Uri.fromFile(file);


        // 카메라 작동시키는 Action으로 인텐트 설정, OutputFileURI 추가
    	intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
    	// requestCode지정해서 인텐트 실행
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * 사진촬영 및 갤러리를 이용하여 사진을 가져온 뒤 이루어지는 작업
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                /*case REQUEST_IMAGE_ALBUM:
                    try {
                        //갤러리에서 불러옴
                        Uri selectImg = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor imageCursor = this.getContentResolver().query(selectImg, filePathColumn, null, null, null);
                        imageCursor.moveToFirst();
                        int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
                        String path = imageCursor.getString(columnIndex);
                        String name = path.substring(path.lastIndexOf("/")+1);
                        imgName = name;

                        uploadFilePath = path;
                        Log.e("uploadFilePath", uploadFilePath);

                        imageCursor.close();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        bm = BitmapFactory.decodeFile(path, options);
                        //불러온 이미지 회전
                        ExifInterface exif = new ExifInterface(path);
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        bm = rotate(bm, exifDegree);
                        picture_img.setScaleType(ImageView.ScaleType.FIT_XY);
                        picture_img.setImageBitmap(bm);

//                        byteArray = mBitmapTrans.bitmapToByteArray(bm2);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;*/

                case REQUEST_IMAGE_CAPTURE:
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inSampleSize = 4;
                        bm = BitmapFactory.decodeFile(filePath, options);

                        ExifInterface exif = new ExifInterface(filePath);
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        bm = rotate(bm, exifDegree);
                        picture_img.setScaleType(ImageView.ScaleType.FIT_XY);
                        picture_img.setImageBitmap(bm);

                        if(imgState == 0 || imgState == 3){
                            imgOne.setImageBitmap(bm);
                            imgState = 1;
                        } else if(imgState == 1){
                            imgTwo.setImageBitmap(bm);
                            imgState = 2;
                        } else {
                            imgThree.setImageBitmap(bm);
                            imgState = 3;
                        }
                        Log.e("Image State", Integer.toString(imgState));

                        imgNameArray.add(imgName);
                        imgPathArray.add(filePath);
                        Log.e("Name Array", imgNameArray.toString());
                        Log.e("Path Array", imgPathArray.toString());
//                        byteArray = mBitmapTrans.bitmapToByteArray(bm);
//                        Log.e("CAPTURE ByteArray", byteArray);

                        // File 저장 예제
                        try {
                        	// 외부저장소 경로
                        	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            // 폴더명 및 파일명
                        	folderName = "WheelChair";// 폴더명
//                        	fileName = Long.toString(System.currentTimeMillis()); // 파일명
                            // 폴더 경로 및 파일 경로
                        	folderPath = path + "/" + folderName;
                        	filePath = folderPath + "/" + fileName + ".jpg";

                            // 파일 이름 지정
                        	File file = new File(filePath);
                        	FileOutputStream fos = new FileOutputStream(file);
                            // 비트맵을 PNG방식으로 압축하여 저장
                        	if (fos != null){
                        		bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        		fos.close();
                        	}
                            // 로그 및 토스트
                        	String logMessage = "File Save Success, File : " + filePath;
//                        	Toast.makeText(getApplicationContext(), logMessage, Toast.LENGTH_LONG).show();
                        	Log.d(TAG, logMessage);
                        } catch (Exception e)	{
                        	e.printStackTrace();
                        	Log.d(TAG, "File Save Failed");
                        }

                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * 이미지가 회전되어 있는지 확인
     * @param exifOrientation
     * @return
     */
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotate = 90;
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotate = 180;
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotate = 270;
            return 270;
        } else {
            rotate = 0;
            return 0;
        }
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
     * 지정한 해상도에 가장 최적화 된 카메라 캡쳐 사이즈 구해주는 함수
     * @param sizeList
     * @param width
     * @param height
     * @return
     */
    private Size getOptimalPictureSize(List<Size> sizeList, int width, int height) {
        Log.d(TAG, "getOptimalPictureSize, 기준 width,height : (" + width + ", " + height + ")");
        Size prevSize = sizeList.get(0);
        Size optSize = sizeList.get(1);
        for (Size size : sizeList) {
            // 현재 사이즈와 원하는 사이즈의 차이
            int diffWidth = Math.abs((size.width - width));
            int diffHeight = Math.abs((size.height - height));
            // 이전 사이즈와 원하는 사이즈의 차이
            int diffWidthPrev = Math.abs((prevSize.width - width));
            int diffHeightPrev = Math.abs((prevSize.height - height));
            // 현재까지 최적화 사이즈와 원하는 사이즈의 차이
            int diffWidthOpt = Math.abs((optSize.width - width));
            int diffHeightOpt = Math.abs((optSize.height - height));
            // 이전 사이즈보다 현재 사이즈의 가로사이즈 차이가 적을 경우 && 현재까지 최적화 된 세로높이 차이보다 현재 세로높이 차이가 적거나 같을 경우에만 적용
            if (diffWidth < diffWidthPrev && diffHeight <= diffHeightOpt) {
                optSize = size;
                Log.d(TAG, "가로사이즈 변경 / 기존 가로사이즈 : " + prevSize.width + ", 새 가로사이즈 : " + optSize.width);
            }
            // 이전 사이즈보다 현재 사이즈의 세로사이즈 차이가 적을 경우 && 현재까지 최적화 된 가로길이 차이보다 현재 가로길이 차이가 적거나 같을 경우에만 적용
            if (diffHeight < diffHeightPrev && diffWidth <= diffWidthOpt) {
                optSize = size;
                Log.d(TAG, "세로사이즈 변경 / 기존 세로사이즈 : " + prevSize.height + ", 새 세로사이즈 : " + optSize.height);
            }
            // 현재까지 사용한 사이즈를 이전 사이즈로 지정
            prevSize = size;
        }
        Log.d(TAG, "결과 OptimalPictureSize : " + optSize.width + ", " + optSize.height);
        return optSize;
    }

    /**
     * Timepicker 사용
     */
    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
            String hour, min;
            if(hourOfDay < 10){
                hour = "0"+hourOfDay;
            } else {
                hour = Integer.toString(hourOfDay);
            }

            if(minute < 10){
                min = "0"+minute;
            } else {
                min = Integer.toString(minute);
            }

            if(state == 0){
                txt_time_from.setText(hour + " : " + min);
            } else {
                txt_time_to.setText(hour + " : " + min);
            }
//            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };

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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.edit_name:
                if(hasFocus){
                    ed_name.setBackgroundResource(R.drawable.xml_border_underline_focus);
                } else {
                    ed_name.setBackgroundResource(R.drawable.xml_border_underline);
                }
                break;
            case R.id.edit_location:
                if(hasFocus){
                    ed_location.setBackgroundResource(R.drawable.xml_border_underline_focus);
                } else {
                    ed_location.setBackgroundResource(R.drawable.xml_border_underline);
                }
                break;
            case R.id.edit_day:
                if(hasFocus){
                    ed_day.setBackgroundResource(R.drawable.xml_border_underline_focus);
                } else {
                    ed_day.setBackgroundResource(R.drawable.xml_border_underline);
                }
                break;
            case R.id.edit_memo:
                if(hasFocus){
                    ed_memo.setBackgroundResource(R.drawable.xml_border_box_focus);
                } else {
                    ed_memo.setBackgroundResource(R.drawable.xml_border_box);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb1:
                radioSelect = 1;
//                Toast.makeText(this, "RadioButton : 행정기관", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb2:
                radioSelect = 2;
//                Toast.makeText(this, "RadioButton : 복지관", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb3:
                radioSelect = 3;
//                Toast.makeText(this, "RadioButton : 지하철", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rb4:
                radioSelect = 4;
//                Toast.makeText(this, "RadioButton : 기타", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void registerData(){

        name = ed_name.getText().toString();
        location = ed_location.getText().toString();
        callnum = ed_callnum.getText().toString();
        day = ed_day.getText().toString();
        openTime = txt_time_from.getText().toString();
        closeTime = txt_time_to.getText().toString();
        memo = ed_memo.getText().toString();

        writeID = "010-1234-5678";

        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if (!tMgr.equals(null)) writeID =tMgr.getLine1Number();

        category = radioSelect; // 카테고리

        // 좌표를 이용하여 주소 찾기 //
        mapPoint = MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude);
        reverseGeoCoder = new MapReverseGeoCoder(API_KEY, mapPoint, RegisterActivity.this, RegisterActivity.this);
        reverseGeoCoder.startFindingAddress(); // 주소 추출 후 기록되도록 하기 위해 onReverseGeoCoderFoundAddress에서 처리


        Log.e("Register Data", "Lat,Lon : "+latitude+", "+longitude+"\nname : "+name+"\n"+"address : "+address+"\n"+"location : "+location+"\n"+"day : "+day+"\n"+"writeID : "+writeID+"\n"+"opentime : "+openTime+"\n"+"closetime : "+closeTime+"\n"+"memo : "+memo
        +"\nimgName : "+imgName);
        if(!imgNameArray.isEmpty()){
            imgsName = "";
            for(String s : imgNameArray){
                imgsName += s + ",";
            }
        }

        // 지연시간 만들기 -> 좌표로 주소를 받아오는 부분이 쓰레드로 작동해서 주소를 받아오기전에 저장작업이 실행됨
    }

    /**
     * Daum Map : 좌표를 이용하여 주소찾기
     * @param mapReverseGeoCoder
     * @param addressString
     */
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String addressString) {
        // 주소를 찾은 경우.
//        Log.d("좌표로 주소호출 성공 : ", addressString);
        address = addressString; // 주소 넣기

        // 좌표에서 주소가 추출되었을때만 기록되도록 처리 하기 위해 최종 확인 부분
        if(name.equals(null) || name.equals("")){
            Toast.makeText(this, "충전소 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (location.equals(null) || location.equals("")){
            Toast.makeText(this, "충전소 상세 장소를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (radioSelect == 0){
            Toast.makeText(this, "건물유형을 선택해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!gps.isGetLocation()){
            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show();
        } else {
            // 데이터베이스에 입력 //
            UploadData task = new UploadData();
            task.execute(new String[]{upLoadServerUri});
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("saved", "OK");
            startActivity(intent);
        }
    }
    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        // 호출에 실패한 경우.
        Log.d(TAG, "좌표로 주소호출 실패");
        Toast.makeText(this, "GPS정보로 충전소 주소 확인이 불가능합니다.\n 잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
        if (addressGetCount > 2) {
            // 데이터베이스에 입력 //
            address = "주소입력오류";
            UploadData task = new UploadData();
            task.execute(new String[]{upLoadServerUri});
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("saved", "OK");
            startActivity(intent);
        } else {
            addressGetCount ++;
        }
    }

    // 이미지 서버에 저장 //
    private class  UploadData extends AsyncTask<String, Void, Boolean> {

//        ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);

        protected void onPreExecute() {
            mDialog.setMessage("Sending Data.... ");
            mDialog.show();   ////우선수행
        }
            @Override
            protected Boolean doInBackground(String... urls) {

                String url = urls[0];
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
            int maxBufferSize = 1 * 1024 * 720;
            try{
                for(int i = 0; i < imgPathArray.size(); i++){
                    uploadFilePath = imgPathArray.get(i);

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(uploadFilePath);
                    URL url1 = new URL(url);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url1.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", uploadFilePath);
                    Log.e("uploadFilePath", uploadFilePath);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + uploadFilePath + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.e("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    if(serverResponseMessage.equals("OK")){
                        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                        BufferedReader rd = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String response = rd.readLine();
                        responseImgName = response;
                        Log.e("Response Register", response);
                    }
                }


            } catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }
        protected void onPostExecute(Boolean result) {
            if(result == true) {
                InsertData task1 = new InsertData();
                task1.execute(new String[]{SERVER_ADDRESS});
            }else{
//                Toast.makeText(RegisterActivity.this, "Error",Toast.LENGTH_LONG).show();
            }
        }
    }

    // 데이터 입력 //
    private class InsertData extends AsyncTask<String, Void, Boolean> {

//        ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
//
//        protected void onPreExecute() {
//            mDialog.setMessage("Sending Data.... ");
//            mDialog.show();   ////우선수행
//        }
        @Override
        protected Boolean doInBackground(String... urls) {

            String url = urls[0];
            try {
                ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("wc_name", name));
                pairs.add(new BasicNameValuePair("wc_address", address));
                pairs.add(new BasicNameValuePair("wc_location", location));

                if (!callnum.equals(null) || !callnum.equals("")) {
                    pairs.add(new BasicNameValuePair("wc_callnum", callnum));
                } else {
                    pairs.add(new BasicNameValuePair("wc_callnum", "0000000000"));
                }

                if (!writeID.equals(null) || !writeID.equals("")) {
                    pairs.add(new BasicNameValuePair("wc_writeID", writeID));
                } else {
                    pairs.add(new BasicNameValuePair("wc_writeID", "0000000000"));
                }
                pairs.add(new BasicNameValuePair("wc_latitude", latitude));
                pairs.add(new BasicNameValuePair("wc_longitude", longitude));
                pairs.add(new BasicNameValuePair("wc_category", Integer.toString(category)));
                if (!day.equals(null) || !day.equals("")) {
                    pairs.add(new BasicNameValuePair("wc_day", day));
                } else {
                    pairs.add(new BasicNameValuePair("wc_day", ""));
                }
                if (!openTime.equals(null) || !openTime.equals("")) {
                    pairs.add(new BasicNameValuePair("wc_open_time", openTime));
                } else {
                    pairs.add(new BasicNameValuePair("wc_open_time", "00 : 00"));
                }
                if (!closeTime.equals(null) || !closeTime.equals("")) {
                    pairs.add(new BasicNameValuePair("wc_close_time", closeTime));
                } else {
                    pairs.add(new BasicNameValuePair("wc_close_time", "24 : 00"));
                }
                if (!memo.equals(null) || !memo.equals("")) {
                    pairs.add(new BasicNameValuePair("wc_memo", memo));
                } else {
                    pairs.add(new BasicNameValuePair("wc_memo", ""));
                }
//                if (!responseImgName.equals(null) || !responseImgName.equals("")
// ) {
//                    pairs.add(new BasicNameValuePair("wc_image", responseImgName));
//                }
                if (!imgNameArray.isEmpty()) {
                    if(imgState == 1){
                        pairs.add(new BasicNameValuePair("wc_image", imgNameArray.get(0)));
                        pairs.add(new BasicNameValuePair("wc_image2", ""));
                        pairs.add(new BasicNameValuePair("wc_image3", ""));
                    } else if (imgState == 2){
                        pairs.add(new BasicNameValuePair("wc_image", imgNameArray.get(0)));
                        pairs.add(new BasicNameValuePair("wc_image2", imgNameArray.get(1)));
                        pairs.add(new BasicNameValuePair("wc_image3", ""));
                    } else if (imgState == 3){
                        pairs.add(new BasicNameValuePair("wc_image", imgNameArray.get(0)));
                        pairs.add(new BasicNameValuePair("wc_image2", imgNameArray.get(1)));
                        pairs.add(new BasicNameValuePair("wc_image3", imgNameArray.get(2)));
                    }
                } else {
                    pairs.add(new BasicNameValuePair("wc_image", ""));
                    pairs.add(new BasicNameValuePair("wc_image2", ""));
                    pairs.add(new BasicNameValuePair("wc_image3", ""));
                }
//                pairs.add(new BasicNameValuePair("wc_img_rotate", Integer.toString(rotate)));
                pairs.add(new BasicNameValuePair("wc_img_rotate", Integer.toString(0)));
                ////// 입력하는 요소를 namevaluepair 형식으로 만들어 전송한다.

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);
                post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
                HttpResponse response = client.execute(post);

                HttpEntity ent = response.getEntity();
                result_txt = EntityUtils.toString(ent);
                Log.e("result_txt", result_txt);

            } catch (ClientProtocolException e) {
//                Toast.makeText(RegisterActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
//                Toast.makeText(RegisterActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if(result == true) {

//                Intent intent = new Intent(RegisterActivity.this, SearchActivity.class);
//                intent.putExtra("saved", "OK");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                finish();

              Log.e("1_Insert Register Data", "name : "+name+"\n"+"address : "+address+"\n"+"location : "+location+"\n"+"day : "+day+"\n"+"writeID : "+writeID+"\n"+"opentime : "+openTime+"\n"+"closetime : "+closeTime+"\n"+"memo : "+memo
                        +"\nimgName : "+imgsName);
                Log.e("RegisterActivity", "Insert Success");
            }else{
//                Toast.makeText(RegisterActivity.this, "Error",Toast.LENGTH_LONG).show();
            }

            if(mDialog != null){
                if(mDialog.isShowing()){
                    mDialog.dismiss();
                }
            }
        }
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
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


}
