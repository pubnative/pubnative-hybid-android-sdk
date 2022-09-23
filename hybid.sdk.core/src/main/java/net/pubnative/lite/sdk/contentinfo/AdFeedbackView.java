package net.pubnative.lite.sdk.contentinfo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.UrlHandler;

public class AdFeedbackView implements MRAIDViewListener, MRAIDNativeFeatureListener {
    public interface AdFeedbackLoadListener {
        void onLoadFinished();

        void onLoadFailed(Throwable error);

        void onFormClosed();
    }

    private MRAIDInterstitial mViewContainer;
    private AdFeedbackLoadListener mListener;
    private AdFeedbackData mAdFeedbackData;
    private UrlHandler mUrlHandlerDelegate;
    private boolean mIsReady = false;

    public void prepare(Context context, String url, AdFeedbackLoadListener listener) {
        prepare(context, url, null, null, null, listener);
    }

    public void prepare(Context context, String url, Ad ad,
                        String adFormat, IntegrationType integrationType, AdFeedbackLoadListener listener) {
        if (!TextUtils.isEmpty(HyBid.getContentInfoUrl()) && HyBid.isAdFeedbackEnabled()) {
            url = HyBid.getContentInfoUrl().concat("/index.html?apptoken=").concat(FeedbackMacros.MACRO_APP_TOKEN);
        }

        mUrlHandlerDelegate = new UrlHandler(context);
        mAdFeedbackData = new AdFeedbackDataCollector().collectData(ad, adFormat, integrationType);

        FeedbackMacros macroHelper = new FeedbackMacros();

        String processedUrl = macroHelper.processUrl(url, mAdFeedbackData);

        if (!TextUtils.isEmpty(processedUrl)) {
            url = processedUrl;
        }

        mViewContainer = new MRAIDInterstitial(context, url, null, new String[]{
                MRAIDNativeFeature.CALENDAR,
                MRAIDNativeFeature.INLINE_VIDEO,
                MRAIDNativeFeature.SMS,
                MRAIDNativeFeature.STORE_PICTURE,
                MRAIDNativeFeature.TEL,
                MRAIDNativeFeature.LOCATION
        }, this, this, null);
        mListener = listener;
    }

    @Override
    public void mraidNativeFeatureCallTel(String url) {

    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        mUrlHandlerDelegate.handleUrl(url);
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        FeedbackJSInterface jsInterface = new FeedbackJSInterface();
        jsInterface.submitData(mAdFeedbackData, mraidView);
        this.mIsReady = true;
        if (mListener != null) {
            mListener.onLoadFinished();
        }
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        this.mIsReady = false;
        if (mListener != null) {
            mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {
        if (mListener != null) {
            mListener.onFormClosed();
        }
    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return false;
    }

    @Override
    public void mraidShowCloseButton() {

    }

    public void showFeedbackForm(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            showFeedbackForm(activity);
        } else {
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK, "The feedback form requires an Activity context"));
            }
        }
    }

    public void showFeedbackForm(Activity activity) {
        if (mViewContainer != null && mViewContainer.isLoaded() && mIsReady) {
            mViewContainer.show(activity);
        } else {
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
            }
        }
    }
}
