package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.Collections;
import java.util.List;

public class Wrapper implements VastAdSource {
    @Attribute
    private String followAdditionalWrappers;

    @Attribute
    private String allowMultipleAds;

    @Attribute
    private String fallbackOnNoAd;

    @Tag("Impression")
    private List<Impression> impressions;

    @Tag
    private VASTAdTagURI vastAdTagURI;

    @Tag
    private AdSystem adSystem;

    @Tag
    private Pricing pricing;

    @Tag("Error")
    private List<Error> errors;

    @Tag
    private ViewableImpression viewableImpression;

    @Tag
    private AdVerifications adVerifications;

    @Tag
    private Extensions extensions;

    @Tag
    private Creatives creatives;

    @Tag("BlockedAdCategories")
    private List<BlockedAdCategories> blockedAdCategories;

    public String isFollowAdditionalWrappers() {
        return followAdditionalWrappers;
    }

    public String isAllowMultipleAds() {
        return allowMultipleAds;
    }

    public String isFallbackOnNoAd() {
        return fallbackOnNoAd;
    }

    @Override
    public List<Impression> getImpressions() {
        return impressions;
    }

    public VASTAdTagURI getVastAdTagURI() {
        return vastAdTagURI;
    }

    @Override
    public AdSystem getAdSystem() {
        return adSystem;
    }

    @Override
    public Pricing getPricing() {
        return pricing;
    }

    @Override
    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public ViewableImpression getViewableImpression() {
        return viewableImpression;
    }

    @Override
    public AdVerifications getAdVerifications() {
        return adVerifications;
    }

    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    @Override
    public Creatives getCreatives() {
        return creatives;
    }

    public List<BlockedAdCategories> getBlockedAdCategories() {
        return blockedAdCategories;
    }

    @Override
    public AdServingId getAdServingId() {
        return null;
    }

    @Override
    public List<Category> getCategories() {
        return Collections.emptyList();
    }
}
