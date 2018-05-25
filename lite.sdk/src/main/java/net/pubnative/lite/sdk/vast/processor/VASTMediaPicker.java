package net.pubnative.lite.sdk.vast.processor;

import net.pubnative.lite.sdk.vast.model.VASTMediaFile;

import java.util.List;

public interface VASTMediaPicker {
    VASTMediaFile pickVideo(List<VASTMediaFile> list);
}
