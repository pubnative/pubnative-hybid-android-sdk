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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ContentInfo;
import net.pubnative.lite.sdk.models.ContentInfoDisplay;
import net.pubnative.lite.sdk.models.ContentInfoIconXPosition;
import net.pubnative.lite.sdk.source.pnapi.R;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;
import net.pubnative.lite.sdk.utils.WrapperURLDigger;
import net.pubnative.lite.sdk.utils.ViewUtils;

import java.util.List;

public class PNAPIContentInfoView extends FrameLayout {

    private static final String TAG = PNAPIContentInfoView.class.getSimpleName();
    private static final int MAX_WIDTH_DP = 120;
    private static final int MAX_HEIGHT_DP = 30;
    private ContentInfoIconXPosition contentInfoIconXPosition;
    private String iconClickURL = null;
    private List<String> clickTrackers = null;
    private boolean isIconDownloading = false;

    public interface ContentInfoListener {
        void onIconClicked(List<String> clickTrackers);

        void onLinkClicked(String url);
    }

    private LinearLayout mContainerView;
    private TextView mContentInfoText;
    private ImageView mContentInfoIcon;
    private ContentInfoListener mContentInfoListener;
    private ContentInfoDisplay mContentInfoDisplay = ContentInfoDisplay.SYSTEM_BROWSER;

    private Handler mHandler;

    private final Runnable mCloseTask = PNAPIContentInfoView.this::closeLayout;

    public PNAPIContentInfoView(Context context, ContentInfoIconXPosition contentInfoIconXPosition) {
        super(context);
        this.contentInfoIconXPosition = contentInfoIconXPosition;
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
        /*if (contentInfoIconXPosition == ContentInfoIconXPosition.RIGHT) {
            mContainerView = (LinearLayout) inflater.inflate(R.layout.content_info_right_aligned_layout, this, false);
        } else {
            mContainerView = (LinearLayout) inflater.inflate(R.layout.content_info_left_aligned_layout, this, false);
        }*/
        mContainerView = (LinearLayout) inflater.inflate(R.layout.content_info_left_aligned_layout, this, false);

        mContentInfoIcon = mContainerView.findViewById(R.id.ic_context_icon);
        mContentInfoText = mContainerView.findViewById(R.id.tv_context_text);
        addView(mContainerView);
    }

    public void setContentInfoListener(ContentInfoListener listener) {
        if (listener != null) {
            mContentInfoListener = listener;
        }
    }

    public void setContentInfoDisplay(ContentInfoDisplay contentInfoDisplay) {
        if (contentInfoDisplay != null) {
            mContentInfoDisplay = contentInfoDisplay;
        }
    }

    public void openLayout() {
        if (iconClickURL == null || TextUtils.isEmpty(iconClickURL)) {
            if (mContentInfoListener != null && clickTrackers != null) {
                mContentInfoListener.onIconClicked(clickTrackers);
            }
        } else {
            mContentInfoText.setVisibility(VISIBLE);
            mHandler.postDelayed(mCloseTask, 3000);
            mContentInfoIcon.setOnClickListener(view -> {
                openLink();
            });
        }
    }

    public void closeLayout() {
        mContentInfoText.setVisibility(GONE);
        mContentInfoIcon.setOnClickListener(view -> {
            openLayout();
        });
    }

    public void setIconId(int iconId) {
        if (iconId != -1 && mContentInfoIcon != null) {
            mContentInfoIcon.setId(iconId);
        }
    }

    public void setIconUrl(String iconUrl) {
        setIconUrl(iconUrl, false);
    }

    public void setIconUrl(String iconUrl, boolean isDefault) {
        setIconUrl(iconUrl, isDefault, false);
    }

