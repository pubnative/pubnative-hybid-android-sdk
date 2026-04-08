// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded;

import android.view.View;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.views.CloseableContainer;

public interface RewardedActivityInteractor {

    void addContentInfoView(View mContentInfoView, FrameLayout.LayoutParams layoutParams);

    void removeContentInfoView(View mContentInfoView);

    void addWatermarkView(View watermarkView);

    void setCloseSize(int reducedCloseButtonSize);

    void showProgressBar();

    void hideProgressBar();

    void finishActivity();

    void addProgressBarView(FrameLayout.LayoutParams pBarParams);

    void addAdView(View adView, FrameLayout.LayoutParams params);

    void showRewardedCloseButton(CloseableContainer.OnCloseListener mCloseListener);

    void showRewardedSkipButton(CloseableContainer.OnSkipListener skipListener);

    void hideRewardedCloseButton();

    void hideRewardedSkipButton();

    void setContentLayout();

    void setSkipSize(int reducedCloseButtonSize);
}
