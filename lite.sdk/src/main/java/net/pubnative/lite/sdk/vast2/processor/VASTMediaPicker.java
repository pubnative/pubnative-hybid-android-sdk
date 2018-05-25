package net.pubnative.lite.sdk.vast2.processor;

import net.pubnative.lite.sdk.vast2.model.VASTMediaFile;

import java.util.List;

public interface VASTMediaPicker {
    VASTMediaFile pickVideo(List<VASTMediaFile> list);
}
