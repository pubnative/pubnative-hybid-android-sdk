// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.browser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.utils.Logger;

public class BrowserActivity extends Activity implements BrowserView {

    private static final String TAG = BrowserActivity.class.getName();
    private static final String KEY_CTA_URL = "KEY_CTA_URL";

    private WebView webView;
    private TextView tvHostname;
    private ProgressBar progressBar;
    private View btnNavigationBackward;
    private View btnNavigationForward;

    private BrowserPresenter browserPresenter;

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KEY_CTA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BrowserCookieManager browserCookieManager = new BrowserCookieManager(CookieManager.getInstance());

        BrowserModel browserModel = new BrowserModel(new BaseWebViewClient(this::onWebViewCloseRequested), new BaseWebChromeClient(), browserCookieManager);

        ClipboardManager clipboardManager = (ClipboardManager) getApplication().getSystemService(Context.CLIPBOARD_SERVICE);

        browserPresenter = new BrowserPresenter(browserModel, new UrlCreator(), clipboardManager);

        if (browserPresenter == null) {
            Log.e(TAG, "Sdk is not initialized.");
            finish();
            return;
        }

        setContentView(R.layout.activity_internal_browser);

        initViews();
        initWebView();
        initLogic();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (browserPresenter != null) {
            browserPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (browserPresenter != null) {
            browserPresenter.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }

        if (browserPresenter != null) {
            browserPresenter.dropView();
        }
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        View btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new DoubleClickPreventionListener() {
            @Override
            protected void processClick() {
                finish();
            }
        });

        View btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new DoubleClickPreventionListener() {
            @Override
            protected void processClick() {
                if (browserPresenter != null) {
                    browserPresenter.onReloadClicked();
                }
            }
        });

        btnNavigationBackward = findViewById(R.id.btnBackward);
        btnNavigationBackward.setOnClickListener(new DoubleClickPreventionListener() {
            @Override
            protected void processClick() {
                if (browserPresenter != null) {
                    browserPresenter.onPageNavigationBackClicked();
                }
            }
        });

        btnNavigationForward = findViewById(R.id.btnForward);
        btnNavigationForward.setOnClickListener(new DoubleClickPreventionListener() {
            @Override
            protected void processClick() {
                if (browserPresenter != null) {
                    browserPresenter.onPageNavigationForwardClicked();
                }
            }
        });

        /*View btnOpenExternalBrowser = findViewById(R.id.btnOpenExternal);
        btnOpenExternalBrowser.setOnClickListener(new DoubleClickPreventionListener() {
            @Override
            protected void processClick() {
                if(browserPresenter!= null) {
                    browserPresenter.onOpenExternalBrowserClicked();
                }
            }
        });*/

        tvHostname = findViewById(R.id.tvHostname);
        tvHostname.setOnLongClickListener((view) -> {
            if (browserPresenter != null) {
                browserPresenter.onCopyHostnameClicked();
                return true;
            }
            return false;
        });
    }

    private void initWebView() {
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setUseWideViewPort(true);
            /*
             * Some devices have zoom gesture functionality disabled by default.
             * See https://stackoverflow.com/questions/5125851
             */
            webSettings.setSupportZoom(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
        }
    }

    private void initLogic() {
        if (webView != null) {
            if (browserPresenter != null) {
                browserPresenter.initWithView(this, webView);
            }
        }

        String url = getIntent().getStringExtra(KEY_CTA_URL);

        if (browserPresenter != null) {
            browserPresenter.loadUrl(url);
        }
    }

    @Override
    public void showHostname(String hostname) {
        if (tvHostname != null) {
            tvHostname.setText(hostname);
        }
    }

    @Override
    public void showConnectionSecure(boolean secure) {
        int drawableResId;
        if (secure) {
            drawableResId = R.drawable.browser_secure_connection;
        } else {
            drawableResId = 0;
        }
        if (tvHostname != null) {
            tvHostname.setCompoundDrawablesWithIntrinsicBounds(drawableResId, 0, 0, 0);
        }
    }

    @Override
    public void setPageNavigationBackEnabled(boolean enabled) {
        if (btnNavigationBackward != null) {
            btnNavigationBackward.setEnabled(enabled);
        }
    }

    @Override
    public void setPageNavigationForwardEnabled(boolean enabled) {
        if (btnNavigationForward != null) {
            btnNavigationForward.setEnabled(enabled);
        }
    }

    @Override
    public void launchExternalBrowser(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Logger.e(TAG, "The url seems to be invalid while launching external browser");
        }
        finish();
    }

    @Override
    public void redirectToExternalApp(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Logger.e(TAG, "The url seems to be invalid while redirecting to external app");
        }
    }

    @Override
    public void showProgressIndicator() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressIndicator() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateProgressIndicator(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void closeBrowser() {
        finish();
    }

    public interface WebViewCloseListener {
        void onWebViewCloseRequested();
    }

    private void onWebViewCloseRequested() {
        finish(); // Close the BrowserActivity
    }
}
