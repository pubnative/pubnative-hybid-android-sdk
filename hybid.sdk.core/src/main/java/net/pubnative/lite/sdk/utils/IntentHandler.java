// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;

import java.util.Iterator;
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (canHandleIntent(intent)) {
            context.startActivity(intent);
            return true;
        }
        return false;
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
                context.startActivity(intent);
                return true;
            }
        } else {
            return handleDeepLink(uri);
        }
    }
}
