package net.pubnative.lite.sdk.vpaid.helpers;

import android.content.Context;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.enums.VastError;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import java.util.Locale;

public class AssetsLoader {

    public interface OnAssetsLoaded {
        void onAssetsLoaded(String videoFilePath, EndCardData endCardData, String endCardFilePath);

        void onError(PlayerInfo info);
    }

    private static final String LOG_TAG = AssetsLoader.class.getSimpleName();

    private OnAssetsLoaded mListener;
    private FileLoader mVideoLoader;
    private FileLoader mFileLoader;
    private AdParams mAdParams;
    private Context mContext;

    private int videoFileIndex;
    private int endCardFileIndex;
    private String mVideoFilePath;

    public void load(AdParams adParams, Context context, OnAssetsLoaded assetsLoadListener) {
        mContext = context;
        mAdParams = adParams;
        mListener = assetsLoadListener;

        videoFileIndex = 0;
        endCardFileIndex = 0;
        mVideoFilePath = null;

        if (adParams.isVpaid()) {
            loadEndCard();
        } else {
            loadVideoAndEndCard();
        }
    }

    private void loadVideoAndEndCard() {
        if (mAdParams.getVideoFileUrlsList() == null || mAdParams.getVideoFileUrlsList().isEmpty()) {
            mListener.onError(new PlayerInfo("No video file found"));
        } else {
            mVideoLoader = new FileLoader(mAdParams.getVideoFileUrlsList().get(videoFileIndex), mContext, new FileLoader.Callback() {
                @Override
                public void onFileLoaded(String filePath) {
                    Logger.d(LOG_TAG, "onFullVideoLoaded");
                    mVideoFilePath = filePath;
                    loadEndCard();
                }

                @Override
                public void onError(PlayerInfo info) {
                    Logger.e(LOG_TAG, "Load video fail:" + info.getMessage());
                    videoFileIndex++;
                    if (videoFileIndex < mAdParams.getVideoFileUrlsList().size()) {
                        loadVideoAndEndCard();
                    } else {
                        mListener.onError(info);
                    }
                }

                @Override
                public void onProgress(double progress) {
                    String percent = String.format(Locale.US, "Loaded: %.2f%%", progress * 100);
                    Logger.d(LOG_TAG, percent);
                }
            });
            mVideoLoader.start();
        }
    }

    private void loadEndCard() {
        if (mAdParams.getEndCardList() == null || mAdParams.getEndCardList().isEmpty()) {
            mListener.onAssetsLoaded(mVideoFilePath, null, null);
            return;
        }
        final EndCardData endCardData = mAdParams.getEndCardList().get(endCardFileIndex);
        if (endCardData != null && endCardData.getType() == EndCardData.Type.STATIC_RESOURCE) {
            mFileLoader = new FileLoader(endCardData.getContent(), mContext, new FileLoader.Callback() {
                @Override
                public void onFileLoaded(String filePath) {
                    mListener.onAssetsLoaded(mVideoFilePath, endCardData, filePath);
                }

                @Override
                public void onError(PlayerInfo info) {
                    ErrorLog.postError(mContext, VastError.COMPANION);
                    endCardFileIndex++;
                    if (endCardFileIndex < mAdParams.getEndCardList().size()) {
                        loadEndCard();
                    } else {
                        mListener.onAssetsLoaded(mVideoFilePath, null, null);
                    }
                }

                @Override
                public void onProgress(double progress) {
                    String percent = String.format(Locale.US, "Loaded: %.2f%%", progress * 100);
                    Logger.d(LOG_TAG, percent);
                }
            });
            mFileLoader.start();
        } else {
            mListener.onAssetsLoaded(mVideoFilePath, endCardData, null);
        }

    }

    public void breakLoading() {
        if (mVideoLoader != null) {
            mVideoLoader.stop();
        }
        if (mFileLoader != null) {
            mFileLoader.stop();
        }
    }
}
