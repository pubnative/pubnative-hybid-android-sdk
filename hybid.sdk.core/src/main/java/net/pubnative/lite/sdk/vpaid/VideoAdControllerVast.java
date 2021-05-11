package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.enums.EventConstants;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.models.vpaid.TrackingEvent;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.vast.ViewControllerVast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class VideoAdControllerVast implements VideoAdController {

    private static final String LOG_TAG = VideoAdControllerVast.class.getSimpleName();
    private static final int DELAY_UNTIL_EXECUTE = 100;

    private final ViewControllerVast mViewControllerVast;
    private final AdParams mAdParams;
    private final BaseVideoAdInternal mBaseAdInternal;

    private List<TrackingEvent> mTrackingEventsList = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private TimerWithPause mTimerWithPause;
    private TimerWithPause mSkipTimerWithPause;
    private String mVideoUri;
    private String mImageUri;
    private int mSkipTimeMillis = -1;

    private boolean finishedPlaying = false;

    private HyBidViewabilityNativeVideoAdSession mViewabilityAdSession;
    private List<HyBidViewabilityFriendlyObstruction> mViewabilityFriendlyObstructions;

    VideoAdControllerVast(BaseVideoAdInternal baseAd, AdParams adParams, HyBidViewabilityNativeVideoAdSession viewabilityAdSession) {
        mBaseAdInternal = baseAd;
        mAdParams = adParams;
        mViewabilityAdSession = viewabilityAdSession;
        mViewabilityFriendlyObstructions = new ArrayList<>();
        mViewControllerVast = new ViewControllerVast(this);
    }

    @Override
    public void prepare(OnPreparedListener listener) {
        listener.onPrepared();
    }

    @Override
    public void setVideoFilePath(String videoUri) {
        this.mVideoUri = videoUri;
    }

    @Override
    public void setEndCardFilePath(String imageUri) {
        this.mImageUri = imageUri;
    }

    @Override
    public void buildVideoAdView(VideoAdView bannerView) {
        mViewControllerVast.buildVideoAdView(bannerView);
    }

    @Override
    public void playAd() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    startMediaPlayer();

                } catch (IllegalStateException e) {
                    Logger.e(LOG_TAG, "mediaPlayer IllegalStateException: " + e.getMessage());
                    tryReInitMediaPlayer();
                } catch (IOException e) {
                    Logger.e(LOG_TAG, "mediaPlayer IOException: " + e.getMessage());
                    closeSelf();
                }
            }
        }, DELAY_UNTIL_EXECUTE);
    }

    private void tryReInitMediaPlayer() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    startMediaPlayer();
                } catch (Exception e) {
                    Logger.e(LOG_TAG, "mediaPlayer re-init: " + e.getMessage());
                    closeSelf();
                }
            }
        }, DELAY_UNTIL_EXECUTE);
    }

    private void startMediaPlayer() throws IOException, IllegalStateException {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(mVideoUri);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.prepareAsync();
    }

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            ErrorLog.postError(mBaseAdInternal.getContext(), VastError.MEDIA_FILE_UNSUPPORTED);
            return false;
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mViewControllerVast.adjustLayoutParams(mp.getVideoWidth(), mp.getVideoHeight());
            mMediaPlayer.setSurface(mViewControllerVast.getSurface());
            if (mTimerWithPause != null && mTimerWithPause.isPaused()) {
                mMediaPlayer.seekTo((int) mTimerWithPause.timePassed());
            } else {
                createTimer(mp.getDuration());
            }

            muteVideo(mViewControllerVast.isMute(), false);
            mMediaPlayer.start();
        }
    };


    private void createTimer(final int duration) {
        initSkipTime(duration);
        createProgressPoints(duration);
        mTimerWithPause = new TimerWithPause(duration, 10, true) {
            @Override
            public void onTick(long millisUntilFinished) {
                mViewControllerVast.setProgress((int) millisUntilFinished, duration);
                int doneMillis = duration - (int) millisUntilFinished;
                List<TrackingEvent> eventsToRemove = new ArrayList<>();
                for (TrackingEvent event : mTrackingEventsList) {
                    if (doneMillis > event.timeMillis) {
                        EventTracker.post(mBaseAdInternal.getContext(), event.url);
                        fireViewabilityTrackingEvent(event.name);
                        eventsToRemove.add(event);
                    }
                }

                mTrackingEventsList.removeAll(eventsToRemove);
            }

            @Override
            public void onFinish() {
                mViewControllerVast.resetProgress();
            }
        }.create();

        if (mSkipTimeMillis > 0) {
            mSkipTimerWithPause = new TimerWithPause(mSkipTimeMillis, 1, true) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mViewControllerVast.setSkipProgress((int) millisUntilFinished, mSkipTimeMillis);
                }

                @Override
                public void onFinish() {
                    mViewControllerVast.endSkip();
                }
            }.create();
        }
    }

    private void fireViewabilityTrackingEvent(String name) {
        if (!TextUtils.isEmpty(name)) {
            switch (name) {
                case EventConstants.START:
                    getViewabilityAdSession().fireStart(getAdParams().getDuration(), true);
                    break;
                case EventConstants.FIRST_QUARTILE:
                    getViewabilityAdSession().fireFirstQuartile();
                    break;
                case EventConstants.MIDPOINT:
                    getViewabilityAdSession().fireMidpoint();
                    break;
                case EventConstants.THIRD_QUARTILE:
                    getViewabilityAdSession().fireThirdQuartile();
                    break;
            }
        }
    }

    private void initSkipTime(int duration) {

        int publisherSkipMilliseconds = mAdParams.getPublisherSkipSeconds() * 1000;
        if (publisherSkipMilliseconds > 0) {
            mSkipTimeMillis = publisherSkipMilliseconds;
        }

        int globalSkipMilliseconds = HyBid.getInterstitialSkipOffset() * 1000;

        if (globalSkipMilliseconds > 0 && mSkipTimeMillis <= 0) {
            mSkipTimeMillis = globalSkipMilliseconds;
        }

        if (mSkipTimeMillis <= 0) {
            if (TextUtils.isEmpty(mAdParams.getSkipTime())) {
                mSkipTimeMillis = -1;
            } else {
                if (mAdParams.getSkipTime().contains("%")) {
                    mSkipTimeMillis = duration * Utils.parsePercent(mAdParams.getSkipTime()) / 100;
                } else {
                    mSkipTimeMillis = Utils.parseDuration(mAdParams.getSkipTime()) * 1000;
                }
            }
        }
    }

    private void createProgressPoints(int duration) {
        mTrackingEventsList.clear();
        for (String url : mAdParams.getImpressions()) {
            mTrackingEventsList.add(new TrackingEvent(url));
        }

        if (mAdParams.getEvents() != null) {
            for (Tracking tracking : mAdParams.getEvents()) {
                TrackingEvent event = new TrackingEvent(tracking.getText());
                if (tracking.getEvent().equalsIgnoreCase(EventConstants.CREATIVE_VIEW)) {
                    event.timeMillis = 0;
                    event.name = EventConstants.CREATIVE_VIEW;
                    mTrackingEventsList.add(event);
                }
                if (tracking.getEvent().equalsIgnoreCase(EventConstants.START)) {
                    event.timeMillis = 0;
                    event.name = EventConstants.START;
                    mTrackingEventsList.add(event);
                }
                if (tracking.getEvent().equalsIgnoreCase(EventConstants.FIRST_QUARTILE)) {
                    event.timeMillis = duration / 4;
                    event.name = EventConstants.FIRST_QUARTILE;
                    mTrackingEventsList.add(event);
                }
                if (tracking.getEvent().equalsIgnoreCase(EventConstants.MIDPOINT)) {
                    event.timeMillis = duration / 2;
                    event.name = EventConstants.MIDPOINT;
                    mTrackingEventsList.add(event);
                }
                if (tracking.getEvent().equalsIgnoreCase(EventConstants.THIRD_QUARTILE)) {
                    event.timeMillis = duration * 3 / 4;
                    event.name = EventConstants.THIRD_QUARTILE;
                    mTrackingEventsList.add(event);
                }
                if (tracking.getEvent().equalsIgnoreCase(EventConstants.PROGRESS)) {
                    if (tracking.getOffset() == null) {
                        continue;
                    }
                    if (tracking.getOffset().contains("%")) {
                        event.timeMillis = duration * Utils.parsePercent(tracking.getOffset()) / 100;
                    } else {
                        event.timeMillis = Utils.parseDuration(tracking.getOffset()) * 1000;
                    }
                    mTrackingEventsList.add(event);
                }
            }
        }
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mBaseAdInternal.onAdDidReachEnd();
                    skipVideo(false);
                    EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.COMPLETE);
                }
            };

    private void postDelayed(Runnable action, long delayMillis) {
        mViewControllerVast.postDelayed(action, delayMillis);
    }

    @Override
    public void skipVideo() {
        skipVideo(true);
    }

    private void skipVideo(boolean skipEvent) {
        finishedPlaying = true;

        if (skipEvent) {
            getViewabilityAdSession().fireSkipped();
        } else {
            getViewabilityAdSession().fireComplete();
        }

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        if (mTimerWithPause != null) {
            mTimerWithPause.pause();
            mTimerWithPause = null;
        }

        if (mSkipTimerWithPause != null) {
            mSkipTimerWithPause.pause();
            mSkipTimerWithPause = null;
        }

        if (TextUtils.isEmpty(mImageUri)) {
            if (skipEvent) {
                closeSelf();
            }
        } else {
            mViewControllerVast.showEndCard(mImageUri);
        }
        if (skipEvent) {
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.SKIP);
        }
    }

    @Override
    public void toggleMute() {
        mViewControllerVast.muteVideo();
    }

    @Override
    public void setVolume(boolean mute) {
        muteVideo(mute, true);
    }

    private void muteVideo(boolean mute, boolean postEvent) {
        if (mMediaPlayer == null) {
            return;
        }
        getViewabilityAdSession().fireVolumeChange(mute);
        if (mute) {
            mMediaPlayer.setVolume(0f, 0f);
            if (postEvent) {
                EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.MUTE);
            }
        } else {
            float systemVolume = Utils.getSystemVolume();
            mMediaPlayer.setVolume(systemVolume, systemVolume);
            if (postEvent) {
                EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.UNMUTE);
            }
        }
    }

    private String trackVideoClicks() {
        String clickUrl = mAdParams.getVideoRedirectUrl();

        for (String trackUrl : mAdParams.getVideoClicks()) {
            EventTracker.post(mBaseAdInternal.getContext(), trackUrl);
        }

        return clickUrl;
    }

    private String trackEndCardClicks() {
        String clickUrl = mAdParams.getEndCardRedirectUrl();

        for (String trackUrl : mAdParams.getEndCardClicks()) {
            EventTracker.post(mBaseAdInternal.getContext(), trackUrl);
        }

        return clickUrl;
    }

    @Override
    public void openUrl(String url) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            url = trackVideoClicks();
        } else {
            url = trackEndCardClicks();

            if (url == null) {
                url = trackVideoClicks();
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Logger.d(LOG_TAG, "Handle external url");
        if (Utils.isOnline()) {
            Context context = mBaseAdInternal.getContext();
            UrlHandler urlHandler = new UrlHandler(context);
            urlHandler.handleUrl(url);
        } else {
            Logger.e(LOG_TAG, "No internet connection");
        }

        mBaseAdInternal.onAdClicked();
    }

    public void closeSelf() {
        EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.CLOSE);
        mBaseAdInternal.dismiss();
    }

    @Override
    public void dismiss() {
        mViewControllerVast.dismiss();
    }

    @Override
    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mViewControllerVast.destroy();
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mTimerWithPause != null) {
                mTimerWithPause.pause();
            }
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.PAUSE);
            getViewabilityAdSession().firePause();
        }
    }

    @Override
    public void resume() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying() && mViewControllerVast.isEndCard()) {
            playAd();
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.RESUME);
            getViewabilityAdSession().fireResume();
        }
    }

    @Override
    public boolean adFinishedPlaying() {
        return finishedPlaying;
    }

    @Override
    public boolean isRewarded() {
        return mBaseAdInternal.isRewarded();
    }

    @Override
    public AdParams getAdParams() {
        return mAdParams;
    }

    public HyBidViewabilityNativeVideoAdSession getViewabilityAdSession() {
        return mViewabilityAdSession;
    }

    @Override
    public void addViewabilityFriendlyObstruction(View view, FriendlyObstructionPurpose purpose, String reason) {
        if (view != null && !TextUtils.isEmpty(reason)) {
            mViewabilityFriendlyObstructions.add(new HyBidViewabilityFriendlyObstruction(view, purpose, reason));
        }
    }

    @Override
    public List<HyBidViewabilityFriendlyObstruction> getViewabilityFriendlyObstructions() {
        return mViewabilityFriendlyObstructions;
    }
}
