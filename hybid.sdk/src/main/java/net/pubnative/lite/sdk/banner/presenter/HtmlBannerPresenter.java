package net.pubnative.lite.sdk.banner.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.HyBidHtmlWebView;

public class HtmlBannerPresenter implements BannerPresenter, HyBidHtmlWebView.WebViewListener {
    private final Context mContext;
    private final Ad mAd;
    private final UrlHandler mUrlHandlerDelegate;

    private BannerPresenter.Listener mListener;
    private HyBidHtmlWebView mBanner;
    private boolean mIsDestroyed;

    public HtmlBannerPresenter(Context context, Ad ad) {
        mContext = context;
        mAd = ad;
        mUrlHandlerDelegate = new UrlHandler(context);
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public Ad getAd() {
        return mAd;
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "HtmlBannerPresenter is destroyed")) {
            return;
        }

        mBanner = new HyBidHtmlWebView(mContext, this);

        if (mAd.getAssetUrl(APIAsset.HTML_BANNER) != null) {
            mBanner.loadUrl(mAd.getAssetUrl(APIAsset.HTML_BANNER));
        } else if (mAd.getAssetHtml(APIAsset.HTML_BANNER) != null) {
            mBanner.loadHtml(mAd.getAssetHtml(APIAsset.HTML_BANNER));
        }

    }

    @Override
    public void destroy() {
        if (mBanner != null) {
            mBanner.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {

    }

    @Override
    public void stopTracking() {

    }

    @Override
    public void htmlViewLoaded(HyBidHtmlWebView view) {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onBannerLoaded(this, view);
        }
    }

    @Override
    public void htmlViewOpenBrowser(String url) {
        if (mIsDestroyed) {
            return;
        }

        mUrlHandlerDelegate.handleUrl(url);
        if (mListener != null) {
            mListener.onBannerClicked(this);
        }
    }
}
