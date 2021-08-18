package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Text;

public class ClosedCaptionFile {
    @Attribute
    private boolean type;

    @Attribute
    private boolean language;

    @Text
    private String text;

    public boolean isType() {
        return type;
    }

    public boolean isLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }
}
