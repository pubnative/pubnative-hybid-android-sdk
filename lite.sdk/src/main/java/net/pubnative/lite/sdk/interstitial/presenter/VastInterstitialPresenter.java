package net.pubnative.lite.sdk.interstitial.presenter;

import android.app.Activity;
import android.content.Intent;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.vast.VASTParser;
import net.pubnative.lite.sdk.vast.model.VASTModel;
import net.pubnative.lite.sdk.vast2.activity.VASTActivity;

public class VastInterstitialPresenter implements InterstitialPresenter {
    private final Activity mActivity;
    private final Ad mAd;

    private InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public VastInterstitialPresenter(Activity activity, Ad ad) {
        mActivity = activity;
        mAd = ad;
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
        new VASTParser(mActivity).setListener(new VASTParser.Listener() {
            @Override
            public void onVASTParserError(int error) {
                mListener.onInterstitialError(VastInterstitialPresenter.this);
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {
                Intent vastPlayerIntent = new Intent(mActivity, VASTActivity.class);
                vastPlayerIntent.putExtra(VASTActivity.EXTRA_VAST_MODEL, model);
                mActivity.startActivity(vastPlayerIntent);
            }
        }).execute(mAd.getVast());
    }

    @Override
    public void show() {

    }

    @Override
    public void destroy() {
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void hide() {

    }
}
