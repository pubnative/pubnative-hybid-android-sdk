package net.pubnative.lite.demo.util

import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.sdk.models.RemoteConfig
import net.pubnative.lite.demo.models.RemoteConfigParam

object RemoteConfigParamUtilisation {

    fun convertAdCustomizationToRemoteConfigParam(adCustomizationsManager: AdCustomizationsManager): List<RemoteConfigParam> {
        val configs = ArrayList<RemoteConfigParam>()
        var param: RemoteConfigParam

        if (adCustomizationsManager.initial_audio_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.AUDIO_STATE.fieldName
            param.value = adCustomizationsManager.initial_audio_value
            configs.add(param)
        }

        if (adCustomizationsManager.end_card_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.END_CARD_ENABLED.fieldName
            param.value = adCustomizationsManager.end_card_value
            configs.add(param)
        }

        if (adCustomizationsManager.custom_end_card_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CUSTOM_END_CARD_ENABLED.fieldName
            param.value = adCustomizationsManager.custom_end_card_value
            configs.add(param)
        }

        if (adCustomizationsManager.custom_end_card_display_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CUSTOM_END_CARD_DISPLAY.fieldName
            param.value = adCustomizationsManager.custom_end_card_display_value
            configs.add(param)
        }

        if (adCustomizationsManager.end_card_close_delay_skip_offset_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.END_CARD_CLOSE_DELAY.fieldName
            param.value = adCustomizationsManager.end_card_close_delay_skip_offset_value
            configs.add(param)
        }

        if (adCustomizationsManager.html_skip_offset_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.HTML_SKIP_OFFSET.fieldName
            param.value = adCustomizationsManager.html_skip_offset_value
            configs.add(param)
        }

        if (adCustomizationsManager.rewarded_html_skip_offset_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.REWARDED_HTML_SKIP_OFFSET.fieldName
            param.value = adCustomizationsManager.rewarded_html_skip_offset_value
            configs.add(param)
        }

        if (adCustomizationsManager.rewarded_video_skip_offset_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.REWARDED_VIDEO_SKIP_OFFSET.fieldName
            param.value = adCustomizationsManager.rewarded_video_skip_offset_value
            configs.add(param)
        }

        if (adCustomizationsManager.video_skip_offset_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.VIDEO_SKIP_OFFSET.fieldName
            param.value = adCustomizationsManager.video_skip_offset_value
            configs.add(param)
        }

        if (adCustomizationsManager.auto_close_interstitial_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CLOSE_INTER_AFTER_FINISH.fieldName
            param.value = adCustomizationsManager.auto_close_interstitial_value
            configs.add(param)
        }

        if (adCustomizationsManager.auto_close_rewarded_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CLOSE_REWARD_AFTER_FINISH.fieldName
            param.value = adCustomizationsManager.auto_close_rewarded_value
            configs.add(param)
        }

        if (adCustomizationsManager.mraid_expand_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.MRAID_EXPAND.fieldName
            param.value = adCustomizationsManager.mraid_expand_value
            configs.add(param)
        }

        if (adCustomizationsManager.click_behaviour_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.FULL_SCREEN_CLICKABILITY.fieldName
            param.value = adCustomizationsManager.click_behaviour_value
            configs.add(param)
        }

        if (adCustomizationsManager.content_info_url_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CONTENT_INFO_URL.fieldName
            param.value = adCustomizationsManager.content_info_url_value
            configs.add(param)
        }

        if (adCustomizationsManager.content_info_icon_url_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CONTENT_INFO_ICON_URL.fieldName
            param.value = adCustomizationsManager.content_info_icon_url_value
            configs.add(param)
        }

        if (adCustomizationsManager.content_info_icon_click_action_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CONTENT_INFO_ICON_CLICK_ACTION.fieldName
            param.value = adCustomizationsManager.content_info_icon_click_action_value
            configs.add(param)
        }

        if (adCustomizationsManager.content_info_display_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.CONTENT_INFO_DISPLAY.fieldName
            param.value = adCustomizationsManager.content_info_display_value
            configs.add(param)
        }

        if (adCustomizationsManager.back_button_delay_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.BACK_BUTTON_DELAY.fieldName
            param.value = adCustomizationsManager.back_button_delay_value
            configs.add(param)
        }

        if (adCustomizationsManager.close_button_delay_enabled) {
            param = RemoteConfigParam()
            param.name = RemoteConfig.NATIVE_CLOSE_BUTTON_DELAY.fieldName
            param.value = adCustomizationsManager.close_button_delay_value
            configs.add(param)
        }

        if (adCustomizationsManager.count_down_enabled) {
            param = RemoteConfigParam()
            param.name = "count_down_style"
            param.value = adCustomizationsManager.count_down_value
            configs.add(param)
        }

        if (adCustomizationsManager.imp_tracking_enabled) {
            param = RemoteConfigParam()
            param.name = "imp_tracking"
            param.value = adCustomizationsManager.imp_tracking_value
            configs.add(param)
        }

        if (adCustomizationsManager.min_visibility_time_enabled) {
            param = RemoteConfigParam()
            param.name = "min_visible_time"
            param.value = adCustomizationsManager.min_visibility_time_value
            configs.add(param)
        }

        if (adCustomizationsManager.min_visibility_percent_enabled) {
            param = RemoteConfigParam()
            param.name = "min_visible_percent"
            param.value = adCustomizationsManager.min_visibility_percent_value
            configs.add(param)
        }

        return configs
    }
}