package net.pubnative.lite.sdk.mrect.presenter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.vast.VASTParser;
import net.pubnative.lite.sdk.vast.VASTPlayer;
import net.pubnative.lite.sdk.vast.model.VASTModel;

public class VastMRectPresenter implements MRectPresenter, VASTPlayer.Listener {
    private final Context mContext;
    private final Ad mAd;
    private final UrlHandler mUrlHandlerDelegate;

    private MRectPresenter.Listener mListener;
    private VASTPlayer mPlayer;
    private boolean mIsDestroyed;

    public VastMRectPresenter(Context context, Ad ad) {
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastMRectPresenter is destroyed")) {
            return;
        }

        mPlayer = new VASTPlayer(mContext, mAd.getContentInfoContainer(mContext));
        mPlayer.setListener(this);
        mPlayer.onMuteClick();

        new VASTParser(mContext).setListener(new VASTParser.Listener() {
            @Override
            public void onVASTParserError(int error) {
                if (mListener != null) {
                    mListener.onMRectError(VastMRectPresenter.this);
                }
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {
                mPlayer.load(model);
            }
        }).execute(mAd.getVast());
    }

    @Override
    public void destroy() {

    }

    private View buildView() {
        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        container.addView(mPlayer, layoutParams);

        return container;
    }

    @Override
    public void onVASTPlayerLoadFinish() {
        if (mIsDestroyed) {
            return;
        }

        //TODO handle play action when using PNLiteAdView
        //mPlayer.play();

        if (mListener != null) {
            mListener.onMRectLoaded(this, buildView());
        }
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {
        if (mListener != null) {
            mListener.onMRectError(this);
        }
    }

    @Override
    public void onVASTPlayerOpenOffer() {
        if (mIsDestroyed) {
            return;
        }

        //mUrlHandlerDelegate.handleUrl(url);
        if (mListener != null) {
            mListener.onMRectClicked(this);
        }
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

    }
}