    public void setIconUrl(String iconUrl, boolean isDefault, boolean isRemoteConfig) {
        if (iconUrl == null || TextUtils.isEmpty(iconUrl) || isIconDownloading)
            return;

        isIconDownloading = true;
        String url = new WrapperURLDigger().getURL(iconUrl);

        new PNBitmapDownloader().download(url.trim(), mContentInfoIcon.getWidth(), mContentInfoIcon.getHeight(), new PNBitmapDownloader.DownloadListener() {
            @Override
            public void onDownloadFinish(String url, Bitmap bitmap) {
                isIconDownloading = false;
                if (bitmap != null) {
                    mContentInfoIcon.setImageBitmap(bitmap);
                } else if (!isDefault) {
                    setIconUrl(Ad.CONTENT_INFO_ICON_URL, true);
                    if (!isRemoteConfig) {
                        setIconClickUrl(Ad.CONTENT_INFO_LINK_URL);
                    }
                }
            }

            @Override
            public void onDownloadFailed(String url, Exception ex) {
                isIconDownloading = false;
                if (!isDefault) {
                    setIconUrl(Ad.CONTENT_INFO_ICON_URL, true);
                    if (!isRemoteConfig) {
                        setIconClickUrl(Ad.CONTENT_INFO_LINK_URL);
                    }
                }
            }
        });
    }

    public void setIconClickUrl(final String iconClickUrl) {
        this.iconClickURL = new WrapperURLDigger().getURL(iconClickUrl);

        mContentInfoText.setOnClickListener(view -> {
            openLink();
        });
    }

    public void setIconClickTrackers(List<String> clickTrackers) {
        this.clickTrackers = clickTrackers;
    }

    public List<String> getIconClickTrackers() {
        return clickTrackers;
    }

    public void openLink() {
        if (mContentInfoListener == null || mContentInfoDisplay == ContentInfoDisplay.SYSTEM_BROWSER || !(getContext() instanceof Activity) || TextUtils.isEmpty(iconClickURL)) {
            if (mContentInfoListener != null && clickTrackers != null) {
                mContentInfoListener.onIconClicked(clickTrackers);
            }
            try {
                Intent openLink = new Intent(Intent.ACTION_VIEW);
                openLink.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                openLink.setData(Uri.parse(iconClickURL.trim()));
                getContext().startActivity(openLink);
            } catch (Exception e) {
                Log.e(TAG, "error on click content info text", e);
            }
        } else {
            mContentInfoListener.onLinkClicked(iconClickURL);
        }
    }

    public void setContextText(String text) {
        if (text != null && !text.isEmpty()) {
            mContentInfoText.setText(text);
        }
    }

    public void setDpDimensions(ContentInfo contentInfo) {
        /*if (contentInfo.getPositionX() == PositionX.RIGHT) {
            mContainerView.removeView(mContentInfoIcon);
            mContainerView.addView(mContentInfoIcon);
        }*/

        LinearLayout.LayoutParams imageLayoutParams = (LinearLayout.LayoutParams) mContentInfoIcon.getLayoutParams();
        LinearLayout.LayoutParams textLayoutParams = (LinearLayout.LayoutParams) mContentInfoText.getLayoutParams();

        if (contentInfo.getWidth() != -1 && contentInfo.getHeight() != -1) {
            int width = contentInfo.getWidth();
            int height = contentInfo.getHeight();

            if (height > MAX_HEIGHT_DP || width > MAX_WIDTH_DP) {
                int aspectRatio = width / height;

                if (aspectRatio == 1) {
                    width = MAX_HEIGHT_DP;
                    height = MAX_HEIGHT_DP;
                } else if (width > height) {
                    if (width > MAX_WIDTH_DP) {
                        height = (int) (MAX_WIDTH_DP * ((float) height / (float) width));
                        width = MAX_WIDTH_DP;
                    }
                } else {
                    width = (int) (MAX_HEIGHT_DP * ((float) width / (float) height));
                    height = MAX_HEIGHT_DP;
                }
            }

            imageLayoutParams.width = ViewUtils.asIntPixels(width, getContext());
            imageLayoutParams.height = ViewUtils.asIntPixels(height, getContext());

            textLayoutParams.width = LayoutParams.WRAP_CONTENT;
            textLayoutParams.height = ViewUtils.asIntPixels(height, getContext());
        }

        mContentInfoIcon.setLayoutParams(imageLayoutParams);
        mContentInfoText.setLayoutParams(textLayoutParams);
        mContentInfoText.setGravity(Gravity.CENTER_VERTICAL);
    }

    public String getIconClickURL() {
        return iconClickURL;
    }
}