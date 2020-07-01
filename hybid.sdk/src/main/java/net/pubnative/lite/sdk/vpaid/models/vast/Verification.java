package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

public class Verification {
    @Attribute
    private String vendor;

    @Tag
    private JavaScriptResource javaScriptResource;

    @Tag
    private TrackingEvents trackingEvents;

    @Tag
    private VerificationParameters verificationParameters;

    public String getVendor() {
        return vendor;
    }

    public JavaScriptResource getJavaScriptResource() {
        return javaScriptResource;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public VerificationParameters getVerificationParameters() {
        return verificationParameters;
    }
}
