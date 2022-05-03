package net.pubnative.lite.sdk.vpaid.models.vast;

import android.text.TextUtils;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class CreativeExtension {
    @Attribute
    private String type;

    @Tag
    private VerveCTAButton verveCTAButton;

    @Text
    private String text;

    public String getType() {
        return type;
    }

    public VerveCTAButton getVerveCTAButton() {
        return verveCTAButton;
    }

    public String getText() {
        return TextUtils.isEmpty(text) ? "" : text.trim();
    }
}
