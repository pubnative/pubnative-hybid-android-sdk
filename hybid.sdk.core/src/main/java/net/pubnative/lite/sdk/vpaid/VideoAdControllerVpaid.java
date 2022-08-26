package net.pubnative.lite.sdk.vpaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.enums.EventConstants;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.macros.MacroHelper;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.models.vpaid.CreativeParams;
import net.pubnative.lite.sdk.vpaid.models.vpaid.TrackingEvent;
import net.pubnative.lite.sdk.vpaid.protocol.BridgeEventHandler;
import net.pubnative.lite.sdk.vpaid.protocol.ViewControllerVpaid;
import net.pubnative.lite.sdk.vpaid.protocol.VpaidBridge;
import net.pubnative.lite.sdk.vpaid.protocol.VpaidBridgeImpl;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

class VideoAdControllerVpaid implements VideoAdController, BridgeEventHandler {

    private static final String LOG_TAG = VideoAdControllerVpaid.class.getSimpleName();
    private static final String BASE_URL = "http://pubnative.net";
    private static final String ENVIRONMENT_VARS = "{ " +
            "slot: document.getElementById('slot'), " +
            "videoSlot: document.getElementById('video-slot'), " +
            "videoSlotCanAutoPlay: true }";
    private static final String HTML_SOURCE_FILE = "ad.html";
    private static final String VPAID_CREATIVE_URL_STRING = "[VPAID_CREATIVE_URL]";
    private static final String MIME_TYPE = "text/html";

    private final AdSpotDimensions mAdSpotDimensions;
    private final VpaidBridge mVpaidBridge;
    private final AdParams mAdParams;
    private final BaseVideoAdInternal mBaseAdInternal;
    private final ViewControllerVpaid mViewControllerVpaid;
    private final MacroHelper mMacroHelper;

    private OnPreparedListener mOnPreparedListener;
    private WebView mWebView;
    private boolean mIsWaitingForSkippableState;
    private boolean mIsWaitingForWebView;
    private boolean mIsStarted;

    private boolean mIsVisible = false;
    private boolean mFinishedPlaying = false;

    private final HyBidViewabilityNativeVideoAdSession mViewabilityAdSession;
    private final List<HyBidViewabilityFriendlyObstruction> mViewabilityFriendlyObstructions;

    VideoAdControllerVpaid(BaseVideoAdInternal baseAd, AdParams adParams,
                           AdSpotDimensions adSpotDimensions,
                           String vastFileContent,
                           HyBidViewabilityNativeVideoAdSession viewabilityAdSession) {
        mBaseAdInternal = baseAd;
        mAdParams = adParams;
        mAdSpotDimensions = adSpotDimensions;
        mViewabilityAdSession = viewabilityAdSession;
        mViewabilityFriendlyObstructions = new ArrayList<>();
        mVpaidBridge = new VpaidBridgeImpl(this, createCreativeParams());
        mViewControllerVpaid = new ViewControllerVpaid(this);
        mMacroHelper = new MacroHelper();
    }

    @Override
    public void resumeEndCardCloseButtonTimer() {

    }

    @Override
    public void pauseEndCardCloseButtonTimer() {

    }

    //region VideoAdController methods
    @Override
    public void prepare(VideoAdController.OnPreparedListener listener) {
        mOnPreparedListener = listener;
        try {
            initWebView();
            String html = Utils.readAssets(mBaseAdInternal.getContext().getAssets(), HTML_SOURCE_FILE);
            String finalHtml = html.replace(VPAID_CREATIVE_URL_STRING, mAdParams.getVpaidJsUrl());
            mWebView.loadDataWithBaseURL(BASE_URL, finalHtml, MIME_TYPE, "UTF-8", null);
        } catch (Exception e) {
            Logger.e(LOG_TAG, "Can't read assets: " + e.getMessage());
        }
    }

    @Override
    public void setVideoFilePath(String filePath) {
    }

    @Override
    public void setEndCardFilePath(String endCardFilePath) {
    }

    @Override
    public void setEndCardData(EndCardData endCardData) {
    }

    @Override
    public void buildVideoAdView(VideoAdView bannerView) {
        mViewControllerVpaid.buildVideoAdView(bannerView, mWebView);
    }

    @Override
    public void playAd() {
        mIsStarted = true;
        mVpaidBridge.startAd();
    }

    @Override
    public void pause() {
        if (mIsStarted) {
            mVpaidBridge.pauseAd();
        }
    }

    @Override
    public void resume() {
        mVpaidBridge.resumeAd();
    }

