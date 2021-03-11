package net.pubnative.lite.adapters.dfp;

import android.os.Bundle;

import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;

import java.util.HashSet;

public class HyBidGAMBidUtils {

    private static final HashSet<String> usedKeys;

    static {
        usedKeys = new HashSet<>();
    }

    public static void addBids(Ad ad, PublisherAdRequest.Builder builder){
        addBids(ad, builder, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
    }

    public static void addBids(Ad ad, PublisherAdRequest.Builder builder, HeaderBiddingUtils.KeywordMode mode){
        PublisherAdRequest publisherAdRequest = builder.build();
        removeUsedCustomTargetingForDFP(publisherAdRequest);

        Bundle keywordsBundle = HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, mode);
        for (String key : keywordsBundle.keySet()){
            builder.addCustomTargeting(key, keywordsBundle.getString(key));
        }
    }

    public static void addBids(Ad ad, PublisherAdRequest publisherAdRequest){
        addBids(ad, publisherAdRequest, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
    }

    public static void addBids(Ad ad, PublisherAdRequest publisherAdRequest, HeaderBiddingUtils.KeywordMode mode){
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

    public static void addBids(Ad ad, AdManagerAdRequest.Builder builder){
        addBids(ad, builder, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
    }

    public static void addBids(Ad ad, AdManagerAdRequest.Builder builder, HeaderBiddingUtils.KeywordMode mode){
        AdManagerAdRequest adManagerAdRequest = builder.build();
        removeUsedCustomTargetingForDFP(adManagerAdRequest);

        Bundle keywordsBundle = HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, mode);
        for (String key : keywordsBundle.keySet()){
            builder.addCustomTargeting(key, keywordsBundle.getString(key));
        }
    }

    public static void addBids(Ad ad, AdManagerAdRequest adManagerAdRequest){
        addBids(ad, adManagerAdRequest, HeaderBiddingUtils.KeywordMode.THREE_DECIMALS);
    }

    public static void addBids(Ad ad, AdManagerAdRequest adManagerAdRequest, HeaderBiddingUtils.KeywordMode mode){
        removeUsedCustomTargetingForDFP(adManagerAdRequest);
        Bundle keywordsBundle = HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, mode);
        Bundle customTargetingbundle = adManagerAdRequest.getCustomTargeting();
        if (customTargetingbundle != null){
            for (String key : keywordsBundle.keySet()){
                customTargetingbundle.putString(key, keywordsBundle.getString(key));
                addUsedKeys(key);
            }
        }
    }

    private static void addUsedKeys(String key) {
        synchronized (usedKeys) {
            usedKeys.add(key);
        }
    }

    private static void removeUsedCustomTargetingForDFP(PublisherAdRequest publisherAdRequest) {
        Bundle customTargetingBundle = (Bundle) publisherAdRequest.getCustomTargeting();
        if (customTargetingBundle != null && usedKeys != null) {
            for (String key : usedKeys) {
                customTargetingBundle.remove(key);
            }
        }
    }

    private static void removeUsedCustomTargetingForDFP(AdManagerAdRequest adManagerAdRequest) {
        Bundle customTargetingBundle = (Bundle) adManagerAdRequest.getCustomTargeting();
        if (customTargetingBundle != null && usedKeys != null) {
            for (String key : usedKeys) {
                customTargetingBundle.remove(key);
            }
        }
    }

}
