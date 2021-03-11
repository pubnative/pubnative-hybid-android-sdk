package com.monet.bidder;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.webkit.ValueCallback;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.List;

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

    public static void addBids(AdView adView, final AdRequest adRequest, String appMonetAdUnitId,
                               int timeout, final ValueCallback<AdRequest> valueCallback) {
        final RequestManager requestManager = new RequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        AdRequest newAdRequest = addAdmobKeywords(adRequest, bidKeywords);
                        valueCallback.onReceiveValue(newAdRequest);
                    } else {
                        valueCallback.onReceiveValue(adRequest);
                    }
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(adRequest);
                }
                requestManager.destroy();
            }
        });
        requestManager.setAdSize(mapAdSize(adView.getAdSize()));
        requestManager.requestAd();
    }

    public static void addBids(final PublisherAdView adView, final PublisherAdRequest adRequest, String appMonetAdUnitId,
                               int timeout, final ValueCallback<PublisherAdRequest> valueCallback) {
        final RequestManager requestManager = new RequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        PublisherAdRequest newAdRequest = addGAMKeywords(adRequest, bidKeywords);
                        valueCallback.onReceiveValue(newAdRequest);
                    } else {
                        valueCallback.onReceiveValue(adRequest);
                    }
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(adRequest);
                }
                requestManager.destroy();
            }
        });
        requestManager.setAdSize(mapAdSize(adView.getAdSize()));
        requestManager.requestAd();
    }

    public static void addBids(final PublisherAdRequest adRequest, String appMonetAdUnitId,
                               int timeout, final ValueCallback<PublisherAdRequest> valueCallback) {
        final RequestManager requestManager = new RequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        PublisherAdRequest newAdRequest = addGAMKeywords(adRequest, bidKeywords);
                        valueCallback.onReceiveValue(newAdRequest);
                    } else {
                        valueCallback.onReceiveValue(adRequest);
                    }
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(adRequest);
                }
                requestManager.destroy();
            }
        });
        requestManager.setAdSize(AdSize.SIZE_300x250);
        requestManager.requestAd();
    }

    public static PublisherAdRequest addBids(PublisherAdRequest adRequest, String appMonetAdUnitId) {
        // This will be left empty because there's no synchronous API supported for this.
        return adRequest;
    }

    public static void addBids(PublisherInterstitialAd interstitialAd, final PublisherAdRequest adRequest,
                               String appMonetAdUnitId, int timeout,
                               final ValueCallback<PublisherAdRequest> valueCallback) {
        final InterstitialRequestManager requestManager = new InterstitialRequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingInterstitialKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        PublisherAdRequest newAdRequest = addGAMKeywords(adRequest, bidKeywords);
                        valueCallback.onReceiveValue(newAdRequest);
                    } else {
                        valueCallback.onReceiveValue(adRequest);
                    }
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(adRequest);
                }
                requestManager.destroy();
            }
        });
        requestManager.requestAd();
    }

    public static void addBids(InterstitialAd interstitialAd, final AdRequest adRequest,
                               String appMonetAdUnitId,
                               int timeout, final ValueCallback<AdRequest> valueCallback) {
        final InterstitialRequestManager requestManager = new InterstitialRequestManager();
        requestManager.setZoneId(appMonetAdUnitId);
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (valueCallback != null) {
                    String bidKeywords = HeaderBiddingUtils.getAppMonetBiddingInterstitialKeywords(ad);
                    if (!TextUtils.isEmpty(bidKeywords)) {
                        AdRequest newAdRequest = addAdmobKeywords(adRequest, bidKeywords);
                        valueCallback.onReceiveValue(newAdRequest);
                    } else {
                        valueCallback.onReceiveValue(adRequest);
                    }
                }
                requestManager.destroy();
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(adRequest);
                }
                requestManager.destroy();
            }
        });
        requestManager.requestAd();

    }

    public static PublisherAdRequest addBids(PublisherAdView adView, PublisherAdRequest adRequest) {
        return addBids(adView, adRequest, adView.getAdUnitId());
    }

    public static PublisherAdRequest addBids(PublisherAdView adView, PublisherAdRequest adRequest,
                                             String appMonetAdUnitId) {
        // This will be left empty because there's no synchronous API supported for this.
        return adRequest;
    }

    private static AdSize mapAdSize(com.google.android.gms.ads.AdSize adSize) {
        if (adSize == com.google.android.gms.ads.AdSize.BANNER) {
            return AdSize.SIZE_320x50;
        } else if (adSize == com.google.android.gms.ads.AdSize.LARGE_BANNER) {
            return AdSize.SIZE_320x100;
        } else if (adSize == com.google.android.gms.ads.AdSize.LEADERBOARD) {
            return AdSize.SIZE_728x90;
        } else if (adSize == com.google.android.gms.ads.AdSize.WIDE_SKYSCRAPER) {
            return AdSize.SIZE_160x600;
        } else {
            return AdSize.SIZE_300x250;
        }
    }

    private static AdRequest addAdmobKeywords(AdRequest adRequest, String bidKeywords) {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (!TextUtils.isEmpty(adRequest.getContentUrl())) {
            builder.setContentUrl(adRequest.getContentUrl());
        }
        if (adRequest.getLocation() != null) {
            builder.setLocation(adRequest.getLocation());
        }
        if (!TextUtils.isEmpty(bidKeywords)) {
            builder.addKeyword(bidKeywords);
        }
        for (String keyword : adRequest.getKeywords()) {
            builder.addKeyword(keyword);
        }

        return builder.build();
    }

    private static PublisherAdRequest addGAMKeywords(PublisherAdRequest adRequest, String bidKeywords) {
        PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();
        if (!TextUtils.isEmpty(adRequest.getContentUrl())) {
            builder.setContentUrl(adRequest.getContentUrl());
        }
        if (adRequest.getLocation() != null) {
            builder.setLocation(adRequest.getLocation());
        }

        if (!TextUtils.isEmpty(bidKeywords)) {
            builder.addKeyword(bidKeywords);
            String[] params = bidKeywords.split(":");
            builder.addCustomTargeting(params[0], params[1]);
        }

        for (String keyword : adRequest.getKeywords()) {
            builder.addKeyword(keyword);
        }

        for (String key : adRequest.getCustomTargeting().keySet()) {
            if (adRequest.getCustomTargeting().get(key) instanceof String) {
                builder.addCustomTargeting(key, adRequest.getCustomTargeting().getString(key));
            } else if (adRequest.getCustomTargeting().get(key) instanceof List) {
                builder.addCustomTargeting(key, adRequest.getCustomTargeting().getStringArrayList(key));
            }
        }

        return builder.build();
    }
}
