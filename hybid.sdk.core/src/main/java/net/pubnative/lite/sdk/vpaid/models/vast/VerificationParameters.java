// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Text;

public class VerificationParameters {
    @Text
    private String text;

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
