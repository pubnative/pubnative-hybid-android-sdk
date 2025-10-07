// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.browser.BrowserActivity;

import java.util.Iterator;
import java.util.List;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class IntentHandler {
    private static final String TAG = IntentHandler.class.getSimpleName();
    private final Context context;

    public IntentHandler(Context context) {
        this.context = context;
    }

    public boolean canHandleIntent(Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> resolveInfos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            resolveInfos = packageManager.queryIntentActivities(
                    intent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY)
            );
        } else {
            resolveInfos = packageManager.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
        }
        return !resolveInfos.isEmpty();
    }

    public boolean canHandleIntent(Uri uri) {
        if (uri == null) {
            return false;
        }
        return canHandleIntent(createViewIntent(uri));
    }

    public boolean handleDeepLink(Uri uri) {
        return startActivitySafely(createViewIntent(uri));
    }

    public boolean handleBrowserLink(Uri uri) {
        if (HyBid.getBrowserManager().containsPriorities()) {
            Intent intent = getPriorityBrowserIntent(uri);
            if (intent == null) {
                return handleDeepLink(uri);
            } else {
                return startActivitySafely(intent);
            }
        } else {
            return handleDeepLink(uri);
        }
    }

    public boolean handleBrowserLinkBrowserActivity(Uri uri) {
        try {
            Intent intent = BrowserActivity.createIntent(context, uri.toString());
            context.startActivity(intent);
        } catch (RuntimeException e) {
            Logger.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    Intent createViewIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    Intent getPriorityBrowserIntent(Uri uri) {
        Intent intent = null;
        List<String> priorities = HyBid.getBrowserManager().getPackagePriorities();
        Iterator<String> iterator = priorities.listIterator();
        while (iterator.hasNext() && intent == null) {
            String packageName = iterator.next();
            if (!TextUtils.isEmpty(packageName.trim())) {
                Intent newIntent = createViewIntent(uri);
                newIntent.setPackage(packageName);
                if (canHandleIntent(newIntent)) {
                    intent = newIntent;
                }
            }
        }
        return intent;
    }

    boolean startActivitySafely(Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (RuntimeException e) {
            Logger.e(TAG, e.getMessage());
            return false;
        }
    }
}
