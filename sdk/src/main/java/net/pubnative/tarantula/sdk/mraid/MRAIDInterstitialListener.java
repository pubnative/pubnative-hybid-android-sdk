package net.pubnative.tarantula.sdk.mraid;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface MRAIDInterstitialListener {

    /******************************************************************************
     * A listener for basic MRAIDInterstitial ad functionality.
     ******************************************************************************/

    void mraidInterstitialLoaded(MRAIDInterstitial mraidInterstitial);

    void mraidInterstitialShow(MRAIDInterstitial mraidInterstitial);

    void mraidInterstitialHide(MRAIDInterstitial mraidInterstitial);
}
