// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
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

    private MRectPresenter.Listener mListener;
    private VASTPlayer mPlayer;
    private boolean mIsDestroyed;
    private boolean mLoaded = false;

    public VastMRectPresenter(Context context, Ad ad) {
        mContext = context;
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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastMRectPresenter is destroyed")) {
            return;
        }

        mPlayer = new VASTPlayer(mContext);
        mPlayer.setListener(this);

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
        if (mPlayer != null) {
            mPlayer.destroy();
        }
        mListener = null;
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        mPlayer.onMuteClick();
        mPlayer.play();
    }

    @Override
    public void stopTracking() {
        mPlayer.stop();
    }

    private View buildView() {
        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        container.addView(mPlayer, layoutParams);

        View contentInfo = getAd().getContentInfoContainer(mContext);
        if (contentInfo != null) {

            container.addView(contentInfo);
        }

        return container;
    }

    @Override
    public void onVASTPlayerLoadFinish() {
        if (mIsDestroyed) {
            return;
        }

        if (!mLoaded) {
            mLoaded = true;
            if (mListener != null) {
                mListener.onMRectLoaded(this, buildView());
            }
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
