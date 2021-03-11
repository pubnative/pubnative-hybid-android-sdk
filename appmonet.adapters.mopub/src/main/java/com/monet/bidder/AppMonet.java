package com.monet.bidder;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.webkit.ValueCallback;

import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.RequestParameters;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code AppMonet} class contains static methods that are entry points to the AppMonet library.
 * All interactions will happen through this class.
 */
public class AppMonet {
    private static final String TAG = AppMonet.class.getSimpleName();

    private AppMonet() {
    }

    /**
     * This method initializes the AppMonet library and all its internal components.
     * <p/>
     * You need to define {@code appmonet.application.id} {@code meta-data} in your
     * {@code AndroidManifest.xml}:
     * <pre>
     * &lt;manifest ...&gt;
     *
     * ...
     *
     *   &lt;application ...&gt;
     *     &lt;meta-data
     *       android:name="appmonet.application.id"
     *       android:value="@string/app_monet_app_id" /&gt;
     *
     *       ...
     *
     *   &lt;/application&gt;
     * &lt;/manifest&gt;
     * </pre>
     * <p/>
     * This must be called before your application can use the AppMonet library. The recommended
     * way is to call {@code AppMonet.init} at the {@code Application}'s {@code onCreate} method:
     * <p/>
     * <pre>
     * public class YourApplication extends Application {
     *     public void onCreate() {
     *          AppMonet.init(this);
     *     }
     * }
     * </pre>
     *
     * @param context The context ({@link Context}) of your application.
     */
    public static void init(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String appToken = applicationInfo.metaData
                    .getString("appmonet.application.id", "");
            init(context, new AppMonetConfiguration.Builder().applicationId(appToken).build());
        } catch (PackageManager.NameNotFoundException exception) {
            Logger.e(TAG, "Error initialising the AppMonet SDK: ", exception);
        }
    }

    /**
     * This method initializes the AppMonet library and all its internal components.
     * <p/>
     * This method is required if you do not wish to define {@code appmonet.application.id}
     * {@code meta-data} in your {@code AndroidManifest.xml}
     * <p/>
     * This must be called before your application can use the AppMonet library. The recommended
     * way is to call {@code AppMonet.init} at the {@code Application}'s {@code onCreate} method:
     * <p/>
     * <pre>
     * public class YourApplication extends Application {
     *     public void onCreate() {
     *          AppMonet.init(this, &quot;AppMonet application id&quot;);
     *     }
     * }
     * </pre>
     *
     * @param context               The context ({@link Context}) of your application.
     * @param appMonetConfiguration The application configurations needed to initialize the sdk.
     */
    public static void init(Context context, AppMonetConfiguration appMonetConfiguration) {
        AppMonetConfiguration internalConfiguration = appMonetConfiguration;
        if (appMonetConfiguration == null) {
            internalConfiguration = new AppMonetConfiguration.Builder().build();
        }
        if (TextUtils.isEmpty(internalConfiguration.applicationId)) {

        } else {
            HyBid.initialize(internalConfiguration.applicationId, (Application) context.getApplicationContext());
        }
    }

    /**
     * Register application state callbacks (e.g. foreground/background)
     *
     * @param application your Application instance
     */
    public static void registerCallbacks(Application application) {

    }

    /**
     * This method allows you to enable or disable verbose logging coming from the AppMonet library.
     *
     * @param verboseLogging This boolean indicates if verbose logging should be activated.
     */
    public static void enableVerboseLogging(boolean verboseLogging) {
        HyBid.setLogLevel(verboseLogging ? Logger.Level.verbose : Logger.Level.info);
    }

    /**
     * This method allows the SDK to get test demand that always fills. Use it only during development.
     */
    public static void testMode() {
        HyBid.setTestMode(true);
    }

    /**
     * Check if the AppMonet SDK has already been initialized
     *
     * @return true of init() has already successfully been called
     */
    public static boolean isInitialized() {
        return HyBid.isInitialized();
    }

    /**
     * Set the level of the logger
     *
     * @param level a log level (integer)
     */
    public static void setLogLevel(int level) {
        switch (level) {
            case 1:
                HyBid.setLogLevel(Logger.Level.verbose);
                break;
            case 2:
                HyBid.setLogLevel(Logger.Level.debug);
                break;
            case 3:
                HyBid.setLogLevel(Logger.Level.info);
                break;
            case 4:
                HyBid.setLogLevel(Logger.Level.warning);
                break;
            case 5:
                HyBid.setLogLevel(Logger.Level.error);
                break;
            case 6:
                HyBid.setLogLevel(Logger.Level.none);
                break;
            default:
                HyBid.setLogLevel(Logger.Level.info);
        }
    }

    public static void addBids(final MoPubView adView, final String appMonetAdUnitId, int timeout,
                               final ValueCallback<MoPubView> valueCallback) {
        final RequestManager requestManager = new RequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        if (TextUtils.isEmpty(adView.getKeywords())) {
                            adView.setKeywords(bidKeywords);
                        } else {
                            String currentKeywords = adView.getKeywords();
                            adView.setKeywords(mergeKeywords(currentKeywords, bidKeywords));
                        }

                    }
                    valueCallback.onReceiveValue(adView);
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(adView);
                }
                requestManager.destroy();
            }
        });
        requestManager.setAdSize(mapAdSize(adView));
        requestManager.requestAd();
    }

    public static void addBids(final MoPubInterstitial moPubInterstitial, String adUnitId,
                               String appMonetAdUnitId, int timeout,
                               final ValueCallback<MoPubInterstitial> valueCallback) {
        final InterstitialRequestManager requestManager = new InterstitialRequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingInterstitialKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        if (TextUtils.isEmpty(moPubInterstitial.getKeywords())) {
                            moPubInterstitial.setKeywords(bidKeywords);
                        } else {
                            String currentKeywords = moPubInterstitial.getKeywords();
                            moPubInterstitial.setKeywords(mergeKeywords(currentKeywords, bidKeywords));
                        }

                    }
                    valueCallback.onReceiveValue(moPubInterstitial);
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(moPubInterstitial);
                }
                requestManager.destroy();
            }
        });
        requestManager.requestAd();
    }

    public static MoPubView addBids(MoPubView adView) {
        // This has been left empty because we don't support any synchronous API.
        return adView;
    }

    public static void addNativeBids(MoPubNative nativeAd, RequestParameters requestParameters,
                                     String adUnitId, int timeout,
                                     final ValueCallback<NativeAddBidsResponse> valueCallback) {
        if (valueCallback != null) {
            valueCallback.onReceiveValue(null);
        }
    }

    public static void addNativeBids(MoPubNative nativeAd,
                                     String adUnitId, int timeout,
                                     final ValueCallback<NativeAddBidsResponse> valueCallback) {
        if (valueCallback != null) {
            valueCallback.onReceiveValue(null);
        }
    }

    private static String mergeKeywords(String viewKeywords, String newKeywords) {
        Map<String, String> viewKVMap = keywordsToMap(viewKeywords);
        Map<String, String> newKVMap = keywordsToMap(newKeywords);
        viewKVMap.putAll(newKVMap);
        return getKeywords(viewKVMap);
    }

    private static Map<String, String> keywordsToMap(String keyWords) {
        Map<String, String> kvMap = new HashMap<>();
        String[] keyValueArr = keyWords.split(",");
        for (String kv : keyValueArr) {
            String[] splitKV = kv.split(":");
            if (splitKV.length == 2) {
                kvMap.put(splitKV[0], splitKV[1]);
            }
        }
        return kvMap;
    }

    private static String getKeywords(Map<String, String> kv) {
        // get keywords out of the local extras
        StringBuilder buffer = new StringBuilder();
        for (String key : kv.keySet()) {
                buffer.append(key);
                buffer.append(":");
                buffer.append(kv.get(key));
                buffer.append(",");
        }
        return buffer.toString();
    }

    private static AdSize mapAdSize(MoPubView adView) {
        MoPubView.MoPubAdSize adSize = adView.getAdSize();
        if (adSize == MoPubView.MoPubAdSize.HEIGHT_280 || adSize == MoPubView.MoPubAdSize.HEIGHT_250) {
            return AdSize.SIZE_300x250;
        } else if (adSize == MoPubView.MoPubAdSize.HEIGHT_90) {
            return AdSize.SIZE_728x90;
        } else if (adSize == MoPubView.MoPubAdSize.HEIGHT_50) {
            return AdSize.SIZE_320x50;
        } else {
            return AdSize.SIZE_300x250;
        }
    }
}
