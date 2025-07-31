// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk;

import android.content.Context;

import net.pubnative.lite.sdk.models.Topic;
import net.pubnative.lite.sdk.provider.TopicProvider;
import net.pubnative.lite.sdk.provider.TopicsApiImpl;

import java.util.ArrayList;
import java.util.List;

public class TopicManager {

    private final String TAG = TopicManager.class.getSimpleName();
    private final List<TopicProvider> providers = new ArrayList<>();
    private List<Topic> topics = null;

    public TopicManager(Context context){
        initProviders();
        fetchTopics(context);
    }

    private void initProviders() {
        providers.add(new TopicsApiImpl());
    }

    private void fetchTopics(Context context){

        if(providers.isEmpty()) return;
        topics = new ArrayList<>();
        TopicProvider.Callback callback = this::addTopics;
        for (TopicProvider provider : providers) {
            provider.getTopics(context, callback);
        }
    }

    public List<Topic> getTopics(){
        return topics;
    }

    private synchronized void addTopics(List<Topic> newTopics) {

        if (newTopics == null || newTopics.isEmpty()) {
            return; // Nothing to add, exit early
        }

        if (this.topics == null) {
            this.topics = new ArrayList<>();
        }

        for (Topic topic : newTopics) {
            if (topic != null && !this.topics.contains(topic)) {
                this.topics.add(topic);
            }
        }
    }
}
