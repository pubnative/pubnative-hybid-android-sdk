// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class Impression {
    @Attribute
    private String id;

    @Text
    private String text;

    public String getId() { return id; }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
