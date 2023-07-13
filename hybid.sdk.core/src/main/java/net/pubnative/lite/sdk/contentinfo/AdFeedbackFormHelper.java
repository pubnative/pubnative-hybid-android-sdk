package net.pubnative.lite.sdk.contentinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.contentinfo.listeners.AdFeedbackLoadListener;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.Logger;

public class AdFeedbackFormHelper extends ResultReceiver{
    private static final String TAG = AdFeedbackFormHelper.class.getSimpleName();

    private AdFeedbackLoadListener mListener;

    public AdFeedbackFormHelper() {
        super(null);
    }

    public void showFeedbackForm(
            Context context,
            String url,
            Ad ad,
            String adFormat,
            IntegrationType integrationType
    ) {
        showFeedbackForm(context, url, ad, adFormat, integrationType, null);
    }

    public synchronized void showFeedbackForm(
            Context context,
            String url,
            Ad ad,
            String adFormat,
            IntegrationType integrationType,
            AdFeedbackLoadListener listener
    ) {

        mListener = listener;

        String parsedUrl = parseUrl(url);

        if(parsedUrl == null){
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
            }
            return;
        }

        AdFeedbackData mAdFeedbackData = new AdFeedbackDataCollector().collectData(ad, adFormat, integrationType);
        FeedbackMacros macroHelper = new FeedbackMacros();
        parsedUrl = macroHelper.processUrl(parsedUrl, mAdFeedbackData);

        try {
            Intent intent = new Intent(context, AdFeedbackActivity.class);;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_URL, parsedUrl);
            intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_CALLBACK, this);
            intent.putExtra(AdFeedbackActivity.EXTRA_FEEDBACK_FORM_DATA, mAdFeedbackData);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        } catch (Exception exception){
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK, "The feedback form requires an Activity context"));
            }
        }
    }

    private String parseUrl(String url){
        String parsedUrl = null;
        if (!TextUtils.isEmpty(url)) {
            try {
                Uri uri = Uri.parse(url);
                if (uri != null && TextUtils.isEmpty(uri.getQueryParameter("apptoken"))) {
                    String appTokenMacroPlaceholder = "token_macro";
                    uri = uri.buildUpon().appendQueryParameter("apptoken", appTokenMacroPlaceholder).build();
                    url = uri.toString();
                    // Workaround for uri encoding of the macro
                    parsedUrl = url.replace(appTokenMacroPlaceholder, FeedbackMacros.MACRO_APP_TOKEN);
                }
            } catch (RuntimeException exception) {
                Logger.e(TAG, exception.getMessage());
                HyBid.reportException(exception);
            }
        }
        return parsedUrl;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        handleResult(resultCode, resultData);
    }

    private void handleResult(int resultCode, Bundle resultData){
        if(resultCode == FeedbackFormAction.OPEN.code){
            if (mListener != null) {
                mListener.onLoad("");
            }
        } else if (resultCode == FeedbackFormAction.CLOSE.code){
            if (mListener != null) {
                mListener.onFormClosed();
            }
        } else if (resultCode == FeedbackFormAction.ERROR.code){
            if (mListener != null) {
                mListener.onLoadFailed(new HyBidError(HyBidErrorCode.ERROR_LOADING_FEEDBACK));
            }
        }
    }

    enum FeedbackFormAction {
        OPEN(1),
        ERROR(-1),
        CLOSE(0);
        final int code;
        FeedbackFormAction(int i) {
            code = i;
        }
    }
}
