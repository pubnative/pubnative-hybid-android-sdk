package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class MediaFiles {

    @Tag("MediaFile")
    private List<MediaFile> mediaFileList;

    public List<MediaFile> getMediaFileList() {
        return mediaFileList;
    }
}
