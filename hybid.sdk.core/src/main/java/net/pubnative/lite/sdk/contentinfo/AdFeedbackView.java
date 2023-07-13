package net.pubnative.lite.sdk.contentinfo;

import static net.pubnative.lite.sdk.models.Ad.CONTENT_INFO_LINK_URL;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.RemoteConfig;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;

public class AdFeedbackView implements MRAIDViewListener, MRAIDNativeFeatureListener {
    private static final String TAG = AdFeedbackView.class.getSimpleName();

    public interface AdFeedbackLoadListener {

        void onLoad(String url);

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

        if (!TextUtils.isEmpty(url)) {
            try {
                Uri uri = Uri.parse(url);
                if (uri != null && TextUtils.isEmpty(uri.getQueryParameter("apptoken"))) {
                    String appTokenMacroPlaceholder = "token_macro";
                    uri = uri.buildUpon().appendQueryParameter("apptoken", appTokenMacroPlaceholder).build();
                    url = uri.toString();
                    // Workaround for uri encoding of the macro
                    url = url.replace(appTokenMacroPlaceholder, FeedbackMacros.MACRO_APP_TOKEN);
                }
            } catch (RuntimeException exception) {
                Logger.e(TAG, exception.getMessage());
                HyBid.reportException(exception);
            }
        }

        mUrlHandlerDelegate = new UrlHandler(context);
        mAdFeedbackData = new AdFeedbackDataCollector().collectData(ad, adFormat, integrationType);

        FeedbackMacros macroHelper = new FeedbackMacros();

        String processedUrl = macroHelper.processUrl(url, mAdFeedbackData);

        if (!TextUtils.isEmpty(processedUrl)) {
            url = processedUrl;
        }

        mViewContainer = new MRAIDInterstitial(context, url, null, true, true,
                new String[]{
                        MRAIDNativeFeature.CALENDAR,
                        MRAIDNativeFeature.INLINE_VIDEO,
                        MRAIDNativeFeature.SMS,
                        MRAIDNativeFeature.STORE_PICTURE,
                        MRAIDNativeFeature.TEL,
                        MRAIDNativeFeature.LOCATION
                }, this, this, null);

        mViewContainer.markCreativeAdComingFromFeedbackForm();

        mListener = listener;

        mListener.onLoad(url);
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
            Logger.d(TAG, "Feedback form loaded");
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

    @Override
    public void onExpandedAdClosed() {

    }

    public void showFeedbackForm(Context context, String url) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            showFeedbackForm(activity, url);
        } else {
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK, "The feedback form requires an Activity context"));
            }
        }
    }

    public synchronized void showFeedbackForm(Activity activity, String url) {
        if (mViewContainer != null && mViewContainer.isLoaded() && mIsReady) {
            URLValidator.isValidURL(url, isValid -> {
                if (isValid) {
                    mViewContainer.show(activity, () -> {
                        mViewContainer.showDefaultContentInfoURL(CONTENT_INFO_LINK_URL);
                        mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
                    }, url);
                } else {
                    if (mListener != null) {
                        mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
                    }
                }
            });
        } else {
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
            }
        }
    }
}
