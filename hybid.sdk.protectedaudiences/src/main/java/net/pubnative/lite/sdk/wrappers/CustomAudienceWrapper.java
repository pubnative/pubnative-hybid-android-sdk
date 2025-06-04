// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.wrappers;

import android.adservices.common.AdData;
import android.adservices.common.AdSelectionSignals;
import android.adservices.common.AdTechIdentifier;
import android.adservices.customaudience.CustomAudience;
import android.adservices.customaudience.TrustedBiddingData;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import net.pubnative.lite.sdk.clients.CustomAudienceClient;
import net.pubnative.lite.sdk.utils.Logger;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.json.JSONObject;

@RequiresApi(api = 34)
public class CustomAudienceWrapper {
    private static final String TAG = CustomAudienceWrapper.class.getSimpleName();
    private final Executor mExecutor;
    private final CustomAudienceClient mCaClient;

    public CustomAudienceWrapper(Context context, Executor executor) {
        mExecutor = executor;
        mCaClient = new CustomAudienceClient.Builder().setContext(context).setExecutor(executor).build();
    }

    /**
     * Joins a CA.
     *
     * @param name              The name of the CA to join.
     * @param owner             The owner of the CA
     * @param buyer             The buyer of ads
     * @param biddingUri        The URL to retrieve the bidding logic
     * @param renderUri         The URL to render the ad
     * @param dailyUpdateUri    The URL for daily updates for the CA
     * @param trustedBiddingUri The URL to retrieve trusted bidding data
     * @param statusReceiver    A consumer function that is run after the API call and returns a
     *                          string indicating the outcome of the call.
     */
    public void joinCa(String name, String owner, AdTechIdentifier buyer, Uri biddingUri,
                       Uri renderUri, Uri dailyUpdateUri, Uri trustedBiddingUri, Consumer<String> statusReceiver,
                       Instant expiry) {
        try {
            joinCustomAudience(
                    new CustomAudience.Builder()
                            .setBuyer(buyer)
                            .setName(name)
                            .setDailyUpdateUri(dailyUpdateUri)
                            .setBiddingLogicUri(biddingUri)
                            .setAds(Collections.singletonList(new AdData.Builder()
                                    .setRenderUri(renderUri)
                                    .setMetadata(new JSONObject().toString())
                                    .build()))
                            .setActivationTime(Instant.now())
                            .setExpirationTime(expiry)
                            .setTrustedBiddingData(new TrustedBiddingData.Builder()
                                    .setTrustedBiddingKeys(Collections.singletonList("key"))
                                    .setTrustedBiddingUri(trustedBiddingUri).build())
                            .setUserBiddingSignals(AdSelectionSignals.EMPTY)
                            .build(),
                    statusReceiver);
        } catch (Exception e) {
            statusReceiver.accept("Got the following exception when trying to join " + name
                    + " custom audience: " + e);
            Logger.e(TAG, "Exception calling joinCustomAudience", e);
        }
    }

    /**
     * Creates a CA with empty user bidding signals, trusted bidding data, and ads.
     *
     * @param name           The name of the CA to join.
     * @param owner          The owner of the CA
     * @param buyer          The buyer of ads
     * @param biddingUri     The URL to retrieve the bidding logic
     * @param dailyUpdateUri The URL for daily updates for the CA
     * @param statusReceiver A consumer function that is run after the API call and returns a
     */
    public void joinEmptyFieldsCa(String name, String owner, AdTechIdentifier buyer, Uri biddingUri,
                                  Uri dailyUpdateUri, Consumer<String> statusReceiver, Instant expiry) {
        try {
            joinCustomAudience(
                    new CustomAudience.Builder()
                            .setBuyer(buyer)
                            .setName(name)
                            .setDailyUpdateUri(dailyUpdateUri)
                            .setBiddingLogicUri(biddingUri)
                            .setActivationTime(Instant.now())
                            .setExpirationTime(expiry)
                            .build(),
                    statusReceiver);
        } catch (Exception e) {
            statusReceiver.accept("Got the following exception when trying to join " + name
                    + " custom audience: " + e);
            Logger.e(TAG, "Exception calling joinCustomAudience", e);
        }
    }

    /**
     * Leaves a CA.
     *
     * @param name           The name of the CA to leave.
     * @param statusReceiver A consumer function that is run after the API call and returns a
     *                       string indicating the outcome of the call.
     */
    public void leaveCa(String name, String owner, AdTechIdentifier buyer, Consumer<String> statusReceiver) {
        try {
            Futures.addCallback(mCaClient.leaveCustomAudience(owner, buyer, name),
                    new FutureCallback<Void>() {
                        public void onSuccess(Void unused) {
                            statusReceiver.accept("Left " + name + " custom audience");
                        }

                        public void onFailure(@NonNull Throwable e) {
                            statusReceiver.accept("Error when leaving " + name
                                    + " custom audience: " + e.getMessage());
                        }
                    }, mExecutor);
        } catch (Exception e) {
            statusReceiver.accept("Got the following exception when trying to leave " + name
                    + " custom audience: " + e);
            Logger.e(TAG, "Exception calling leaveCustomAudience", e);
        }
    }

    private void joinCustomAudience(CustomAudience ca, Consumer<String> statusReceiver) {
        Futures.addCallback(mCaClient.joinCustomAudience(ca),
                new FutureCallback<Void>() {
                    public void onSuccess(Void unused) {
                        statusReceiver.accept("Joined " + ca.getName() + " custom audience");
                    }

                    public void onFailure(@NonNull Throwable e) {
                        statusReceiver.accept("Error when joining " + ca.getName() + " custom audience: "
                                + e.getMessage());
                        Logger.e(TAG, "Exception during CA join process ", e);
                    }
                }, mExecutor);
    }

    public boolean isApiAvailable() {
        return mCaClient != null && mCaClient.isApiAvailable();
    }
}
