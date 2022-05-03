package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Creative {

    @Attribute
    private String id;

    @Attribute
    private String adId;

    @Attribute
    private String sequence;

    @Attribute
    private String apiFramework;

    @Tag("UniversalAdId")
    private List<UniversalAdId> universalAdIds;

    @Tag
    private CreativeExtensions creativeExtensions;

    @Tag
    private Linear linear;

    @Tag
    private NonLinearAds nonLinearAds;

    @Tag
    private CompanionAds companionAds;



    public String getId() {
        return id;
    }

    public String getAdId() {
        return adId;
    }

    public String getSequence() {
        return sequence;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public List<UniversalAdId> getUniversalAdIds() {
        return universalAdIds;
    }

    public CreativeExtensions getCreativeExtensions() {
        return creativeExtensions;
    }

    public Linear getLinear() {
        return linear;
    }

    public NonLinearAds getNonLinearAds() {
        return nonLinearAds;
    }

    public CompanionAds getCompanionAds() {
        return companionAds;
    }
}
