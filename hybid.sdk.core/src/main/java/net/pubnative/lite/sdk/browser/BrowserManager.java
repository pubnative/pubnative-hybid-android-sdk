// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.browser;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class BrowserManager {
    private static final String TAG = BrowserManager.class.getSimpleName();

    private final List<String> mPriorityList;

    public BrowserManager() {
        this.mPriorityList = new ArrayList<>();
    }

    public void addBrowser(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            mPriorityList.add(packageName);
        }
    }

    public List<String> getPackagePriorities() {
        return mPriorityList;
    }

    public boolean containsPriorities() {
        return !mPriorityList.isEmpty();
    }

    public void cleanPriorities() {
        mPriorityList.clear();
    }
}
