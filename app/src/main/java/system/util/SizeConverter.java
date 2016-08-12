package system.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Tori on 14. 2. 10.
 */

public class SizeConverter {
    private static SizeConverter _instance = null;
    private static final int MODE_PRIVATE = 0;
    private Context context = null;

    public static SizeConverter getInstance(Context context) {
        if (_instance == null)
            _instance = new SizeConverter(context);
        return _instance;
    }

    private SizeConverter(Context context) {
        this.context = context;
    }

    public float convertDpToPixel(float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public float convertPixelsToDp(float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
}

