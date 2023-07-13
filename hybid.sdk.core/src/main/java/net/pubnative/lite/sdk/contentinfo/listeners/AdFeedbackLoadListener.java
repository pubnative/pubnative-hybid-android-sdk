package net.pubnative.lite.sdk.contentinfo.listeners;

import android.os.Parcelable;

import java.io.Serializable;

public interface AdFeedbackLoadListener {

    void onLoad(String url);

    void onLoadFinished();

    void onLoadFailed(Throwable error);

    void onFormClosed();
}
