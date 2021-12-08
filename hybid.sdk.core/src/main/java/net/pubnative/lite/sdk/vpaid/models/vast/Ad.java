package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Ad {

    @Attribute
    private String id;

    @Attribute
    private String sequence;

    @Attribute
    private String conditionalAd;

    @Attribute
    private String adType;

    @Tag
    private InLine inLine;

    @Tag
    private Wrapper wrapper;

    public String getId() {
        return id;
    }

    public String getSequence() {
        return sequence;
    }

    public String getConditionalAd() {
        return conditionalAd;
    }

    public String getAdType() {
        return adType;
    }

    public InLine getInLine() {
        return inLine;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}
