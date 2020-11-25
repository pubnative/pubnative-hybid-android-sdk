package net.pubnative.lite.sdk.auction;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils;

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
        if (mConfig != null && TextUtils.isEmpty(mConfig.getVastTagUrl())) {
            PNHttpClient.makeRequest(mContext, processTagUrl(mConfig.getVastTagUrl()),
                    null, null, false,
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
                                    listener.onAdFetched(ad);
                                }
                            } else {
                                if (listener != null) {
                                    listener.onError(new Exception("The server responded with an empty ad"));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Logger.e(TAG, "Request failed: " + error.toString());
                            if (listener != null) {
                                listener.onError(error);
                            }
                        }
                    });
        } else {
            if (listener != null) {
                listener.onError(new Exception("VAST tag fetch failed. Invalid config"));
            }
        }
    }

    private String processTagUrl(String tagUrl) {
        return VastUrlUtils.formatURL(tagUrl);
    }
}
