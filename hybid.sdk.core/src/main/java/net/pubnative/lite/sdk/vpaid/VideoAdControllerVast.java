package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.FriendlyObstructionPurpose;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.enums.EventConstants;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause;
import net.pubnative.lite.sdk.vpaid.macros.MacroHelper;
import net.pubnative.lite.sdk.vpaid.models.CloseCardData;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.models.vast.Tracking;
import net.pubnative.lite.sdk.vpaid.models.vpaid.TrackingEvent;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.utils.Utils;
import net.pubnative.lite.sdk.vpaid.vast.ViewControllerVast;
import net.pubnative.lite.sdk.vpaid.volume.IVolumeObserver;
import net.pubnative.lite.sdk.vpaid.volume.VolumeObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class VideoAdControllerVast implements VideoAdController, IVolumeObserver {

    private static final String LOG_TAG = VideoAdControllerVast.class.getSimpleName();
    private static final int DELAY_UNTIL_EXECUTE = 100;

    private final ViewControllerVast mViewControllerVast;
    private final AdParams mAdParams;
    private final BaseVideoAdInternal mBaseAdInternal;
    private final MacroHelper mMacroHelper;

    private final List<TrackingEvent> mTrackingEventsList = new ArrayList<>();
    private final AdPresenter.ImpressionListener mImpressionListener;
    private MediaPlayer mMediaPlayer;
    private TimerWithPause mTimerWithPause;
    private TimerWithPause mSkipTimerWithPause;
    private String mVideoUri;
    private EndCardData mEndCardData;
    private CloseCardData mCloseCardData;
    private String mImageUri;
    private int mSkipTimeMillis = -1;
    private int mDuration = -1;
    private int mDoneMillis = -1;

    private boolean videoVisible = false;
    private boolean finishedPlaying = false;
    private boolean isImpressionFired = false;
    private boolean isVideoSkipped = false;
    private boolean isVideoCompleted = false;
    private boolean containsStartEvent = false;

    private boolean hasEndcard = false;

    private final HyBidViewabilityNativeVideoAdSession mViewabilityAdSession;
    private final List<HyBidViewabilityFriendlyObstruction> mViewabilityFriendlyObstructions;
    private Boolean isAndroid6VersionDevice = false;

    private final VolumeObserver observer;

    private final Map<Action, List<Action>> mPendingActions = new LinkedHashMap<>();
    private final List<Action> mActions = new Vector<>();
    private final Handler mActionsProcessingHandler = new Handler(Looper.getMainLooper());
    private Boolean isActionsProcessingRun = false;
    private Action currentAction = Action.INITIAL;

    VideoAdControllerVast(BaseVideoAdInternal baseAd, AdParams adParams, HyBidViewabilityNativeVideoAdSession viewabilityAdSession, boolean isFullscreen, AdPresenter.ImpressionListener impressionListener) {
        mBaseAdInternal = baseAd;
        mAdParams = adParams;
        mViewabilityAdSession = viewabilityAdSession;
        mViewabilityFriendlyObstructions = new ArrayList<>();
        mViewControllerVast = new ViewControllerVast(this, isFullscreen, getEndcardCloseDelay(baseAd), getFullScreenClickability(baseAd));
        mMacroHelper = new MacroHelper();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            isAndroid6VersionDevice = true;
        }
        if (isFullscreen) {
            this.videoVisible = true;
        }

        observer = VolumeObserver.getInstance();
        observer.registerVolumeObserver(this, mBaseAdInternal.getContext());
        this.mImpressionListener = impressionListener;
    }

    private synchronized void addAction(Action action) {

        if(mActions.isEmpty() || !mActions.get(mActions.size() - 1).equals(action))
            mActions.add(action);

        if(!mPendingActions.isEmpty() && mPendingActions.containsKey(action)){
            List<Action> pendingActions = mPendingActions.get(action);
            if(pendingActions != null && !pendingActions.isEmpty()){
                mActions.addAll(pendingActions);
            }
            mPendingActions.remove(action);
        }
    }

    private synchronized void addPendingAction(Action action, Action waitingAction) {
        if(mPendingActions.containsKey(waitingAction) && mPendingActions.get(waitingAction) != null){
            mPendingActions.get(waitingAction).add(action);
        }else{
            LinkedList<Action> newList = new LinkedList<>();
            newList.add(action);
            mPendingActions.put(waitingAction, newList);
        }
    }

    private synchronized void cancelPendingPauseAction(){

        if(!mActions.isEmpty() && mActions.get(mActions.size() -1) == Action.PAUSE){
            mActions.remove(mActions.size() -1);
        }

        if(mPendingActions.containsKey(Action.PLAY)){
            List<Action> pendingActions = mPendingActions.get(Action.PLAY);
            if(pendingActions != null && !pendingActions.isEmpty() &&
                    pendingActions.get(pendingActions.size() - 1).equals(Action.PAUSE)){
                mPendingActions.get(Action.PLAY).remove(pendingActions.size() - 1);
            }
        }
    }

    private void clearAllActions(){
        mActions.clear();
        mPendingActions.clear();
    }

    private synchronized void processActions() {

        if (mActions.isEmpty() || isActionsProcessingRun) return;
        isActionsProcessingRun = true;
        mActionsProcessingHandler.post(() -> {
            while (!mActions.isEmpty()) {
                Action currentAction = mActions.get(0);
                executeAction(currentAction);
                this.currentAction = currentAction;
                if(!mActions.isEmpty()) mActions.remove(0);
                if(!mPendingActions.isEmpty() && mPendingActions.containsKey(currentAction)){
                    List<Action> pendingActions = mPendingActions.get(currentAction);
                    if(pendingActions != null && !pendingActions.isEmpty()){
                        mActions.addAll(0, pendingActions);
                    }
                    mPendingActions.remove(currentAction);
                }
            }
            isActionsProcessingRun = false;
        });
    }

    private synchronized void executeAction(Action action) {
        switch (action) {
            case PREPARE:
                try {
                    processPrepareAction();
                } catch (IOException e) {
                    tryReInitMediaPlayer();
                }
                break;
            case PLAY:
                processPlayAction();
                break;
            case PAUSE:
                processPauseAction();
                break;
            case RESUME:
                processResumeAction();
                break;
        }
    }

    private void tryReInitMediaPlayer() {

        postDelayed(() -> {
            try {
                processPrepareAction();
            } catch (Exception e) {
                Logger.e(LOG_TAG, "mediaPlayer re-init: " + e.getMessage());
                closeSelf();
            }
        });
    }

    private void processPrepareAction() throws IOException, IllegalStateException {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mVideoUri);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Logger.e(LOG_TAG, "startMediaPlayer: " + e.getMessage());
            mBaseAdInternal.onAdLoadFailInternal(new PlayerInfo("Error loading media file"));
        }
    }

    private void processPlayAction() {

        if (mMediaPlayer == null) return;
        muteVideo(mViewControllerVast.isMute(), false);
        mViewControllerVast.adjustLayoutParams(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
        mMediaPlayer.setSurface(mViewControllerVast.getSurface());
        createTimer(mMediaPlayer.getDuration());
        getViewabilityAdSession().fireImpression();
        if (mBaseAdInternal != null && mBaseAdInternal.getAdListener() != null) {
            mBaseAdInternal.getAdListener().onAdStarted();
        }
        mMediaPlayer.start();
    }

    private void processPauseAction() {

        if (mTimerWithPause != null) {
            mTimerWithPause.pause();
        }

        if (mSkipTimerWithPause != null) {
            mSkipTimerWithPause.pause();
        }

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            ReportingEvent event = new ReportingEvent();
            event.setEventType(Reporting.EventType.VIDEO_PAUSE);
            event.setCreativeType(Reporting.CreativeType.VIDEO);
            event.setTimestamp(System.currentTimeMillis());
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(event);
            }
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.PAUSE, mMacroHelper, false);
            getViewabilityAdSession().firePause();
        }
    }

    private void processResumeAction() {

        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(mViewControllerVast.getSurface());
            mMediaPlayer.start();
        }

        if (mTimerWithPause != null && mTimerWithPause.isPaused()) {
            mTimerWithPause.resume();
        }

        if (mSkipTimerWithPause != null && mSkipTimerWithPause.isPaused()) {
            mSkipTimerWithPause.resume();
        }

        if (!isVideoCompleted) {
            ReportingEvent event = new ReportingEvent();
            event.setEventType(Reporting.EventType.VIDEO_RESUME);
            event.setCreativeType(Reporting.CreativeType.VIDEO);
            event.setTimestamp(System.currentTimeMillis());
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(event);
            }
        }
        EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.RESUME, mMacroHelper, false);
        getViewabilityAdSession().fireResume();
    }

    @Override
    public void resumeEndCardCloseButtonTimer() {
        mViewControllerVast.resumeEndCardCloseButtonTimer();
    }

    @Override
    public void pauseEndCardCloseButtonTimer() {
        mViewControllerVast.pauseEndCardCloseButtonTimer();
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
    public void setEndCardData(EndCardData endCardData) {
        this.mEndCardData = endCardData;
    }

    @Override
    public void setEndCardFilePath(String imageUri) {
        this.mImageUri = imageUri;
    }

    @Override
    public void setCloseCardData(CloseCardData closeCardData) {
        this.mCloseCardData = closeCardData;
    }

    @Override
    public void buildVideoAdView(VideoAdView bannerView) {
        mViewControllerVast.buildVideoAdView(bannerView);
    }

    @Override
    public void playAd() {
        addAction(Action.PREPARE);
        addAction(Action.PLAY);
        processActions();
    }

    @Override
    public void pause() {
        if(currentAction == Action.INITIAL){
            addPendingAction(Action.PAUSE, Action.PLAY);
        }else{
            addAction(Action.PAUSE);
        }
        processActions();
    }

    private void resumeAd() {

        if (currentAction == Action.PAUSE){
            addAction(Action.RESUME);
        }else if(isVideoVisible()){
            cancelPendingPauseAction();
        }
        processActions();
    }

    private final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //1 : MediaPlayer.MEDIA_ERROR_UNKNOWN
            //True if the method handled the error, false if it didn't. Returning false, or not having an OnErrorListener at all, will cause the OnCompletionListener to be called.
            ErrorLog.postError(mBaseAdInternal.getContext(), VastError.MEDIA_FILE_UNSUPPORTED);
            mBaseAdInternal.onAdLoadFailInternal(new PlayerInfo("Error loading media file"));
            return true;
        }
    };


    private void createTimer(final int duration) {
        mDuration = duration;
        mDoneMillis = -1;
        initSkipTime(duration);
        createProgressPoints(duration);

        mTimerWithPause = new TimerWithPause(duration, 10) {
            @Override
            public void onTick(long millisUntilFinished) {

                mViewControllerVast.setProgress((int) millisUntilFinished, duration);
                mDoneMillis = duration - (int) millisUntilFinished;

                if (!isImpressionFired && !containsStartEvent) {
                    mImpressionListener.onImpression();
                    isImpressionFired = true;
                }

                List<TrackingEvent> eventsToRemove = new ArrayList<>();
                for (TrackingEvent event : mTrackingEventsList) {
                    if (mDoneMillis > event.timeMillis) {
                        if (event.name != null && event.name.equals(EventConstants.START) && !isImpressionFired && containsStartEvent) {
                            mImpressionListener.onImpression();
                            isImpressionFired = true;
                        }
                        EventTracker.post(mBaseAdInternal.getContext(), event.url, mMacroHelper, false);
                        fireViewabilityTrackingEvent(event.name);
                        eventsToRemove.add(event);
                    }
                }

                mTrackingEventsList.removeAll(eventsToRemove);
            }

            @Override
            public void onFinish() {
                mViewControllerVast.resetProgress();
                handleMediaPlayerComplete();
            }
        }.create();

        if (mSkipTimeMillis > 0) {
            mSkipTimerWithPause = new TimerWithPause(mSkipTimeMillis, 1) {
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
        int unskippableVideoDurationMillis = mBaseAdInternal.getAd().getUnskippableVideoDuration() * 1000;
        int minimumSkipOffsetMillis = mBaseAdInternal.getAd().getMinimumSkipOffset() * 1000;
        int maximumSkipOffsetMillis = mBaseAdInternal.getAd().getMaximumSkipOffset() * 1000;
        int publisherSkipMilliseconds = mAdParams.getPublisherSkipSeconds() * 1000;
        SkipOffset globalSkip = HyBid.getVideoInterstitialSkipOffset();
        int globalSkipMilliseconds = globalSkip.getOffset() * 1000;
        int lowerLimit = 0;
        int upperLimit = 0;

        if (unskippableVideoDurationMillis >= duration) {
            mSkipTimeMillis = -1;
            return;
        } else {
            if (publisherSkipMilliseconds > 0) {
                lowerLimit = publisherSkipMilliseconds;
                upperLimit = publisherSkipMilliseconds;
            }

            if (minimumSkipOffsetMillis > 0) {
                lowerLimit = minimumSkipOffsetMillis;
                if (upperLimit > 0 && upperLimit < lowerLimit) {
                    upperLimit = lowerLimit;
                }
            }

            if (maximumSkipOffsetMillis > 0) {
                upperLimit = maximumSkipOffsetMillis;
                if (upperLimit < lowerLimit) {
                    upperLimit = lowerLimit;
                }
            }

            if (lowerLimit > 0 && lowerLimit > mSkipTimeMillis) {
                mSkipTimeMillis = lowerLimit;
            }

            if (upperLimit > 0 && mSkipTimeMillis > upperLimit) {
                mSkipTimeMillis = upperLimit;
            }
        }

        if (publisherSkipMilliseconds > 0 && publisherSkipMilliseconds > minimumSkipOffsetMillis && publisherSkipMilliseconds < maximumSkipOffsetMillis) {
            mSkipTimeMillis = publisherSkipMilliseconds;
        }

        if (globalSkipMilliseconds > 0 && globalSkipMilliseconds > minimumSkipOffsetMillis && globalSkipMilliseconds < maximumSkipOffsetMillis) {
            mSkipTimeMillis = globalSkipMilliseconds;
        }

        if (mSkipTimeMillis > duration) {
            mSkipTimeMillis = -1;
            return;
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
                    containsStartEvent = true;
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

    private final MediaPlayer.OnCompletionListener mOnCompletionListener = mp -> handleMediaPlayerComplete();

    private void postDelayed(Runnable action) {
        mViewControllerVast.postDelayed(action, VideoAdControllerVast.DELAY_UNTIL_EXECUTE);
    }

    @Override
    public void skipVideo() {
        skipVideo(true);
        isVideoSkipped = true;
    }

    @Override
    public void closeEndCard() {

        if (hasValidCloseCard()) {
            mViewControllerVast.showCloseCard(mCloseCardData);
        } else {
            closeSelf();
        }
    }

    private boolean hasValidCloseCard() {
        return mCloseCardData != null && mCloseCardData.getTitle() != null && !mCloseCardData.getTitle().isEmpty() && mCloseCardData.getIcon() != null && mCloseCardData.getBannerImage() != null;
    }

    boolean isAutoClose;
    Boolean isAutoCloseRemoteConfig = null;

    private void skipVideo(boolean skipEvent) {

        if (finishedPlaying)
            return;

        finishedPlaying = true;
        clearAllActions();

        if (skipEvent) {
            getViewabilityAdSession().fireSkipped();
            mBaseAdInternal.onAdSkipped();
        } else {
            if (!isVideoSkipped) getViewabilityAdSession().fireComplete();
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

        if (skipEvent) {

            ReportingEvent event = new ReportingEvent();
            event.setEventType(Reporting.EventType.VIDEO_AD_SKIPPED);
            event.setCreativeType(Reporting.CreativeType.VIDEO);
            event.setTimestamp(System.currentTimeMillis());
            if (HyBid.getReportingController() != null) {
                HyBid.getReportingController().reportEvent(event);
            }

            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.SKIP, mMacroHelper, true);
        }

        if (isRewarded()) {
            isAutoClose = HyBid.getCloseVideoAfterFinishForRewarded();
            if (mBaseAdInternal != null && mBaseAdInternal.getAd() != null)
                isAutoCloseRemoteConfig = mBaseAdInternal.getAd().needCloseRewardAfterFinish();
        } else {
            isAutoClose = HyBid.getCloseVideoAfterFinish();
            if (mBaseAdInternal != null && mBaseAdInternal.getAd() != null)
                isAutoCloseRemoteConfig = mBaseAdInternal.getAd().needCloseInterAfterFinish();
        }

        if (isAutoCloseRemoteConfig != null)
            isAutoClose = isAutoCloseRemoteConfig;

        if (isAutoClose) {
            hasEndcard = false;
            closeSelf();
            return;
        } else {
            if (mEndCardData == null || !isEndCardShowable() || (mEndCardData.getType() == EndCardData.Type.STATIC_RESOURCE && TextUtils.isEmpty(mImageUri))) {
                if (hasValidCloseCard()) {
                    mViewControllerVast.showCloseCard(mCloseCardData);
                    if (mBaseAdInternal != null) {
                        mBaseAdInternal.onAdCloseButtonVisible();
                    }
                } else if (skipEvent) {
                    closeSelf();
                }
            } else {
                hasEndcard = true;
                if (mBaseAdInternal != null) {
                    mViewControllerVast.showEndCard(mEndCardData, mImageUri, mBaseAdInternal::onAdCloseButtonVisible);
                }
            }
        }

        postDelayed(() -> {
            if (mBaseAdInternal != null && mBaseAdInternal.isInterstitial() && finishedPlaying && mImageUri == null && isAutoClose) {
                if (!hasEndcard) {
                    closeSelf();
                }
            }
        });
    }

    @Override
    public void toggleMute() {
        mViewControllerVast.muteVideo();
    }

    @Override
    public void setVolume(boolean mute) {
        muteVideo(mute, true);
    }

    private synchronized void muteVideo(boolean mute, boolean postEvent) {
        if (mMediaPlayer == null) {
            return;
        }

        try {
            getViewabilityAdSession().fireVolumeChange(mute);

            if (mute) {
                mMediaPlayer.setVolume(0f, 0f);
                if (postEvent) {
                    ReportingEvent event = new ReportingEvent();
                    event.setEventType(Reporting.EventType.VIDEO_MUTE);
                    event.setCreativeType(Reporting.CreativeType.VIDEO);
                    event.setTimestamp(System.currentTimeMillis());
                    if (HyBid.getReportingController() != null) {
                        HyBid.getReportingController().reportEvent(event);
                    }
                    EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.MUTE, mMacroHelper, false);
                }
            } else {
                float systemVolume = Utils.getSystemVolume(mBaseAdInternal.getContext());
                mMediaPlayer.setVolume(systemVolume, systemVolume);
                if (postEvent) {
                    ReportingEvent event = new ReportingEvent();
                    event.setEventType(Reporting.EventType.VIDEO_UNMUTE);
                    event.setCreativeType(Reporting.CreativeType.VIDEO);
                    event.setTimestamp(System.currentTimeMillis());
                    if (HyBid.getReportingController() != null) {
                        HyBid.getReportingController().reportEvent(event);
                    }
                    EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.UNMUTE, mMacroHelper, false);
                }
            }
        } catch (RuntimeException runtimeException) {
            HyBid.reportException(runtimeException);
            Logger.w(LOG_TAG, runtimeException.getMessage());
        }
    }

    private String trackVideoClicks() {
        String clickUrl = mAdParams.getVideoRedirectUrl();

        for (String trackUrl : mAdParams.getVideoClicks()) {
            EventTracker.post(mBaseAdInternal.getContext(), trackUrl, mMacroHelper, false);
        }

        return clickUrl;
    }

    private String trackEndCardClicks() {
        String clickUrl = mAdParams.getEndCardRedirectUrl();

        for (String trackUrl : mAdParams.getEndCardClicks()) {
            EventTracker.post(mBaseAdInternal.getContext(), trackUrl, mMacroHelper, false);
        }

        return clickUrl;
    }

    @Override
    public void openUrl(String url) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            url = trackVideoClicks();
        } else {
            url = trackEndCardClicks();

            String videoClickUrl = trackVideoClicks();
            if (url == null) {
                url = videoClickUrl;
            }
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        Logger.d(LOG_TAG, "Handle external url");
        if (Utils.isOnline(mBaseAdInternal.getContext())) {
            Context context = mBaseAdInternal.getContext();
            UrlHandler urlHandler = new UrlHandler(context);
            urlHandler.handleUrl(url);
        } else {
            Logger.e(LOG_TAG, "No internet connection");
        }

        mBaseAdInternal.onAdClicked();
    }

    public void closeSelf() {
        EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.CLOSE, mMacroHelper, true);
        EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.CLOSE_LINEAR, mMacroHelper, true);
        mBaseAdInternal.dismiss();
    }

    @Override
    public void dismiss() {
        mViewControllerVast.dismiss();
        observer.unregisterVolumeObserver(this, mBaseAdInternal.getContext());
    }

    @Override
    public void destroy() {

        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
            } catch (RuntimeException exception) {
                Logger.e(LOG_TAG, "Error releasing HyBid video player");
            }
        }

        if (currentAction == Action.INITIAL) {
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.NOT_USED, mMacroHelper, true);
        }

        finishedPlaying = true;

        if (mTimerWithPause != null) {
            mTimerWithPause.pause();
            mTimerWithPause = null;
        }

        if (mSkipTimerWithPause != null) {
            mSkipTimerWithPause.pause();
            mSkipTimerWithPause = null;
        }

        mViewControllerVast.destroy();

        clearAllActions();

        observer.unregisterVolumeObserver(this, mBaseAdInternal.getContext());
    }


    @Override
    public void resume() {
        if (isAndroid6VersionDevice && mMediaPlayer != null) {
            mViewControllerVast.getTexture().setSurfaceTextureListener(mCreateTextureListener);
        } else {
            resumeAd();
        }
    }

    private final TextureView.SurfaceTextureListener mCreateTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Surface asd = new Surface(surface);
            mMediaPlayer.setSurface(asd);
            if (!adFinishedPlaying()) resumeAd();
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

    private void handleMediaPlayerComplete() {
        if (isVideoCompleted)
            return;

        if (mBaseAdInternal.isInterstitial()) {
            mViewControllerVast.hideSkipButton();
        }
        isVideoCompleted = true;
        mViewControllerVast.hideTimerAndMuteButton();
        mBaseAdInternal.onAdDidReachEnd();
        skipVideo(false);
        if (!isVideoSkipped)
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.COMPLETE, mMacroHelper, true);
    }

    private void recoverMediaPlayerSurface() {

        if (mMediaPlayer == null) return;

        postDelayed(() -> {
            try {
                mMediaPlayer.setSurface(mViewControllerVast.getSurface());
                if (finishedPlaying) mMediaPlayer.seekTo(mDuration);
            } catch (IllegalStateException e) {
                Logger.e(LOG_TAG, "mediaPlayer cant recover surface: " + e.getMessage());
            }
        });
    }

    private boolean isEndCardShowable() {
        Ad ad = mBaseAdInternal.getAd();

        if (ad != null && ad.hasEndCard()) {
            if (ad.isEndCardEnabled() != null) {
                return ad.isEndCardEnabled();
            } else {
                return HyBid.isEndCardEnabled();
            }
        } else {
            return false;
        }
    }

    private Integer getEndcardCloseDelay(BaseVideoAdInternal baseAd) {
        Integer endcardCloseDelay = null;
        if (baseAd != null && baseAd.getAd() != null) {
            endcardCloseDelay = baseAd.getAd().getEndCardCloseDelay();
        }
        return endcardCloseDelay;
    }

    private Boolean getFullScreenClickability(BaseVideoAdInternal baseAd) {
        Boolean fullScreenClickability = null;
        if (baseAd != null && baseAd.getAd() != null) {
            fullScreenClickability = baseAd.getAd().getFullScreenClickability();
        }
        return fullScreenClickability;
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

    @Override
    public boolean isVideoVisible() {
        return videoVisible;
    }

    @Override
    public int getProgress() {
        if (mDoneMillis == -1 || mDuration == -1) {
            return -1;
        }
        return (mDoneMillis * 100) / mDuration;
    }

    @Override
    public void setVideoVisible(boolean videoVisible) {
        if (this.videoVisible && videoVisible) {
            recoverMediaPlayerSurface();
        }
        this.videoVisible = videoVisible;
    }

    @Override
    public void onSystemVolumeChanged() {
        muteVideo(mViewControllerVast.isMute(), false);
    }

    private enum Action {
        PREPARE,
        PLAY,
        PAUSE,
        RESUME,
        INITIAL,
    }
}
