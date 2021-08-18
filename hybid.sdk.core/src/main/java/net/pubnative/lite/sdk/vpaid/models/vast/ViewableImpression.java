package net.pubnative.lite.sdk.vpaid.models.vast;

import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;

import java.util.List;

public class ViewableImpression {
    @Attribute
    private String id;

    @Tag("Viewable")
    private List<Viewable> viewableList;

    @Tag("NotViewable")
    private List<NotViewable> notViewableList;

    @Tag("ViewUndetermined")
    private List<ViewUndetermined> viewUndeterminedList;

    public String getId() {
        return id;
    }

    public List<Viewable> getViewableList() {
        return viewableList;
    }

    public List<NotViewable> getNotViewableList() {
        return notViewableList;
    }

    public List<ViewUndetermined> getViewUndeterminedList() {
        return viewUndeterminedList;
    }
}
