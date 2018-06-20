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
package net.pubnative.lite.demo.models

import android.os.Parcel
import android.os.Parcelable
import net.pubnative.lite.demo.createParcel

/**
 * Created by erosgarciaponte on 30.01.18.
 */
data class SettingsModel(var appToken: String,
                         var zoneIds: List<String>,
                         var gender: String,
                         var age: String,
                         var keywords: List<String>,
                         var coppa: Boolean,
                         var testMode: Boolean,
                         var mopubBannerAdUnitId: String,
                         var mopubMediumAdUnitId: String,
                         var mopubInterstitialAdUnitId: String,
                         var mopubMediationBannerAdUnitId: String,
                         var mopubMediationMediumAdUnitId: String,
                         var mopubMediationInterstitialAdUnitId: String,
                         var dfpBannerAdUnitId: String,
                         var dfpMediumAdUnitId: String,
                         var dfpInterstitialAdUnitId: String) : Parcelable {
    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { SettingsModel(it) }
    }

    protected constructor(parcel: Parcel) : this(
            parcel.readString(),
            mutableListOf<String>().apply {
                parcel.readStringList(this)
            },
            parcel.readString(),
            parcel.readString(),
            mutableListOf<String>().apply {
                parcel.readStringList(this)
            },
            parcel.readInt() != 0,
            parcel.readInt() != 0,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(appToken)

        dest?.writeStringList(zoneIds)

        dest?.writeString(gender)
        dest?.writeString(age)

        dest?.writeStringList(keywords)

        val coppaByte: Int
        if (coppa) {
            coppaByte = 1
        } else {
            coppaByte = 0
        }
        dest?.writeInt(coppaByte)

        val testModeByte: Int
        if (testMode) {
            testModeByte = 1
        } else {
            testModeByte = 0
        }
        dest?.writeInt(testModeByte)

        dest?.writeString(mopubBannerAdUnitId)
        dest?.writeString(mopubMediumAdUnitId)
        dest?.writeString(mopubInterstitialAdUnitId)

        dest?.writeString(mopubMediationBannerAdUnitId)
        dest?.writeString(mopubMediationMediumAdUnitId)
        dest?.writeString(mopubMediationInterstitialAdUnitId)

        dest?.writeString(dfpBannerAdUnitId)
        dest?.writeString(dfpMediumAdUnitId)
        dest?.writeString(dfpInterstitialAdUnitId)
    }

    override fun describeContents(): Int = 0
}