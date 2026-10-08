// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import android.view.View;
import android.widget.FrameLayout;

import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.views.TouchAreaDelegate;

public interface InterstitialActivityInteractor {

    void addContentInfoView(View mContentInfoView, FrameLayout.LayoutParams layoutParams);

    void removeContentInfoView(View mContentInfoView);

    void addWatermarkView(View watermarkView);

    void setCloseSize(int reducedCloseButtonSize);

    void showProgressBar();

    void hideProgressBar();

    void finishActivity();

    void addProgressBarView(FrameLayout.LayoutParams pBarParams);

    void addAdView(View adView, FrameLayout.LayoutParams params);

    void showInterstitialCloseButton(CloseableContainer.OnCloseListener mCloseListener);

    void showInterstitialSkipButton(CloseableContainer.OnSkipListener mSkipListener);

    void hideInterstitialCloseButton();

    void hideInterstitialSkipButton();

    void setContentLayout();

    void setSkipSize(int reducedCloseButtonSize);

    // Default no-ops: additive hooks that must not break third-party implementations compiled
    // against an older SDK. The built-in activity overrides them.
    default void setCloseTouchSize(int touchSize) { }

    default void setCloseInitialTouchSize(int initialTouchSize) { }

    default void setCloseClickStateProvider(TouchAreaDelegate.ClickStateProvider provider) { }
}
