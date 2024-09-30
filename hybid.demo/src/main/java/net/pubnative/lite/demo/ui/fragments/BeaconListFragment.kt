package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.BeaconListAdapter
import net.pubnative.lite.demo.util.BeaconDescription
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdResponse
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.utils.json.JsonOperations
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BeaconListFragment : Fragment(R.layout.fragment_beacon_list) {
    val TAG = BeaconListFragment::class.java.simpleName

    private lateinit var itemList: RecyclerView
    private lateinit var copyButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = view.findViewById(R.id.list_beacon)
        copyButton = view.findViewById(R.id.button_json)

        val adapter = BeaconListAdapter(getBeaconList())

        copyButton.setOnClickListener {
            context?.let { it1 ->
                ClipboardUtils.copyToClipboard(
                    it1, convertToJson(getBeaconList())
                )
            }
        }

        itemList.itemAnimator = DefaultItemAnimator()
        itemList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        itemList.adapter = adapter
    }

    private fun convertToJson(beaconList: List<BeaconDescription>): String {
        val jsonArray = JSONArray()

        for (beacon in beaconList) {
            val jsonObject = JSONObject()
            JsonOperations.putJsonString(jsonObject, "beaconUrl", beacon.beaconUrl)
            JsonOperations.putJsonString(jsonObject, "description", beacon.description)
            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    private fun getBeaconList(): List<BeaconDescription> {
        val response = requireActivity().intent.getStringExtra("response")
        var json: JSONObject? = null
        var isJson = true

        try {
            json = response?.let { JSONObject(it) }
        } catch (e: JSONException) {
            Logger.e(TAG, "Error parsing json: ", e)
            isJson = false
        }

        val beaconList: MutableList<BeaconDescription> = mutableListOf()

        if (isJson) {
            var ad: Ad? = null
            try {
                ad = AdResponse(json).ads[0]
            } catch (e: Exception) {
                Logger.e(TAG, "Error parsing json: ", e)
            }

            if (ad != null) {
                if (ad.getBeacons(Ad.Beacon.IMPRESSION) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.IMPRESSION)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(BeaconDescription(url.url, "PN " + Ad.Beacon.IMPRESSION))
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(BeaconDescription(url.js, "PN " + Ad.Beacon.IMPRESSION))
                        }

                    }
                }
                if (ad.getBeacons(Ad.Beacon.CLICK) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.CLICK)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(BeaconDescription(url.url, "PN " + Ad.Beacon.CLICK))
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(BeaconDescription(url.js, "PN " + Ad.Beacon.CLICK))
                        }
                    }
                }
                if (ad.getBeacons(Ad.Beacon.CUSTOM_CTA_SHOW) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.CUSTOM_CTA_SHOW)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.url,
                                    "PN " + Ad.Beacon.CUSTOM_CTA_SHOW
                                )
                            )
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.js,
                                    "PN " + Ad.Beacon.CUSTOM_CTA_SHOW
                                )
                            )
                        }
                    }
                }
                if (ad.getBeacons(Ad.Beacon.CUSTOM_CTA_CLICK) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.CUSTOM_CTA_CLICK)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.url,
                                    "PN " + Ad.Beacon.CUSTOM_CTA_CLICK
                                )
                            )
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.js,
                                    "PN " + Ad.Beacon.CUSTOM_CTA_CLICK
                                )
                            )
                        }
                    }
                }
                if (ad.getBeacons(Ad.Beacon.CUSTOM_END_CARD_IMPRESSION) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.CUSTOM_END_CARD_IMPRESSION)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.url,
                                    "PN " + Ad.Beacon.CUSTOM_END_CARD_IMPRESSION
                                )
                            )
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.js,
                                    "PN " + Ad.Beacon.CUSTOM_END_CARD_IMPRESSION
                                )
                            )
                        }
                    }
                }
                if (ad.getBeacons(Ad.Beacon.CUSTOM_END_CARD_CLICK) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.CUSTOM_END_CARD_CLICK)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.url,
                                    "PN " + Ad.Beacon.CUSTOM_END_CARD_CLICK
                                )
                            )
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.js,
                                    "PN " + Ad.Beacon.CUSTOM_END_CARD_CLICK
                                )
                            )
                        }
                    }
                }
                if (ad.getBeacons(Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK) != null) {
                    for (url in ad.getBeacons(Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK)) {
                        if (!TextUtils.isEmpty(url.url)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.url,
                                    "PN " + Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK
                                )
                            )
                        } else if (!TextUtils.isEmpty(url.js)) {
                            beaconList.add(
                                BeaconDescription(
                                    url.js,
                                    "PN " + Ad.Beacon.CUSTOM_CTA_ENDCARD_CLICK
                                )
                            )
                        }
                    }
                }
            }
        }

        val videoAdCacheItem = HyBid.getVideoAdCache().inspectLatest()

        if (videoAdCacheItem != null) {
            for (impression in videoAdCacheItem.adParams.impressions) {
                beaconList.add(BeaconDescription(impression, "Impression"))
            }
            for (videoClick in videoAdCacheItem.adParams.videoClicks) {
                beaconList.add(BeaconDescription(videoClick, "Video Click"))
            }
            for (endcardClick in videoAdCacheItem.adParams.endCardClicks) {
                beaconList.add(BeaconDescription(endcardClick, "Endcard click"))
            }
            for (companionCreativeViewEvent in videoAdCacheItem.adParams.companionCreativeViewEvents) {
                beaconList.add(
                    BeaconDescription(
                        companionCreativeViewEvent.url,
                        "Companion Creative View Event: Impression"
                    )
                )
            }
            for (ctaExtensionClick in videoAdCacheItem.adParams.ctaExtensionClicks) {
                beaconList.add(BeaconDescription(ctaExtensionClick, "CTA Extension Click"))
            }
            for (event in videoAdCacheItem.adParams.events) {
                beaconList.add(BeaconDescription(event.text, event.event))
            }

            if (!videoAdCacheItem.adParams.videoRedirectUrl.isNullOrEmpty()) {
                beaconList.add(
                    BeaconDescription(
                        videoAdCacheItem.adParams.videoRedirectUrl,
                        "ClickThrough"
                    )
                )
            }

            if (!videoAdCacheItem.adParams.endCardRedirectUrl.isNullOrEmpty()) {
                beaconList.add(
                    BeaconDescription(
                        videoAdCacheItem.adParams.endCardRedirectUrl,
                        "CompanionClickThrough"
                    )
                )
            }
        }

        return beaconList
    }
}