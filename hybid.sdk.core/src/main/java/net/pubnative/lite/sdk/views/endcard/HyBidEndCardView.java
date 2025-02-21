package net.pubnative.lite.sdk.views.endcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.views.CustomImageView;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;

import java.util.List;
import java.util.Map;

public class HyBidEndCardView extends FrameLayout {

    private ImageView staticEndCardView;
    private FrameLayout htmlEndCardContainer;
    private MRAIDBanner mHtmlEndCardView;

    private ImageView mSkipView;
    private ImageView mCloseView;
    private SimpleTimer mSkipEndcardTimer;
    private SimpleTimer mCloseEndcardTimer;

    private EndCardViewListener endcardViewListener;

    private boolean isViewShowingCloseButton = false;
    private boolean isViewShowingSkipButton = false;

    private String endCardType = "";

    private GestureDetector gestureDetector;

    // Config
    private SkipOffset skipOffset = new SkipOffset(SkipOffsetManager.getDefaultEndcardSkipOffset(), false);

    private Boolean isCustomEndCard = false;
    private boolean hasReducedClose = false;

    // Listeners
    private final MRAIDViewListener mraidViewListener = new MRAIDViewListener() {

        @Override
        public void mraidViewLoaded(MRAIDView mraidView) {
            if (endcardViewListener != null) {
                endcardViewListener.onLoadSuccess(isCustomEndCard);
                endcardViewListener.onShow(isCustomEndCard, endCardType);
            }
            ViewTreeObserver.OnDrawListener listener = new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    if (htmlEndCardContainer != null) {
                        htmlEndCardContainer.post(() -> {
                            if (htmlEndCardContainer != null)
                                htmlEndCardContainer.getViewTreeObserver().removeOnDrawListener(this);
                        });
                    }

                    if (mHtmlEndCardView != null && mHtmlEndCardView.getMeasuredHeight() < 50) {
                        mHtmlEndCardView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                }
            };
            mHtmlEndCardView.getViewTreeObserver().addOnDrawListener(listener);
        }

        @Override
        public void mraidViewError(MRAIDView mraidView) {
            endcardViewListener.onLoadFail(isCustomEndCard);
        }

        @Override
        public void mraidViewExpand(MRAIDView mraidView) {
        }

        @Override
        public void mraidViewClose(MRAIDView mraidView) {
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
    };

    private MRAIDNativeFeatureListener mraidNativeFeatureListener = new MRAIDNativeFeatureListener() {
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
            if (endcardViewListener != null) {
                endcardViewListener.onClick(url, isCustomEndCard, endCardType);
            }
        }

        @Override
        public void mraidNativeFeatureStorePicture(String url) {
        }

