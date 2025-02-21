package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.AdCustomizationPrefs
import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.CountdownStyle
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.ContentInfoDisplay
import net.pubnative.lite.sdk.models.ContentInfoIconAction
import net.pubnative.lite.sdk.models.CustomEndCardDisplay
import net.pubnative.lite.sdk.utils.URLValidator
import net.pubnative.lite.sdk.vpaid.enums.AudioState

class AdCustomizationFragment : Fragment(R.layout.fragment_ad_customization) {

    private var isMaximumIntegerValueMessageDisplayed: Boolean = false
    private var isWrongUrlUsed: Boolean = false

    private lateinit var initialAudioGroup: RadioGroup

    private lateinit var htmlSkipOffsetInput: EditText
    private lateinit var videoSkipOffsetInput: EditText
    private lateinit var rewardedHtmlSkipOffsetInput: EditText
    private lateinit var rewardedVideoSkipOffsetInput: EditText
    private lateinit var endCardCloseButtonDelayInput: EditText
    private lateinit var navigationModeInput: EditText
    private lateinit var contentInfoUrlInput: EditText
    private lateinit var contentInfoIconUrlInput: EditText
    private lateinit var contentInfoIconClickActionGroup: RadioGroup
    private lateinit var contentInfoDisplayGroup: RadioGroup
    private lateinit var closeButtonDelayInput: EditText
    private lateinit var clickBehaviourGroup: RadioGroup
    private lateinit var settingManager: SettingsManager
    private lateinit var countdownStyleGroup: RadioGroup
    private lateinit var customEndCardGroup: RadioGroup
    private lateinit var impTracking: EditText
    private lateinit var minVisibilityTime: EditText
    private lateinit var minVisibilityPercent: EditText
    private lateinit var customEndCardHTML: EditText
    private lateinit var customCTAAppName: EditText
    private lateinit var customCTAIconURL: EditText
    private lateinit var customCTADelay: EditText
    private lateinit var customCTATypeGroup: RadioGroup
    private lateinit var bundleId: EditText

    private lateinit var cbInitialAudio: CheckBox
    private lateinit var cbMraidExpand: CheckBox
    private lateinit var cbAutoClose: CheckBox
    private lateinit var cbEnableEndcard: CheckBox
    private lateinit var cbEnableCustomEndcard: CheckBox
    private lateinit var cbEnableCustomEndcardDisplay: CheckBox
    private lateinit var cbAutoCloseRewarded: CheckBox
    private lateinit var cbInputSkipOffset: CheckBox
    private lateinit var cbInputVideoSkipOffset: CheckBox
    private lateinit var cbInputRewardedSkipOffset: CheckBox
    private lateinit var cbInputRewardedVideoSkipOffset: CheckBox
    private lateinit var cbInputEndcardCloseButtonDelay: CheckBox
    private lateinit var cbInputNavigationMode: CheckBox
    private lateinit var cbLandingPage: CheckBox
    private lateinit var cbGroupClickBehaviour: CheckBox
    private lateinit var cbContentInfoUrl: CheckBox
    private lateinit var cbContentInfoIconUrl: CheckBox
    private lateinit var cbContentInfoIconClickAction: CheckBox
    private lateinit var cbContentInfoDisplay: CheckBox
    private lateinit var cbInputCloseButtonDelay: CheckBox
    private lateinit var cbCountdownStyle: CheckBox
    private lateinit var cbImpTracking: CheckBox
    private lateinit var cbMinVisibilityTime: CheckBox
    private lateinit var cbMinVisibilityPercent: CheckBox
    private lateinit var cbCustomCTAEnabled: CheckBox
    private lateinit var cbInputCustomCTADelay: CheckBox
    private lateinit var cbEnableReducedButtons: CheckBox

    private lateinit var mraidExpandSwitch: SwitchCompat
    private lateinit var enableAutoCloseSwitch: SwitchCompat
    private lateinit var enableAutoCloseSwitchRewarded: SwitchCompat
    private lateinit var enableEndcardSwitch: SwitchCompat
    private lateinit var enableCustomEndcardSwitch: SwitchCompat
    private lateinit var enableCustomCTASwitch: SwitchCompat
    private lateinit var enableReducedButtonsSwitch: SwitchCompat
    private lateinit var landingPageSwitch: SwitchCompat

