package net.pubnative.lite.sdk;

public interface CustomEndCardListener {
    void onCustomEndCardShow();

    void onCustomEndCardClick();

    void onDefaultEndCardShow();

    void onDefaultEndCardClick();

    void onEndCardLoadSuccess(boolean isCustomEndCard);

    void onEndCardLoadFailure(boolean isCustomEndCard);
}
