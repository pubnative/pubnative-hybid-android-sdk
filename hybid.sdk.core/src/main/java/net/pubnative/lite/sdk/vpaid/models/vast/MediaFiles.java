package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class MediaFiles {

    @Tag("MediaFile")
    private List<MediaFile> mediaFiles;

    @Tag("Mezzanine")
    private List<Mezzanine> mezzanines;

    @Tag("InteractiveCreativeFile")
    private List<InteractiveCreativeFile> interactiveCreativeFiles;

    @Tag
    private ClosedCaptionFiles closedCaptionFiles;

    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public List<Mezzanine> getMezzanines() {
        return mezzanines;
    }

    public List<InteractiveCreativeFile> getInteractiveCreativeFiles() {
        return interactiveCreativeFiles;
    }

    public ClosedCaptionFiles getClosedCaptionFiles() {
        return closedCaptionFiles;
    }
}
