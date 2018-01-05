package net.pubnative.tarantula.sdk.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class ViewUtils {
    @NonNull
    private static final AtomicInteger nextGeneratedId = new AtomicInteger(1);

    public static void removeFromParent(@Nullable View view) {
        if (view == null || view.getParent() == null) {
            return;
        }

        if (view.getParent() instanceof ViewGroup) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = nextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) {
                    newValue = 1;
                }
                if (nextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }
}
