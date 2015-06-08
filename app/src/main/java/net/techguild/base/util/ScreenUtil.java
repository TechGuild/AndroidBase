package net.techguild.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

// Singleton class for screen utilities
public class ScreenUtil {
    private static ScreenUtil screenUtil;
    private int contentViewTop;
    private int width;
    private int height;

    private ScreenUtil() {
        width = 0;
        height = 0;
        contentViewTop = 0;
    }

    public static ScreenUtil getInstance() {
        if (null == screenUtil) {
            screenUtil = new ScreenUtil();
        }
        return screenUtil;
    }

    public static int convertToPixel(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float logicalDensity = metrics.density;
        return (int) Math.ceil(dp * logicalDensity);
    }

    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static int getWidth(Context context) {
        ScreenUtil screenUtil = getInstance();
        if (screenUtil.width == 0) {
            Point size = new Point();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
            screenUtil.width = size.x;
        }
        return screenUtil.width;
    }

    public static int getHeight(Context context) {
        ScreenUtil screenUtil = getInstance();
        if (screenUtil.height == 0) {
            Point size = new Point();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
            screenUtil.height = size.y;
        }
        return screenUtil.height;
    }

    public static int getContentViewTop(Activity activity) {
        ScreenUtil screenUtil = getInstance();
        if (screenUtil.contentViewTop == 0) {
            screenUtil.contentViewTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        }
        return screenUtil.contentViewTop;
    }
}
