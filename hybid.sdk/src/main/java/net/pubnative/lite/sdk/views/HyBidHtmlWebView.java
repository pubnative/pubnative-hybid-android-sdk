package net.pubnative.lite.sdk.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.pubnative.lite.sdk.mraid.internal.MRAIDLog;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.ViewGestureDetector;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HyBidHtmlWebView extends HyBidWebView implements ViewGestureDetector.UserClickListener {
    private static final String TAG = HyBidHtmlWebView.class.getSimpleName();

    public interface WebViewListener {
        void htmlViewLoaded(HyBidHtmlWebView view);

        void htmlViewOpenBrowser(String url);
    }

    private ViewGroup mContentInfo;
    private final ViewGestureDetector mViewGestureDetector;
    private final WebViewListener mListener;
    private boolean mClicked;

    public HyBidHtmlWebView(Context context) {
        this(context, null);
    }

    public HyBidHtmlWebView(Context context, WebViewListener listener) {
        super(context);

        mListener = listener;
        mViewGestureDetector = new ViewGestureDetector(context, this, new GestureDetector.SimpleOnGestureListener());
        mViewGestureDetector.setUserClickListener(this);

        setupWebView();
    }

    private void setupWebView() {
        disableScrollingAndZoom();
        getSettings().setJavaScriptEnabled(true);

        enablePlugins(true);
        setBackgroundColor(Color.TRANSPARENT);

        setWebViewClient(new HyBidHtmlWebViewClient());
        setWebChromeClient(new HyBidHtmlWebChromeClient());
    }

    public void init() {
        initializeOnTouchListener();
    }

    @Override
    public void loadUrl(final String url) {
        if (url == null) {
            return;
        }

        if (url.startsWith("javascript:")) {
            super.loadUrl(url);
            return;
        }

        Logger.d(TAG, "Loading url: " + url);
    }

    @Override
    public void stopLoading() {
        if (mIsDestroyed) {
            Logger.w(TAG, "#stopLoading() called after destroy()");
            return;
        }

        final WebSettings webSettings = getSettings();
        if (webSettings == null) {
            Logger.w(TAG, "#getSettings() returned null");
            return;
        }

        webSettings.setJavaScriptEnabled(false);
        super.stopLoading();
        webSettings.setJavaScriptEnabled(true);
    }

    private void disableScrollingAndZoom() {
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(false);
        setVerticalScrollBarEnabled(false);
        setVerticalScrollbarOverlay(false);
        getSettings().setSupportZoom(false);
    }

    public void loadHtml(String html) {
        loadDataWithBaseURL("", html,
                "text/html", "utf-8", null);
    }

    void initializeOnTouchListener() {
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mViewGestureDetector.sendTouchEvent(event);

                // We're not handling events if the current action is ACTION_MOVE
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    private void addContentInfo(View view) {
        if (mContentInfo != null) {
            ((ViewGroup) view).addView(mContentInfo);
        }
    }

    @Override
    public void onUserClick() {
        mClicked = true;
    }

    @Override
    public void onResetUserClick() {
        mClicked = false;
    }

    @Override
    public boolean wasClicked() {
        return mClicked;
    }

    /**************************************************************************
     * WebChromeClient and WebViewClient
     **************************************************************************/

    private class HyBidHtmlWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Logger.d(TAG, "onPageFinished: " + url);

            if (mListener != null) {
                mListener.htmlViewLoaded(HyBidHtmlWebView.this);
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onPageStarted");
        }

        public void onPageCommitVisible(WebView view, String url) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onPageCommitVisibile");
        }

        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedError code: " + error.getErrorCode());
            } else {
                Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedError: " + error);
            }
        }

        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedHttpError");
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedSslError");
        }

        public void onTooManyRedirects(WebView view, Message cancelMsg,
                                       Message continueMsg) {
            cancelMsg.sendToTarget();
            Logger.d(TAG, "HyBidHtmlWebViewClient - onTooManyRedirects");
        }

        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {

            Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedClientCertRequest");
        }

        public void onReceivedHttpAuthRequest(WebView view,
                                              HttpAuthHandler handler, String host, String realm) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedHttpAuthRequest");
            handler.cancel();
        }

        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {

            Logger.d(TAG, "HyBidHtmlWebViewClient - shouldOverrideKeyEvent");
            return false;
        }

        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onScaleChanged");
        }

        public void onReceivedLoginRequest(WebView view, String realm,
                                           String account, String args) {
            Logger.d(TAG, "HyBidHtmlWebViewClient - onReceivedLoginRequest");
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Logger.d(TAG, "onReceivedError: " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d(TAG, "shouldOverrideUrlLoading: " + url);

            try {
                open(URLEncoder.encode(url, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;

        }
    }

    private class HyBidHtmlWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            if (cm == null || cm.message() == null) {
                return false;
            }
            if (!cm.message().contains("Uncaught ReferenceError")) {
                Logger.d("JS console", cm.message()
                        + (cm.sourceId() == null ? "" : " at " + cm.sourceId())
                        + ":" + cm.lineNumber());
            }
            return true;
        }
    }

    private void open(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
            Logger.d(TAG + "-JS callback", "open " + url + " touched: " + wasClicked());
            if (!wasClicked()) {
                Logger.d(TAG + "- JS callback", "open called, but no touch recorded, aborting");
                return;
            }
            if (mListener != null) {
                mListener.htmlViewOpenBrowser(url);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
