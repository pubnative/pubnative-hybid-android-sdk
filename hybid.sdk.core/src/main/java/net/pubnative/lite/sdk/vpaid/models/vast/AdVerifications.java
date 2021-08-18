package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class AdVerifications {
    @Tag("Verification")
    private List<Verification> verificationList;

    public List<Verification> getVerificationList() {
        return verificationList;
    }
}
