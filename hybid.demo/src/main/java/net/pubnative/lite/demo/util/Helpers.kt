// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.util

import android.content.Context

fun convertDpToPx(context: Context, dp: Float) = (dp * context.resources.displayMetrics.density).toInt()

fun convertPxToDp(context: Context, px: Float) = (px / context.resources.displayMetrics.density).toInt()