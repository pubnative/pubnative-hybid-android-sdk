package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Wrapper implements VastAdSource {
    @Attribute
    private boolean followAdditonalWrappers;

    @Attribute
    private boolean allowMultipleAds;

    @Attribute
    private boolean fallbackOnNoAd;

    @Tag
    private VASTAdTagURI vastAdTagURI;

    @Tag
    private AdSystem adSystem;

    @Tag("Impression")
    private List<Impression> impressionList;

    @Tag
    private Creatives creatives;

    @Tag
    private Error error;

    public boolean isFollowAdditonalWrappers() {
        return followAdditonalWrappers;
    }

    public boolean isAllowMultipleAds() {
        return allowMultipleAds;
    }

    public boolean isFallbackOnNoAd() {
        return fallbackOnNoAd;
    }

    public VASTAdTagURI getVastAdTagURI() {
        return vastAdTagURI;
    }

    @Override
    public AdSystem getAdSystem() {
        return adSystem;
    }

    @Override
    public List<Impression> getImpressionList() {
        return impressionList;
    }

    @Override
    public Creatives getCreatives() {
        return creatives;
    }

    @Override
    public Error getError() {
        return error;
    }
}
