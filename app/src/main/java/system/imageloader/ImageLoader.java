package system.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.joseph.wheelchair.R;
import system.util.SharedPreference;

public class ImageLoader {

    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();
    private Context mContext;
    private boolean isResize;

    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        mContext = context;
    }

    final int stub_id = R.drawable.ic_loading;

    public void DisplayImage(String url, ImageView imageView, ScaleType scaleType) {
        imageViews.put(imageView, url);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        imageView.setImageResource(stub_id);
//        imageView.setBackgroundColor(Color.argb(68, 0, 0, 0));
        queuePhoto(url, imageView, scaleType);
        isResize = false;
    }

    public void DisplayImage(String url, ImageView imageView, ScaleType scaleType, boolean resize) {
        imageViews.put(imageView, url);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        imageView.setImageResource(stub_id);
//        imageView.setBackgroundColor(Color.argb(68, 0, 0, 0));
        queuePhoto(url, imageView, scaleType);
        isResize = resize;
    }

    private void queuePhoto(String url, ImageView imageView, ScaleType scaleType) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p, scaleType));
    }

    public void saveWebImage(String url) {
        try {
            File f = fileCache.getFile(url);

            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils util = new Utils();
            util.CopyStream(is, os);
            os.close();
            conn.disconnect();
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                System.gc();
        }
    }

    private Bitmap getBitmap(String url) {
        if (url.equals("null")) return null;
        File f = fileCache.getFile(url);
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils util = new Utils();
            util.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError) {
                System.gc();
            }
            return null;
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            int outOfMemoryCount = 0;
            while (outOfMemoryCount < 5) {
                try {
                    if (!isResize) {
                        FileInputStream realstream = new FileInputStream(f);
                        Bitmap bit = BitmapFactory.decodeStream(realstream,
                                null, options);
                        realstream.close();
                        return bit;
                    } else {
                        int realwidth = (int) (SharedPreference.getInstance(mContext).getRealwidth() * 0.8);
                        int realheight = (int) (SharedPreference.getInstance(mContext).getRealheight() * 0.8);
                        FileInputStream realstream = new FileInputStream(f);
                        Bitmap bit = BitmapFactory.decodeStream(realstream,
                                null, options);
                        realstream.close();
                        if (bit.getWidth() < realwidth || bit.getHeight() < realheight) {
                            if (bit.getWidth() > bit.getHeight()) {
                                int calheight = bit.getHeight() * realwidth / bit.getWidth();
                                return Bitmap.createScaledBitmap(bit, realwidth, calheight, true);
                            } else {
                                int calwidth = bit.getWidth() * realheight / bit.getHeight();
                                return Bitmap.createScaledBitmap(bit, calwidth, realheight, true);
                            }

                        } else {
                            return bit;
                        }
                    }
                } catch (OutOfMemoryError e) {
                    options.inSampleSize = options.inSampleSize + 1;
                    outOfMemoryCount++;
                    System.gc();
                } catch (Exception e) {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        ScaleType scaleType;

        PhotosLoader(PhotoToLoad photoToLoad, ScaleType scaleType) {
            this.photoToLoad = photoToLoad;
            this.scaleType = scaleType;
        }

        @Override
        public void run() {
            try {
                Bitmap bmp = getBitmap(photoToLoad.url);
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad, scaleType);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        ScaleType scaleType;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p, ScaleType scaleType) {
            bitmap = b;
            photoToLoad = p;
            this.scaleType = scaleType;
        }

        @Override
        public void run() {
            if (bitmap != null) {
                photoToLoad.imageView.setScaleType(scaleType);
                Animation fadein = AnimationUtils.loadAnimation(mContext, R.anim.caching_fade_in);
                fadein.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        photoToLoad.imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        photoToLoad.imageView.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                photoToLoad.imageView.startAnimation(fadein);

            } else {
                photoToLoad.imageView.setScaleType(ScaleType.CENTER_INSIDE);
                photoToLoad.imageView.setImageResource(stub_id);
//                photoToLoad.imageView.setBackgroundColor(Color.argb(68, 0, 0, 0));
            }
        }
    }

    public void deleteFile(String url) {
        fileCache.clearFile(url);
    }

    public void clearCache() {
        fileCache.clear();
    }
}
