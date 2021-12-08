package net.pubnative.lite.sdk.auction;

import android.os.CountDownTimer;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Auction {

    private static final String TAG = Auction.class.getSimpleName();

    public interface Listener {
        void onAuctionSuccess(PriorityQueue<Ad> auctionResult);

        void onAuctionFailure(Throwable error);
    }

    private enum AuctionState {
        READY, AWAITING_RESPONSES, PROCESSING_RESULTS, DONE
    }

    private final ReportingController mReportingController;
    private final Listener mListener;
    private final CountDownTimer mTimer;
    private final List<AdSource> mAuctionAdSources;
    private final List<Ad> mAdResponses;
    private int mMissingResponses;
    private AuctionState mAuctionState;
    private final String mAdFormat;
    private AdSize mAdSize;
    private final List<String> mFillList;
    private final List<String> mNoFillList;

    public Auction(List<AdSource> adSources, long timeoutInMillis, ReportingController reportingController, Listener listener, String adFormat) {
        this.mReportingController = reportingController;
        this.mListener = listener;
        this.mAuctionAdSources = new ArrayList<>(adSources.size());
        this.mAuctionAdSources.addAll(adSources);
        this.mAdResponses = Collections.synchronizedList(new ArrayList<Ad>());
        this.mAdFormat = adFormat;
        this.mFillList = new ArrayList<>();
        this.mNoFillList = new ArrayList<>();

        this.mTimer = new CountDownTimer(timeoutInMillis, timeoutInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }

            @Override
            public void onFinish() {
                if (mAuctionState == AuctionState.AWAITING_RESPONSES) {
                    processResults();
                }
            }
        };

        this.mAuctionState = AuctionState.READY;
    }

    public void runAuction() {
        mMissingResponses = mAuctionAdSources.size();
        mAdResponses.clear();
        mFillList.clear();
        mNoFillList.clear();
        mAuctionState = AuctionState.AWAITING_RESPONSES;
        mTimer.start();
        reportAuctionStart();
        requestFromAdSources();
    }

    private void requestFromAdSources() {
        for (AdSource adSource : mAuctionAdSources) {
            adSource.fetchAd(mAdSourceListener);
        }
    }

    private final AdSource.Listener mAdSourceListener = new AdSource.Listener() {
        @Override
        public void onAdFetched(Ad ad) {
            if (ad != null) {
                mAdResponses.add(ad);
                if (!TextUtils.isEmpty(ad.getAdSourceName())) {
                    mFillList.add(ad.getAdSourceName());
                }
                mMissingResponses--;
                if (mAuctionState == AuctionState.AWAITING_RESPONSES && mMissingResponses <= 0) {
                    processResults();
                }
            }
        }

        @Override
        public void onError(AuctionError error) {
            Logger.e(TAG, "Error fetching from ad source: ", error.getError());
            if (!TextUtils.isEmpty(error.getAdSourceName())) {
                mNoFillList.add(error.getAdSourceName());
            }
            mMissingResponses--;
            if (mAuctionState == AuctionState.AWAITING_RESPONSES && mMissingResponses <= 0) {
                processResults();
            }
        }
    };

    private void processResults() {
        mAuctionState = AuctionState.PROCESSING_RESULTS;

        if (!mAdResponses.isEmpty()) {
            PriorityQueue<Ad> adQueue = new PriorityQueue<>(mAdResponses);
            if (mListener != null) {
                mAuctionState = AuctionState.DONE;
                reportAuctionFinished(adQueue.peek());
                mListener.onAuctionSuccess(adQueue);
            }
        } else {
            if (mListener != null) {
                mAuctionState = AuctionState.DONE;
                reportAuctionFinished();
                mListener.onAuctionFailure(new HyBidError(HyBidErrorCode.AUCTION_NO_AD));
            }
        }
    }

    private void reportAuctionStart() {
        ReportingEvent event = new ReportingEvent();
        event.setTimestamp(getTimestampString());
        event.setCustomString(Reporting.Key.EVENT_TYPE, Reporting.EventType.AUCTION_START);
        event.setAdFormat(mAdFormat);
        try {
            JSONObject participants = new JSONObject();
            for (AdSource adSource : mAuctionAdSources) {
                participants.put(adSource.getName(), adSource.getECPM());
                if (TextUtils.isEmpty(event.getAdSize())) {
                    mAdSize = adSource.getAdSize();
                    event.setAdSize(mAdSize.toString());
                }
            }
            event.setCustomJSONObject(Reporting.Key.PARTICIPANTS, participants);
        } catch (JSONException jsonException) {
            Logger.e(TAG, "Error adding participants to auction report: ", jsonException);
        }

        if (mReportingController != null) {
            mReportingController.reportEvent(event);
        }
    }

    private void reportAuctionFinished() {
        reportAuctionFinished(null);
    }

    private void reportAuctionFinished(Ad winner) {
        ReportingEvent event = new ReportingEvent();
        event.setTimestamp(getTimestampString());
        event.setCustomString(Reporting.Key.EVENT_TYPE, Reporting.EventType.AUCTION_FINISHED);
        event.setAdFormat(mAdFormat);
        if (mAdSize != null) {
            event.setAdSize(mAdSize.toString());
        }
        event.setCustomJSONArray(Reporting.EventType.FILL, new JSONArray(mFillList));
        event.setCustomJSONArray(Reporting.EventType.NO_FILL, new JSONArray(mNoFillList));

        if (winner != null && !TextUtils.isEmpty(winner.getAdSourceName())) {
            event.setCustomString(Reporting.EventType.WINNER, winner.getAdSourceName());
        }

        if (mReportingController != null) {
            mReportingController.reportEvent(event);
        }
    }

    public void reportAuctionNextItem(Ad nextAdSource) {
        ReportingEvent event = new ReportingEvent();
        event.setAdFormat(mAdFormat);
        if (mAdSize != null) {
            event.setAdSize(mAdSize.toString());
        }
        event.setTimestamp(getTimestampString());
        event.setCustomString(Reporting.Key.EVENT_TYPE, Reporting.EventType.AUCTION_NEXT_ITEM);
        String nextAdSourceName = nextAdSource.getAdSourceName();
        event.setCustomString(Reporting.EventType.NEXT_AD_SOURCE, nextAdSourceName);
    }

    private String getTimestampString() {
        Long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp);
    }
}
