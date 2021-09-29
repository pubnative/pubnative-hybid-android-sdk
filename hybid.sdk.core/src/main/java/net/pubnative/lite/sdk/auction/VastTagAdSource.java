package net.pubnative.lite.sdk.auction;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils;

import java.util.HashMap;
import java.util.Map;

public class VastTagAdSource implements AdSource {
    private static final String TAG = VastTagAdSource.class.getSimpleName();
    private final Context mContext;
    private final AdSourceConfig mConfig;
    private final AdSize mAdSize;

    public VastTagAdSource(Context context, AdSourceConfig config, AdSize adSize) {
        this.mContext = context;
        this.mConfig = config;
        this.mAdSize = adSize;
    }

    @Override
    public void fetchAd(final Listener listener) {
        if (mConfig != null && !TextUtils.isEmpty(mConfig.getVastTagUrl())) {
            Map<String, String> headers = new HashMap<>();
            String userAgent = HyBid.getDeviceInfo().getUserAgent();
            if (!TextUtils.isEmpty(userAgent)){
                headers.put("User-Agent", userAgent);
            }

            PNHttpClient.makeRequest(mContext, processTagUrl(mConfig.getVastTagUrl()),
                    headers, null, false,
                    new PNHttpClient.Listener() {
                        @Override
                        public void onSuccess(String response) {
                            if (TextUtils.isEmpty(response)) {
                                int assetGroup = 4;
                                if (mAdSize == AdSize.SIZE_INTERSTITIAL) {
                                    assetGroup = 15;
                                }

                                Ad.AdType type = Ad.AdType.VIDEO;
                                Ad ad = new Ad(assetGroup, response, type);
                                if (listener != null) {
                                    ad.setAdSourceName(mConfig.getName());
                                    listener.onAdFetched(ad);
                                }
                            } else {
                                if (listener != null) {
                                    listener.onError(new AuctionError(mConfig.getName(), new Exception("The server responded with an empty ad")));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Logger.e(TAG, "Request failed: " + error.toString());
                            if (listener != null) {
                                listener.onError(new AuctionError(mConfig.getName(), error));
                            }
                        }
                    });
        } else {
            if (listener != null) {
                listener.onError(new AuctionError(mConfig.getName(), new Exception("VAST tag fetch failed. Invalid config")));
            }
        }
    }

    @Override
    public AdSize getAdSize(){
        return mAdSize;
    }

    @Override
    public String getName(){
        return mConfig.getName();
    }

    @Override
    public double getECPM(){
        return mConfig.getECPM();
    }

    private String processTagUrl(String tagUrl) {
        return VastUrlUtils.formatURL(tagUrl);
    }
}
