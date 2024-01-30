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
import net.pubnative.lite.sdk.models.CustomCTAData;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.MediaPlayerErrors;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityFriendlyObstruction;
import net.pubnative.lite.sdk.viewability.HyBidViewabilityNativeVideoAdSession;
import net.pubnative.lite.sdk.vpaid.enums.EventConstants;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.helpers.ErrorLog;
import net.pubnative.lite.sdk.vpaid.helpers.EventTracker;
import net.pubnative.lite.sdk.vpaid.helpers.TimerWithPause;
import net.pubnative.lite.sdk.vpaid.macros.MacroHelper;
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
    private final boolean CLOSE_VIDEO_AFTER_FINISH_DEFAULT = false;
    private final boolean CLOSE_VIDEO_AFTER_FINISH_REWARDED_DEFAULT = false;

    private final ViewControllerVast mViewControllerVast;
    private final AdParams mAdParams;
    private final BaseVideoAdInternal mBaseAdInternal;
    private final MacroHelper mMacroHelper;

    private final List<TrackingEvent> mTrackingEventsList = new ArrayList<>();
    private final AdPresenter.ImpressionListener mImpressionListener;
    private final AdTracker mCreativeViewEventsTracker;
    private final IntegrationType mIntegrationType;
    private Boolean isCreativeViewEventsTracked = false;
    private MediaPlayer mMediaPlayer;
    private TimerWithPause mTimerWithPause;
    private TimerWithPause mSkipTimerWithPause;
    private String mVideoUri;
    private final List<EndCardData> mEndCardsData = new ArrayList<>();
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

    private boolean startFired = false;
    private boolean firstQuartileFired = false;
    private boolean midpointFired = false;
    private boolean thirdQuartileFired = false;

    private Boolean hasEndcard;
    private boolean isFullscreen = false;

    private final HyBidViewabilityNativeVideoAdSession mViewabilityAdSession;
    private final List<HyBidViewabilityFriendlyObstruction> mViewabilityFriendlyObstructions;
    private Boolean isAndroid6VersionDevice = false;

    private final VolumeObserver observer;

    private final Map<Action, List<Action>> mPendingActions = new LinkedHashMap<>();
    private final List<Action> mActions = new Vector<>();
    private final Handler mActionsProcessingHandler = new Handler(Looper.getMainLooper());
    private Boolean isActionsProcessingRun = false;
    private Action currentAction = Action.INITIAL;
    private Boolean isLastEndCardCustom = false;

    VideoAdControllerVast(BaseVideoAdInternal baseAd,
                          AdParams adParams,
                          HyBidViewabilityNativeVideoAdSession viewabilityAdSession,
                          boolean isFullscreen,
                          AdPresenter.ImpressionListener impressionListener,
                          AdCloseButtonListener adCloseButtonListener,
                          CustomCTAData customCTAData,
                          Integer customCTADelay,
                          IntegrationType integrationType) {
        mBaseAdInternal = baseAd;
        mAdParams = adParams;
        mViewabilityAdSession = viewabilityAdSession;
        mViewabilityFriendlyObstructions = new ArrayList<>();
        mViewControllerVast = new ViewControllerVast(
                this,
                isFullscreen,
                getEndcardCloseDelay(baseAd),
                getBackButtonDelay(baseAd),
                getFullScreenClickability(baseAd),
                adCloseButtonListener,
                customCTAData,
                customCTADelay
        );
        mMacroHelper = new MacroHelper();
        mCreativeViewEventsTracker = new AdTracker(getAdParams().getCompanionCreativeViewEvents(), null);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            isAndroid6VersionDevice = true;
        }
        if (isFullscreen) {
            this.videoVisible = true;
        }
        this.isFullscreen = isFullscreen;
        observer = VolumeObserver.getInstance();
        observer.registerVolumeObserver(this, mBaseAdInternal.getContext());
        this.mImpressionListener = impressionListener;

        if (isRewarded()) {
            isAutoClose = CLOSE_VIDEO_AFTER_FINISH_REWARDED_DEFAULT;
            if (mBaseAdInternal.getAd() != null)
                isAutoCloseRemoteConfig = mBaseAdInternal.getAd().needCloseRewardAfterFinish();
        } else {
            isAutoClose = CLOSE_VIDEO_AFTER_FINISH_DEFAULT;
            if (mBaseAdInternal.getAd() != null)
                isAutoCloseRemoteConfig = mBaseAdInternal.getAd().needCloseInterAfterFinish();
        }

        if (isAutoCloseRemoteConfig != null) isAutoClose = isAutoCloseRemoteConfig;

        hasEndcard = AdEndCardManager.getDefaultEndCard();
        mIntegrationType = integrationType;
    }

    private synchronized void addAction(Action action) {

        if (mActions.isEmpty() || !mActions.get(mActions.size() - 1).equals(action))
            mActions.add(action);

        if (!mPendingActions.isEmpty() && mPendingActions.containsKey(action)) {
            List<Action> pendingActions = mPendingActions.get(action);
            if (pendingActions != null && !pendingActions.isEmpty()) {
                mActions.addAll(pendingActions);
            }
            mPendingActions.remove(action);
        }
    }

    private synchronized void addPendingAction(Action action, Action waitingAction) {
        if (mPendingActions.containsKey(waitingAction) && mPendingActions.get(waitingAction) != null) {
            mPendingActions.get(waitingAction).add(action);
        } else {
            LinkedList<Action> newList = new LinkedList<>();
            newList.add(action);
            mPendingActions.put(waitingAction, newList);
        }
    }

    private synchronized void cancelPendingPauseAction() {

        if (!mActions.isEmpty() && mActions.get(mActions.size() - 1) == Action.PAUSE) {
            mActions.remove(mActions.size() - 1);
        }

        if (mPendingActions.containsKey(Action.PLAY)) {
            List<Action> pendingActions = mPendingActions.get(Action.PLAY);
            if (pendingActions != null && !pendingActions.isEmpty() && pendingActions.get(pendingActions.size() - 1).equals(Action.PAUSE)) {
                mPendingActions.get(Action.PLAY).remove(pendingActions.size() - 1);
            }
        }
    }

    private void clearAllActions() {
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
                if (!mActions.isEmpty()) mActions.remove(0);
                if (!mPendingActions.isEmpty() && mPendingActions.containsKey(currentAction)) {
                    List<Action> pendingActions = mPendingActions.get(currentAction);
                    if (pendingActions != null && !pendingActions.isEmpty()) {
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
            getViewabilityAdSession().firePause();
        }

        if (!isVideoCompleted && !isVideoSkipped) {
            fireReportingEvent(Reporting.EventType.VIDEO_PAUSE);
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.PAUSE, mMacroHelper, false);
        }
    }

    private void processResumeAction() {

        if (!isVideoCompleted && mMediaPlayer != null) {
            mMediaPlayer.setSurface(mViewControllerVast.getSurface());
            mMediaPlayer.start();
        }

        if (isVideoCompleted) {
            recoverMediaPlayerSurface();
        }

        if (mTimerWithPause != null && mTimerWithPause.isPaused()) {
            mTimerWithPause.resume();
        }

        if (mSkipTimerWithPause != null && mSkipTimerWithPause.isPaused()) {
            mSkipTimerWithPause.resume();
        }

        if (!isVideoCompleted && !isVideoSkipped) {
            fireReportingEvent(Reporting.EventType.VIDEO_RESUME);
            getViewabilityAdSession().fireResume();
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.RESUME, mMacroHelper, false);
        }
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
    public void onEndCardClosed(Boolean isCustomEndCard) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onEndCardClosed(isCustomEndCard);
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
    public void addEndCardData(EndCardData endCardData) {
        if (endCardData != null) {
            this.mEndCardsData.add(endCardData);
        }
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
        addAction(Action.PREPARE);
        addAction(Action.PLAY);
        processActions();
    }

    @Override
    public void pause() {
        if (currentAction == Action.INITIAL) {
            addPendingAction(Action.PAUSE, Action.PLAY);
        } else {
            addAction(Action.PAUSE);
        }
        mViewControllerVast.pause();
        processActions();
    }

    private void resumeAd() {
        if (currentAction == Action.PAUSE && !isVideoSkipped) {
            addAction(Action.RESUME);
        } else if (isVideoVisible()) {
            cancelPendingPauseAction();
        }
        mViewControllerVast.resume();
        processActions();
    }

    private final MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (extra == MediaPlayerErrors.MEDIA_ERROR_SYSTEM || extra == MediaPlayerErrors.MEDIA_ERROR_SYSTEM_CALLBACK) {
                return false;
            }
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
        addMissingDefaultEvents(duration);
        hasEndcard = AdEndCardManager.isEndCardEnabled(mBaseAdInternal.getAd());

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
                if (mViewControllerVast != null) {
                    mViewControllerVast.resetProgress();
                    handleMediaPlayerComplete();
                }
            }
        }.create();

        if (mSkipTimeMillis > 0 && isFullscreen) {
            mSkipTimerWithPause = new TimerWithPause(mSkipTimeMillis, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mViewControllerVast.setSkipProgress((int) millisUntilFinished, mSkipTimeMillis);
                }

                @Override
                public void onFinish() {
                    if (mViewControllerVast != null) {
                        mViewControllerVast.endSkip(isAutoClose, hasEndcard);
                    }
                }
            }.create();
        } else if (mSkipTimeMillis == 0) {
            if (mViewControllerVast != null) {
                mViewControllerVast.endSkip(isAutoClose, hasEndcard);
            }
        }
    }

    private void fireViewabilityTrackingEvent(String name) {
        if (!TextUtils.isEmpty(name)) {
            switch (name) {
                case EventConstants.START:
                    getViewabilityAdSession().fireStart(getAdParams().getDuration(), true);
                    if (!startFired) {
                        fireReportingEvent(Reporting.EventType.VIDEO_STARTED);
                        startFired = true;
                    }
                    break;
                case EventConstants.FIRST_QUARTILE:
                    getViewabilityAdSession().fireFirstQuartile();
                    if (!firstQuartileFired) {
                        fireReportingEvent(Reporting.EventType.VIDEO_AD_FIRST_QUARTILE);
                        firstQuartileFired = true;
                    }
                    break;
                case EventConstants.MIDPOINT:
                    getViewabilityAdSession().fireMidpoint();
                    if (!midpointFired) {
                        fireReportingEvent(Reporting.EventType.VIDEO_AD_MIDPOINT);
                        midpointFired = true;
                    }
                    break;
                case EventConstants.THIRD_QUARTILE:
                    getViewabilityAdSession().fireThirdQuartile();
                    if (!thirdQuartileFired) {
                        fireReportingEvent(Reporting.EventType.VIDEO_AD_THIRD_QUARTILE);
                        thirdQuartileFired = true;
                    }
                    break;
            }
        }
    }

    private void initSkipTime(int duration) {
        if (mBaseAdInternal == null || mBaseAdInternal.getAd() == null) return;

        int adParamsSkipTime = -1;
        int publisherSkipTime = -1;

        if (mAdParams != null) {
            publisherSkipTime = mAdParams.getPublisherSkipSeconds();
            if (!TextUtils.isEmpty(mAdParams.getSkipTime())) {
                if (mAdParams.getSkipTime().contains("%")) {
                    adParamsSkipTime = duration * Utils.parsePercent(mAdParams.getSkipTime()) / 100;
                } else {
                    adParamsSkipTime = Utils.parseDuration(mAdParams.getSkipTime()) * 1000;
                }
            }
        }

        hasEndcard = AdEndCardManager.isEndCardEnabled(mBaseAdInternal.getAd());

        if (isRewarded()) {
            mSkipTimeMillis = SkipOffsetManager.getRewardedSkipOffset(mBaseAdInternal.getAd().getVideoRewardedSkipOffset(), publisherSkipTime, adParamsSkipTime, hasEndcard) * 1000;
            // Add 500 to account for millisecond rounding
        } else {
            mSkipTimeMillis = SkipOffsetManager.getInterstitialVideoSkipOffset(mBaseAdInternal.getAd().getVideoSkipOffset(), null, adParamsSkipTime, hasEndcard) * 1000;
        }
        if (mSkipTimeMillis > duration || (duration - mSkipTimeMillis < 500 && duration - mSkipTimeMillis >= 0)) {
            mSkipTimeMillis = -1;
        }
    }

    private void createProgressPoints(int duration) {
        mTrackingEventsList.clear();

        if (mAdParams == null) return;
        if (mAdParams.getImpressions() != null) {
            for (String url : mAdParams.getImpressions()) {
                mTrackingEventsList.add(new TrackingEvent(url));
            }
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

    private void addMissingDefaultEvents(int duration) {

        List<String> defaultEvents = List.of(
                EventConstants.START,
                EventConstants.FIRST_QUARTILE,
                EventConstants.MIDPOINT,
                EventConstants.THIRD_QUARTILE
        );

        for (String eventName : defaultEvents) {

            boolean isContains = false;
            for (TrackingEvent trackingEvent : mTrackingEventsList) {
                if (trackingEvent.name != null && trackingEvent.name.equalsIgnoreCase(eventName)) {
                    isContains = true;
                    break;
                }
            }

            if (!isContains) {
                mTrackingEventsList.add(createDefaultEvent(eventName, null, duration, null));
            }
        }
    }

    private TrackingEvent createDefaultEvent(String name, String url, int duration, String offset) {
        TrackingEvent event = new TrackingEvent(url);
        event.name = name;
        if (name.equalsIgnoreCase(EventConstants.START)) {
            event.timeMillis = 0;
            event.name = EventConstants.START;
            containsStartEvent = true;
        }
        if (name.equalsIgnoreCase(EventConstants.FIRST_QUARTILE)) {
            event.timeMillis = duration / 4;
            event.name = EventConstants.FIRST_QUARTILE;
        }
        if (name.equalsIgnoreCase(EventConstants.MIDPOINT)) {
            event.timeMillis = duration / 2;
            event.name = EventConstants.MIDPOINT;
        }
        if (name.equalsIgnoreCase(EventConstants.THIRD_QUARTILE)) {
            event.timeMillis = duration * 3 / 4;
            event.name = EventConstants.THIRD_QUARTILE;
        }
        return event;
    }

    private final MediaPlayer.OnCompletionListener mOnCompletionListener = mp -> handleMediaPlayerComplete();

    private void postDelayed(Runnable action) {
        mViewControllerVast.postDelayed(action, VideoAdControllerVast.DELAY_UNTIL_EXECUTE);
    }

    @Override
    public void skipVideo() {
        mViewControllerVast.hideSkipButton();
        mViewControllerVast.hideTimerAndMuteButton();
        skipVideo(true);
        isVideoSkipped = true;
    }

    @Override
    public void skipEndCard() {

        EndCardData endCardData = getNextEndCard();
        Boolean isLastEndCard = !hasNextEndCard();

        if (endCardData == null || !isEndCardShowable() || (endCardData.getType() == EndCardData.Type.STATIC_RESOURCE && TextUtils.isEmpty(mImageUri))) {
            closeSelf();
            return;
        }

        if (mBaseAdInternal != null) {
            mBaseAdInternal.onEndCardSkipped(isLastEndCardCustom);
            mViewControllerVast.showEndCard(endCardData, mImageUri, isLastEndCard, mBaseAdInternal::onAdCloseButtonVisible, mBaseAdInternal::onBackButtonClickable);
        }
    }

    @Override
    public void closeEndCard() {
        closeSelf();
    }

    boolean isAutoClose;
    Boolean isAutoCloseRemoteConfig = null;

    private void skipVideo(boolean skipEvent) {

        if (finishedPlaying) return;

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
            fireReportingEvent(Reporting.EventType.VIDEO_AD_SKIPPED);
            EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.SKIP, mMacroHelper, true);
        }


        if (isAutoClose) {
            hasEndcard = false;
            closeSelf();
            return;
        } else {
            EndCardData endCardData = getNextEndCard();
            Boolean isLastEndCard = !hasNextEndCard();
            if (endCardData == null || !isEndCardShowable() || (endCardData.getType() == EndCardData.Type.STATIC_RESOURCE && TextUtils.isEmpty(mImageUri))) {
                if (skipEvent) {
                    closeSelf();
                } else {
                    if (mBaseAdInternal != null) {
                        mBaseAdInternal.onAdCloseButtonVisible();
                    }
                }
            } else {
                hasEndcard = true;
                isLastEndCardCustom = endCardData.isCustom();
                if (mBaseAdInternal != null) {
                    mViewControllerVast.showEndCard(endCardData, mImageUri, isLastEndCard, mBaseAdInternal::onAdCloseButtonVisible, mBaseAdInternal::onBackButtonClickable);
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
                    fireReportingEvent(Reporting.EventType.VIDEO_MUTE);
                    EventTracker.postEventByType(mBaseAdInternal.getContext(), mAdParams.getEvents(), EventConstants.MUTE, mMacroHelper, false);
                }
            } else {
                float systemVolume = Utils.getSystemVolume(mBaseAdInternal.getContext());
                mMediaPlayer.setVolume(systemVolume, systemVolume);
                if (postEvent) {
                    fireReportingEvent(Reporting.EventType.VIDEO_UNMUTE);
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
    public void openUrl(String url, Boolean isCustomEndCard, Boolean isCTAClick) {
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
            if (!isCustomEndCard)
                mBaseAdInternal.onAdClicked();
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

        if (isCustomEndCard || isCTAClick)
            return;

        mBaseAdInternal.onAdClicked();
    }

    @Override
    public void onCustomEndCardShow(String endCardType) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onCustomEndCardShow(endCardType);
    }

    public void onCustomCTAShow() {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onCustomCTAShow();
    }

    @Override
    public void onCustomCTAClick(boolean isEndcardVisible) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onCustomCTAClick(isEndcardVisible);
    }

    @Override
    public void onCustomCTALoadFail() {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onCustomCTALoadFail();
    }

    @Override
    public void onDefaultEndCardShow(String endCardType) {

        if (mBaseAdInternal != null)
            mBaseAdInternal.onDefaultEndCardShow(endCardType);

        if (mCreativeViewEventsTracker != null && !isCreativeViewEventsTracked) {
            mCreativeViewEventsTracker.trackImpression();
            if (HyBid.getReportingController() != null) {
                ReportingEvent reportingEvent = new ReportingEvent();
                reportingEvent.setEventType(Reporting.EventType.CREATIVE_VIEW);
                if (isRewarded()) {
                    reportingEvent.setAdFormat(Reporting.AdFormat.REWARDED);
                } else if (isFullscreen) {
                    reportingEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
                } else {
                    reportingEvent.setAdFormat(Reporting.AdFormat.BANNER);
                }
                reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
                reportingEvent.setPlatform(Reporting.Platform.ANDROID);
                reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
                reportingEvent.setTimestamp(System.currentTimeMillis());
                if (mBaseAdInternal != null && mBaseAdInternal.getAd() != null) {
                    reportingEvent.setImpId(mBaseAdInternal.getAd().getSessionId());
                    reportingEvent.setCampaignId(mBaseAdInternal.getAd().getCampaignId());
                    reportingEvent.setConfigId(mBaseAdInternal.getAd().getConfigId());
                }
                HyBid.getReportingController().reportEvent(reportingEvent);
            }
            isCreativeViewEventsTracked = true;
        }
    }

    @Override
    public void onCustomEndCardClick(String endCardType) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onCustomEndCardClick(endCardType);
    }

    @Override
    public void onDefaultEndCardClick(String endCardType) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onDefaultEndCardClick(endCardType);
    }

    @Override
    public void onEndCardLoadSuccess(Boolean isCustomEndCard) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onEndCardLoadSuccess(isCustomEndCard);
    }

    @Override
    public void onEndCardLoadFail(Boolean isCustomEndCard) {
        if (mBaseAdInternal != null)
            mBaseAdInternal.onEndCardLoadFail(isCustomEndCard);
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
            if (mViewControllerVast != null && mViewControllerVast.getTexture() != null) {
                mViewControllerVast.getTexture().setSurfaceTextureListener(mCreateTextureListener);
            } else {
                resumeAd();
            }
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
        if (isVideoCompleted) return;
        mViewControllerVast.hideSkipButton();
        isVideoCompleted = true;
        mViewControllerVast.hideTimerAndMuteButton();
        mBaseAdInternal.onAdDidReachEnd();
        fireReportingEvent(Reporting.EventType.VIDEO_AD_COMPLETE);
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

    private EndCardData getNextEndCard() {
        if (mEndCardsData.isEmpty()) return null;
        EndCardData endCardData = mEndCardsData.get(0);
        mEndCardsData.remove(0);
        return endCardData;
    }

    private Boolean hasNextEndCard() {
        return !mEndCardsData.isEmpty();
    }

    private boolean isEndCardShowable() {
        Ad ad = mBaseAdInternal.getAd();
        if (ad != null) {
            return AdEndCardManager.isEndCardEnabled(ad);
        }
        return false;
    }

    private Integer getEndcardCloseDelay(BaseVideoAdInternal baseAd) {
        Integer endcardCloseDelay = null;
        if (baseAd != null && baseAd.getAd() != null) {
            endcardCloseDelay = baseAd.getAd().getEndCardCloseDelay();
        }
        return endcardCloseDelay;
    }

    private Integer getBackButtonDelay(BaseVideoAdInternal baseAd) {
        Integer backButtonDelay = null;
        if (baseAd != null && baseAd.getAd() != null) {
            backButtonDelay = baseAd.getAd().getBackButtonDelay();
        }
        return backButtonDelay;
    }

    private Integer getNativeCloseButtonDelay(BaseVideoAdInternal baseAd) {
        Integer nativeCloseButtonDelay = null;
        if (baseAd != null && baseAd.getAd() != null) {
            nativeCloseButtonDelay = getCloseButtonDelay(baseAd.getAd());
        }
        return nativeCloseButtonDelay;
    }

    public Integer getCloseButtonDelay(Ad ad) {
        return SkipOffsetManager.getNativeCloseButtonDelay(ad.getNativeCloseButtonDelay());
    }

    private Boolean getFullScreenClickability(BaseVideoAdInternal baseAd) {
        Boolean fullScreenClickability = null;
        if (baseAd != null && baseAd.getAd() != null) {
            fullScreenClickability = baseAd.getAd().getFullScreenClickability();
        }
        return fullScreenClickability;
    }

    private void fireReportingEvent(String eventType) {
        ReportingEvent event = new ReportingEvent();
        event.setEventType(eventType);
        if (isRewarded()) {
            event.setAdFormat(Reporting.AdFormat.REWARDED);
        } else if (isFullscreen) {
            event.setAdFormat(Reporting.AdFormat.FULLSCREEN);
        } else {
            event.setAdFormat(Reporting.AdFormat.BANNER);
        }
        event.setCreativeType(Reporting.CreativeType.VIDEO);
        event.setPlatform(Reporting.Platform.ANDROID);
        event.setSdkVersion(HyBid.getSDKVersionInfo(IntegrationType.STANDALONE));
        event.setTimestamp(System.currentTimeMillis());
        if (mBaseAdInternal != null && mBaseAdInternal.getAd() != null) {
            event.setImpId(mBaseAdInternal.getAd().getSessionId());
            event.setCampaignId(mBaseAdInternal.getAd().getCampaignId());
            event.setConfigId(mBaseAdInternal.getAd().getConfigId());
        }
        if (HyBid.getReportingController() != null) {
            HyBid.getReportingController().reportEvent(event);
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
        PREPARE, PLAY, PAUSE, RESUME, INITIAL,
    }
}
