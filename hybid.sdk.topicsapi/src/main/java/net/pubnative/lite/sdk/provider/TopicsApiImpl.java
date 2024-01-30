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
package net.pubnative.lite.sdk.provider;

import android.adservices.topics.GetTopicsRequest;
import android.adservices.topics.GetTopicsResponse;
import android.adservices.topics.TopicsManager;
import android.content.Context;
import android.os.Build;
import android.os.OutcomeReceiver;
import android.os.ext.SdkExtensions;

import net.pubnative.lite.sdk.models.Topic;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TopicsApiImpl implements TopicProvider {

    private final String TAG = TopicsApiImpl.class.getSimpleName();

    @Override
    public void getTopics(Context context, Callback callback) {

        if(context == null || callback == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 4) {
            TopicsManager mTopicsManager = context.getSystemService(TopicsManager.class);
            Executor mExecutor = Executors.newCachedThreadPool();
            GetTopicsRequest.Builder mTopicsRequestBuilder = new GetTopicsRequest.Builder();
            mTopicsRequestBuilder.setAdsSdkName(context.getPackageName());
            if (SdkExtensions.getExtensionVersion(SdkExtensions.AD_SERVICES) >= 5) {
                mTopicsRequestBuilder.setShouldRecordObservation(true);
            }
            if (mTopicsManager != null) {
                try {
                    mTopicsManager.getTopics(mTopicsRequestBuilder.build(),mExecutor, new OutcomeReceiver<GetTopicsResponse, Exception>() {
                        @Override
                        public void onResult(GetTopicsResponse result) {
                            List<Topic> resultList = new ArrayList<>();
                            for (int i = 0; i < result.getTopics().size(); i++) {
                                android.adservices.topics.Topic topic = result.getTopics().get(i);
                                Topic mTopic = new Topic(topic.getTopicId(), topic.getTaxonomyVersion(), "Chromium Topics API taxonomy");
                                if(!resultList.contains(mTopic)){
                                    resultList.add(mTopic);
                                }
                            }
                            callback.onResult(resultList);
                        }

                        @Override
                        public void onError(Exception error) {
                            Logger.e(TAG, error.getMessage());
                            callback.onResult(null);
                            OutcomeReceiver.super.onError(error);
                        }
                    });
                } catch (Exception e){
                    Logger.e(TAG, e.getMessage());
                    callback.onResult(null);
                }
            } else {
                callback.onResult(null);
            }
        } else {
            callback.onResult(null);
        }
    }
}
