package net.pubnative.lite.sdk.vpaid;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import com.iab.omid.library.pubnativenet.adsession.VerificationScriptResource;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.helpers.AssetsLoader;
import net.pubnative.lite.sdk.vpaid.models.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.response.VastProcessor;

import java.net.URL;

public class VideoAdProcessor {
    private static final String TAG = VideoAdProcessor.class.getSimpleName();
    public interface Listener {
        void onCacheSuccess(AdParams adParams, String videoFilePath, String endCardFilePath);
        void onCacheError(Throwable error);
    }

    public void process(final Context context, String vast, View adView, final Listener listener) {
        VastProcessor vastProcessor = new VastProcessor(context, getAdSpotDimensions(context, adView));
        vastProcessor.parseResponse(vast, new VastProcessor.Listener() {
            @Override
            public void onParseSuccess(AdParams adParams, String vastFileContent) {
                appendOMVerificationScript(adParams);
                prepare(context, adParams, listener);
            }

            @Override
            public void onParseError(PlayerInfo message) {
                if (listener != null) {
                    listener.onCacheError(new Exception(message.getMessage()));
                }
            }
        });
    }

    private void appendOMVerificationScript(AdParams adParams) {
        try {
            final URL url = new URL("https://s3-us-west-2.amazonaws.com/omsdk-files/compliance-js/omid-validation-verification-script-v1-ssl.js");
            final String vendorKey = "iabtechlab.com-omid";
            final String params = "iabtechlab-Pubnativenet";
            VerificationScriptResource resource = VerificationScriptResource.
                    createVerificationScriptResourceWithParameters(vendorKey, url, params);
            adParams.getVerificationScriptResources().add(resource);
        } catch (Exception exception) {
            Logger.e(TAG, "Error adding verification script: ", exception);
        }
    }

    private void prepare(Context context, final AdParams adParams, final Listener listener) {
        AssetsLoader assetsLoader = new AssetsLoader();
        assetsLoader.load(adParams, context, new AssetsLoader.OnAssetsLoaded() {
            @Override
            public void onAssetsLoaded(String videoFilePath, String endCardFilePath) {
                if (listener != null) {
                    listener.onCacheSuccess(adParams, videoFilePath, endCardFilePath);
                }
            }

            @Override
            public void onError(PlayerInfo info) {
                if (listener != null) {
                    listener.onCacheError(new Exception(info.getMessage()));
                }
            }
        });
    }

    private AdSpotDimensions getAdSpotDimensions(Context context, View view) {
        if (view != null) {
            return new AdSpotDimensions(view.getWidth(), view.getHeight());
        } else {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return new AdSpotDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
    }
}
