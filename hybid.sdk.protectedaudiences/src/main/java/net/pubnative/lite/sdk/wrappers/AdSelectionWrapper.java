// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.wrappers;

import android.adservices.adselection.AdSelectionConfig;
import android.adservices.adselection.AdSelectionOutcome;
import android.adservices.adselection.ReportImpressionRequest;
import android.adservices.common.AdSelectionSignals;
import android.adservices.common.AdTechIdentifier;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import net.pubnative.lite.sdk.clients.AdSelectionClient;
import net.pubnative.lite.sdk.models.BuyerSignal;
import net.pubnative.lite.sdk.models.BuyerSignals;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Wrapper for the FLEDGE Ad Selection API. This wrapper is opinionated and makes several
 * choices such as running impression reporting immediately after every successful ad auction or leaving
 * the ad signals empty to limit the complexity that is exposed the user.
 */
@RequiresApi(api = 34)
public class AdSelectionWrapper {
    private static final String TAG = AdSelectionWrapper.class.getSimpleName();
    private AdSelectionConfig mAdSelectionConfig;
    private final AdSelectionClient mAdClient;
    private final Executor mExecutor;

    /**
     * Initializes the ad selection wrapper with a specific seller, list of buyers, and decision
     * endpoint.
     *
     * @param buyers      A list of buyers for the auction.
     * @param seller      The name of the seller for the auction
     * @param decisionUri The URI to retrieve the seller scoring and reporting logic from
     * @param context     The application context.
     * @param executor    An executor to use with the FLEDGE API calls.
     */
    public AdSelectionWrapper(List<AdTechIdentifier> buyers, AdTechIdentifier seller, Uri decisionUri, Uri trustedDataUri, BuyerSignals buyerSignals, Context context,
                              Executor executor) {

        mAdSelectionConfig = new AdSelectionConfig.Builder()
                .setSeller(seller)
                .setDecisionLogicUri(decisionUri)
                .setCustomAudienceBuyers(buyers)
                .setAdSelectionSignals(AdSelectionSignals.EMPTY)
                .setSellerSignals(AdSelectionSignals.EMPTY)
                .setPerBuyerSignals(mapBuyerSignals(buyers, buyerSignals))
                .setTrustedScoringSignalsUri(trustedDataUri)
                .build();
        mAdClient = new AdSelectionClient.Builder().setContext(context).setExecutor(executor).build();
        mExecutor = executor;
    }

    private Map<AdTechIdentifier, AdSelectionSignals> mapBuyerSignals(List<AdTechIdentifier> buyers, BuyerSignals buyerSignals) {
        if (buyerSignals != null && buyerSignals.getBuyerSignals() != null && !buyerSignals.getBuyerSignals().isEmpty()) {
            Map<String, BuyerSignal> buyerSignalMap = buyerSignals.getBuyerSignals().stream()
                    .collect(Collectors.toMap(BuyerSignal::getOrigin, buyerSignal -> buyerSignal));

            return buyers.stream().collect(Collectors.toMap(buyer -> buyer, buyer -> {
                if (buyerSignalMap.containsKey(buyer.toString())) {
                    BuyerSignal signal = buyerSignalMap.get(buyer.toString());
                    if (signal != null && signal.getBuyerData() != null) {
                        return AdSelectionSignals.fromString(signal.getBuyerDataJson());
                    } else {
                        return AdSelectionSignals.EMPTY;
                    }
                } else {
                    return AdSelectionSignals.EMPTY;
                }
            }));
        } else {
            return buyers.stream()
                    .collect(Collectors.toMap(buyer -> buyer, buyer -> AdSelectionSignals.EMPTY));
        }
    }

    /**
     * Runs ad selection and passes a string describing its status to the input receivers. If ad
     * selection succeeds, also report impressions.
     *
     * @param statusReceiver    A consumer function that is run after ad selection and impression reporting
     *                          with a string describing how the auction and reporting went.
     * @param renderUriReceiver A consumer function that is run after ad selection with a message describing the render URI
     *                          or lack thereof.
     */
    public void runAdSelection(Consumer<String> statusReceiver, Consumer<String> renderUriReceiver) {
        try {
            Futures.addCallback(mAdClient.selectAds(mAdSelectionConfig),
                    new FutureCallback<AdSelectionOutcome>() {
                        public void onSuccess(AdSelectionOutcome adSelectionOutcome) {
                            statusReceiver.accept("Ran ad selection! Id: " + adSelectionOutcome.getAdSelectionId());
                            renderUriReceiver.accept("Would display ad from " + adSelectionOutcome.getRenderUri());
                        }

                        public void onFailure(@NonNull Throwable e) {
                            statusReceiver.accept("Error when running ad selection: " + e.getMessage());
                            renderUriReceiver.accept("Ad selection failed -- no ad to display");
                            Logger.e(TAG, "Exception during ad selection", e);
                        }
                    }, mExecutor);
        } catch (Exception e) {
            statusReceiver.accept("Got the following exception when trying to run ad selection: " + e);
            renderUriReceiver.accept("Ad selection failed -- no ad to display");
            Logger.e(TAG, "Exception calling runAdSelection", e);
        }

    }

    /**
     * Helper function of {@link #runAdSelection}. Runs impression reporting.
     *
     * @param adSelectionId  The auction to report impression on.
     * @param statusReceiver A consumer function that is run after impression reporting
     *                       with a string describing how the auction and reporting went.
     */
    public void reportImpression(long adSelectionId, Consumer<String> statusReceiver) {
        ReportImpressionRequest request = new ReportImpressionRequest(adSelectionId, mAdSelectionConfig);

        Futures.addCallback(mAdClient.reportImpression(request),
                new FutureCallback<>() {
                    public void onSuccess(Void unused) {
                        statusReceiver.accept("Reported impressions from ad selection");
                    }

                    public void onFailure(@NonNull Throwable e) {
                        statusReceiver.accept("Error when reporting impressions: " + e.getMessage());
                        Logger.e(TAG, e.toString(), e);
                    }
                }, mExecutor);
    }

    public boolean isApiAvailable() {
        return mAdClient != null && mAdClient.isApiAvailable();
    }
}
