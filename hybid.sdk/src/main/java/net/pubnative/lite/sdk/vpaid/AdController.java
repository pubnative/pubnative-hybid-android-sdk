package net.pubnative.lite.sdk.vpaid;

public interface AdController {

    interface OnPreparedListener {
        void onPrepared();
    }

    void prepare(OnPreparedListener listener);

    void setVideoFilePath(String filePath);

    void setEndCardFilePath(String filePath);

    void buildVideoAdView(VideoBannerView bannerView);

    void openUrl(String url);

    void setVolume(boolean mute);

    void skipVideo();

    void closeSelf();

    void playAd();

    void pause();

    void resume();

    void dismiss();

    void destroy();

}
