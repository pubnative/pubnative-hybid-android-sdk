// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface MRAIDViewListener {
    /******************************************************************************
     * A listener for basic MRAIDView banner ad functionality.
     ******************************************************************************/

    void mraidViewLoaded(MRAIDView mraidView);

    void mraidViewError(MRAIDView mraidView);

    void mraidViewExpand(MRAIDView mraidView);

    void mraidViewClose(MRAIDView mraidView);

    boolean mraidViewResize(MRAIDView mraidView, int width, int height, int offsetX, int offsetY);

    void mraidShowCloseButton();

    void onExpandedAdClosed();
}
