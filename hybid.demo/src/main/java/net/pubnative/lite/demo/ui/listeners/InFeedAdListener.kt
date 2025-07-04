// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.listeners

interface InFeedAdListener {
    fun onInFeedAdLoaded()

    fun onInFeedAdLoadError(error: Throwable?)

    fun onInFeedAdCreativeId(creativeId: String?)
}