package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.enums.EventConstants;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause;
import net.pubnative.lite.sdk.vpaid.models.TrackingEvent;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
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
    private String mVideoUri;
    private String mImageUri;
    private int mSkipTimeMillis;
    private View mAdView;

    VideoAdControllerVast(BaseVideoAdInternal baseAd, AdParams adParams) {
        mBaseAdInternal = baseAd;
        mAdParams = adParams;
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
        mAdView = bannerView;
    }

    @Override
    public void playAd() {
        // fix back screen for Samsung tablet android 4.4.4
        postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    startMediaPlayer();
                } catch (IllegalStateException e) {
                    Logger.e(LOG_TAG,"mediaPlayer IllegalStateException: " + e.getMessage());
                    tryReInitMediaPlayer();
                } catch (IOException e) {
                    Logger.e(LOG_TAG,"mediaPlayer IOException: " + e.getMessage());
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
                    Logger.e(LOG_TAG,"mediaPlayer re-init: " + e.getMessage());
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
            ErrorLog.postError(VastError.MEDIA_FILE_UNSUPPORTED);
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
                mTimerWithPause.resume();
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

                if (mSkipTimeMillis >= 0 && doneMillis > mSkipTimeMillis) {
                    mViewControllerVast.showSkipButton();
                    mSkipTimeMillis = -1;
                }

                List<TrackingEvent> eventsToRemove = new ArrayList<>();
                for (TrackingEvent event : mTrackingEventsList) {
                    if (doneMillis > event.timeMillis) {
                        EventTracker.post(event.url);
                        eventsToRemove.add(event);
                    }
                }

                mTrackingEventsList.removeAll(eventsToRemove);
            }

            @Override
            public void onFinish() {
            }
        }.create();
    }

    private void initSkipTime(int duration) {
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

    private void createProgressPoints(int duration) {
        mTrackingEventsList.clear();
        for (String url : mAdParams.getImpressions()) {
            mTrackingEventsList.add(new TrackingEvent(url));
        }
        for (Tracking tracking : mAdParams.getEvents()) {
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.CREATIVE_VIEW)) {
                event.timeMillis = 0;
                mTrackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.START)) {
                event.timeMillis = 0;
                mTrackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.FIRST_QUARTILE)) {
                event.timeMillis = duration / 4;
                mTrackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.MIDPOINT)) {
                event.timeMillis = duration / 2;
                mTrackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.THIRD_QUARTILE)) {
                event.timeMillis = duration * 3 / 4;
                mTrackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.PROGRESS)) {
                if (tracking.getOffset() == null) {
                    continue;
                }
                if (tracking.getOffset().contains("%")) {
                    event.timeMillis = duration * Utils.parsePercent(mAdParams.getSkipTime()) / 100;
                } else {
                    event.timeMillis = Utils.parseDuration(tracking.getOffset()) * 1000;
                }
                mTrackingEventsList.add(event);
            }
        }
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mBaseAdInternal.onAdDidReachEnd();
            skipVideo(false);
            EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.COMPLETE);
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
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        if (mTimerWithPause != null) {
            mTimerWithPause.pause();
            mTimerWithPause = null;
        }
        if (TextUtils.isEmpty(mImageUri)) {
            closeSelf();
        } else {
            mViewControllerVast.showEndCard(mImageUri);
        }
        if (skipEvent) {
            EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.SKIP);
        }
    }

    @Override
    public void setVolume(boolean mute) {
        muteVideo(mute, true);
    }

    private void muteVideo(boolean mute, boolean postEvent) {
        if (mMediaPlayer == null) {
            return;
        }
        if (mute) {
            mMediaPlayer.setVolume(0f, 0f);
            if (postEvent) {
                EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.MUTE);
            }
        } else {
            float systemVolume = Utils.getSystemVolume();
            mMediaPlayer.setVolume(systemVolume, systemVolume);
            if (postEvent) {
                EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.UNMUTE);
            }
        }
    }

    @Override
    public void openUrl(String url) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            url = mAdParams.getVideoRedirectUrl();
            for (String trackUrl : mAdParams.getVideoClicks()) {
                EventTracker.post(trackUrl);
            }
        } else {
            url = mAdParams.getEndCardRedirectUrl();
            for (String trackUrl : mAdParams.getEndCardClicks()) {
                EventTracker.post(trackUrl);
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Logger.d(LOG_TAG, "Handle external url");
        if (Utils.isOnline()) {
            Context context = mBaseAdInternal.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else {
            Logger.e(LOG_TAG, "No internet connection");
        }
    }

    public void closeSelf() {
        EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.CLOSE);
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
            EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.PAUSE);
        }
    }

    @Override
    public void resume() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying() && mViewControllerVast.isEndCard()) {
            playAd();
            EventTracker.postEventByType(mAdParams.getEvents(), EventConstants.RESUME);
        }
    }
}
