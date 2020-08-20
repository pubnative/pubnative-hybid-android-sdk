package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class ScreenDimensionsUtils {

    /**
     * This method must be called to store deviceHeight and deviceWidth in Point
     */

    public Point getScreenDimensionsToPoint (Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

}
