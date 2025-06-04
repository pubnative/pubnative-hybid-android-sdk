// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import android.view.View;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.views.CloseableContainer;

public interface InterstitialActivityInteractor {

    void addContentInfoView(View mContentInfoView, FrameLayout.LayoutParams layoutParams);

    void removeContentInfoView(View mContentInfoView);

    void setCloseSize(int reducedCloseButtonSize);

    void showProgressBar();

    void hideProgressBar();

    void finishActivity();

    void addProgressBarView(FrameLayout.LayoutParams pBarParams);

    void addAdView(View adView, FrameLayout.LayoutParams params);

    void showInterstitialCloseButton(CloseableContainer.OnCloseListener mCloseListener);

    void hideInterstitialCloseButton();

    void setContentLayout();
}
