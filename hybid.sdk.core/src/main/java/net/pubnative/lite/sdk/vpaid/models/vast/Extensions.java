// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class Extensions {
    @Tag("Extension")
    private List<Extension> extensionList;

    public List<Extension> getExtensions() {
        return extensionList;
    }
}
