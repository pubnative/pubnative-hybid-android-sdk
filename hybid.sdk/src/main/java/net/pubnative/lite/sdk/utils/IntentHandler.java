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
        final List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        return !resolveInfos.isEmpty();
    }

    public boolean handleDeepLink(Uri uri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (RuntimeException e) {
            Logger.e(TAG, e.getMessage());
            return false;
        }
    }

    public boolean handleBrowserLink(Uri uri) {
        if (HyBid.getBrowserManager().containsPriorities()) {
            Intent intent = null;

            List<String> priorities = HyBid.getBrowserManager().getPackagePriorities();
            Iterator<String> iterator = priorities.listIterator();

            do {
                String packageName = iterator.next();

                if (!TextUtils.isEmpty(packageName.trim())) {
                    Intent newIntent = new Intent(Intent.ACTION_VIEW);
                    newIntent.setData(uri);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    newIntent.setPackage(packageName);
                    if (canHandleIntent(newIntent)) {
                        intent = newIntent;
                    }
                }
            } while (iterator.hasNext() && intent == null);

            if (intent == null) {
                return handleDeepLink(uri);
            } else {
                try {
                    context.startActivity(intent);
                } catch (RuntimeException e) {
                    Logger.e(TAG, e.getMessage());
                    return false;
                }
                return true;
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

}
