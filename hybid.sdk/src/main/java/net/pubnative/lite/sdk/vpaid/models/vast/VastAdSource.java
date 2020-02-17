package net.pubnative.lite.sdk.vpaid.models.vast;

import java.util.List;

public interface VastAdSource {
    AdSystem getAdSystem();

    List<Impression> getImpressionList();

    Creatives getCreatives();

    Error getError();
}
