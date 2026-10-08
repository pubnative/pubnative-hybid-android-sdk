// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;

import net.pubnative.lite.sdk.models.Topic;

import java.util.Collections;
import java.util.List;

/**
 * @deprecated The AdServices Topics API integration has been removed.
 * This class is kept as a no-op shell for binary/source compatibility and will be removed in a future major release.
 * {@link #getTopics()} always returns an empty list.
 */
@Deprecated
public class TopicManager {

    public TopicManager(Context context) {
        // No-op.
    }

    /**
     * @deprecated Always returns an empty list.
     */
    @Deprecated
    public List<Topic> getTopics() {
        return Collections.emptyList();
    }
}
