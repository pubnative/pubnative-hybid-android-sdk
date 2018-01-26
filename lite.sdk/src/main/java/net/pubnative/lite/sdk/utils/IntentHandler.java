package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class IntentHandler {
    private final Context context;

    public IntentHandler(Context context) {
        this.context = context;
    }

    public boolean canHandleIntent(Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        return !resolveInfos.isEmpty();
    }

    public boolean handleDeepLink(Uri uri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (canHandleIntent(intent)) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
