// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
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
