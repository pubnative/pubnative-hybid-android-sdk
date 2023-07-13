package net.pubnative.lite.sdk.views.endcard;

import static net.pubnative.lite.sdk.views.endcard.EndCardConstants.CUSTOM_END_CARD_CLICK_URL;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.utils.ImageUtils;

public class HyBidEndCardView extends FrameLayout {

    private ImageView staticEndCardView;
    private FrameLayout htmlEndCardContainer;
    private MRAIDBanner mHtmlEndCardView;

    private SimpleTimer mEndcardTimer;

    private EndCardViewListener endcardViewListener;

    // Config
    private SkipOffset skipOffset = new SkipOffset(SkipOffsetManager.getDefaultEndcardSkipOffset(), false);

    // Listeners
    private final MRAIDViewListener mraidViewListener = new MRAIDViewListener() {

        @Override
        public void mraidViewLoaded(MRAIDView mraidView) {
            ViewTreeObserver.OnDrawListener listener = new ViewTreeObserver.OnDrawListener() {
                @Override
                public void onDraw() {
                    if (htmlEndCardContainer != null) {
                        htmlEndCardContainer.post(() -> {
                                    if (htmlEndCardContainer != null)
                                        htmlEndCardContainer.getViewTreeObserver().removeOnDrawListener(this);
                                }
                        );
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
                Boolean isCustomEndCardClick = url != null && url.contains(EndCardConstants.CUSTOM_END_CARD_CLICK_URL);
                endcardViewListener.onClick(isCustomEndCardClick, url);
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
        this(context, null, 0);
    }

    public HyBidEndCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HyBidEndCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
    }

    public void setSkipOffset(SkipOffset skipOffset) {
        this.skipOffset = skipOffset;
    }

    public void setEndCardViewListener(EndCardViewListener listener) {
        this.endcardViewListener = listener;
    }

    private void initUi() {
        this.setVisibility(GONE);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    private ImageView createStaticEndCardView() {
        LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ImageView imageView = new ImageView(this.getContext());
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setVisibility(View.GONE);
        return imageView;
    }

    private FrameLayout createHtmlEndCardContainer() {
        LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = new FrameLayout(this.getContext());
        frameLayout.setVisibility(View.GONE);
        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        return frameLayout;
    }

    public void show(EndCardData endCardData, String imageUri) {
        this.setVisibility(View.VISIBLE);
        if (endCardData != null) {
            configUi(endCardData);
            clearEndCardViews();
            if (endCardData.getType() == EndCardData.Type.STATIC_RESOURCE) {
                staticEndCardView = createStaticEndCardView();
                this.addView(staticEndCardView);
                staticEndCardView.setVisibility(View.VISIBLE);
                ImageUtils.setScaledImage(staticEndCardView, imageUri);
            } else if (!TextUtils.isEmpty(endCardData.getContent())) {
                htmlEndCardContainer = createHtmlEndCardContainer();
                this.addView(htmlEndCardContainer);
                htmlEndCardContainer.setVisibility(View.VISIBLE);
                if (endCardData.getType() == EndCardData.Type.IFRAME_RESOURCE) {
                    mHtmlEndCardView = new MRAIDBanner(this.getContext(), endCardData.getContent(), "", false, false, new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                } else {
                    mHtmlEndCardView = new MRAIDBanner(this.getContext(), "", endCardData.getContent(), false, false, new String[]{}, mraidViewListener, mraidNativeFeatureListener, null);
                }
                mHtmlEndCardView.setSkipOffset(skipOffset.getOffset());
                mHtmlEndCardView.setUseCustomClose(true);
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (endCardData.isCustom()) {
                    height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                lp.gravity = Gravity.CENTER_VERTICAL;
                mHtmlEndCardView.setLayoutParams(lp);
                htmlEndCardContainer.addView(mHtmlEndCardView);
            }
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
        this.addView(staticEndCardView);
        staticEndCardView.setVisibility(View.VISIBLE);
        ImageUtils.setScaledImage(staticEndCardView, imageUri);
    }

    public void hide() {
        clearEndCardViews();
        this.setVisibility(View.GONE);
    }

    public void showCloseButton(CloseButtonListener listener) {

        int delay = skipOffset.getOffset();

        if (delay >= 0) {
            int endCardDelayInMillis = delay * 1000;

            mEndcardTimer = new SimpleTimer(endCardDelayInMillis, new SimpleTimer.Listener() {
                @Override
                public void onFinish() {
                    listener.onCloseButtonVisible();
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            });
            mEndcardTimer.start();
        } else {
            listener.onCloseButtonVisible();
        }
    }

    public void pause() {
        if (mEndcardTimer != null) mEndcardTimer.pause();
        if (mHtmlEndCardView != null) mHtmlEndCardView.pause();
    }

    public void resume() {
        if (mEndcardTimer != null) mEndcardTimer.resume();
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
        if (mEndcardTimer != null) {
            mEndcardTimer.cancel();
        }
        clearEndCardViews();
    }

    public interface EndCardViewListener {

        public void onClick(Boolean isCustomEndCard, String openUrl);
    }
}
