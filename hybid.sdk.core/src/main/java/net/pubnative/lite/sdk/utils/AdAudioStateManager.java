// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.vpaid.enums.AudioState;

public class AdAudioStateManager {

    public static AudioState getAudioState(Ad ad, boolean isFullscreen){

        AudioState audioState;

        if(isFullscreen){
            if(ad != null && ad.getAudioState() != null && AudioState.fromString(ad.getAudioState()) != null){
                audioState = AudioState.fromString(ad.getAudioState());
            } else {
                audioState = HyBid.getVideoAudioStatus();
            }
        } else {
            audioState = AudioState.MUTED;
        }

        return audioState;
    }
}
