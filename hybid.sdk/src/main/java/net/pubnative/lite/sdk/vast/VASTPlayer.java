//
// Copyright (c) 2016, PubNative, Nexage Inc.
// All rights reserved.
// Provided under BSD-3 license as follows:
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice, this
// list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// Neither the name of Nexage, PubNative nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package net.pubnative.lite.sdk.vast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.R;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.vast.model.TRACKING_EVENTS_TYPE;
import net.pubnative.lite.sdk.vast.model.VASTModel;
import net.pubnative.lite.sdk.vast.util.HttpTools;
import net.pubnative.lite.sdk.vast.widget.CountDownView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VASTPlayer extends RelativeLayout implements MediaPlayer.OnCompletionListener,
                                                          MediaPlayer.OnErrorListener,
                                                          MediaPlayer.OnPreparedListener,
                                                          MediaPlayer.OnVideoSizeChangedListener,
                                                          View.OnClickListener {
    //SurfaceHolder.Callback {

    private static final String TAG            = VASTPlayer.class.getName();
    private static final String TEXT_BUFFERING = "Buffering...";

    /**
     * Player type will lead to different layouts and behaviour to improve campaign type
     */
    public enum CampaignType {

        // Cost per click, this will improve player click possibilities
        CPC,

        // Cost per million (of impressions), this will improve impression behaviour (keep playing)
        CPM
    }

    /**
     * Callbacks for following the player behaviour
     */
    public interface Listener {

        void onVASTPlayerLoadFinish();

        void onVASTPlayerFail(Exception exception);

        void onVASTPlayerPlaybackStart();

        void onVASTPlayerPlaybackFinish();

        void onVASTPlayerOpenOffer();
    }

    private enum PlayerState {

        Empty,
        Loading,
        Ready,
        Playing,
        Pause
    }

    // LISTENERS
    private Listener mListener = null;

    // TIMERS
    private Timer mLayoutTimer;
    private Timer mProgressTimer;
    private Timer mTrackingEventsTimer;

    private static final long TIMER_TRACKING_INTERVAL = 250;
    private static final long TIMER_PROGRESS_INTERVAL = 50;
    private static final long TIMER_LAYOUT_INTERVAL   = 50;

    private static final int MAX_PROGRESS_TRACKING_POINTS = 20;

    // TRACKING
    private HashMap<TRACKING_EVENTS_TYPE, List<String>> mTrackingEventMap;

    // DATA
    private VASTModel mVastModel;
    private String mSkipName;
    private int       mSkipDelay;

    // PLAYER
    private MediaPlayer mMediaPlayer;

    // VIEWS
    private RelativeLayout mRoot;
    private View mOpen;
    // Load
    private View mLoader;
    private TextView mLoaderText;
    // Player
    private View mPlayer;
    private SurfaceView mSurface;
    private TextView mSkip;
    private ImageView mMute;
    private CountDownView mCountDown;

    // OTHERS
    private Handler mMainHandler      = null;
    private int           mVideoHeight      = 0;
    private int           mVideoWidth       = 0;
    private boolean       mIsVideoMute      = false;
    private boolean       mIsBufferingShown = false;
    private boolean       mIsDataSourceSet  = false;
    private int           mQuartile         = 0;
    private CampaignType  mCampaignType     = CampaignType.CPM;
    private PlayerState   mPlayerState      = PlayerState.Empty;
    private List<Integer> mProgressTracker  = null;
    private double        mTargetAspect     = -1.0;

    public VASTPlayer(Context context) {
        super(context);

        init();
    }

    /**
     * Constructor, generally used automatically by a layout inflater
     *
     * @param context
     * @param attrs
     */
    public VASTPlayer(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    public VASTPlayer(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mMainHandler = new Handler(getContext().getMainLooper());

        createLayout();
        setEmptyState();
    }

    /**
     * Sets the desired aspect ratio.  The value is <code>width / height</code>.
     */
    public void setAspectRatio(double aspectRatio) {
        if (aspectRatio < 0) {
            throw new IllegalArgumentException();
        }
        Logger.d(TAG, "Setting aspect ratio to " + aspectRatio + " (was " + mTargetAspect + ")");
        if (mTargetAspect != aspectRatio) {
            mTargetAspect = aspectRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Target aspect ratio will be < 0 if it hasn't been set yet.  In that case,
        // we just use whatever we've been handed.
        if (mTargetAspect > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            // factor the padding out
            int horizPadding = getPaddingLeft() + getPaddingRight();
            int vertPadding = getPaddingTop() + getPaddingBottom();
            initialWidth -= horizPadding;
            initialHeight -= vertPadding;

            double viewAspectRatio = (double) initialWidth / initialHeight;
            double aspectDiff = mTargetAspect / viewAspectRatio - 1;

            if (Math.abs(aspectDiff) < 0.01) {
                // We're very close already.  We don't want to risk switching from e.g. non-scaled
                // 1280x720 to scaled 1280x719 because of some floating-point round-off error,
                // so if we're really close just leave it alone.
                Logger.d(TAG, "aspect ratio is good (target=" + mTargetAspect +
                           ", view=" + initialWidth + "x" + initialHeight + ")");
            } else {
                if (aspectDiff > 0) {
                    // limited by narrow width; restrict height
                    initialHeight = (int) (initialWidth / mTargetAspect);
                } else {
                    // limited by short height; restrict width
                    initialWidth = (int) (initialHeight * mTargetAspect);
                }
                Logger.d(TAG, "new size=" + initialWidth + "x" + initialHeight + " + padding " +
                           horizPadding + "x" + vertPadding);
                initialWidth += horizPadding;
                initialHeight += vertPadding;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //=======================================================
    // State machine
    //=======================================================

    private boolean canSetState(PlayerState playerState) {

        boolean result = false;

        switch (playerState) {

            case Empty:
                result = true;
                break;
            case Loading:
                result = true;
                break;
            case Ready:
                result = (PlayerState.Loading == mPlayerState);
                break;
            case Playing:
                result = ((mPlayerState == PlayerState.Ready)
                          || (mPlayerState == PlayerState.Pause));
                break;
            case Pause:
                result = true;
                break;
        }

        return result;
    }

    /**
     * this method controls the associated state machine of the video player behaviour
     *
     * @param playerState state to set
     * @return
     */
    private void setState(PlayerState playerState) {

        Logger.d(TAG, "setState: " + playerState.name());

        if (canSetState(playerState)) {

            switch (playerState) {

                case Empty:
                    setEmptyState();
                    break;
                case Loading:
                    setLoadingState();
                    break;
                case Ready:
                    setReadyState();
                    break;
                case Playing:
                    setPlayingState();
                    break;
                case Pause:
                    setPauseState();
                    break;
            }

            mPlayerState = playerState;
        }
    }

    private void setEmptyState() {

        Logger.d(TAG, "setEmptyState");

        // Visual aspect total empty
        hideSurface();
        hidePlayerLayout();
        hideOpen();
        hideLoader();

        /**
         * Do not change this order, since cleaning the media player before invalidating timers
         * could make the timers threads access an invalid media player
         */
        stopTimers();
        cleanMediaPlayer();
        // Reset all other items
        mIsDataSourceSet = false;
        mVastModel = null;
        mQuartile = 0;
        mTrackingEventMap = null;
        mProgressTracker = null;
    }

    private void setLoadingState() {

        Logger.d(TAG, "setLoadingState");

        // Show loader
        hidePlayerLayout();

        showSurface();
        showLoader("");

        mTrackingEventMap = mVastModel.getTrackingUrls();
        createMediaPlayer();
        turnVolumeOff();
        startCaching();
    }

    private void setReadyState() {

        Logger.d(TAG, "setReadyState");


        hideLoader();
        hidePlayerLayout();
        showOpen();
        showSurface();

        turnVolumeOff();
    }

    private void setPlayingState() {

        Logger.d(TAG, "setPlayingState");

        hideLoader();
        showOpen();
        showSurface();
        showPlayerLayout();
        /**
         * Don't change the order of this, since starting the media player after te timers could
         * lead to an invalid mediaplayer required inside the timers.
         */
        if (mSurface != null && mSurface.getHolder() != null) {
            final Surface surface = mSurface.getHolder().getSurface();

            if ( surface.isValid() ) {
                mMediaPlayer.setDisplay(mSurface.getHolder());
            }
        }
        calculateAspectRatio();
        refreshVolume();
        mMediaPlayer.start();
        startTimers();
    }

    private void setPauseState() {

        Logger.d(TAG, "setPauseState");

        hideLoader();
        hidePlayerLayout();

        showOpen();
        showSurface();
        turnVolumeOff();
        refreshVolume();
    }

    //=======================================================
    // PUBLIC
    //=======================================================

    /**
     * Sets listener for callbacks related to status of player
     *
     * @param listener Listener
     */
    public void setListener(Listener listener) {

        Logger.d(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Sets the campaign type of the player
     *
     * @param campaignType campign type
     */
    public void setCampaignType(CampaignType campaignType) {

        Logger.d(TAG, "setCampaignType");
        mCampaignType = campaignType;
    }

    /**
     * This will set up the skip button behaviour setting a name and a delay for it to show up
     *
     * @param name  name of the string to be shown, any empty string will disable the button
     * @param delay delay in milliseconds to show the skip button, negative values will disable
     *              the button
     */
    public void setSkip(String name, int delay) {


        if (TextUtils.isEmpty(name)) {
            Logger.w(TAG, "Skip name set to empty value, this will disable the button");
        } else if (delay < 0) {
            Logger.w(TAG, "Skip time set to negative value, this will disable the button");
        }

        mSkipName = name;
        mSkipDelay = delay;
    }

    /**
     * Sets skip string to be shown in the skip button
     *
     * @param skipName skip label
     * @deprecated Please use setSkip(String, int) instead
     */
    @Deprecated
    public void setSkipName(String skipName) {

        // Does nothing
    }

    /**
     * Sets the amount of time that has to be played to be able to skip the video
     *
     * @param skipTime skip time
     * @deprecated Please use setSkip(String, int) instead
     */
    @Deprecated
    public void setSkipTime(int skipTime) {

        // Does nothing
    }

    //=======================================================
    // Player actions
    //=======================================================

    /**
     * Starts loading a video VASTModel in the player, it will notify when it's ready with
     * CachingListener.onVASTPlayerCachingFinish(), so you can start video reproduction.
     *
     * @param model model containing the parsed VAST XML
     */
    public void load(VASTModel model) {

        Logger.d(TAG, "load");

        // Clean, assign, load
        setState(PlayerState.Empty);
        mVastModel = model;
        mIsDataSourceSet = false;
        setState(PlayerState.Loading);
    }

    /**
     * Starts video playback if possible
     */
    public void play() {

        Logger.d(TAG, "play");

        if (canSetState(PlayerState.Playing)) {
            setState(PlayerState.Playing);
        } else if (mPlayerState == PlayerState.Empty) {
            setState(PlayerState.Ready);
        } else {
            Logger.e(TAG, "ERROR, player in wrong state: " + mPlayerState.name());
        }
    }

    /**
     * Stops video playback
     */
    public void stop() {

        Logger.d(TAG, "stop");

        if (canSetState(PlayerState.Loading) && mIsDataSourceSet) {

            stopTimers();
            if(mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mIsDataSourceSet = false;
            }

        } else {
            Logger.e(TAG, "ERROR, player in wrong state: " + mPlayerState.name());
        }
    }

    /**
     * Stops video playback
     */
    public void pause() {

        Logger.d(TAG, "pause");

        if (canSetState(PlayerState.Pause) && mIsDataSourceSet) {

            if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.seekTo(0);
                mMediaPlayer.pause();
            }
            setState(PlayerState.Pause);
        } else {
            Logger.e(TAG, "ERROR, player in wrong state: " + mPlayerState.name());
        }
    }

    /**
     * Destroys current player and clears all loaded data and tracking items
     */
    public void destroy() {

        Logger.d(TAG, "clear");
        setState(PlayerState.Empty);
    }

    //=======================================================
    // Private
    //=======================================================

    // User Interaction
    //-------------------------------------------------------

    public void onMuteClick() {

        Logger.d(TAG, "onMuteClick");

        if (mMediaPlayer != null) {
            processEvent(mIsVideoMute ? TRACKING_EVENTS_TYPE.unmute : TRACKING_EVENTS_TYPE.mute);
            mIsVideoMute = !mIsVideoMute;
            refreshVolume();
        }
    }

    public void onSkipClick() {

        Logger.d(TAG, "onSkipClick");
        processEvent(TRACKING_EVENTS_TYPE.close);
        stop();
    }

    public void onOpenClick() {

        Logger.d(TAG, "onOpenClick");
        openOffer();
    }

    private void openOffer() {

        String clickThroughUrl = mVastModel.getVideoClicks().getClickThrough();
        Logger.d(TAG, "openOffer - clickThrough url: " + clickThroughUrl);

        // Before we send the app to the click through url, we will process ClickTracking URL's.
        List<String> urls = mVastModel.getVideoClicks().getClickTracking();
        fireUrls(urls);

        // Navigate to the click through url
        UrlHandler urlHandler = new UrlHandler(getContext());
        urlHandler.handleUrl(clickThroughUrl);
        invokeOnPlayerOpenOffer();
    }

    // Layout
    //-------------------------------------------------------
    private void createLayout() {

        Logger.d(TAG, "createLayout");

        if (mRoot == null) {

            mRoot = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.pn_vast_player, null);

            mPlayer = mRoot.findViewById(R.id.player);

            // Player contained
            mSurface = mPlayer.findViewById(R.id.surface);
            mSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    Logger.d(TAG, "surfaceCreated");
                    final Surface surface = holder.getSurface();

                    if ( surface == null ) return;

                    final boolean invalidSurface = ! surface.isValid();

                    if ( invalidSurface ) return;

                    createMediaPlayer();
                    mMediaPlayer.setDisplay(holder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                    Logger.d(TAG, "surfaceChanged");
                    calculateAspectRatio();
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                    Logger.d(TAG, "surfaceDestroyed");
                }
            });

            mMute = mPlayer.findViewById(R.id.mute);
            mMute.setVisibility(INVISIBLE);
            mMute.setOnClickListener(this);

            mCountDown = mPlayer.findViewById(R.id.count_down);
            mCountDown.setVisibility(INVISIBLE);

            mSkip = mPlayer.findViewById(R.id.skip);
            mSkip.setVisibility(INVISIBLE);
            mSkip.setOnClickListener(this);

            // Root contained
            mLoader = mRoot.findViewById(R.id.loader);
            mLoaderText = mRoot.findViewById(R.id.loader_text);
            mLoaderText.setVisibility(GONE);

            mOpen = mRoot.findViewById(R.id.open);
            mOpen.setVisibility(INVISIBLE);
            mOpen.setOnClickListener(this);

            addView(mRoot);
        }
    }

    private void showLoader(String message) {

        if(mPlayerState != PlayerState.Pause) {
            mLoader.setVisibility(VISIBLE);
            mLoaderText.setText(message);
            mLoaderText.setVisibility(TextUtils.isEmpty(message) ? GONE : VISIBLE);
        }
    }

    private void hideLoader() {

        mLoader.setVisibility(INVISIBLE);
    }

    private void hideOpen() {

        mOpen.setVisibility(INVISIBLE);
    }

    private void showOpen() {

        mOpen.setVisibility(VISIBLE);
    }

    private void hideSurface() {

        mSurface.setVisibility(INVISIBLE);
    }

    private void showSurface() {

        mSurface.setVisibility(VISIBLE);
    }

    private void hidePlayerLayout() {

        mSkip.setVisibility(INVISIBLE);
        mMute.setVisibility(INVISIBLE);
        mCountDown.setVisibility(INVISIBLE);
    }

    private void showPlayerLayout() {

        mSkip.setVisibility(TextUtils.isEmpty(mSkipName) ? INVISIBLE : VISIBLE);
        mMute.setVisibility(VISIBLE);
        mCountDown.setVisibility(VISIBLE);
    }

    // Media player
    //-------------------------------------------------------

    private void createMediaPlayer() {

        Logger.d(TAG, "createMediaPlayer");

        if (mMediaPlayer == null) {

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void cleanMediaPlayer() {

        Logger.d(TAG, "cleanUpMediaPlayer");

        if (mMediaPlayer != null) {

            turnVolumeOff();
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void refreshVolume() {

        if (mIsVideoMute) {
            turnVolumeOff();
            mMute.setImageResource(R.drawable.pn_btn_unmute);
        } else {
            turnVolumeOn();
            mMute.setImageResource(R.drawable.pn_btn_mute);
        }
    }

    public void turnVolumeOff() {
        mMediaPlayer.setVolume(0.0f, 0.0f);
    }

    public void turnVolumeOn() {
        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    protected void calculateAspectRatio() {

        Logger.d(TAG, "calculateAspectRatio");

        if (mVideoWidth == 0 || mVideoHeight == 0) {

            Logger.w(TAG, "calculateAspectRatio - video source width or height is 0, skipping...");
            return;
        }

        double widthRatio = 1.0 * getWidth() / mVideoWidth;
        double heightRatio = 1.0 * getHeight() / mVideoHeight;

        double scale = Math.max(widthRatio, heightRatio);

        int surfaceWidth = (int) (scale * mVideoWidth);
        int surfaceHeight = (int) (scale * mVideoHeight);

        Logger.d(TAG, " view size:     " + getWidth() + "x" + getHeight());
        Logger.d(TAG, " video size:    " + mVideoWidth + "x" + mVideoHeight);
        Logger.d(TAG, " surface size:  " + surfaceWidth + "x" + surfaceHeight);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(surfaceWidth, surfaceHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSurface.setLayoutParams(params);
        mSurface.getHolder().setFixedSize(surfaceWidth, surfaceHeight);

        updateLayout();

        setAspectRatio((double) mVideoWidth / mVideoHeight);
    }

    private void updateLayout() {

        Logger.d(TAG, "updateLayout");

        RelativeLayout.LayoutParams muteParams = (RelativeLayout.LayoutParams)mMute.getLayoutParams();
        muteParams.addRule(RelativeLayout.ALIGN_TOP, R.id.surface);
        muteParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.surface);
        mMute.setLayoutParams(muteParams);

        RelativeLayout.LayoutParams openParams = (RelativeLayout.LayoutParams)mOpen.getLayoutParams();
        openParams.addRule(RelativeLayout.ALIGN_TOP, R.id.surface);
        openParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.surface);
        mOpen.setLayoutParams(openParams);

        RelativeLayout.LayoutParams countDownParams = (RelativeLayout.LayoutParams)mCountDown.getLayoutParams();
        countDownParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.surface);
        countDownParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.surface);
        mCountDown.setLayoutParams(countDownParams);

        RelativeLayout.LayoutParams skipParams = (RelativeLayout.LayoutParams)mSkip.getLayoutParams();
        skipParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.surface);
        skipParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.surface);
        mSkip.setLayoutParams(skipParams);

    }

    private void startCaching() {

        Logger.d(TAG, "startCaching");

        try {

            if(!mIsDataSourceSet) {
                mIsDataSourceSet = true;
                String videoURL = mVastModel.getPickedMediaFileURL();
                mMediaPlayer.setDataSource(videoURL);
            }

            mMediaPlayer.prepareAsync();

        } catch (Exception exception) {

            invokeOnFail(exception);
            destroy();
        }
    }

    // Event processing
    //-------------------------------------------------------
    private void processEvent(TRACKING_EVENTS_TYPE eventName) {

        Logger.d(TAG, "processEvent: " + eventName);

        if (mTrackingEventMap != null) {

            List<String> urls = mTrackingEventMap.get(eventName);
            fireUrls(urls);
        }
    }

    private void processImpressions() {

        Logger.d(TAG, "processImpressions");

        List<String> impressions = mVastModel.getImpressions();
        fireUrls(impressions);
    }

    private void processErrorEvent() {

        Logger.d(TAG, "processErrorEvent");

        List<String> errorUrls = mVastModel.getErrorUrl();
        fireUrls(errorUrls);
    }

    private void fireUrls(List<String> urls) {

        Logger.d(TAG, "fireUrls");

        if (urls != null) {

            for (String url : urls) {

                Logger.d(TAG, "\tfiring url:" + url);
                HttpTools.httpGetURL(url);
            }

        } else {

            Logger.d(TAG, "\turl list is null");
        }
    }

    //=======================================================
    // Timers
    //=======================================================

    private void stopTimers() {

        Logger.d(TAG, "stopTimers");

        stopQuartileTimer();
        stopLayoutTimer();
        stopVideoProgressTimer();

        mMainHandler.removeMessages(0);
    }

    private void startTimers() {

        Logger.d(TAG, "startTimers");

        // Stop previous timers so they don't remain hold
        stopTimers();

        // start timers
        startQuartileTimer();
        startLayoutTimer();
        startVideoProgressTimer();
    }

    // Progress timer
    //-------------------------------------------------------
    private void startVideoProgressTimer() {

        Logger.d(TAG, "startVideoProgressTimer");

        mProgressTimer = new Timer();
        mProgressTracker = new ArrayList<Integer>();
        mProgressTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if (mProgressTracker.size() > MAX_PROGRESS_TRACKING_POINTS) {

                    int firstPosition = mProgressTracker.get(0);
                    int lastPosition = mProgressTracker.get(mProgressTracker.size() - 1);

                    if (lastPosition > firstPosition) {

                        if (mIsBufferingShown) {

                            mIsBufferingShown = false;
                            mMainHandler.post(new Runnable() {

                                @Override
                                public void run() {

                                    hideLoader();
                                }
                            });
                        }

                    } else {

                        if (!mIsBufferingShown) {

                            mIsBufferingShown = true;
                            mMainHandler.post(new Runnable() {

                                @Override
                                public void run() {

                                    showLoader(TEXT_BUFFERING);
                                }
                            });
                        }
                    }

                    mProgressTracker.remove(0);
                }

                mProgressTracker.add(mMediaPlayer.getCurrentPosition());
            }

        }, 0, TIMER_PROGRESS_INTERVAL);
    }

    private void stopVideoProgressTimer() {

        Logger.d(TAG, "stopVideoProgressTimer");

        if (mProgressTimer != null) {

            mProgressTimer.cancel();
            mProgressTimer = null;
        }
    }

    // Quartile timer
    //-------------------------------------------------------
    private void startQuartileTimer() {

        Logger.d(TAG, "startQuartileTimer");

        mQuartile = 0;

        mTrackingEventsTimer = new Timer();
        mTrackingEventsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                int percentage = 0;
                try {

                    // wait for the video to really start
                    if (mMediaPlayer.getCurrentPosition() == 0) {

                        return;
                    }

                    percentage = 100 * mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration();

                } catch (Exception e) {

                    Logger.e(TAG, "QuartileTimer error: " + e.getMessage());
                    cancel();

                    return;
                }

                if (percentage >= 25 * mQuartile) {

                    if (mQuartile == 0) {

                        Logger.d(TAG, "Video at start: (" + percentage + "%)");
                        processImpressions();
                        processEvent(TRACKING_EVENTS_TYPE.start);
                        invokeOnPlayerPlaybackStart();

                    } else if (mQuartile == 1) {

                        Logger.d(TAG, "Video at first quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.firstQuartile);

                    } else if (mQuartile == 2) {

                        Logger.d(TAG, "Video at midpoint: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.midpoint);

                    } else if (mQuartile == 3) {

                        Logger.d(TAG, "Video at third quartile: (" + percentage + "%)");
                        processEvent(TRACKING_EVENTS_TYPE.thirdQuartile);
                        stopQuartileTimer();
                    }

                    mQuartile++;
                }
            }

        }, 0, TIMER_TRACKING_INTERVAL);
    }

    private void stopQuartileTimer() {

        Logger.d(TAG, "stopQuartileTimer");

        if (mTrackingEventsTimer != null) {

            mTrackingEventsTimer.cancel();
            mTrackingEventsTimer = null;
        }
    }

    // Layout timer
    //-------------------------------------------------------
    private void startLayoutTimer() {

        Logger.d(TAG, "startLayoutTimer");

        mLayoutTimer = new Timer();
        mLayoutTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if (mMediaPlayer == null) {

                    cancel();
                    return;
                }

                // Execute with handler to be sure we execute this on the UIThread
                mMainHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        try {

                            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {

                                int currentPosition = mMediaPlayer.getCurrentPosition();
                                mCountDown.setProgress(currentPosition, mMediaPlayer.getDuration());

                                if (!TextUtils.isEmpty(mSkipName) && mSkipDelay > currentPosition) {

                                    mSkip.setText(mSkipName);
                                    mSkip.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (Exception e) {

                            Logger.e(TAG, "Layout timer error: " + e);

                            cancel();
                            return;
                        }
                    }
                });
            }
        }, 0, TIMER_LAYOUT_INTERVAL);
    }

    private void stopLayoutTimer() {

        Logger.d(TAG, "stopLayoutTimer");

        if (mLayoutTimer != null) {

            mLayoutTimer.cancel();
            mLayoutTimer = null;
        }
    }

    // Listener helpers
    //-------------------------------------------------------

    private void invokeOnPlayerOpenOffer() {

        Logger.d(TAG, "invokeOnPlayerClick");

        if (mListener != null) {

            mListener.onVASTPlayerOpenOffer();
        }
    }

    private void invokeOnPlayerLoadFinish() {

        Logger.d(TAG, "invokeOnPlayerLoadFinish");

        if (mListener != null) {

            mListener.onVASTPlayerLoadFinish();
        }
    }

    private void invokeOnFail(Exception exception) {

        Logger.d(TAG, "invokeOnFail");

        if (mListener != null) {

            mListener.onVASTPlayerFail(exception);
        }
    }

    private void invokeOnPlayerPlaybackStart() {

        Logger.d(TAG, "invokeOnPlayerPlaybackStart");

        if (mListener != null) {

            mListener.onVASTPlayerPlaybackStart();
        }
    }

    private void invokeOnPlayerPlaybackFinish() {

        Logger.d(TAG, "invokeOnPlayerPlaybackFinish");

        if (mListener != null) {

            mListener.onVASTPlayerPlaybackFinish();
        }
    }

    //=============================================
    // CALLBACKS
    //=============================================

    // MediaPlayer.OnCompletionListener
    //---------------------------------------------
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        Logger.d(TAG, "onCompletion -- (MediaPlayer callback)");

        if (mQuartile > 3) {

            processEvent(TRACKING_EVENTS_TYPE.complete);
            invokeOnPlayerPlaybackFinish();
        }

        stop();
    }

    // MediaPlayer.OnErrorListener
    //---------------------------------------------
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        Logger.d(TAG, "onError -- (MediaPlayer callback)");

        processErrorEvent();

        String exceptionMessage;
        switch (what) {

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                exceptionMessage = "server died: ";
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
            default:
                exceptionMessage = "unknown: ";
        }

        switch (extra) {

            case MediaPlayer.MEDIA_ERROR_IO:
                exceptionMessage += "MEDIA_ERROR_IO";
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                exceptionMessage += "MEDIA_ERROR_MALFORMED";
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                exceptionMessage += "MEDIA_ERROR_UNSUPPORTED";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                exceptionMessage += "MEDIA_ERROR_TIMED_OUT";
                break;
            default:
                exceptionMessage += "low-level system error";
        }

        invokeOnFail(new Exception("VASTPlayer error: " + exceptionMessage));
        destroy();

        return true;
    }

    // MediaPlayer.OnPreparedListener
    //---------------------------------------------
    @Override
    public void onPrepared(MediaPlayer mp) {

        Logger.d(TAG, "onPrepared --(MediaPlayer callback) ....about to play");
        setState(PlayerState.Ready);
        invokeOnPlayerLoadFinish();
    }

    // MediaPlayer.OnVideoSizeChangedListener
    //---------------------------------------------
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        Logger.d(TAG, "onVideoSizeChanged -- " + width + " x " + height);

        mVideoWidth = width;
        mVideoHeight = height;
    }

    // View.OnClickListener
    //---------------------------------------------
    public void onClick(View view) {

        Logger.d(TAG, "onClick -- (View.OnClickListener callback)");

        if (mOpen == view) {

            onOpenClick();

        } else if (mSkip == view) {

            onSkipClick();

        } else if (mMute == view) {

            onMuteClick();

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        Logger.d(TAG, "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                calculateAspectRatio();
            }
        });
    }
}
