// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.banner.presenter;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.contentinfo.AdFeedbackFormHelper;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.HybidConsumer;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.PNAPIContentInfoView;
import net.pubnative.lite.sdk.views.ProgressDialogFragment;
import net.pubnative.lite.sdk.visibility.ImpressionManager;
import net.pubnative.lite.sdk.visibility.ImpressionTracker;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class MraidAdPresenter implements AdPresenter, MRAIDViewListener, MRAIDNativeFeatureListener, ImpressionTracker.Listener, PNAPIContentInfoView.ContentInfoListener {
    private static final String TAG = MraidAdPresenter.class.getSimpleName();
    private static final boolean MRAID_EXPAND_DEFAULT = true;
    private final Context mContext;
    private final Ad mAd;
    private final ImpressionTrackingMethod mTrackingMethod;
    private final UrlHandler mUrlHandlerDelegate;
    private final String[] mSupportedNativeFeatures;
    private AdSize mAdSize;

    private AdPresenter.Listener mListener;
    private ImpressionListener mImpressionListener;
    private MRAIDBanner mMRAIDBanner;
    private boolean mIsDestroyed = false;

    private MRAIDViewListener mRaidListener;
    private ReportingController mReportingController;

    public MraidAdPresenter(Context context, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod) {
        mContext = context;
        mAdSize = adSize;
        mAd = ad;

        ImpressionTrackingMethod trackingMethodFinal = trackingMethod;

        mReportingController = HyBid.getReportingController();

        if (ad != null && ad.getImpressionTrackingMethod() != null && ImpressionTrackingMethod.fromString(ad.getImpressionTrackingMethod()) != null) {
            trackingMethodFinal = ImpressionTrackingMethod.fromString(ad.getImpressionTrackingMethod());
        }

        if (trackingMethodFinal != null) {
            mTrackingMethod = trackingMethodFinal;
        } else {
            mTrackingMethod = ImpressionTrackingMethod.AD_VIEWABLE;
        }

        mUrlHandlerDelegate = new UrlHandler(context);
        mSupportedNativeFeatures = new String[]{MRAIDNativeFeature.CALENDAR, MRAIDNativeFeature.INLINE_VIDEO, MRAIDNativeFeature.SMS, MRAIDNativeFeature.STORE_PICTURE, MRAIDNativeFeature.TEL, MRAIDNativeFeature.LOCATION};
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void setImpressionListener(ImpressionListener listener) {
        mImpressionListener = listener;
    }

    @Override
    public void setVideoListener(VideoListener listener) {
        //Do nothing. No need for video listener in the MRAID presenter
    }

    @Override
    public void setMRaidListener(MRAIDViewListener listener) {
        mRaidListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MraidAdPresenter is destroyed")) {
            return;
        }

        Boolean isExpandEnabled = mAd.getMraidExpand();
        if (isExpandEnabled == null) {
            isExpandEnabled = MRAID_EXPAND_DEFAULT;
        }
        if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
            mMRAIDBanner = new MRAIDBanner(mContext, mAd.getAssetUrl(APIAsset.HTML_BANNER), "", true, isExpandEnabled, mSupportedNativeFeatures, this, this, mAd.getContentInfoContainer(mContext, this));
        } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
            mMRAIDBanner = new MRAIDBanner(mContext, "", mAd.getAssetHtml(APIAsset.HTML_BANNER), true, isExpandEnabled, mSupportedNativeFeatures, this, this, mAd.getContentInfoContainer(mContext, this));
        }
    }

    @Override
    public void destroy() {
        if (mMRAIDBanner != null) {
            mMRAIDBanner.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        startTracking(null);
    }

    @Override
    public void startTracking(HybidConsumer<Double> consumer) {
        if (mMRAIDBanner != null && mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
            ImpressionManager.startTrackingView(mMRAIDBanner, mAdSize, mAd.getImpressionMinVisibleTime()
                    , mAd.getImpressionVisiblePercent(), this, consumer);
        }
    }

    @Override
    public void stopTracking() {
        if (mMRAIDBanner != null) {
            mMRAIDBanner.stopAdSession();
            if (mTrackingMethod == ImpressionTrackingMethod.AD_VIEWABLE) {
                ImpressionManager.stopTrackingView(mMRAIDBanner);
            }
        }
    }

    @Override
    public JSONObject getPlacementParams() {
        return null;
    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onAdLoaded(this, mMRAIDBanner);
            if (mTrackingMethod == ImpressionTrackingMethod.AD_RENDERED && mImpressionListener != null) {
                mImpressionListener.onImpression();
            }
        }
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onAdError(this);
        }
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {
        if (mIsDestroyed) {
            return;
        }
    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {
        if (mRaidListener != null) mRaidListener.onExpandedAdClosed();
    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return true;
    }

    @Override
    public void mraidShowCloseButton() {
    }

    @Override
    public void onExpandedAdClosed() {

    }

    @Override
    public void mraidNativeFeatureCallTel(String url) {
        if (mIsDestroyed) {
            return;
        }
        if (mListener != null) {
            mListener.onAdClicked(this);
        }
    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        if (mIsDestroyed) {
            return;
        }
        String navigationMode = null;
        if (mAd != null) {
            navigationMode = mAd.getNavigationMode();
        }
        mUrlHandlerDelegate.handleUrl(url, navigationMode);
        if (mListener != null) {
            mListener.onAdClicked(this);
        }
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {
        if (mIsDestroyed) {
            return;
        }
        if (mListener != null) {
            mListener.onAdClicked(this);
        }
    }

    @Override
    public void onImpression(View visibleView) {
        if (mImpressionListener != null) {
            mImpressionListener.onImpression();
        }
    }

    // Content info listener
    @Override
    public void onIconClicked(List<String> clickTrackers) {
        if (clickTrackers != null && !clickTrackers.isEmpty()) {
            for (int i = 0; i < clickTrackers.size(); i++) {
                EventTracker.post(mContext, clickTrackers.get(i), null, false);
            }
        }

        invokeOnContentInfoClick();
    }

    private void invokeOnContentInfoClick() {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CONTENT_INFO_CLICK);
            reportingEvent.setTimestamp(System.currentTimeMillis());
            if (mAdSize == AdSize.SIZE_INTERSTITIAL) {
                reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            } else {
                reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
            }
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            Ad ad = getAd();
            if (ad != null) {
                reportingEvent.setImpId(ad.getSessionId());
                reportingEvent.setCampaignId(ad.getCampaignId());
                reportingEvent.setConfigId(ad.getConfigId());
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    String processedURL = "";

    @Override
    public void onLinkClicked(String url) {
        AdFeedbackFormHelper adFeedbackFormHelper = new AdFeedbackFormHelper();
        adFeedbackFormHelper.showFeedbackForm(mContext, url, mAd, Reporting.AdFormat.BANNER, IntegrationType.STANDALONE);
    }


    /*todo : to be enhanced : presenter should not have reference to view progress*/
    public void showProgressDialog(FragmentManager fragmentManager, String title, String message) {
        Fragment prev = fragmentManager.findFragmentByTag("progress dialog");

        if (prev != null) {
            fragmentManager.beginTransaction().remove(prev).commit();
        }

        fragmentManager.beginTransaction().addToBackStack(null).commit();

        DialogFragment newFragment = ProgressDialogFragment.newInstance(title, message);
        newFragment.show(fragmentManager, "progress dialog");
    }

    public void hideProgressDialog(FragmentManager fragmentManager) {
        Fragment prev = fragmentManager.findFragmentByTag("progress dialog");

        if (prev != null) {
            fragmentManager.beginTransaction().remove(prev).commit();
        }
    }
}
