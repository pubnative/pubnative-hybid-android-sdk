// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.PositionX;
import net.pubnative.lite.sdk.source.pnapi.R;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;
import net.pubnative.lite.sdk.utils.ViewUtils;

public class PNAPIContentInfoView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = PNAPIContentInfoView.class.getSimpleName();

    public interface ContentInfoListener {
        void onIconClicked();

        void onLinkClicked(String url);
    }

    private LinearLayout mContainerView;
    private TextView mContentInfoText;
    private ImageView mContentInfoIcon;
    private ContentInfoListener mContentInfoListener;
    private boolean mAdFeedbackEnabled = false;

    private Handler mHandler;

    private final Runnable mCloseTask = PNAPIContentInfoView.this::closeLayout;

    public PNAPIContentInfoView(Context context) {
        super(context);
        init(context);
    }

    public PNAPIContentInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PNAPIContentInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mHandler = new Handler(Looper.getMainLooper());
        mContainerView = (LinearLayout) inflater.inflate(R.layout.content_info_layout, this, false);
        mContentInfoIcon = mContainerView.findViewById(R.id.ic_context_icon);
        mContentInfoText = mContainerView.findViewById(R.id.tv_context_text);
        addView(mContainerView);
    }

    public void setContentInfoListener(ContentInfoListener listener) {
        if (listener != null) {
            mContentInfoListener = listener;
        }
    }

    public void setAdFeedbackEnabled(boolean feedbackEnabled) {
        this.mAdFeedbackEnabled = feedbackEnabled;
    }

    public void openLayout() {
        mContentInfoText.setVisibility(VISIBLE);
        mHandler.postDelayed(mCloseTask, 3000);
    }

    public void closeLayout() {
        mContentInfoText.setVisibility(GONE);
    }

    public void setIconUrl(String iconUrl) {
        new PNBitmapDownloader().download(iconUrl, mContentInfoIcon.getWidth(), mContentInfoIcon.getHeight(), new PNBitmapDownloader.DownloadListener() {
            @Override
            public void onDownloadFinish(String url, Bitmap bitmap) {
                mContentInfoIcon.setImageBitmap(bitmap);
            }

            @Override
            public void onDownloadFailed(String url, Exception ex) {

            }
        });
    }

    public void setIconClickUrl(final String iconClickUrl) {
        mContentInfoText.setOnClickListener(view -> {
            if (mContentInfoListener == null || !mAdFeedbackEnabled || !(getContext() instanceof Activity)) {
                try {
                    Intent openLink = new Intent(Intent.ACTION_VIEW);
                    openLink.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    openLink.setData(Uri.parse(iconClickUrl));
                    view.getContext().startActivity(openLink);
                } catch (Exception e) {
                    Log.e(TAG, "error on click content info text", e);
                }
            } else {
                mContentInfoListener.onLinkClicked(iconClickUrl);
            }
        });
    }

    public void setContextText(String text) {
        if (text != null && !text.isEmpty()) {
            mContentInfoText.setText(text);
        }
    }

    public void setDpDimensions(ContentInfo contentInfo) {
        if (contentInfo.getPositionX() == PositionX.RIGHT) {
            mContainerView.removeView(mContentInfoIcon);
            mContainerView.addView(mContentInfoIcon);
        }

        LinearLayout.LayoutParams imageLayoutParams = (LinearLayout.LayoutParams) mContentInfoIcon.getLayoutParams();
        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) mContentInfoText.getLayoutParams();

        if (contentInfo.getWidth() != -1 && contentInfo.getHeight() != -1) {
            imageLayoutParams.width = ViewUtils.asIntPixels(contentInfo.getWidth(), getContext());
            imageLayoutParams.height = ViewUtils.asIntPixels(contentInfo.getHeight(), getContext());

            textLayoutParams.width = LayoutParams.WRAP_CONTENT;
            textLayoutParams.height = ViewUtils.asIntPixels(contentInfo.getHeight(), getContext());
        }

        mContentInfoIcon.setLayoutParams(imageLayoutParams);
        mContentInfoText.setLayoutParams(textLayoutParams);
    }

    @Override
    public void onClick(View v) {
        if (mContentInfoListener != null) {
            mContentInfoListener.onIconClicked();
        }
        openLayout();
    }
}