package net.pubnative.tarantula.sdk.mraid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.pubnative.tarantula.sdk.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class MRAIDBrowser extends Activity {
    private static final String TAG = MRAIDBrowser.class.getSimpleName();

    public static final String URL_EXTRA = "extra_url";
    public static final String MANAGER_EXTRA = "extra_manager";

    private RelativeLayout rootLayout;

    private WebView webView;

    private ImageButton backButton;
    private ImageButton forwardButton;
    private ImageButton refreshButton;
    private ImageButton closeButton;

    private ArrayList<String> supportedNativeFeatures;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        if (getIntent().getExtras() != null) {
            supportedNativeFeatures = (ArrayList<String>) getIntent().getExtras().getSerializable(MANAGER_EXTRA);
        }

        createUi();
        setButtonListeners();
        setContentView(rootLayout);

        Intent intent = getIntent();
        initializeWebView(intent);
        enableCookies();
    }

    @SuppressWarnings("deprecation")
    private void createUi() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        float density = metrics.density;
        int densityDpi = metrics.densityDpi;
        String msg = "screen " + screenWidth + "x" + screenHeight + ", density=" + density
                + ", densityDpi=" + density + " (";
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                msg += "DENSITY_LOW)";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                msg += "DENSITY_MEDIUM)";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                msg += "DENSITY_HIGH)";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                msg += "DENSITY_XHIGH)";
                break;
        }

        Log.d(TAG, msg);

        RelativeLayout.LayoutParams params;

        rootLayout = new RelativeLayout(this);
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(params);
        rootLayout.setPadding(0, 0, 0, 0);
        rootLayout.setBackgroundColor(Color.RED);

        LinearLayout buttonLayout = new LinearLayout(this);
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLayout.setLayoutParams(params);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 0, 0, 0);
        Drawable backgroundDrawable = Assets.getDrawableFromBase64(getResources(), Assets.bkgrnd);
        buttonLayout.setBackgroundDrawable(backgroundDrawable);
        buttonLayout.setId(ViewUtils.generateViewId());

        int buttonWidth = screenWidth >>> 2;
        int buttonHeight = Math.min(buttonWidth >>> 1, screenHeight / 10);
        Log.d(TAG, "button size " + buttonWidth + "x" + buttonHeight + " min(" + buttonWidth / 2 + "," + screenHeight / 10 + ")");
        int padding = buttonHeight >>> 3;
        Log.d(TAG, "padding " + padding);

        backButton = createButton(buttonWidth, buttonHeight, padding, Assets.unleftarrow);
        forwardButton = createButton(buttonWidth, buttonHeight, padding, Assets.unrightarrow);
        refreshButton = createButton(buttonWidth, buttonHeight, padding, Assets.refresh);
        closeButton = createButton(buttonWidth, buttonHeight, padding, Assets.mraidClose);

        buttonLayout.addView(backButton);
        buttonLayout.addView(forwardButton);
        buttonLayout.addView(refreshButton);
        buttonLayout.addView(closeButton);

        rootLayout.addView(buttonLayout);

        webView = new WebView(this);
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, buttonLayout.getId());
        webView.setLayoutParams(params);
        rootLayout.addView(webView);
    }

    ImageButton createButton(int width, int height, int padding, String pngSrc) {
        ImageButton button = new ImageButton(this);
        Drawable drawable = Assets.getDrawableFromBase64(getResources(), pngSrc);
        button.setImageDrawable(drawable);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.gravity = Gravity.CENTER_VERTICAL;
        button.setLayoutParams(params);
        button.setPadding(0, padding, 0, padding);
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // button.setBackgroundColor(color);
        button.setBackgroundColor(Color.TRANSPARENT);
        return button;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView(Intent intent) {
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        /* Pinch to zoom is apparently not enabled by default on all devices, so
         * declare zoom support explicitly.
         * http://stackoverflow.com/questions/5125851/enable-disable-zoom-in-android-webview
         */
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);

        webView.loadUrl(intent.getStringExtra(URL_EXTRA));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                Activity a = (Activity) view.getContext();
                Toast.makeText(a, "MRAID error: " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                Uri uri = Uri.parse(url);
                String host = uri.getHost();

                if (url.startsWith("market:") || url.startsWith("tel:") ||
                        url.startsWith("voicemail:") || url.startsWith("sms:") ||
                        url.startsWith("mailto:") || url.startsWith("geo:") ||
                        url.startsWith("google.streetview:") ||
                        "play.google.com".equals(host) ||
                        "market.android.com".equals(host)) {
                    try {
                        if (url.startsWith("tel:")) {
                            if (supportedNativeFeatures.contains(MRAIDNativeFeature.TEL)) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                        } else if (url.startsWith("sms:")) {
                            if (supportedNativeFeatures.contains(MRAIDNativeFeature.SMS)) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        }

                    } catch (ActivityNotFoundException exception) {
                        Log.w("MoPub", "Unable to start activity for " + url + ". " +
                                "Ensure that your phone can handle this intent.");
                    }

                    finish();
                    return true;
                }

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Drawable forwardDrawable = Assets.getDrawableFromBase64(getResources(), Assets.unrightarrow);
                forwardButton.setImageDrawable(forwardDrawable);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Drawable backDrawable;
                if (!view.canGoBack()) {
                    backDrawable = Assets.getDrawableFromBase64(getResources(), Assets.unleftarrow);
                } else {
                    backDrawable = Assets.getDrawableFromBase64(getResources(), Assets.leftarrow);
                }
                backButton.setImageDrawable(backDrawable);

                Drawable forwardDrawable;
                if (!view.canGoForward()) {
                    forwardDrawable = Assets.getDrawableFromBase64(getResources(), Assets.unrightarrow);
                } else {
                    forwardDrawable = Assets.getDrawableFromBase64(getResources(), Assets.rightarrow);
                }
                forwardButton.setImageDrawable(forwardDrawable);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                Activity a = (Activity) view.getContext();
                a.setTitle("Loading...");
                a.setProgress(progress * 100);
                if (progress == 100) {
                    a.setTitle(view.getUrl());
                }
            }
        });
    }

    private void setButtonListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webView.reload();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MRAIDBrowser.this.finish();
            }
        });
    }

    private void enableCookies() {
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }
}
