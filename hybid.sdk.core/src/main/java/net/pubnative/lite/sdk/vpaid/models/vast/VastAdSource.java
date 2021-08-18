package net.pubnative.lite.sdk.vpaid.models.vast;

import java.util.List;

public interface VastAdSource {
    AdSystem getAdSystem();

    List<Impression> getImpressions();

    Creatives getCreatives();

    List<Error> getErrors();

    Pricing getPricing();

    Extensions getExtensions();

    ViewableImpression getViewableImpression();

    AdVerifications getAdVerifications();

    List<Category> getCategories();

    AdServingId getAdServingId();
}
