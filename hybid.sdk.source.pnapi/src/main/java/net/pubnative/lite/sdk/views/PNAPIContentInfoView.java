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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.pubnative.lite.sdk.source.pnapi.R;
import net.pubnative.lite.sdk.utils.PNBitmapDownloader;

public class PNAPIContentInfoView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = PNAPIContentInfoView.class.getSimpleName();

    private TextView mContentInfoText;
    private ImageView mContentInfoIcon;

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

        LayoutInflater inflator = LayoutInflater.from(context);
        mHandler = new Handler(Looper.getMainLooper());
        RelativeLayout containerView = (RelativeLayout) inflator.inflate(R.layout.content_info_layout, this, true);
        mContentInfoIcon = containerView.findViewById(R.id.ic_context_icon);
        mContentInfoText = containerView.findViewById(R.id.tv_context_text);
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

            try {
                Intent openLink = new Intent(Intent.ACTION_VIEW);
                openLink.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                openLink.setData(Uri.parse(iconClickUrl));
                view.getContext().startActivity(openLink);
            } catch (Exception e) {
                Log.e(TAG, "error on click content info text", e);
            }
        });
    }

    public void setContextText(String text) {
        if (text != null && !text.isEmpty()) {
            mContentInfoText.setText(text);
        }
    }

    @Override
    public void onClick(View v) {
        openLayout();
    }
}