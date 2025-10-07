// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TopicTest {

    @Test
    public void constructor_andGetters_workCorrectly() {
        Topic topic = new Topic(123, 1L, "v1.0");

        assertEquals(123, topic.getId());
        assertEquals(Long.valueOf(1L), topic.getTaxonomyVersion());
        assertEquals("v1.0", topic.getTaxonomyVersionName());
    }

    @Test
    public void equals_and_hashCode_areBasedOnIdAndTaxonomyVersionOnly() {
        // Create three topics. The first two are identical, the third has a different name
        // but the same id and taxonomyVersion.
        Topic topic1 = new Topic(100, 2L, "v2.0");
        Topic topic2 = new Topic(100, 2L, "v2.0");
        Topic topic3 = new Topic(100, 2L, "version_two"); // Different name

        // Create topics that should not be equal
        Topic differentIdTopic = new Topic(999, 2L, "v2.0");
        Topic differentVersionTopic = new Topic(100, 999L, "v2.0");

        // 1. Test for standard equality
        assertEquals(topic1, topic2);
        assertEquals(topic1.hashCode(), topic2.hashCode());

        // 2. Test that `taxonomyVersionName` is correctly ignored in equals/hashCode
        assertEquals(topic1, topic3);
        assertEquals(topic1.hashCode(), topic3.hashCode());

        // 3. Test for inequality
        assertNotEquals(topic1, differentIdTopic);
        assertNotEquals(topic1, differentVersionTopic);
        assertNotEquals(null, topic1);
        assertNotEquals(new Object(), topic1);
    }
}