package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

/**
 * Created by erosgarciaponte on 14.02.18.
 */

public class PNPermissionUtil {
    public static boolean hasPermission(Context context, String permission) {
        return checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static int checkSelfPermission(Context context, String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
    }
}