        @Override
        public void mraidNativeFeatureSendSms(String url) {
        }
    };

    public HyBidEndCardView(Context context) {
        super(context);
        initUi();
        initControlViews();
    }

    public HyBidEndCardView(Context context, boolean hasReducedClose) {
        super(context);
        this.hasReducedClose = hasReducedClose;
        initUi();
        initControlViews();
    }

    public HyBidEndCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
        initControlViews();
    }

    public HyBidEndCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
        initControlViews();
    }

    private void initControlViews() {
        if (!isValidContext()) {
            if (endcardViewListener != null)
                endcardViewListener.onLoadFail(isCustomEndCard);
            return;
        }
        int skipViewSize = (int) ViewUtils.convertDpToPixel(30f, getContext());
        if (hasReducedClose) {
            skipViewSize = (int) ViewUtils.convertDpToPixel(20f, getContext());
        }
        LayoutParams lp = new LayoutParams(skipViewSize, skipViewSize);
        lp.gravity = Gravity.START;
        int margin = (int) ViewUtils.convertDpToPixel(8f, getContext());
        lp.setMargins(margin, margin, 0, 0);

        mSkipView = new ImageView(getContext());
        if (hasReducedClose) {
            mSkipView.setId(R.id.end_card_skip_view_small);
        } else {
            mSkipView.setId(R.id.end_card_skip_view);
        }
        mSkipView.setLayoutParams(lp);
        mSkipView.setImageResource(R.mipmap.skip);
        mSkipView.setVisibility(View.GONE);
        mSkipView.setOnClickListener(v -> {
            if (endcardViewListener != null) endcardViewListener.onSkip();
        });

        mCloseView = new ImageView(getContext());
        if (hasReducedClose) {
            mCloseView.setId(R.id.button_fullscreen_close_small);
        } else {
            mCloseView.setId(R.id.button_fullscreen_close);
        }
        mCloseView.setLayoutParams(lp);
        mCloseView.setImageResource(R.mipmap.close);
        mCloseView.setVisibility(View.GONE);
        mCloseView.setOnClickListener(v -> {
            if (endcardViewListener != null) endcardViewListener.onClose(isCustomEndCard);
        });

        this.addView(mSkipView);
        this.addView(mCloseView);
    }

    public void setSkipOffset(SkipOffset skipOffset) {
        this.skipOffset = skipOffset;
    }

    public void setEndCardViewListener(EndCardViewListener listener) {
        this.endcardViewListener = listener;
    }

    private void initUi() {
        if (!isValidContext()) {
            if (endcardViewListener != null)
                endcardViewListener.onLoadFail(isCustomEndCard);
            return;
        }
        this.setVisibility(GONE);
        this.setBackgroundColor(Color.TRANSPARENT);
        try {
            gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    private ImageView createStaticEndCardView() {
        if (!isValidContext()) {
            return null;
        }
        LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        CustomImageView imageView = new CustomImageView(this.getContext());
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(view -> {
            if (endcardViewListener != null)
                endcardViewListener.onClick(null, isCustomEndCard, endCardType);
        });

        endCardType = Reporting.Key.END_CARD_STATIC;
        return imageView;
    }

    private FrameLayout createHtmlEndCardContainer() {
        if (!isValidContext()) {
            return null;
        }
        FrameLayout frameLayout = new FrameLayout(this.getContext());
        frameLayout.setVisibility(View.GONE);
        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        return frameLayout;
    }

    public void show(EndCardData endCardData, String imageUri) {
        removeExistingEndcardViews();
        this.setVisibility(View.VISIBLE);
        if (endCardData != null) {
            this.isCustomEndCard = endCardData.isCustom();
            configUi(endCardData);
            clearEndCardViews();
            if (endCardData.getType() == EndCardData.Type.STATIC_RESOURCE) {
                staticEndCardView = createStaticEndCardView();
                if (staticEndCardView == null) {
                    if (endcardViewListener != null)
                        endcardViewListener.onLoadFail(isCustomEndCard);
                    return;
                }
                this.addView(staticEndCardView);
                staticEndCardView.setVisibility(View.VISIBLE);
                ImageUtils.setScaledImage(staticEndCardView, imageUri);
                if (endcardViewListener != null) {
                    endcardViewListener.onLoadSuccess(isCustomEndCard);
                    endcardViewListener.onShow(endCardData.isCustom(), endCardType);
                }
                endCardType = Reporting.Key.END_CARD_STATIC;
            } else if (!TextUtils.isEmpty(endCardData.getContent())) {
                htmlEndCardContainer = createHtmlEndCardContainer();
                if (htmlEndCardContainer == null) {
                    if (endcardViewListener != null)
                        endcardViewListener.onLoadFail(isCustomEndCard);
                    return;
                }
                this.addView(htmlEndCardContainer);
                htmlEndCardContainer.setVisibility(View.VISIBLE);
                if (endCardData.getType() == EndCardData.Type.IFRAME_RESOURCE) {
                    endCardType = Reporting.Key.END_CARD_IFRAME;
                    if (!isValidContext()) {
                        if (endcardViewListener != null)
                            endcardViewListener.onLoadFail(isCustomEndCard);
                    }
                    PNHttpClient.makeRequest(getContext(), endCardData.getContent(), null, null, true, new PNHttpClient.Listener() {
                        @Override
                        public void onSuccess(String response, Map<String, List<String>> headers) {
                            if (!TextUtils.isEmpty(response)) {
                                renderHtmlEndcard(response, endCardData.isCustom());
                            }
                        }

                        @Override
                        public void onFailure(Throwable error) {
                        }
                    });
                } else {
                    endCardType = Reporting.Key.END_CARD_HTML;
                    renderHtmlEndcard(endCardData.getContent(), endCardData.isCustom());
                }
            }
        }
    }

    private void renderHtmlEndcard(String content, boolean isCustom) {
        if (!isValidContext()) {
            endcardViewListener.onLoadFail(isCustomEndCard);
            return;
        }
        mHtmlEndCardView = new MRAIDBanner(this.getContext(), "", content, false, false, new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
        mHtmlEndCardView.setSkipOffset(skipOffset.getOffset());
        mHtmlEndCardView.setUseCustomClose(true);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (isCustom) {
            height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        lp.gravity = Gravity.CENTER_VERTICAL;
        mHtmlEndCardView.setLayoutParams(lp);
        if (htmlEndCardContainer != null) {
            htmlEndCardContainer.addView(mHtmlEndCardView);
            if (endcardViewListener != null) {
                endcardViewListener.onLoadSuccess(isCustomEndCard);
                endcardViewListener.onShow(isCustomEndCard, endCardType);
            }
        } else {
            if (endcardViewListener != null) {
                endcardViewListener.onLoadFail(isCustomEndCard);
            }
        }
    }

    private void removeExistingEndcardViews() {
        if (staticEndCardView != null) {
            this.removeView(staticEndCardView);
            staticEndCardView = null;
        }
        if (htmlEndCardContainer != null) {
            this.removeView(htmlEndCardContainer);
            htmlEndCardContainer = null;
        }
    }

    private void configUi(EndCardData endCardData) {
        if (endCardData == null) return;
        if (endCardData.isCustom()) {
            this.setBackgroundColor(Color.TRANSPARENT);
        } else {
            this.setBackgroundColor(Color.BLACK);
        }
    }

    public void show(String imageUri) {
        clearEndCardViews();
        staticEndCardView = createStaticEndCardView();
        if (staticEndCardView == null) {
            endcardViewListener.onLoadFail(isCustomEndCard);
            return;
        }
        this.addView(staticEndCardView);
        staticEndCardView.setVisibility(View.VISIBLE);
        ImageUtils.setScaledImage(staticEndCardView, imageUri);
    }

    public void hide() {
        clearEndCardViews();
        this.setVisibility(View.GONE);
    }

    public void hideSkipButton() {
        mSkipView.setVisibility(GONE);
    }

    public synchronized void showSkipButton() {
        if (!isViewShowingSkipButton) {
            mCloseView.setVisibility(INVISIBLE);
            startSkipOffsetTimer(() -> {
                mSkipView.setVisibility(VISIBLE);
                mSkipView.bringToFront();
            });
            isViewShowingSkipButton = true;
        }
    }


    public synchronized void showCloseButton(CloseButtonListener closeButtonListener) {
        if (!isViewShowingCloseButton) {
            mSkipView.setVisibility(INVISIBLE);
            startCloseTimer(() -> {
                mCloseView.setVisibility(VISIBLE);
                mCloseView.bringToFront();
                closeButtonListener.onCloseButtonVisible();
            });
            isViewShowingCloseButton = true;
        }
    }

    public synchronized void startSkipOffsetTimer(Runnable callback) {
        int delay = skipOffset.getOffset();
        if (delay >= 0) {
            mSkipEndcardTimer = new SimpleTimer(delay * 1000L, new SimpleTimer.Listener() {
                @Override
                public void onFinish() {
                    if (callback != null) {
                        callback.run();
                    }
                    if (mSkipEndcardTimer != null) {
                        mSkipEndcardTimer.cancel();
                        mSkipEndcardTimer = null;
                    }
                    isViewShowingSkipButton = false;
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            });
            mSkipEndcardTimer.start();
        } else {
            callback.run();
        }
    }

    public synchronized void startCloseTimer(Runnable callback) {
        int delay = skipOffset.getOffset();
        if (delay >= 0) {
            mCloseEndcardTimer = new SimpleTimer(delay * 1000L, new SimpleTimer.Listener() {
                @Override
                public void onFinish() {
                    if (callback != null) {
                        callback.run();
                    }
                    if (mCloseEndcardTimer != null) {
                        mCloseEndcardTimer.cancel();
                        mCloseEndcardTimer = null;
                    }
                    isViewShowingCloseButton = false;
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            });
            mCloseEndcardTimer.start();
        } else {
            callback.run();
        }
    }

    public void pause() {
        if (mSkipEndcardTimer != null) mSkipEndcardTimer.pause();
        if (mCloseEndcardTimer != null) mCloseEndcardTimer.pause();
        if (mHtmlEndCardView != null) mHtmlEndCardView.pause();
    }

    public void resume() {
        if (mSkipEndcardTimer != null) mSkipEndcardTimer.resume();
        if (mCloseEndcardTimer != null) mCloseEndcardTimer.resume();
        if (mHtmlEndCardView != null) mHtmlEndCardView.resume();
    }

    private void clearEndCardViews() {
        if (htmlEndCardContainer != null) {
            this.removeView(htmlEndCardContainer);
            htmlEndCardContainer = null;
        }
        if (staticEndCardView != null) {
            this.removeView(staticEndCardView);
            staticEndCardView.setImageDrawable(null);
            staticEndCardView = null;
            if (mHtmlEndCardView != null) {
                mHtmlEndCardView.destroy();
            }
        }
    }

    public void destroy() {
        if (mSkipEndcardTimer != null) {
            mSkipEndcardTimer.cancel();
        }
        if (mCloseEndcardTimer != null) {
            mCloseEndcardTimer.cancel();
        }
        clearEndCardViews();
    }

    public interface EndCardViewListener {

        void onClick(String url, Boolean isCustomEndCard, String endCardType);

        void onSkip();

        void onClose(Boolean isCustomEndCard);

        void onShow(Boolean isCustomEndCard, String endCardType);

        void onLoadSuccess(Boolean isCustomEndCard);

        void onLoadFail(Boolean isCustomEndCard);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(event)) {
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isValidContext() {
        return getContext() != null;
    }
}