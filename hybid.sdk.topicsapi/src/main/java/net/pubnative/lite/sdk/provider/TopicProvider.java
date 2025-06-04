// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.provider;

import android.content.Context;

import net.pubnative.lite.sdk.models.Topic;

import java.util.List;

public interface TopicProvider {

    public void getTopics(Context context, Callback callback);

    interface Callback {
        void onResult(List<Topic> topics);
    }
}
