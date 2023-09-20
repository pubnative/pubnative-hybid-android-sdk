package net.pubnative.lite.sdk.views.endcard;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.pubnative.lite.sdk.core.R;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.mraid.MRAIDBanner;
import net.pubnative.lite.sdk.mraid.MRAIDNativeFeatureListener;
import net.pubnative.lite.sdk.mraid.MRAIDView;
import net.pubnative.lite.sdk.mraid.MRAIDViewListener;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.utils.ViewUtils;
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
    private SimpleTimer mEndcardTimer;

    private EndCardViewListener endcardViewListener;

    // Config
    private SkipOffset skipOffset = new SkipOffset(SkipOffsetManager.getDefaultEndcardSkipOffset(), false);

    private Boolean isCustomEndCard = false;

    // Listeners
    private final MRAIDViewListener mraidViewListener = new MRAIDViewListener() {

        @Override
        public void mraidViewLoaded(MRAIDView mraidView) {
            if (endcardViewListener != null)
                endcardViewListener.onShow(isCustomEndCard);
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
                endcardViewListener.onClick(isCustomEndCardClick);
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
        initControViews();
    }

    public HyBidEndCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
        initControViews();
    }

    public HyBidEndCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
        initControViews();
    }

    private void initControViews() {

        int skipViewSize = (int) ViewUtils.convertDpToPixel(30f, getContext());
        LayoutParams lp = new LayoutParams(skipViewSize, skipViewSize);
        lp.gravity = Gravity.START;
        int margin = (int) ViewUtils.convertDpToPixel(8f, getContext());
        lp.setMargins(margin, margin, 0, 0);


        mSkipView = new ImageView(getContext());
        mSkipView.setId(R.id.end_card_skip_view);
        mSkipView.setLayoutParams(lp);
        mSkipView.setImageResource(R.mipmap.skip);
        mSkipView.setVisibility(View.GONE);
        mSkipView.setOnClickListener(v -> {
            if (endcardViewListener != null) endcardViewListener.onSkip();
        });

        mCloseView = new ImageView(getContext());
        mCloseView.setId(R.id.button_fullscreen_close);
        mCloseView.setLayoutParams(lp);
        mCloseView.setImageResource(R.mipmap.close);
        mCloseView.setVisibility(View.GONE);
        mCloseView.setOnClickListener(v -> {
            if (endcardViewListener != null) endcardViewListener.onClose();
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
        this.setVisibility(GONE);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    private ImageView createStaticEndCardView() {
        LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ImageView imageView = new ImageView(this.getContext());
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endcardViewListener != null)
                    endcardViewListener.onClick(isCustomEndCard);
            }
        });
        return imageView;
    }

    private FrameLayout createHtmlEndCardContainer() {
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
                this.addView(staticEndCardView);
                staticEndCardView.setVisibility(View.VISIBLE);
                ImageUtils.setScaledImage(staticEndCardView, imageUri);
                if (endcardViewListener != null) {
                    endcardViewListener.onShow(endCardData.isCustom());
                }
            } else if (!TextUtils.isEmpty(endCardData.getContent())) {
                htmlEndCardContainer = createHtmlEndCardContainer();
                this.addView(htmlEndCardContainer);
                htmlEndCardContainer.setVisibility(View.VISIBLE);
                if (endCardData.getType() == EndCardData.Type.IFRAME_RESOURCE) {
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
                    renderHtmlEndcard(endCardData.getContent(), endCardData.isCustom());
                }
            }
        }
    }

    private void renderHtmlEndcard(String content, boolean isCustom) {
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
        htmlEndCardContainer.addView(mHtmlEndCardView);
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
        this.addView(staticEndCardView);
        staticEndCardView.setVisibility(View.VISIBLE);
        ImageUtils.setScaledImage(staticEndCardView, imageUri);
    }

    public void hide() {
        clearEndCardViews();
        this.setVisibility(View.GONE);
    }

    public void showSkipButton() {
        mCloseView.setVisibility(INVISIBLE);
        startSkipOffsetTimer(() -> {
            mSkipView.setVisibility(VISIBLE);
            mSkipView.bringToFront();
        });
    }

    public void showCloseButton() {
        mSkipView.setVisibility(INVISIBLE);
        startSkipOffsetTimer(() -> {
            mCloseView.setVisibility(VISIBLE);
            mCloseView.bringToFront();
        });
    }

    public void startSkipOffsetTimer(Runnable callback) {

        int delay = skipOffset.getOffset();

        if (delay >= 0) {
            int endCardDelayInMillis = delay * 1000;

            mEndcardTimer = new SimpleTimer(endCardDelayInMillis, new SimpleTimer.Listener() {
                @Override
                public void onFinish() {
                    callback.run();
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            });
            mEndcardTimer.start();
        } else {
            callback.run();
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

        public void onClick(Boolean isCustomEndCard);

        public void onSkip();

        public void onClose();

        public void onShow(Boolean isCustomEndCard);
    }
}
