// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.rewarded.RewardedActivityInteractor;
import net.pubnative.lite.sdk.rewarded.viewModel.MraidRewardedViewModel;
import net.pubnative.lite.sdk.rewarded.viewModel.RewardedViewModel;
import net.pubnative.lite.sdk.rewarded.viewModel.VastRewardedViewModel;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.vpaid.HyBidActivityInteractor;

public abstract class HyBidRewardedActivity extends Activity implements RewardedActivityInteractor {

    public static final String EXTRA_ZONE_ID = "extra_pn_zone_id";
    public static final String EXTRA_BROADCAST_ID = "extra_pn_broadcast_id";
    public static final String EXTRA_SKIP_OFFSET = "extra_pn_skip_offset";
    public static final String INTEGRATION_TYPE = "integration_type";

    private CloseableContainer mCloseableContainer;
    private ProgressBar mProgressBar;

    protected boolean mIsFinishing = false;

    protected HyBidActivityInteractor mInteractor;
    protected RewardedViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initializeViews();
        initializeViewModel();
    }

    private void initializeViews() {
        mCloseableContainer = new CloseableContainer(this);
        mProgressBar = new ProgressBar(this);
        mCloseableContainer.setBackgroundColor(Color.BLACK);
    }

    private void initializeViewModel() {
        Intent mIntent = getIntent();
        mInteractor = HyBidActivityInteractor.getInstance();
        if (this instanceof VastRewardedActivity) {
            mViewModel = new VastRewardedViewModel(this, mIntent.getStringExtra(EXTRA_ZONE_ID), mIntent.getStringExtra(INTEGRATION_TYPE), mIntent.getIntExtra(EXTRA_SKIP_OFFSET, -1), mIntent.getLongExtra(EXTRA_BROADCAST_ID, -1), this);
        } else {
            mViewModel = new MraidRewardedViewModel(this, mIntent.getStringExtra(EXTRA_ZONE_ID), mIntent.getStringExtra(INTEGRATION_TYPE), mIntent.getIntExtra(EXTRA_SKIP_OFFSET, -1), mIntent.getLongExtra(EXTRA_BROADCAST_ID, -1), this);
        }
        mInteractor.activityCreated();
    }

    protected void dismiss() {
        mViewModel.sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLOSE);
        mIsFinishing = true;
        finish();
    }

    @Override
    public void setContentLayout() {
        setContentView(mCloseableContainer);
    }

    @Override
    public void addContentInfoView(View contentInfoView, FrameLayout.LayoutParams layoutParams) {
        if (mCloseableContainer != null) {
            mCloseableContainer.setClosePosition(CloseableContainer.ClosePosition.TOP_LEFT);
            if (layoutParams != null) mCloseableContainer.addView(contentInfoView, layoutParams);
            else mCloseableContainer.addView(contentInfoView);
        }
    }

    @Override
    public void removeContentInfoView(View contentInfoView) {
        if (mCloseableContainer != null) mCloseableContainer.removeView(contentInfoView);
    }

    @Override
    public void setCloseSize(int reducedCloseButtonSize) {
        if (mCloseableContainer != null) mCloseableContainer.setCloseSize(reducedCloseButtonSize);
    }

    @Override
    public void hideProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishActivity() {
        mIsFinishing = true;
        finish();
    }

    @Override
    public void addProgressBarView(FrameLayout.LayoutParams pBarParams) {
        if (mCloseableContainer != null && mProgressBar != null)
            mCloseableContainer.addView(mProgressBar, pBarParams);
    }

    @Override
    public void addAdView(View adView, FrameLayout.LayoutParams params) {
        if (mCloseableContainer != null && adView != null)
            mCloseableContainer.addView(adView, params);
    }

    @Override
    public void showRewardedCloseButton(CloseableContainer.OnCloseListener closeListener) {
        if (mCloseableContainer != null && !isFinishing()) {
            mCloseableContainer.setCloseVisible(true);
            mCloseableContainer.setOnCloseListener(closeListener);
        }
    }

    @Override
    public void hideRewardedCloseButton() {
        if (mCloseableContainer != null) {
            mCloseableContainer.setCloseVisible(false);
            mCloseableContainer.setOnCloseListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        if (mCloseableContainer != null) {
            mCloseableContainer.removeAllViews();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mViewModel.isAdSkippable()) {
                dismiss();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }
}