    @Override
    public void dismiss() {
        mVpaidBridge.pauseAd();
        mVpaidBridge.stopAd();
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.loadUrl("about:blank");
        }
    }

    @Override
    public void destroy() {
        if (mWebView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWebView.getParent() != null) {
                        ((ViewGroup) mWebView.getParent()).removeAllViews();
                    }
                    mWebView.clearHistory();
                    mWebView.clearCache(true);
                    mWebView.loadUrl("about:blank");
                    mWebView.pauseTimers();
                    mWebView = null;
                }
            });
        }
    }

    @Override
    public void toggleMute() {

    }

    @Override
    public void setVolume(boolean mute) {
    }

    @Override
    public void skipVideo() {
        mIsStarted = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBaseAdInternal.dismiss();
            }
        });

    }
    //endregion

    //region BridgeEventHandler methods
    @Override
    public void runOnUiThread(Runnable runnable) {
        if (mBaseAdInternal != null) {
            mBaseAdInternal.runOnUiThread(runnable);
        }
    }

    @Override
    public void callJsMethod(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.loadUrl("javascript:" + url);
                }
            }
        });
    }

    @Override
    public void onPrepared() {
        mOnPreparedListener.onPrepared();
    }

    @Override
    public void onAdSkipped() {
        if (!mIsStarted) {
            return;
        }
        mIsWaitingForSkippableState = true;
        mVpaidBridge.getAdSkippableState();

        mFinishedPlaying = true;
    }

    @Override
    public void onAdStopped() {
        if (!mIsStarted) {
            return;
        }
        postEvent(EventConstants.CLOSE_LINEAR, true);
        postEvent(EventConstants.CLOSE, true);
        skipVideo();
    }

    @Override
    public void setSkippableState(boolean skippable) {
        if (!mIsStarted) {
            return;
        }
        if (mIsWaitingForSkippableState && skippable) {
            mIsWaitingForSkippableState = false;
            postEvent(EventConstants.SKIP, true);
            skipVideo();
        }
    }

    @Override
    public void openUrl(String url) {
        for (String trackUrl : mAdParams.getVideoClicks()) {
            EventTracker.post(mBaseAdInternal.getContext(), trackUrl, mMacroHelper, true);
        }
        if (TextUtils.isEmpty(url)) {
            url = mAdParams.getVideoRedirectUrl();
        }

        mViewabilityAdSession.fireClick();

        Logger.d(LOG_TAG, "Handle external url");
        if (Utils.isOnline(mBaseAdInternal.getContext())) {
            Context context = mBaseAdInternal.getContext();
            UrlHandler urlHandler = new UrlHandler(context);
            urlHandler.handleUrl(url);
        } else {
            Logger.e(LOG_TAG, "No internet connection");
        }

        mBaseAdInternal.onAdClicked();
    }

    @Override
    public void trackError(String message) {
        ErrorLog.postError(mBaseAdInternal.getContext(), VastError.VPAID);
    }

    @Override
    public void postEvent(String eventType, int value, boolean ignoreIfExist) {
        for (Tracking tracking : mAdParams.getEvents()) {
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.PROGRESS)) {
                if (tracking.getOffset() == null) {
                    continue;
                }
                int sendEventTime = mAdParams.getDuration() - value;
                if (Utils.parseDuration(tracking.getOffset()) == sendEventTime) {
                    EventTracker.post(mBaseAdInternal.getContext(), event.url, mMacroHelper, ignoreIfExist);
                }
            }
        }
    }

    @Override
    public void postEvent(String eventType, boolean ignoreIfExist) {
        EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), eventType, mMacroHelper, ignoreIfExist);
    }
    //endregion

    //region other methods
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
        mWebView = new WebView(mBaseAdInternal.getContext());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Utils.isDebug()) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            mWebView.clearCache(true);
        }
        mWebView.setWebChromeClient(new WebChromeClient());
        mIsWaitingForWebView = true;
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (mIsWaitingForWebView) {
                    mVpaidBridge.prepare();
                    Logger.d(LOG_TAG, "Init webView done");
                    mIsWaitingForWebView = false;
                }
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                Logger.d(LOG_TAG, "WebViewClient - onRenderProcessGone");
                return true;
            }
        });
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.addJavascriptInterface(mVpaidBridge, "android");
    }

    @Override
    public void closeSelf() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsWaitingForWebView = false;
                mVpaidBridge.stopAd();
                mBaseAdInternal.dismiss();
            }
        });
    }

    private CreativeParams createCreativeParams() {
        CreativeParams result = new CreativeParams(mAdSpotDimensions.getWidth(), mAdSpotDimensions.getHeight(), "normal", 720);
        result.setAdParameters("{'AdParameters':'" + mAdParams.getAdParams() + "'}");
        result.setEnvironmentVars(ENVIRONMENT_VARS);
        return result;
    }
    //endregion

    @Override
    public void onDurationChanged() {

    }

    @Override
    public void onAdLinearChange() {

    }

    @Override
    public void onAdVolumeChange() {
    }

    @Override
    public void onAdImpression() {
        for (String url : mAdParams.getImpressions()) {
            EventTracker.post(mBaseAdInternal.getContext(), url, mMacroHelper, true);
            Logger.d(LOG_TAG, "mAdParams.getImpressions() " + url);
        }
    }

    @Override
    public boolean adFinishedPlaying() {
        return mFinishedPlaying;
    }

    @Override
    public boolean isRewarded() {
        return mBaseAdInternal.isRewarded();
    }

    @Override
    public AdParams getAdParams() {
        return mAdParams;
    }

    @Override
    public HyBidViewabilityNativeVideoAdSession getViewabilityAdSession() {
        return mViewabilityAdSession;
    }

    @Override
    public void addViewabilityFriendlyObstruction(View view, FriendlyObstructionPurpose purpose, String reason) {
        if (view != null && !TextUtils.isEmpty(reason)) {
            mViewabilityFriendlyObstructions.add(new HyBidViewabilityFriendlyObstruction(view, purpose, reason));
        }
    }

    @Override
    public List<HyBidViewabilityFriendlyObstruction> getViewabilityFriendlyObstructions() {
        return mViewabilityFriendlyObstructions;
    }

    @Override
    public boolean isVideoVisible() {
        return mIsVisible;
    }

    @Override
    public int getProgress() {
        return -1;
    }

    @Override
    public void setVideoVisible(boolean isVisible) {
        this.mIsVisible = isVisible;
    }
}
