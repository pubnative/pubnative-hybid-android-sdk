// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import net.pubnative.lite.sdk.utils.Logger;

public class BrowserPresenter {
    private static final String TAG = BrowserPresenter.class.getName();
    private final BrowserModel browserModel;
    private final UrlCreator urlCreator;
    private final ClipboardManager clipboardManager;
    private BrowserView browserView;

    private final BrowserModel.Callback browserModelCallback = new BrowserModel.Callback() {

        @Override
        public boolean shouldOverrideUrlLoading(String url) {
            BrowserPresenter.this.loadUrl(url);

            return true;
        }

        @Override
        public void onUrlLoadingStarted(String url) {
            updateHostnameAndSchemeControls(url);
        }

        @Override
        public void onPageNavigationStackChanged(boolean backNavigationEnabled, boolean forwardNavigationEnabled) {
            updateNavigationUiControls(backNavigationEnabled, forwardNavigationEnabled);
        }

        @Override
        public void onProgressChanged(int newProgress) {
            if (browserView == null) {
                return;
            }
            if (newProgress >= 0 && newProgress <= 100) {
                if (newProgress == 100) {
                    browserView.hideProgressIndicator();
                } else {
                    browserView.updateProgressIndicator(newProgress);
                    browserView.showProgressIndicator();
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onHttpError(WebResourceRequest request, WebResourceResponse errorResponse) {
            //no error handling for MVP1. Error logged in a model level
        }

        @Override
        public void onGeneralError(int errorCode, String description, String failingUrl) {
            //no error handling for MVP1. Error logged in a model level
        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void onRenderProcessGone() {
            if (browserView != null) {
                browserView.closeBrowser();
            }
        }
    };

    /*package*/ BrowserPresenter(BrowserModel browserModel,
                                 UrlCreator urlCreator,
                                 //LinkHandler linkhandler,
                                 ClipboardManager clipboardManager) {

        if (browserModel != null) {
            this.browserModel = browserModel;
        } else throw new NullPointerException("browserModel can not be null");

        if (urlCreator != null) {
            this.urlCreator = urlCreator;
        } else throw new NullPointerException("urlCreator can not be null");

        if (clipboardManager != null) {
            this.clipboardManager = clipboardManager;
        } else throw new NullPointerException("clipboardManager can not be null");

        browserModel.setBrowserModelCallback(browserModelCallback);
    }

    public void initWithView(BrowserView browserView, WebView webView) {
        if (browserView != null && webView != null) {
            this.browserView = browserView;
            browserModel.setWebView(webView);
        }
    }

    public void onResume() {
        browserModel.resume();
    }

    public void onPause() {
        browserModel.pause();
    }

    public void dropView() {
        browserView = null;
    }

    public void loadUrl(String url) {
        browserModel.load(url);
    }

    public void onReloadClicked() {
        browserModel.reload();
    }

    public void onPageNavigationBackClicked() {
        browserModel.goBack();
    }

    public void onPageNavigationForwardClicked() {
        browserModel.goForward();
    }

    public void onCopyHostnameClicked() {
        ClipData clip = ClipData.newPlainText(null, browserModel.getCurrentUrl());
        clipboardManager.setPrimaryClip(clip);

        Logger.d(TAG, "Link copied");
    }

    private void updateHostnameAndSchemeControls(String url) {
        if (browserView == null) {
            return;
        }

        String hostname = urlCreator.extractHostname(url);
        browserView.showHostname(hostname);

        String scheme = urlCreator.extractScheme(url);
        boolean connectionSecure = urlCreator.isSecureScheme(scheme);
        browserView.showConnectionSecure(connectionSecure);
    }

    private void updateNavigationUiControls(boolean backNavigationEnabled, boolean forwardNavigationEnabled) {
        if (browserView == null) {
            return;
        }

        browserView.setPageNavigationBackEnabled(backNavigationEnabled);
        browserView.setPageNavigationForwardEnabled(forwardNavigationEnabled);
    }
}