    private lateinit var adCustomizationsManager: AdCustomizationsManager
    private lateinit var prefs: AdCustomizationPrefs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        prefs = AdCustomizationPrefs(requireContext())
        settingManager = SettingsManager.getInstance(requireContext())
        view.findViewById<Button>(R.id.button_save_settings).setOnClickListener {
            saveData()
        }
        fillSavedValues()
    }

    private fun initViews() {
        initialAudioGroup = requireView().findViewById(R.id.group_initial_audio)

        mraidExpandSwitch = requireView().findViewById(R.id.check_mraid_expand)
        enableAutoCloseSwitch = requireView().findViewById(R.id.check_auto_close)
        enableAutoCloseSwitchRewarded = requireView().findViewById(R.id.check_auto_close_rewarded)
        enableEndcardSwitch = requireView().findViewById(R.id.check_enable_endcard)
        enableCustomEndcardSwitch = requireView().findViewById(R.id.check_enable_custom_endcard)
        enableCustomCTASwitch = requireView().findViewById(R.id.check_custom_cta_enabled)
        enableReducedButtonsSwitch =
            requireView().findViewById(R.id.check_reduced_close_skip_buttons)
        landingPageSwitch = requireView().findViewById(R.id.check_landing_page)

        htmlSkipOffsetInput = requireView().findViewById(R.id.input_skip_offset)
        videoSkipOffsetInput = requireView().findViewById(R.id.input_video_skip_offset)
        rewardedHtmlSkipOffsetInput = requireView().findViewById(R.id.input_rewarded_skip_offset)
        rewardedVideoSkipOffsetInput =
            requireView().findViewById(R.id.input_rewarded_video_skip_offset)
        endCardCloseButtonDelayInput =
            requireView().findViewById(R.id.input_endcard_close_button_delay)
        navigationModeInput = requireView().findViewById(R.id.input_navigation_mode)
        clickBehaviourGroup = requireView().findViewById(R.id.group_click_behaviour)
        countdownStyleGroup = requireView().findViewById(R.id.countdown_style)
        impTracking = requireView().findViewById(R.id.input_imp_tracking)
        minVisibilityTime = requireView().findViewById(R.id.input_min_visible_time)
        minVisibilityPercent = requireView().findViewById(R.id.input_min_visible_percent)
        customEndCardGroup = requireView().findViewById(R.id.group_custom_end_card)
        closeButtonDelayInput = requireView().findViewById(R.id.input_close_button_delay)
        contentInfoUrlInput = requireView().findViewById(R.id.input_content_info_url)
        contentInfoIconUrlInput = requireView().findViewById(R.id.input_content_info_icon_url)
        customEndCardHTML = requireView().findViewById(R.id.input_custom_end_card_html)
        customCTAAppName = requireView().findViewById(R.id.input_custom_cta_app_name)
        customCTAIconURL = requireView().findViewById(R.id.input_custom_cta_icon)
        customCTADelay = requireView().findViewById(R.id.input_custom_cta_delay)
        customCTATypeGroup = requireView().findViewById(R.id.group_custom_cta_type)
        bundleId = requireView().findViewById(R.id.input_bundle_id)

        contentInfoIconClickActionGroup =
            requireView().findViewById(R.id.group_content_info_icon_click_action)
        contentInfoDisplayGroup = requireView().findViewById(R.id.group_content_info_display)

        cbInitialAudio = requireView().findViewById(R.id.cb_initial_audio)
        cbMraidExpand = requireView().findViewById(R.id.cb_mraid_expand)
        cbAutoClose = requireView().findViewById(R.id.cb_auto_close)
        cbEnableEndcard = requireView().findViewById(R.id.cb_enable_endcard)
        cbEnableCustomEndcard = requireView().findViewById(R.id.cb_enable_custom_endcard)
        cbEnableCustomEndcardDisplay =
            requireView().findViewById(R.id.cb_enable_custom_endcard_display)
        cbAutoCloseRewarded = requireView().findViewById(R.id.cb_auto_close_rewarded)
        cbInputSkipOffset = requireView().findViewById(R.id.cb_input_skip_offset)
        cbInputVideoSkipOffset = requireView().findViewById(R.id.cb_input_video_skip_offset)
        cbInputRewardedSkipOffset = requireView().findViewById(R.id.cb_input_rewarded_skip_offset)
        cbInputRewardedVideoSkipOffset =
            requireView().findViewById(R.id.cb_input_rewarded_video_skip_offset)
        cbInputEndcardCloseButtonDelay =
            requireView().findViewById(R.id.cb_input_endcard_close_button_delay)
        cbInputNavigationMode = requireView().findViewById(R.id.cb_input_navigation_mode)
        cbLandingPage = requireView().findViewById(R.id.cb_landing_page)
        cbGroupClickBehaviour = requireView().findViewById(R.id.cb_group_click_behaviour)
        cbInputCloseButtonDelay = requireView().findViewById(R.id.cb_input_close_button_delay)
        cbContentInfoUrl = requireView().findViewById(R.id.cb_input_content_info_url)
        cbContentInfoIconUrl = requireView().findViewById(R.id.cb_input_content_info_icon_url)
        cbContentInfoIconClickAction =
            requireView().findViewById(R.id.cb_content_info_icon_click_action)
        cbContentInfoDisplay = requireView().findViewById(R.id.cb_content_info_display)
        cbCountdownStyle = requireView().findViewById(R.id.cb_countdown_style)
        cbImpTracking = requireView().findViewById(R.id.cb_input_imp_tracking)
        cbMinVisibilityTime = requireView().findViewById(R.id.cb_input_min_visible_time)
        cbMinVisibilityPercent = requireView().findViewById(R.id.cb_input_min_visible_percent)
        cbCustomCTAEnabled = requireView().findViewById(R.id.cb_custom_cta_enabled)
        cbInputCustomCTADelay = requireView().findViewById(R.id.cb_input_custom_cta_delay)
        cbEnableReducedButtons = requireView().findViewById(R.id.cb_reduced_skip_close_buttons)

        cbInitialAudio.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_sound_default).isEnabled = checked
            requireView().findViewById<RadioButton>(R.id.radio_sound_on).isEnabled = checked
            requireView().findViewById<RadioButton>(R.id.radio_sound_mute).isEnabled = checked
        }

        cbMraidExpand.setOnCheckedChangeListener { p0, checked ->
            mraidExpandSwitch.isEnabled = checked
        }

        cbAutoClose.setOnCheckedChangeListener { p0, checked ->
            enableAutoCloseSwitch.isEnabled = checked
        }

        cbAutoCloseRewarded.setOnCheckedChangeListener { p0, checked ->
            enableAutoCloseSwitchRewarded.isEnabled = checked
        }

        cbCustomCTAEnabled.setOnCheckedChangeListener { p0, checked ->
            enableCustomCTASwitch.isEnabled = checked
        }

        cbInputCustomCTADelay.setOnCheckedChangeListener { p0, checked ->
            customCTADelay.isEnabled = checked
        }

        cbEnableEndcard.setOnCheckedChangeListener { p0, checked ->
            enableEndcardSwitch.isEnabled = checked
        }

        cbEnableCustomEndcard.setOnCheckedChangeListener { p0, checked ->
            enableCustomEndcardSwitch.isEnabled = checked
        }

        cbEnableReducedButtons.setOnCheckedChangeListener { p0, checked ->
            enableReducedButtonsSwitch.isEnabled = checked
        }

        cbEnableCustomEndcardDisplay.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_fallback).isEnabled = checked
            requireView().findViewById<RadioButton>(R.id.radio_extension).isEnabled = checked
        }

        cbInputSkipOffset.setOnCheckedChangeListener { p0, checked ->
            htmlSkipOffsetInput.isEnabled = checked
        }

        cbInputVideoSkipOffset.setOnCheckedChangeListener { p0, checked ->
            videoSkipOffsetInput.isEnabled = checked
        }

        cbInputRewardedSkipOffset.setOnCheckedChangeListener { p0, checked ->
            rewardedHtmlSkipOffsetInput.isEnabled = checked
        }

        cbInputRewardedVideoSkipOffset.setOnCheckedChangeListener { p0, checked ->
            rewardedVideoSkipOffsetInput.isEnabled = checked
        }

        cbInputEndcardCloseButtonDelay.setOnCheckedChangeListener { p0, checked ->
            endCardCloseButtonDelayInput.isEnabled = checked
        }

        cbInputNavigationMode.setOnCheckedChangeListener { p0, checked ->
            navigationModeInput.isEnabled = checked
        }

        cbLandingPage.setOnCheckedChangeListener { p0, checked ->
            landingPageSwitch.isEnabled = checked
        }

        cbImpTracking.setOnCheckedChangeListener { p0, checked ->
            impTracking.isEnabled = checked
        }

        cbMinVisibilityTime.setOnCheckedChangeListener { p0, checked ->
            minVisibilityTime.isEnabled = checked
        }

        cbMinVisibilityPercent.setOnCheckedChangeListener { p0, checked ->
            minVisibilityPercent.isEnabled = checked
        }

        cbGroupClickBehaviour.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_creative).isEnabled = checked
            requireView().findViewById<RadioButton>(R.id.radio_action_button).isEnabled = checked
        }

        cbInputCloseButtonDelay.setOnCheckedChangeListener { p0, checked ->
            closeButtonDelayInput.isEnabled = checked
        }

        cbContentInfoUrl.setOnCheckedChangeListener { p0, checked ->
            contentInfoUrlInput.isEnabled = checked
        }

        cbContentInfoIconUrl.setOnCheckedChangeListener { p0, checked ->
            contentInfoIconUrlInput.isEnabled = checked
        }

        cbContentInfoIconClickAction.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_expand)
                .isEnabled = checked
            requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_open)
                .isEnabled = checked
        }

        cbContentInfoDisplay.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_content_info_display_inapp)
                .isEnabled = checked
            requireView().findViewById<RadioButton>(R.id.radio_content_info_display_systembrowser)
                .isEnabled = checked
        }

        cbCountdownStyle.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_countdown_style_pie_chart).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_countdown_style_timer).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_countdown_style_progress).isEnabled =
                checked
        }
    }

    private fun fillSavedValues() {
        AdCustomizationsManager.fromJson(prefs.getAdCustomizationData())?.let {
            val obj = it
            fillAdCustomizationData(obj)
        }
    }

    private fun fillAdCustomizationData(adCustomizationsManager: AdCustomizationsManager) {
        //Initial Audio
        cbInitialAudio.isChecked = adCustomizationsManager.initial_audio_enabled
        val selectedInitialAudio = when (adCustomizationsManager.initial_audio_value) {
            0 -> R.id.radio_sound_default
            1 -> R.id.radio_sound_on
            2 -> R.id.radio_sound_mute
            else -> {
                R.id.radio_sound_default
            }
        }
        initialAudioGroup.check(selectedInitialAudio)

        requireView().findViewById<RadioButton>(R.id.radio_sound_default).isEnabled =
            adCustomizationsManager.initial_audio_enabled
        requireView().findViewById<RadioButton>(R.id.radio_sound_on).isEnabled =
            adCustomizationsManager.initial_audio_enabled
        requireView().findViewById<RadioButton>(R.id.radio_sound_mute).isEnabled =
            adCustomizationsManager.initial_audio_enabled

        //Mraid Expand
        cbMraidExpand.isChecked = adCustomizationsManager.mraid_expand_enabled
        mraidExpandSwitch.isEnabled = adCustomizationsManager.mraid_expand_enabled
        mraidExpandSwitch.isChecked = adCustomizationsManager.mraid_expand_value
        //Auto Close Interstitial
        cbAutoClose.isChecked = adCustomizationsManager.auto_close_interstitial_enabled
        enableAutoCloseSwitch.isEnabled = adCustomizationsManager.auto_close_interstitial_enabled
        enableAutoCloseSwitch.isChecked = adCustomizationsManager.auto_close_interstitial_value
        //End Card
        cbEnableEndcard.isChecked = adCustomizationsManager.end_card_enabled
        enableEndcardSwitch.isEnabled = adCustomizationsManager.end_card_enabled
        enableEndcardSwitch.isChecked = adCustomizationsManager.end_card_value
        //Custom End Card
        cbEnableCustomEndcard.isChecked = adCustomizationsManager.custom_end_card_enabled
        enableCustomEndcardSwitch.isEnabled = adCustomizationsManager.custom_end_card_enabled
        enableCustomEndcardSwitch.isChecked = adCustomizationsManager.custom_end_card_value
        //Custom End Card Display
        cbEnableCustomEndcardDisplay.isChecked =
            adCustomizationsManager.custom_end_card_display_enabled
        requireView().findViewById<RadioButton>(R.id.radio_fallback).isEnabled =
            adCustomizationsManager.custom_end_card_display_enabled
        requireView().findViewById<RadioButton>(R.id.radio_extension).isEnabled =
            adCustomizationsManager.custom_end_card_display_enabled
        when (adCustomizationsManager.custom_end_card_display_value) {
            CustomEndCardDisplay.FALLBACK.display -> {
                customEndCardGroup.check(R.id.radio_fallback)
            }

            CustomEndCardDisplay.EXTENSION.display -> {
                customEndCardGroup.check(R.id.radio_extension)
            }

            else -> {
                customEndCardGroup.check(R.id.radio_fallback)
            }
        }
        //Auto Close Rewarded
        cbAutoCloseRewarded.isChecked = adCustomizationsManager.auto_close_rewarded_enabled
        enableAutoCloseSwitchRewarded.isEnabled =
            adCustomizationsManager.auto_close_rewarded_enabled
        enableAutoCloseSwitchRewarded.isChecked = adCustomizationsManager.auto_close_rewarded_value
        //Skip Offset
        cbInputSkipOffset.isChecked = adCustomizationsManager.html_skip_offset_enabled
        htmlSkipOffsetInput.isEnabled = adCustomizationsManager.html_skip_offset_enabled
        htmlSkipOffsetInput.setText(adCustomizationsManager.html_skip_offset_value)
        //Video Skip Offset
        cbInputVideoSkipOffset.isChecked = adCustomizationsManager.video_skip_offset_enabled
        videoSkipOffsetInput.isEnabled = adCustomizationsManager.video_skip_offset_enabled
        videoSkipOffsetInput.setText(adCustomizationsManager.video_skip_offset_value)
        // Rewarded Skip Offset
        cbInputRewardedSkipOffset.isChecked =
            adCustomizationsManager.rewarded_html_skip_offset_enabled
        rewardedHtmlSkipOffsetInput.setText(adCustomizationsManager.rewarded_html_skip_offset_value)
        rewardedHtmlSkipOffsetInput.isEnabled =
            adCustomizationsManager.rewarded_html_skip_offset_enabled
        //Rewarded Video Skip Offset
        cbInputRewardedVideoSkipOffset.isChecked =
            adCustomizationsManager.rewarded_video_skip_offset_enabled
        rewardedVideoSkipOffsetInput.setText(adCustomizationsManager.rewarded_video_skip_offset_value)
        rewardedVideoSkipOffsetInput.isEnabled =
            adCustomizationsManager.rewarded_video_skip_offset_enabled
        //End Card Close Button Delay
        cbInputEndcardCloseButtonDelay.isChecked =
            adCustomizationsManager.end_card_close_delay_skip_offset_enabled
        endCardCloseButtonDelayInput.setText(adCustomizationsManager.end_card_close_delay_skip_offset_value)
        endCardCloseButtonDelayInput.isEnabled =
            adCustomizationsManager.end_card_close_delay_skip_offset_enabled
        // Navigation mode
        navigationModeInput.setText(adCustomizationsManager.navigation_mode_value)
        navigationModeInput.isEnabled = adCustomizationsManager.navigation_mode_enabled
        cbInputNavigationMode.isChecked = adCustomizationsManager.navigation_mode_enabled
        // Landing page
        cbLandingPage.isChecked = adCustomizationsManager.landing_page_enabled
        landingPageSwitch.isEnabled = adCustomizationsManager.landing_page_enabled
        landingPageSwitch.isChecked = adCustomizationsManager.landing_page_value
        //Click Behaviour
        cbGroupClickBehaviour.isChecked = adCustomizationsManager.click_behaviour_enabled
        cbImpTracking.isChecked = adCustomizationsManager.imp_tracking_enabled
        impTracking.setText(adCustomizationsManager.imp_tracking_value)
        impTracking.isEnabled = adCustomizationsManager.imp_tracking_enabled
        cbMinVisibilityTime.isChecked = adCustomizationsManager.min_visibility_time_enabled
        minVisibilityTime.setText(adCustomizationsManager.min_visibility_time_value)
        minVisibilityTime.isEnabled = adCustomizationsManager.min_visibility_time_enabled
        cbMinVisibilityPercent.isChecked = adCustomizationsManager.min_visibility_percent_enabled
        minVisibilityPercent.setText(adCustomizationsManager.min_visibility_percent_value)
        minVisibilityPercent.isEnabled = adCustomizationsManager.min_visibility_percent_enabled
        requireView().findViewById<RadioButton>(R.id.radio_creative).isEnabled =
            adCustomizationsManager.click_behaviour_enabled
        requireView().findViewById<RadioButton>(R.id.radio_action_button).isEnabled =
            adCustomizationsManager.click_behaviour_enabled
        // Reduced skip/close button size
        cbEnableReducedButtons.isChecked = adCustomizationsManager.reduced_buttons_enabled
        enableReducedButtonsSwitch.isEnabled = adCustomizationsManager.reduced_buttons_enabled
        enableReducedButtonsSwitch.isChecked = adCustomizationsManager.reduced_buttons_value


        when (adCustomizationsManager.click_behaviour_value) {
            true -> clickBehaviourGroup.check(R.id.radio_creative)
            false -> clickBehaviourGroup.check(R.id.radio_action_button)
        }

        //Content Info URL
        contentInfoUrlInput.setText(adCustomizationsManager.content_info_url_value)
        contentInfoUrlInput.isEnabled = adCustomizationsManager.content_info_url_enabled
        cbContentInfoUrl.isChecked = adCustomizationsManager.content_info_url_enabled

        //Content Info Icon URL
        contentInfoIconUrlInput.setText(adCustomizationsManager.content_info_icon_url_value)
        contentInfoIconUrlInput.isEnabled = adCustomizationsManager.content_info_icon_url_enabled
        cbContentInfoIconUrl.isChecked = adCustomizationsManager.content_info_icon_url_enabled

        //Content Info Icon Click Action
        cbContentInfoIconClickAction.isChecked =
            adCustomizationsManager.content_info_icon_click_action_enabled
        requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_open).isEnabled =
            adCustomizationsManager.content_info_icon_click_action_enabled
        requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_expand).isEnabled =
            adCustomizationsManager.content_info_icon_click_action_enabled

        when (adCustomizationsManager.content_info_icon_click_action_value) {
            ContentInfoIconAction.EXPAND.action -> contentInfoIconClickActionGroup.check(R.id.radio_content_info_icon_click_action_expand)
            ContentInfoIconAction.OPEN.action -> contentInfoIconClickActionGroup.check(R.id.radio_content_info_icon_click_action_open)
            else -> contentInfoIconClickActionGroup.check(R.id.radio_content_info_icon_click_action_expand)
        }

        //Content Info Display
        cbContentInfoDisplay.isChecked = adCustomizationsManager.content_info_display_enabled
        requireView().findViewById<RadioButton>(R.id.radio_content_info_display_inapp).isEnabled =
            adCustomizationsManager.content_info_display_enabled
        requireView().findViewById<RadioButton>(R.id.radio_content_info_display_systembrowser).isEnabled =
            adCustomizationsManager.content_info_display_enabled

        when (adCustomizationsManager.content_info_display_value) {
            ContentInfoDisplay.IN_APP.display -> contentInfoDisplayGroup.check(R.id.radio_content_info_display_inapp)
            ContentInfoDisplay.SYSTEM_BROWSER.display -> contentInfoDisplayGroup.check(R.id.radio_content_info_display_systembrowser)
            else -> contentInfoDisplayGroup.check(R.id.radio_content_info_display_systembrowser)
        }

        cbInputCloseButtonDelay.isChecked = adCustomizationsManager.close_button_delay_enabled
        closeButtonDelayInput.isEnabled = adCustomizationsManager.close_button_delay_enabled
        closeButtonDelayInput.setText(adCustomizationsManager.close_button_delay_value)

        //Count Down Style
        cbCountdownStyle.isChecked = adCustomizationsManager.count_down_enabled
        requireView().findViewById<RadioButton>(R.id.radio_countdown_style_pie_chart).isEnabled =
            adCustomizationsManager.count_down_enabled
        requireView().findViewById<RadioButton>(R.id.radio_countdown_style_timer).isEnabled =
            adCustomizationsManager.count_down_enabled
        requireView().findViewById<RadioButton>(R.id.radio_countdown_style_progress).isEnabled =
            adCustomizationsManager.count_down_enabled
        when (adCustomizationsManager.count_down_value) {
            CountdownStyle.PIE_CHART.name -> countdownStyleGroup.check(R.id.radio_countdown_style_pie_chart)
            CountdownStyle.TIMER.name -> countdownStyleGroup.check(R.id.radio_countdown_style_timer)
            CountdownStyle.PROGRESS.name -> countdownStyleGroup.check(R.id.radio_countdown_style_progress)
            else -> countdownStyleGroup.check(R.id.radio_countdown_style_timer)
        }

        customEndCardHTML.setText(prefs.getCustomEndCardHTML())

        bundleId.setText(prefs.getBundleId())

        //Custom CTA
        cbCustomCTAEnabled.isChecked = adCustomizationsManager.custom_cta_enabled
        enableCustomCTASwitch.isEnabled = adCustomizationsManager.custom_cta_enabled
        enableCustomCTASwitch.isChecked = adCustomizationsManager.custom_cta_enabled_value
        cbInputCustomCTADelay.isChecked = adCustomizationsManager.custom_cta_delay_enabled
        customCTADelay.isEnabled = adCustomizationsManager.custom_cta_delay_enabled
        customCTADelay.setText(adCustomizationsManager.custom_cta_delay_enabled_value)
        customCTAIconURL.setText(prefs.getCustomCTAIconURL())
        customCTAAppName.setText(prefs.getCustomCTAAppName())

        val selectedCustomCTAType = when (adCustomizationsManager.custom_cta_type_value) {
            0 -> R.id.radio_default
            1 -> R.id.radio_extended
            else -> {
                R.id.radio_default
            }
        }
        customCTATypeGroup.check(selectedCustomCTAType)
    }

    private fun isValidCustomisation(): Boolean {
        try {
            if (cbInputSkipOffset.isChecked && htmlSkipOffsetInput.text.toString().trim()
                    .isEmpty()
            ) {
                return false
            } else {
                if (cbInputSkipOffset.isChecked) {
                    val offset = htmlSkipOffsetInput.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbInputVideoSkipOffset.isChecked && videoSkipOffsetInput.text.toString().trim()
                    .isEmpty()
            ) {
                return false
            } else {
                if (cbInputVideoSkipOffset.isChecked) {
                    val offset = videoSkipOffsetInput.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbInputEndcardCloseButtonDelay.isChecked && endCardCloseButtonDelayInput.text.toString()
                    .trim().isEmpty()
            ) {
                return false
            } else {
                if (cbInputEndcardCloseButtonDelay.isChecked) {
                    val offset = endCardCloseButtonDelayInput.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbInputNavigationMode.isChecked &&
                navigationModeInput.text.toString().trim().isEmpty()
            ) {
                return false
            }

            if (cbInputCustomCTADelay.isChecked && customCTADelay.text.toString().trim()
                    .isEmpty()
            ) {
                return false
            } else {
                if (cbInputCustomCTADelay.isChecked) {
                    val offset = customCTADelay.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbInputRewardedSkipOffset.isChecked && rewardedHtmlSkipOffsetInput.text.toString()
                    .trim().isEmpty()
            ) {
                return false
            } else {
                if (cbInputRewardedSkipOffset.isChecked) {
                    val offset = rewardedHtmlSkipOffsetInput.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbInputRewardedVideoSkipOffset.isChecked && rewardedVideoSkipOffsetInput.text.toString()
                    .trim().isEmpty()
            ) {
                return false
            } else {
                if (cbInputRewardedVideoSkipOffset.isChecked) {
                    val offset = rewardedVideoSkipOffsetInput.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbInputCloseButtonDelay.isChecked && closeButtonDelayInput.text.toString()
                    .trim().isEmpty()
            ) {
                return false
            } else {
                if (cbInputCloseButtonDelay.isChecked) {
                    val offset = closeButtonDelayInput.text.toString().trim().toDouble()
                    if (offset > Int.MAX_VALUE) {
                        isMaximumIntegerValueMessageDisplayed = true
                        return false
                    }
                }
            }

            if (cbContentInfoUrl.isChecked &&
                !URLValidator.isValidURL(contentInfoUrlInput.text.toString().trim())
            ) {
                isWrongUrlUsed = true
                return false
            }

            if (cbContentInfoIconUrl.isChecked &&
                !URLValidator.isValidURL(contentInfoIconUrlInput.text.toString().trim())
            ) {
                isWrongUrlUsed = true
                return false
            }

            return true
        } catch (ex: Exception) {
            return false
        }
    }

    private fun saveData() {

        isMaximumIntegerValueMessageDisplayed = false

        if (isValidCustomisation()) {
            val initialAudioState = when (initialAudioGroup.checkedRadioButtonId) {
                R.id.radio_sound_default -> AudioState.DEFAULT
                R.id.radio_sound_on -> AudioState.ON
                R.id.radio_sound_mute -> AudioState.MUTED
                else -> AudioState.ON
            }

            val clickBehaviour = when (clickBehaviourGroup.checkedRadioButtonId) {
                R.id.radio_creative -> true
                R.id.radio_action_button -> false
                else -> true
            }

            val countdownStyle = when (countdownStyleGroup.checkedRadioButtonId) {
                R.id.radio_countdown_style_pie_chart -> CountdownStyle.PIE_CHART
                R.id.radio_countdown_style_timer -> CountdownStyle.TIMER
                R.id.radio_countdown_style_progress -> CountdownStyle.PROGRESS
                else -> CountdownStyle.TIMER
            }

            val contentInfoIconClickAction =
                when (contentInfoIconClickActionGroup.checkedRadioButtonId) {
                    R.id.radio_content_info_icon_click_action_expand -> ContentInfoIconAction.EXPAND.action
                    R.id.radio_content_info_icon_click_action_open -> ContentInfoIconAction.OPEN.action
                    else -> ContentInfoIconAction.EXPAND.toString()
                }

            val contentInfoDisplay = when (contentInfoDisplayGroup.checkedRadioButtonId) {
                R.id.radio_content_info_display_inapp -> ContentInfoDisplay.IN_APP.display
                R.id.radio_content_info_display_systembrowser -> ContentInfoDisplay.SYSTEM_BROWSER.display
                else -> ContentInfoDisplay.SYSTEM_BROWSER.display
            }

            val customEndCardDisplay = when (customEndCardGroup.checkedRadioButtonId) {
                R.id.radio_fallback -> {
                    CustomEndCardDisplay.FALLBACK.display
                }

                R.id.radio_extension -> {
                    CustomEndCardDisplay.EXTENSION.display
                }

                else -> {
                    CustomEndCardDisplay.FALLBACK.display
                }
            }

            settingManager.setInitialAudioState(getAudioStateInt(initialAudioState))
            settingManager.setCountdownStyle(countdownStyle.id)
            if (cbInitialAudio.isChecked) {
                HyBid.setVideoAudioStatus(initialAudioState)
            }

            adCustomizationsManager = AdCustomizationsManager(
                cbInitialAudio.isChecked,
                getAudioStateInt(initialAudioState),
                cbMraidExpand.isChecked,
                mraidExpandSwitch.isChecked,
                cbAutoClose.isChecked,
                enableAutoCloseSwitch.isChecked,
                cbEnableEndcard.isChecked,
                enableEndcardSwitch.isChecked,
                cbEnableCustomEndcard.isChecked,
                enableCustomEndcardSwitch.isChecked,
                cbEnableCustomEndcardDisplay.isChecked,
                customEndCardDisplay,
                cbInputNavigationMode.isChecked,
                navigationModeInput.text.toString(),
                cbLandingPage.isChecked,
                landingPageSwitch.isChecked,
                cbAutoCloseRewarded.isChecked,
                enableAutoCloseSwitchRewarded.isChecked,
                cbInputSkipOffset.isChecked,
                htmlSkipOffsetInput.text.toString(),
                cbInputVideoSkipOffset.isChecked,
                videoSkipOffsetInput.text.toString(),
                cbInputRewardedSkipOffset.isChecked,
                rewardedHtmlSkipOffsetInput.text.toString(),
                cbInputRewardedVideoSkipOffset.isChecked,
                rewardedVideoSkipOffsetInput.text.toString(),
                cbInputEndcardCloseButtonDelay.isChecked,
                endCardCloseButtonDelayInput.text.toString(),
                cbGroupClickBehaviour.isChecked,
                clickBehaviour,
                cbContentInfoUrl.isChecked,
                contentInfoUrlInput.text.toString(),
                cbContentInfoIconUrl.isChecked,
                contentInfoIconUrlInput.text.toString(),
                cbContentInfoIconClickAction.isChecked,
                contentInfoIconClickAction,
                cbContentInfoDisplay.isChecked,
                contentInfoDisplay,
                cbInputCloseButtonDelay.isChecked,
                closeButtonDelayInput.text.toString(),
                cbCountdownStyle.isChecked,
                countdownStyle.name,
                cbImpTracking.isChecked,
                impTracking.text.toString(),
                cbMinVisibilityTime.isChecked,
                minVisibilityTime.text.toString(),
                cbMinVisibilityPercent.isChecked,
                minVisibilityPercent.text.toString(),
                cbCustomCTAEnabled.isChecked,
                enableCustomCTASwitch.isChecked,
                cbInputCustomCTADelay.isChecked,
                customCTADelay.text.toString(),
                getCustomCtaTypeInt(),
                enableReducedButtonsSwitch.isChecked,
                cbEnableReducedButtons.isChecked
            )
            prefs.setAdCustomizationData(adCustomizationsManager.toJson())
            prefs.setCustomEndCardHTML(
                customEndCardHTML = customEndCardHTML.text.toString().trim()
            )
            prefs.setCustomCTAIconURL(url = customCTAIconURL.text.toString().trim())
            prefs.setCustomCTAAppName(name = customCTAAppName.text.toString().trim())
            prefs.setBundleId(bundleId = bundleId.text.toString().trim())
            activity?.finish()
        } else {
            if (isMaximumIntegerValueMessageDisplayed) {
                Toast.makeText(
                    requireActivity(),
                    "Invalid value length. Please, re-enter valid value",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isWrongUrlUsed) {
                Toast.makeText(
                    requireActivity(),
                    "Invalid URL. Please, re-enter a valid URL",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Please fill mandatory fields above",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun getAudioStateInt(audioState: AudioState): Int {
        return when (audioState) {
            AudioState.DEFAULT -> 0
            AudioState.ON -> 1
            AudioState.MUTED -> 2
        }
    }

    private fun getCustomCtaTypeInt(): Int {
        return when (customCTATypeGroup.checkedRadioButtonId) {
            R.id.radio_default -> 0
            R.id.radio_extended -> 1
            else -> 0
        }
    }
}