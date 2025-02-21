package net.pubnative.lite.demo.managers

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AdCustomizationsManager(
    var initial_audio_enabled: Boolean,
    var initial_audio_value: Int,
    var mraid_expand_enabled: Boolean,
    var mraid_expand_value: Boolean,
    var auto_close_interstitial_enabled: Boolean,
    var auto_close_interstitial_value: Boolean,
    var end_card_enabled: Boolean,
    var end_card_value: Boolean,
    var custom_end_card_enabled: Boolean,
    var custom_end_card_value: Boolean,
    var custom_end_card_display_enabled: Boolean,
    var custom_end_card_display_value: String,
    var navigation_mode_enabled: Boolean,
    var navigation_mode_value: String,
    var landing_page_enabled: Boolean,
    var landing_page_value: Boolean,
    var auto_close_rewarded_enabled: Boolean,
    var auto_close_rewarded_value: Boolean,
    var html_skip_offset_enabled: Boolean,
    var html_skip_offset_value: String,
    var video_skip_offset_enabled: Boolean,
    var video_skip_offset_value: String,
    var rewarded_html_skip_offset_enabled: Boolean,
    var rewarded_html_skip_offset_value: String,
    var rewarded_video_skip_offset_enabled: Boolean,
    var rewarded_video_skip_offset_value: String,
    var end_card_close_delay_skip_offset_enabled: Boolean,
    var end_card_close_delay_skip_offset_value: String,
    var click_behaviour_enabled: Boolean,
    var click_behaviour_value: Boolean,
    var content_info_url_enabled: Boolean,
    var content_info_url_value: String,
    var content_info_icon_url_enabled: Boolean,
    var content_info_icon_url_value: String,
    var content_info_icon_click_action_enabled: Boolean,
    var content_info_icon_click_action_value: String,
    var content_info_display_enabled: Boolean,
    var content_info_display_value: String,
    var close_button_delay_enabled: Boolean,
    var close_button_delay_value: String,
    var count_down_enabled: Boolean,
    var count_down_value: String,
    var imp_tracking_enabled: Boolean,
    var imp_tracking_value: String,
    var min_visibility_time_enabled: Boolean,
    var min_visibility_time_value: String,
    var min_visibility_percent_enabled: Boolean,
    var min_visibility_percent_value: String,
    var custom_cta_enabled: Boolean,
    var custom_cta_enabled_value: Boolean,
    var custom_cta_delay_enabled: Boolean,
    var custom_cta_delay_enabled_value: String,
    var custom_cta_type_value: Int,
    var reduced_buttons_value: Boolean,
    var reduced_buttons_enabled: Boolean
) {

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        fun fromJson(string: String?): AdCustomizationsManager? {
            if (string == null)
                return null
            return Json.decodeFromString<AdCustomizationsManager>(string)
        }
    }
}
