package system.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapLoader {
    private static BitmapLoader _instance = null;
    private Context context = null;

    public static BitmapLoader getInstance(Context context) {
        if (_instance == null)
            _instance = new BitmapLoader(context);
        return _instance;
    }

    private BitmapLoader(Context context) {
        this.context = context;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(int id, int reqWidth,
                                                  int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), id, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;

        int outOfMemoryCount = 0;
        while (outOfMemoryCount < 5) {
            try {
                return BitmapFactory.decodeResource(context.getResources(), id, options);
            } catch (OutOfMemoryError e) {
                System.gc();
                options.inSampleSize = options.inSampleSize + 1;
                outOfMemoryCount++;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public Bitmap decodeSampledBitmapFromStorage(String path, int reqWidth,
                                                 int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;

        int outOfMemoryCount = 0;
        while (outOfMemoryCount < 5) {
            try {
                return BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e) {
                System.gc();
                options.inSampleSize = options.inSampleSize + 1;
                outOfMemoryCount++;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}


