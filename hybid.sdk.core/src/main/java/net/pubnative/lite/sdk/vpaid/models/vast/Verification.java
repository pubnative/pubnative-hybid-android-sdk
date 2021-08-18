package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Verification {
    @Attribute
    private String vendor;

    @Tag("JavaScriptResource")
    private List<JavaScriptResource> javaScriptResources;

    @Tag("ExecutableResource")
    private List<ExecutableResource> executableResources;

    @Tag
    private TrackingEvents trackingEvents;

    @Tag
    private VerificationParameters verificationParameters;

    public String getVendor() {
        return vendor;
    }

    public List<JavaScriptResource> getJavaScriptResources() {
        return javaScriptResources;
    }

    public List<ExecutableResource> getExecutableResources() {
        return executableResources;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public VerificationParameters getVerificationParameters() {
        return verificationParameters;
    }
}
