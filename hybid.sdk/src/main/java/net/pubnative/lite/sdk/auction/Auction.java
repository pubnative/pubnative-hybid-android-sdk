package net.pubnative.lite.sdk.auction;

import android.content.Context;
import android.os.CountDownTimer;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Auction {
    private static final String TAG = Auction.class.getSimpleName();

    public interface Listener {
        void onSuccess(List<Ad> auctionResult);

        void onFailure(Throwable error);
    }

    private enum AuctionState {
        READY, AWAITING_RESPONSES, PROCESSING_RESULTS, DONE
    }

    private final Context mContext;
    private final Listener mListener;
    private final CountDownTimer mTimer;
    private List<AdSource> mAuctionAdSources;
    private List<Ad> mAdResponses;
    private int mMissingResponses;
    private AuctionState mAuctionState;

    public Auction(Context context, List<AdSource> adSources, long timeoutInMillis, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mAuctionAdSources = new ArrayList<>(adSources.size());
        this.mAuctionAdSources.addAll(adSources);
        this.mAdResponses = Collections.synchronizedList(new ArrayList<Ad>());

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
        mAuctionState = AuctionState.AWAITING_RESPONSES;
        mTimer.start();
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
                mMissingResponses--;
                if (mAuctionState == AuctionState.AWAITING_RESPONSES && mMissingResponses <= 0) {
                    processResults();
                }
            }
        }

        @Override
        public void onError(Throwable error) {
            Logger.e(TAG, "Error fetching from ad source: ", error);
            mMissingResponses--;
            if (mAuctionState == AuctionState.AWAITING_RESPONSES && mMissingResponses <= 0) {
                processResults();
            }
        }
    };

    private void processResults() {
        mAuctionState = AuctionState.PROCESSING_RESULTS;

        Collections.sort(mAdResponses, mAdBidComparator);

        if (!mAdResponses.isEmpty()) {
            if (mListener !=  null) {
                mAuctionState = AuctionState.DONE;
                mListener.onSuccess(mAdResponses);
            }
        } else {
            if (mListener != null) {
                mAuctionState = AuctionState.DONE;
                mListener.onFailure(new Exception("The auction concluded without any winning bid."));
            }
        }
    }

    private final Comparator<Ad> mAdBidComparator = new Comparator<Ad>() {
        @Override
        public int compare(Ad ad1, Ad ad2) {
            return (ad2.getECPM() != null ? ad2.getECPM() : 0) - (ad1.getECPM() != null ? ad1.getECPM() : 0);
        }
    };
}
