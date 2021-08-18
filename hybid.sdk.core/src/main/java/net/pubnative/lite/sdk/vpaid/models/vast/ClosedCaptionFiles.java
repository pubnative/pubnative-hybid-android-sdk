package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class ClosedCaptionFiles {
    @Tag("ClosedCaptionFile")
    private List<ClosedCaptionFile> closedCaptionFiles;

    public List<ClosedCaptionFile> getClosedCaptionFiles() {
        return closedCaptionFiles;
    }
}
