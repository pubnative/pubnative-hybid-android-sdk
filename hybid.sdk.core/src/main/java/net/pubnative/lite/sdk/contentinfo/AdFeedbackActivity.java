package net.pubnative.lite.sdk.contentinfo;

import static android.view.ViewGroup.*;
import static net.pubnative.lite.sdk.models.Ad.CONTENT_INFO_LINK_URL;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.mraid.MRAIDInterstitial;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeature;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.URLValidator;
import net.pubnative.lite.sdk.utils.UrlHandler;
import net.pubnative.lite.sdk.views.ProgressDialogFragment;
import net.pubnative.lite.sdk.views.ProgressDialogView;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;

public class AdFeedbackActivity extends Activity implements MRAIDViewListener, MRAIDNativeFeatureListener {

    public static final String EXTRA_FEEDBACK_FORM_URL = "extra_feedback_form_url";
    public static final String EXTRA_FEEDBACK_FORM_CALLBACK = "extra_feedback_form_callback";
    public static final String EXTRA_FEEDBACK_FORM_DATA = "extra_feedback_form_data";

    private ResultReceiver callback;
    private String feedbackFormUrl;
    private MRAIDInterstitial mViewContainer;
    private AdFeedbackData mAdFeedbackData;
    private UrlHandler mUrlHandlerDelegate;
    private SimpleTimer mFeedbackFormExpirationTimer;
    private Boolean mIsFeedbackFormLoading = false;
    // Views
    RelativeLayout rootLayout;
    ProgressDialogView progressDialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        initVariables();
        getDataFromIntent(intent);
        loadFeedbackForm();
        initUi();
        initRootView();
        initViews();
        startProgress();
    }


    private void initRootView() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rootLayout = new RelativeLayout(this);
        rootLayout.setLayoutParams(lp);
        setContentView(rootLayout);
    }

    private void initViews() {

        progressDialogView = new ProgressDialogView(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rootLayout.addView(progressDialogView, lp);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_FEEDBACK_FORM_URL, feedbackFormUrl);
        outState.putParcelable(EXTRA_FEEDBACK_FORM_CALLBACK, callback);
        outState.putSerializable(EXTRA_FEEDBACK_FORM_DATA, mAdFeedbackData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        callback = savedInstanceState.getParcelable(EXTRA_FEEDBACK_FORM_CALLBACK);
        feedbackFormUrl = savedInstanceState.getString(EXTRA_FEEDBACK_FORM_URL);
        mAdFeedbackData = (AdFeedbackData) savedInstanceState.getSerializable(EXTRA_FEEDBACK_FORM_DATA);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initUi(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    private void initVariables() {
        mUrlHandlerDelegate = new UrlHandler(this);
    }

    private void getDataFromIntent(Intent intent) {
        try {
            if(intent.hasExtra(EXTRA_FEEDBACK_FORM_URL) && !TextUtils.isEmpty(intent.getStringExtra(EXTRA_FEEDBACK_FORM_URL))) {
                feedbackFormUrl = intent.getStringExtra(EXTRA_FEEDBACK_FORM_URL);
            } else {
                sendError();
                finish();
            }
            if(intent.hasExtra(EXTRA_FEEDBACK_FORM_CALLBACK)){
                callback = intent.getParcelableExtra(EXTRA_FEEDBACK_FORM_CALLBACK);
            }
            if(intent.hasExtra(EXTRA_FEEDBACK_FORM_DATA) && intent.getSerializableExtra(EXTRA_FEEDBACK_FORM_DATA) != null){
                mAdFeedbackData = (AdFeedbackData) intent.getSerializableExtra(EXTRA_FEEDBACK_FORM_DATA);
            } else {
                sendError();
                finish();
            }
        } catch (Exception e){
            sendError();
            finish();
        }
    }

    private void startProgress(){
        mIsFeedbackFormLoading = true;
        cancelExistingFeedbackTimer();
        showProgressDialog(getString(R.string.feedback_form), getString(R.string.loading));
        mFeedbackFormExpirationTimer = new SimpleTimer(10000, new SimpleTimer.Listener() {
            @Override
            public void onFinish() {
                if (mIsFeedbackFormLoading) {
                    finish();
                }
            }
            @Override
            public void onTick(long millisUntilFinished) {}
        });
        mFeedbackFormExpirationTimer.start();
    }

    private void loadFeedbackForm(){
        if(feedbackFormUrl == null){
            sendError();
            finish();
        }
        mViewContainer = new MRAIDInterstitial(this, feedbackFormUrl, null, true, true,
                new String[]{
                        MRAIDNativeFeature.CALENDAR,
                        MRAIDNativeFeature.INLINE_VIDEO,
                        MRAIDNativeFeature.SMS,
                        MRAIDNativeFeature.STORE_PICTURE,
                        MRAIDNativeFeature.TEL,
                        MRAIDNativeFeature.LOCATION
                }, this, this, null);

        mViewContainer.markCreativeAdComingFromFeedbackForm();
        mViewContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void mraidNativeFeatureCallTel(String url) {

    }

    @Override
    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON) {

    }

    @Override
    public void mraidNativeFeaturePlayVideo(String url) {

    }

    @Override
    public void mraidNativeFeatureOpenBrowser(String url) {
        mUrlHandlerDelegate.handleUrl(url);
    }

    @Override
    public void mraidNativeFeatureStorePicture(String url) {

    }

    @Override
    public void mraidNativeFeatureSendSms(String url) {

    }

    @Override
    public void mraidViewLoaded(MRAIDView mraidView) {
        mIsFeedbackFormLoading = false;
        FeedbackJSInterface jsInterface = new FeedbackJSInterface();
        jsInterface.submitData(mAdFeedbackData, mraidView);
        hideProgressDialog();
        sendOpenAction();
        URLValidator.isValidURL(feedbackFormUrl, isValid -> {
            if (isValid) {
                mViewContainer.show(this, () -> {
                    mViewContainer.showDefaultContentInfoURL(CONTENT_INFO_LINK_URL);
                    sendError();
                }, feedbackFormUrl);
            } else {
                sendError();
                finish();
            }
        });
    }

    @Override
    public void mraidViewError(MRAIDView mraidView) {
        sendError();
        finish();
    }

    @Override
    public void mraidViewExpand(MRAIDView mraidView) {

    }

    @Override
    public void mraidViewClose(MRAIDView mraidView) {
        finish();
    }

    @Override
    public boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY) {
        return false;
    }

    @Override
    public void mraidShowCloseButton() {

    }

    @Override
    public void onExpandedAdClosed() {

    }

    public void showProgressDialog(String title, String message) {
        progressDialogView.show(title, message);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgressDialog() {
        progressDialogView.hide();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void sendCloseAction(){
        if(callback != null){
            callback.send(AdFeedbackFormHelper.FeedbackFormAction.CLOSE.code, null);
        }
    }

    private void sendOpenAction(){
        if(callback != null){
            callback.send(AdFeedbackFormHelper.FeedbackFormAction.OPEN.code, null);
        }
    }

    private void sendError(){
        if(callback != null){
            callback.send(AdFeedbackFormHelper.FeedbackFormAction.ERROR.code, null);
        }
    }

    @Override
    protected void onDestroy() {
        sendCloseAction();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void cancelExistingFeedbackTimer() {
        if (mFeedbackFormExpirationTimer != null) {
            mFeedbackFormExpirationTimer.cancel();
        }
    }
}
