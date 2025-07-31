// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import java.util.Objects;

public class Topic {
    private final int id;
    private final long taxonomyVersion;
    private final String taxonomyVersionName;

    public Topic(int id, long taxonomyVersion, String taxonomyVersionName) {
        this.id = id;
        this.taxonomyVersion = taxonomyVersion;
        this.taxonomyVersionName = taxonomyVersionName;
    }

    public int getId() {
        return id;
    }

    public Long getTaxonomyVersion() {
        return taxonomyVersion;
    }

    public String getTaxonomyVersionName() {
        return taxonomyVersionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id && taxonomyVersion == topic.taxonomyVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taxonomyVersion);
    }
}
