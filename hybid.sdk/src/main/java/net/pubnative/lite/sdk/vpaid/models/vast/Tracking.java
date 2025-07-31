// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class Tracking {

    @Attribute
    private String event;

    @Attribute
    private String offset;

    @Text
    private String text;

    public String getEvent() {
        return TextUtils.isEmpty(event) ? "" : event;
    }

    public String getOffset() {
        return offset;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
