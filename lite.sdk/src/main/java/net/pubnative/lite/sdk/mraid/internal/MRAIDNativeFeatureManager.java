package net.pubnative.lite.sdk.mraid.internal;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;

import java.util.ArrayList;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class MRAIDNativeFeatureManager {
    private final static String TAG = MRAIDNativeFeatureManager.class.getSimpleName();

    private Context context;
    private ArrayList<String> supportedNativeFeatures;

    public MRAIDNativeFeatureManager(Context context, ArrayList<String> supportedNativeFeatures) {
        this.context = context;
        this.supportedNativeFeatures = supportedNativeFeatures;
    }

    public boolean isCalendarSupported() {
        boolean retval =
                supportedNativeFeatures.contains(MRAIDNativeFeature.CALENDAR) &&
                        PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.WRITE_CALENDAR);
        MRAIDLog.d(TAG, "isCalendarSupported " + retval);
        return retval;
    }

    public boolean isInlineVideoSupported() {
        // all Android 2.2+ devices should serve HTML5 video
        boolean retval = supportedNativeFeatures.contains(MRAIDNativeFeature.INLINE_VIDEO);
        MRAIDLog.d(TAG, "isInlineVideoSupported " + retval);
        return retval;
    }

    public boolean isSmsSupported() {
        boolean retval =
                supportedNativeFeatures.contains(MRAIDNativeFeature.SMS) &&
                        PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.SEND_SMS);
        MRAIDLog.d(TAG, "isSmsSupported " + retval);
        return retval;
    }

    public boolean isStorePictureSupported() {
        boolean retval = supportedNativeFeatures.contains(MRAIDNativeFeature.STORE_PICTURE);
        MRAIDLog.d(TAG, "isStorePictureSupported " + retval);
        return retval;
    }

    public boolean isTelSupported() {
        boolean retval =
                supportedNativeFeatures.contains(MRAIDNativeFeature.TEL) &&
                        PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE);
        MRAIDLog.d(TAG, "isTelSupported " + retval);
        return retval;
    }

    public ArrayList<String> getSupportedNativeFeatures() {
        return supportedNativeFeatures;
    }
}
