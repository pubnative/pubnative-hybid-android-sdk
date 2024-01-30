// The MIT License (MIT)
//
// Copyright (c) 2023 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
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
