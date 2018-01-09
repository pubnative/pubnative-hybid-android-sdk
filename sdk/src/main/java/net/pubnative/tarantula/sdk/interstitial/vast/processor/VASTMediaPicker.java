package net.pubnative.tarantula.sdk.interstitial.vast.processor;

import net.pubnative.tarantula.sdk.interstitial.vast.model.VASTMediaFile;

import java.util.List;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public interface VASTMediaPicker {
    VASTMediaFile pickVideo(List<VASTMediaFile> list);
}
