// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.util

import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.sdk.models.RemoteConfig
import net.pubnative.lite.demo.models.RemoteConfigParam

object RemoteConfigParamUtilisation {

    fun convertAdCustomizationToRemoteConfigParam(adCustomizationsManager: AdCustomizationsManager): List<RemoteConfigParam> {
        val configs = ArrayList<RemoteConfigParam>()
        var param: RemoteConfigParam

        adCustomizationsManager.audioSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.AUDIO_STATE.fieldName
                param.value = adCustomizationsManager.audioSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.endCardSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.END_CARD_ENABLED.fieldName
                param.value = adCustomizationsManager.endCardSettings.value
                configs.add(param)
            }

            if (customEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CUSTOM_END_CARD_ENABLED.fieldName
                param.value = adCustomizationsManager.endCardSettings.customValue
                configs.add(param)
            }

            if (customDisplayEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CUSTOM_END_CARD_DISPLAY.fieldName
                param.value = adCustomizationsManager.endCardSettings.customDisplayValue
                configs.add(param)
            }
        }

        adCustomizationsManager.navigationSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.NAVIGATION_MODE.fieldName
                param.value = adCustomizationsManager.navigationSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.landingPageSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.LANDING_PAGE.fieldName
                param.value = adCustomizationsManager.landingPageSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.skipOffsetSettings?.apply {
            if (endCardCloseDelay?.first == true) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.END_CARD_CLOSE_DELAY.fieldName
                param.value = adCustomizationsManager.skipOffsetSettings.endCardCloseDelay?.second
                configs.add(param)
            }

            if (html?.first == true) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.HTML_SKIP_OFFSET.fieldName
                param.value = adCustomizationsManager.skipOffsetSettings.html?.second
                configs.add(param)
            }

            if (rewardedHtml?.first == true) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.REWARDED_HTML_SKIP_OFFSET.fieldName
                param.value = adCustomizationsManager.skipOffsetSettings.rewardedHtml?.second
                configs.add(param)
            }

            if (video?.first == true) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.VIDEO_SKIP_OFFSET.fieldName
                param.value = adCustomizationsManager.skipOffsetSettings.video?.second
                configs.add(param)
            }

            if (rewardedVideo?.first == true) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.REWARDED_VIDEO_SKIP_OFFSET.fieldName
                param.value = adCustomizationsManager.skipOffsetSettings.rewardedVideo?.second
                configs.add(param)
            }

            if (playable?.first == true) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.PLAYABLE_SKIP_OFFSET.fieldName
                param.value = adCustomizationsManager.skipOffsetSettings.playable?.second
                configs.add(param)
            }
        }

        adCustomizationsManager.autoCloseSettings?.apply {
            if (interstitialEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CLOSE_INTER_AFTER_FINISH.fieldName
                param.value = adCustomizationsManager.autoCloseSettings.interstitialValue
                configs.add(param)
            }

            if (rewardedEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CLOSE_REWARD_AFTER_FINISH.fieldName
                param.value = adCustomizationsManager.autoCloseSettings.rewardedValue
                configs.add(param)
            }
        }

        adCustomizationsManager.mraidSettings?.apply {
            if (expandEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.MRAID_EXPAND.fieldName
                param.value = adCustomizationsManager.mraidSettings.expandValue
                configs.add(param)
            }
        }

        adCustomizationsManager.clickBehaviourSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.FULL_SCREEN_CLICKABILITY.fieldName
                param.value = adCustomizationsManager.clickBehaviourSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.contentInfoSettings?.apply {
            if (urlEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CONTENT_INFO_URL.fieldName
                param.value = adCustomizationsManager.contentInfoSettings.urlValue
                configs.add(param)
            }

            if (iconUrlEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CONTENT_INFO_ICON_URL.fieldName
                param.value = adCustomizationsManager.contentInfoSettings.iconUrlValue
                configs.add(param)
            }

            if (iconClickActionEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CONTENT_INFO_ICON_CLICK_ACTION.fieldName
                param.value = adCustomizationsManager.contentInfoSettings.iconClickActionValue
                configs.add(param)
            }

            if (displayEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CONTENT_INFO_DISPLAY.fieldName
                param.value = adCustomizationsManager.contentInfoSettings.displayValue
                configs.add(param)
            }
        }

        adCustomizationsManager.closeButtonSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.NATIVE_CLOSE_BUTTON_DELAY.fieldName
                param.value = adCustomizationsManager.closeButtonSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.reducedButtonsSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.PC_REDUCED_ICON_SIZES.fieldName
                param.value = adCustomizationsManager.reducedButtonsSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.countdownSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = "count_down_style"
                param.value = adCustomizationsManager.countdownSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.learnMoreSettings?.apply {
            if (sizeEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.BC_LEARN_MORE_SIZE.fieldName
                param.value = adCustomizationsManager.learnMoreSettings.sizeValue
                configs.add(param)
            }

            if (locationEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.BC_LEARN_MORE_LOCATION.fieldName
                param.value = adCustomizationsManager.learnMoreSettings.locationValue
                configs.add(param)
            }
        }

        adCustomizationsManager.impressionTrackingSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.IMP_TRACKING_METHOD.fieldName
                param.value = adCustomizationsManager.impressionTrackingSettings.value
                configs.add(param)
            }
        }

        adCustomizationsManager.visibilitySettings?.apply {
            if (minTimeEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.IMP_TRACKING_VISIBLE_TIME.fieldName
                param.value = adCustomizationsManager.visibilitySettings.minTimeValue
                configs.add(param)
            }

            if (minPercentEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.IMP_TRACKING_VISIBLE_PERCENT.fieldName
                param.value = adCustomizationsManager.visibilitySettings.minPercentValue
                configs.add(param)
            }
        }

        adCustomizationsManager.customCtaSettings?.apply {
            if (enabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CUSTOM_CTA_ENABLED.fieldName
                param.value = adCustomizationsManager.customCtaSettings.enabledValue
                configs.add(param)

                param = RemoteConfigParam()
                param.name = RemoteConfig.CUSTOM_CTA_TYPE.fieldName
                param.value = adCustomizationsManager.customCtaSettings.typeValue
                configs.add(param)
            }

            if (delayEnabled) {
                param = RemoteConfigParam()
                param.name = RemoteConfig.CUSTOM_CTA_DELAY.fieldName
                param.value = adCustomizationsManager.customCtaSettings.delayEnabledValue
                configs.add(param)
            }
        }
        return configs
    }
}