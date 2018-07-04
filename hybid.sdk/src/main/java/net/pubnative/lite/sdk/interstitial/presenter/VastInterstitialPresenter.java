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
package net.pubnative.lite.sdk.interstitial.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.vast.VASTParser;
import net.pubnative.lite.sdk.vast.VASTPlayer;
import net.pubnative.lite.sdk.vast.model.VASTModel;

public class VastInterstitialPresenter implements InterstitialPresenter, VASTPlayer.Listener {
    private final Activity mActivity;
    private final Ad mAd;
    private VASTPlayer mPlayer;
    private WindowManager mWindowManager;
    private RelativeLayout mInterstitialView;


    private InterstitialPresenter.Listener mListener;
    private boolean mIsDestroyed;
    private boolean mLoaded = false;
    private boolean mStopped = false;
    private boolean mReady = false;

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
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        mReady = false;
        mPlayer = new VASTPlayer(mActivity);
        mPlayer.setListener(this);

        new VASTParser(mActivity).setListener(new VASTParser.Listener() {
            @Override
            public void onVASTParserError(int error) {
                if (mListener != null) {
                    mListener.onInterstitialError(VastInterstitialPresenter.this);
                }
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {
                mPlayer.load(model);
            }
        }).execute(mAd.getVast());
    }

    @Override
    public boolean isReady() {
        return mReady;
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        mInterstitialView = new RelativeLayout(mActivity) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };

        mInterstitialView.setBackgroundColor(Color.BLACK);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mInterstitialView.addView(mPlayer, layoutParams);

        mInterstitialView.addView(mAd.getContentInfoContainer(mActivity));
        mWindowManager.addView(mInterstitialView, params);

        mListener.onInterstitialShown(this);
        mStopped = false;
        mPlayer.onMuteClick();
        mPlayer.play();
    }

    @Override
    public void destroy() {
        mListener = null;
        mIsDestroyed = true;
        mReady = false;
    }

    @Override
    public void hide() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "VastInterstitialPresenter is destroyed")) {
            return;
        }

        if (!mStopped) {
            mStopped = true;
            if (mPlayer != null) {
                mPlayer.stop();
            }
        }

        if (mInterstitialView != null) {
            mInterstitialView.removeAllViews();
        }

        if (mWindowManager != null) {
            mWindowManager.removeView(mInterstitialView);
            mWindowManager = null;
        }

        mListener.onInterstitialDismissed(this);
    }

    @Override
    public void onVASTPlayerLoadFinish() {
        if (mIsDestroyed) {
            return;
        }

        if (!mLoaded) {
            mLoaded = true;
            mReady = true;
            if (mListener != null) {
                mListener.onInterstitialLoaded(this);
            }
        }
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {
        if (mListener != null) {
            mListener.onInterstitialError(this);
        }
    }

    @Override
    public void onVASTPlayerOpenOffer() {
        if (mIsDestroyed) {
            return;
        }

        if (mListener != null) {
            mListener.onInterstitialClicked(this);
        }
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

    }
}
