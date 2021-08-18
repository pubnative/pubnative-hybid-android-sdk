package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class InLine implements VastAdSource {

    @Tag
    private AdSystem adSystem;

    @Tag
    private AdTitle adTitle;

    @Tag("Impression")
    private List<Impression> impressions;

    @Tag
    private AdServingId adServingId;

    @Tag("Category")
    private List<Category> categories;

    @Tag
    private Description description;

    @Tag
    private Advertiser advertiser;

    @Tag
    private Pricing pricing;

    @Tag("Survey")
    private List<Survey> surveys;

    @Tag("Error")
    private List<Error> errors;

    @Tag
    private Extensions extensions;

    @Tag
    private ViewableImpression viewableImpression;

    @Tag
    private AdVerifications adVerifications;

    @Tag
    private Creatives creatives;

    @Tag
    private Expires expires;

    @Override
    public AdSystem getAdSystem() {
        return adSystem;
    }

    public AdTitle getAdTitle() {
        return adTitle;
    }

    @Override
    public List<Impression> getImpressions() {
        return impressions;
    }

    @Override
    public AdServingId getAdServingId() {
        return adServingId;
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    public Description getDescription() {
        return description;
    }

    public Advertiser getAdvertiser() {
        return advertiser;
    }

    @Override
    public Pricing getPricing() {
        return pricing;
    }

    public List<Survey> getSurveys() {
        return surveys;
    }

    @Override
    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    @Override
    public ViewableImpression getViewableImpression() {
        return viewableImpression;
    }

    @Override
    public AdVerifications getAdVerifications() {
        return adVerifications;
    }

    @Override
    public Creatives getCreatives() {
        return creatives;
    }

    public Expires getExpires() {
        return expires;
    }
}
