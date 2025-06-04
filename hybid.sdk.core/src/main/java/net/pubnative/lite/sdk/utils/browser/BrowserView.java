// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.content.Intent;

public interface BrowserView {

    void showHostname(String hostname);

    void showConnectionSecure(boolean secure);

    void setPageNavigationBackEnabled(boolean enabled);

    void setPageNavigationForwardEnabled(boolean enabled);

    void launchExternalBrowser(Intent intent);

    void redirectToExternalApp(Intent intent);

    void showProgressIndicator();

    void hideProgressIndicator();

    void updateProgressIndicator(int progress);

    void closeBrowser();
}
