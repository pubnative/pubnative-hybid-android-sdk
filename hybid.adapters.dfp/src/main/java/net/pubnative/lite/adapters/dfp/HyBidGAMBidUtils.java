package net.pubnative.lite.adapters.dfp;

import android.os.Bundle;

import com.google.android.gms.ads.admanager.AdManagerAdRequest;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;

import java.util.HashSet;

public class HyBidGAMBidUtils {

    private static final HashSet<String> usedKeys;

    static {
        usedKeys = new HashSet<>();
    }

    public static void addBids(Ad ad, AdManagerAdRequest.Builder builder){
        addBids(ad, builder, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
    }

    public static void addBids(Ad ad, AdManagerAdRequest.Builder builder, HeaderBiddingUtils.KeywordMode mode){
        AdManagerAdRequest publisherAdRequest = builder.build();
        removeUsedCustomTargetingForDFP(publisherAdRequest);

        Bundle keywordsBundle = HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, mode);
        for (String key : keywordsBundle.keySet()){
            builder.addCustomTargeting(key, keywordsBundle.getString(key));
        }
    }

    public static void addBids(Ad ad, AdManagerAdRequest publisherAdRequest, HeaderBiddingUtils.KeywordMode mode){
        removeUsedCustomTargetingForDFP(publisherAdRequest);
        Bundle keywordsBundle = HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, mode);
        Bundle customTargetingbundle = publisherAdRequest.getCustomTargeting();
        if (customTargetingbundle != null){
            for (String key : keywordsBundle.keySet()){
                customTargetingbundle.putString(key, keywordsBundle.getString(key));
                addUsedKeys(key);
            }
        }
    }

    public static void addBids(Ad ad, AdManagerAdRequest adManagerAdRequest){
        addBids(ad, adManagerAdRequest, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
    }

    private static void addUsedKeys(String key) {
        synchronized (usedKeys) {
            usedKeys.add(key);
        }
    }

    private static void removeUsedCustomTargetingForDFP(AdManagerAdRequest publisherAdRequest) {
        Bundle customTargetingBundle = publisherAdRequest.getCustomTargeting();
        if (customTargetingBundle != null && usedKeys != null) {
            for (String key : usedKeys) {
                customTargetingBundle.remove(key);
            }
        }
    }
}
