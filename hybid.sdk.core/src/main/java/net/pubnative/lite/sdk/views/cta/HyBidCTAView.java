package net.pubnative.lite.sdk.views.cta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.utils.PNBitmapDownloader;
import net.pubnative.lite.sdk.utils.ScreenDimensionsUtils;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.views.helpers.ImageHelper;
import net.pubnative.lite.sdk.vpaid.helpers.SimpleTimer;

public class HyBidCTAView extends FrameLayout {

    private CTAViewListener listener;
    private SimpleTimer mShowTimer;

    private ImageView icon;
    private TextView button;

    private final int backgroundColor = Color.argb(102, 0, 0, 0);
    private final int buttonColor = Color.argb(255, 0, 122, 255);

    private final long animationDuration = 1000;

    private Boolean isLoaded = null;
    private Boolean showImmediately = false;

    public HyBidCTAView(Context context) {
        super(context);
        initUi();
        initViews();
    }

    public HyBidCTAView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
        initViews();
    }

    public HyBidCTAView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUi();
        initViews();
    }

    public void setListener(CTAViewListener listener) {
        this.listener = listener;
    }

    private void initUi() {
        this.setVisibility(INVISIBLE);
        this.setBackground(getRoundedDrawable(backgroundColor, 20));
    }

    private void initViews() {

        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setPadding(20, 20, 20, 20);
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        rootLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        this.icon = new ImageView(getContext());
        this.icon.setId(View.generateViewId());
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(ViewUtils.asIntPixels(40, getContext()), ViewUtils.asIntPixels(40, getContext()));
        iconLp.setMarginEnd(5);
        this.icon.setLayoutParams(iconLp);
        this.icon.setOnClickListener(view -> {
            invokeClick();
        });
        this.icon.setContentDescription("ctaIcon");

        this.button = new TextView(getContext());
        this.button.setId(View.generateViewId());
        this.button.setTextSize(18);
        this.button.setPadding(100, 0, 100, 0);
        this.button.setTextColor(Color.WHITE);
        this.button.setGravity(Gravity.CENTER);
        this.button.setAllCaps(true);
        this.button.setTypeface(null, Typeface.BOLD);
        this.button.setBackground(getRoundedDrawable(buttonColor, 20));
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewUtils.asIntPixels(40, getContext()));
        labelLp.setMarginStart(5);
        this.button.setLayoutParams(labelLp);
        this.button.setContentDescription("ctaButton");
        rootLayout.addView(this.icon);
        rootLayout.addView(this.button);
        this.setOnClickListener(view -> {
            invokeClick();
        });;
        this.addView(rootLayout);
    }

    private Drawable getRoundedDrawable(int color, float cornerRadius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(color);
        shape.setCornerRadii(new float[]{
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius
        });
        return shape;
    }

    public void show(String iconUrl, String buttonText, Integer delay) {

        resetAll();

        if(delay == null || delay == 0){
            showImmediately = true;
        }

        setIconUrl(iconUrl);
        setButton(buttonText);

        if (delay != null && delay > 0) {
            showWithDelay(delay);
        } else if(isLoaded != null && isLoaded){
            show();
        }
    }

    public void show(String iconUrl, String buttonText) {
        this.setVisibility(INVISIBLE);
        show(iconUrl, buttonText, null);
    }

    private void showWithDelay(Integer delay) {

        if (delay > 0) {
            int endCardDelayInMillis = delay * 1000;

            mShowTimer = new SimpleTimer(endCardDelayInMillis, new SimpleTimer.Listener() {
                @Override
                public void onFinish() {
                    mShowTimer = null;
                    if (isLoaded != null && isLoaded) {
                        show();
                    }
                }

                @Override
                public void onTick(long millisUntilFinished) {
                }
            });
            mShowTimer.start();
        } else {
            show();
        }
    }

    public void pause() {
        if (mShowTimer != null) mShowTimer.pause();
    }

    public void resume() {
        if (mShowTimer != null) mShowTimer.resume();
    }

    public void show() {
        if(isLoaded == null || !isLoaded || isVisible() || mShowTimer != null) return;
        invokeShow();
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(animationDuration);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        this.setVisibility(VISIBLE);
        this.startAnimation(inFromRight);
    }

    private void setIconUrl(String iconUrl) {

        new PNBitmapDownloader().download(iconUrl, icon.getWidth(), icon.getHeight(), new PNBitmapDownloader.DownloadListener() {
            @Override
            public void onDownloadFinish(String url, Bitmap bitmap) {
                if (bitmap != null) {
                    icon.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 20, ViewUtils.asIntPixels(40, getContext()), ViewUtils.asIntPixels(40, getContext())));
                    isLoaded = true;
                    if(showImmediately) show();
                } else {
                    invokeFail();
                    isLoaded = false;
                }
            }

            @Override
            public void onDownloadFailed(String url, Exception ex) {
                isLoaded = false;
                invokeFail();
            }
        });
    }

    public void hide() {
        if(mShowTimer != null) {
            mShowTimer.cancel();
            mShowTimer = null;
        }
        this.setVisibility(INVISIBLE);
    }

    public boolean isLoaded(){
        return isLoaded != null && isLoaded;
    }

    public boolean isVisible(){
        return this.getVisibility() == VISIBLE;
    }

    private void invokeClick() {
        if (listener != null)
            listener.onClick();
    }

    private void setButton(String label) {
        this.button.setText(label);
    }

    private void invokeFail() {
        if (listener != null)
            listener.onFail();
    }

    private void invokeShow() {
        if (listener != null)
            listener.onShow();
    }

    private void resetAll(){
        this.setVisibility(INVISIBLE);
        isLoaded = null;
        showImmediately = false;
    }

    public interface CTAViewListener {

        void onClick();

        void onShow();

        void onFail();
    }
}
