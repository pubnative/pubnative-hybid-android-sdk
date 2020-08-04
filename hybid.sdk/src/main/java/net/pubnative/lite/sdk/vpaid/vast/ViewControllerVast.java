package net.pubnative.lite.sdk.vpaid.vast;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.VideoAdController;
import net.pubnative.lite.sdk.vpaid.VideoAdView;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.widget.CountDownView;

import java.util.List;

public class ViewControllerVast implements View.OnClickListener {
    private final static String LOG_TAG = ViewControllerVast.class.getSimpleName();

    private final VideoAdController mAdController;

    private VideoAdView mBannerView;
    private CountDownView mCountdownView;
    private View mVideoPlayerLayout;
    private ImageView mEndCardView;
    private View mControlsLayout;
    private View mEndCardLayout;
    private boolean mMuteState;
    private Surface mSurface;
    private View mSkipView;
    private ImageView mMuteView;

    public ViewControllerVast(VideoAdController adController) {
        mAdController = adController;
    }

    public void buildVideoAdView(VideoAdView bannerView) {
        Context context = bannerView.getContext();
        mBannerView = bannerView;
        bannerView.setVisibilityListener(mCreateVisibilityListener);
        bannerView.removeAllViews();

        mControlsLayout = LayoutInflater.from(context).inflate(R.layout.controls, bannerView, false);
        mControlsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdController.getViewabilityAdSession().fireClick();
                mAdController.openUrl(null);
            }
        });

        mVideoPlayerLayout = mControlsLayout.findViewById(R.id.videoPlayerLayout);

        mEndCardLayout = mControlsLayout.findViewById(R.id.endCardLayout);
        mEndCardLayout.setVisibility(View.GONE);
        mEndCardView = mControlsLayout.findViewById(R.id.endCardView);

        mControlsLayout.findViewById(R.id.closeView).setOnClickListener(this);
        mControlsLayout.findViewById(R.id.replayView).setOnClickListener(this);

        mCountdownView = mControlsLayout.findViewById(R.id.count_down);
        ((TextureView) mControlsLayout.findViewById(R.id.textureView))
                .setSurfaceTextureListener(mCreateTextureListener);

        mMuteView = mControlsLayout.findViewById(R.id.muteView);
        mMuteView.setOnClickListener(this);

        mSkipView = mControlsLayout.findViewById(R.id.skipView);
        mSkipView.setOnClickListener(this);

        mAdController.addViewabilityFriendlyObstruction(mControlsLayout, FriendlyObstructionPurpose.VIDEO_CONTROLS, "Video controls");

        bannerView.addView(mControlsLayout);
    }

    private VideoAdView.VisibilityListener mCreateVisibilityListener = new VideoAdView.VisibilityListener() {
        @Override
        public void onVisibilityChanged(int visibility) {
            try {
                if (!mAdController.adFinishedPlaying()) {
                    if (visibility == View.VISIBLE) {
                        mAdController.resume();
                    } else {
                        mAdController.pause();
                    }
                }
            } catch (Exception e) {
                Logger.e(LOG_TAG,"ViewControllerVast.createVisibilityListener: Log: " + Log.getStackTraceString(e));
            }
        }
    };

    private TextureView.SurfaceTextureListener mCreateTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurface = new Surface(surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override

        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };


    public void adjustLayoutParams(int width, int height) {
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mControlsLayout.getLayoutParams();
        ViewGroup.LayoutParams newParams = Utils.calculateNewLayoutParams(
                oldParams,
                width,
                height,
                mBannerView.getWidth(),
                mBannerView.getHeight(),
                Utils.StretchOption.NO_STRETCH
        );
        mControlsLayout.setLayoutParams(newParams);
    }

    public void postDelayed(Runnable action, long delayMillis) {
        mBannerView.postDelayed(action, delayMillis);
    }

    public Surface getSurface() {
        return mSurface;
    }

    public void setProgress(int progress, int total) {
        mCountdownView.setProgress(total - progress, total);
    }

    public boolean isMute() {
        return mMuteState;
    }

    public void showEndCard(String imageUri) {
        mEndCardLayout.setVisibility(View.VISIBLE);
        mVideoPlayerLayout.setVisibility(View.GONE);
        ImageUtils.setScaledImage(mEndCardView, imageUri);
    }

    public void showSkipButton() {
        if (mSkipView != null) {
            mSkipView.setVisibility(View.VISIBLE);
            mSkipView.setClickable(true);
        }
    }

    public boolean isEndCard() {
        return mEndCardLayout != null && mEndCardLayout.getVisibility() != View.VISIBLE;
    }

    public void dismiss() {
        if (mBannerView != null) {
            mBannerView.removeAllViews();
        }
    }

    public void destroy() {
        if (mEndCardView != null) {
            mEndCardView.setImageDrawable(null);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.closeView) {
            closeSelf();
        } else if (v.getId() == R.id.skipView) {
            skipVideo();
        } else if (v.getId() == R.id.muteView) {
            muteVideo();
        } else if (v.getId() == R.id.replayView) {
            replayVideo();
        }
    }

    private void skipVideo() {
        mAdController.skipVideo();
    }

    private void closeSelf() {
        mAdController.closeSelf();
    }

    private void replayVideo() {
        mEndCardLayout.setVisibility(View.GONE);
        mVideoPlayerLayout.setVisibility(View.VISIBLE);
        mAdController.playAd();
    }

    public void muteVideo() {
        mMuteState = !mMuteState;
        mAdController.setVolume(mMuteState);
        if (mMuteState) {
            mMuteView.setImageResource(R.drawable.mute);
        } else {
            mMuteView.setImageResource(R.drawable.unmute);
        }
    }
}
