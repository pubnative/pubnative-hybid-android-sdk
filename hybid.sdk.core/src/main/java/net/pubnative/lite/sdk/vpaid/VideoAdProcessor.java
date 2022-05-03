package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.helpers.AssetsLoader;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.response.VastProcessor;

import java.util.ArrayList;
import java.util.List;

public class VideoAdProcessor {
    private static final String TAG = VideoAdProcessor.class.getSimpleName();

    public interface Listener {
        void onCacheSuccess(AdParams adParams, String videoFilePath, EndCardData endCardData, String endCardFilePath, List<String> omidVendors);

        void onCacheError(Throwable error);
    }

    public void process(final Context context, String vast, AdSize adSize, final Listener listener) {
        VastProcessor vastProcessor = new VastProcessor(context, getAdSpotDimensions(context, adSize));
        vastProcessor.parseResponse(vast, new VastProcessor.Listener() {
            @Override
            public void onParseSuccess(AdParams adParams, String vastFileContent) {
                prepare(context, adParams, listener);
            }

            @Override
            public void onParseError(PlayerInfo message) {
                if (listener != null) {
                    Logger.e(TAG, message.getMessage());
                    listener.onCacheError(new HyBidError(HyBidErrorCode.VAST_PLAYER_ERROR, message.getMessage()));
                }
            }
        });
    }

    private void prepare(Context context, final AdParams adParams, final Listener listener) {
        AssetsLoader assetsLoader = new AssetsLoader();
        assetsLoader.load(adParams, context, new AssetsLoader.OnAssetsLoaded() {
            @Override
            public void onAssetsLoaded(String videoFilePath, EndCardData endCardData, String endCardFilePath) {
                if (listener != null) {
                    listener.onCacheSuccess(adParams, videoFilePath, endCardData, endCardFilePath, getOmidVendors(adParams));
                }
            }

            @Override
            public void onError(PlayerInfo info) {
                if (listener != null) {
                    Logger.e(TAG, info.getMessage());
                    listener.onCacheError(new HyBidError(HyBidErrorCode.VAST_PLAYER_ERROR, info.getMessage()));
                }
            }
        });
    }

    private AdSpotDimensions getAdSpotDimensions(Context context, AdSize adSize) {
        if (adSize != null && adSize != AdSize.SIZE_INTERSTITIAL) {
            return new AdSpotDimensions(adSize.getWidth(), adSize.getHeight());
        } else {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return new AdSpotDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
    }

    private List<String> getOmidVendors(AdParams adParams) {
        List<String> vendors = new ArrayList<>();
        if (adParams != null
                && adParams.getVerificationScriptResources() != null
                && !adParams.getVerificationScriptResources().isEmpty()) {
            for (VerificationScriptResource verification : adParams.getVerificationScriptResources()) {
                if (!TextUtils.isEmpty(verification.getVendorKey())) {
                    vendors.add(verification.getVendorKey());
                }
            }
        }
        return vendors;
    }
}
