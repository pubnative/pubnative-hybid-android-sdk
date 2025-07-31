// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
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
import net.pubnative.lite.demo.managers.AudioSettings
import net.pubnative.lite.demo.managers.AutoCloseSettings
import net.pubnative.lite.demo.managers.ClickBehaviourSettings
import net.pubnative.lite.demo.managers.CloseButtonSettings
import net.pubnative.lite.demo.managers.ContentInfoSettings
import net.pubnative.lite.demo.managers.CountdownSettings
import net.pubnative.lite.demo.managers.CustomCtaSettings
import net.pubnative.lite.demo.managers.EndCardSettings
import net.pubnative.lite.demo.managers.ImpressionTrackingSettings
import net.pubnative.lite.demo.managers.LandingPageSettings
import net.pubnative.lite.demo.managers.LearnMoreSettings
import net.pubnative.lite.demo.managers.MraidSettings
import net.pubnative.lite.demo.managers.NavigationSettings
import net.pubnative.lite.demo.managers.ReducedButtonsSettings
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.managers.SkipOffsetSettings
import net.pubnative.lite.demo.managers.VisibilitySettings
import net.pubnative.lite.sdk.CountdownStyle
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.ContentInfoDisplay
import net.pubnative.lite.sdk.models.ContentInfoIconAction
import net.pubnative.lite.sdk.models.CustomEndCardDisplay
import net.pubnative.lite.sdk.models.LearnMoreLocation
import net.pubnative.lite.sdk.models.LearnMoreSize
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
    private lateinit var bcLearnMoreSizeGroup: RadioGroup
    private lateinit var bcLearnMoreLocationGroup: RadioGroup

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
    private lateinit var cbBcLearnMoreSize: CheckBox
    private lateinit var cbBcLearnMoreLocation: CheckBox

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
        bcLearnMoreSizeGroup = requireView().findViewById(R.id.group_learn_more_size)
        bcLearnMoreLocationGroup = requireView().findViewById(R.id.group_learn_more_location)

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
        cbBcLearnMoreSize = requireView().findViewById(R.id.cb_learn_more_size)
        cbBcLearnMoreLocation = requireView().findViewById(R.id.cb_learn_more_location)

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
            requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_expand).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_open).isEnabled =
                checked
        }

        cbContentInfoDisplay.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_content_info_display_inapp).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_content_info_display_systembrowser).isEnabled =
                checked
        }

        cbCountdownStyle.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_countdown_style_pie_chart).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_countdown_style_timer).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_countdown_style_progress).isEnabled =
                checked
        }

        cbBcLearnMoreSize.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_learn_more_size_default).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_learn_more_size_medium).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_learn_more_size_large).isEnabled =
                checked
        }

        cbBcLearnMoreLocation.setOnCheckedChangeListener { p0, checked ->
            requireView().findViewById<RadioButton>(R.id.radio_learn_more_location_default).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_learn_more_location_bottom_down).isEnabled =
                checked
            requireView().findViewById<RadioButton>(R.id.radio_learn_more_location_bottom_up).isEnabled =
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
        // Initial Audio
        cbInitialAudio.isChecked = adCustomizationsManager.audioSettings?.enabled == true
        val selectedInitialAudio = when (adCustomizationsManager.audioSettings?.value) {
            0 -> R.id.radio_sound_default
            1 -> R.id.radio_sound_on
            2 -> R.id.radio_sound_mute
            else -> R.id.radio_sound_default
        }
        initialAudioGroup.check(selectedInitialAudio)
        requireView().findViewById<RadioButton>(R.id.radio_sound_default).isEnabled =
            adCustomizationsManager.audioSettings?.enabled == true
        requireView().findViewById<RadioButton>(R.id.radio_sound_on).isEnabled =
            adCustomizationsManager.audioSettings?.enabled == true
        requireView().findViewById<RadioButton>(R.id.radio_sound_mute).isEnabled =
            adCustomizationsManager.audioSettings?.enabled == true

        // MRAID Expand
        cbMraidExpand.isChecked = adCustomizationsManager.mraidSettings?.expandEnabled == true
        mraidExpandSwitch.isEnabled = adCustomizationsManager.mraidSettings?.expandEnabled == true
        mraidExpandSwitch.isChecked = adCustomizationsManager.mraidSettings?.expandValue == true

        // Auto Close Interstitial
        cbAutoClose.isChecked =
            adCustomizationsManager.autoCloseSettings?.interstitialEnabled == true
        enableAutoCloseSwitch.isEnabled =
            adCustomizationsManager.autoCloseSettings?.interstitialEnabled == true
        enableAutoCloseSwitch.isChecked =
            adCustomizationsManager.autoCloseSettings?.interstitialValue == true

        // End Card
        cbEnableEndcard.isChecked = adCustomizationsManager.endCardSettings?.enabled == true
        enableEndcardSwitch.isEnabled = adCustomizationsManager.endCardSettings?.enabled == true
        enableEndcardSwitch.isChecked = adCustomizationsManager.endCardSettings?.value == true

        cbEnableCustomEndcard.isChecked =
            adCustomizationsManager.endCardSettings?.customEnabled == true
        enableCustomEndcardSwitch.isEnabled =
            adCustomizationsManager.endCardSettings?.customEnabled == true
        enableCustomEndcardSwitch.isChecked =
            adCustomizationsManager.endCardSettings?.customValue == true

        cbEnableCustomEndcardDisplay.isChecked =
            adCustomizationsManager.endCardSettings?.customDisplayEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_fallback).isEnabled =
            adCustomizationsManager.endCardSettings?.customDisplayEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_extension).isEnabled =
            adCustomizationsManager.endCardSettings?.customDisplayEnabled == true
        when (adCustomizationsManager.endCardSettings?.customDisplayValue) {
            CustomEndCardDisplay.FALLBACK.display -> customEndCardGroup.check(R.id.radio_fallback)
            CustomEndCardDisplay.EXTENSION.display -> customEndCardGroup.check(R.id.radio_extension)
            else -> customEndCardGroup.check(R.id.radio_fallback)
        }

        // Auto Close Rewarded
        cbAutoCloseRewarded.isChecked =
            adCustomizationsManager.autoCloseSettings?.rewardedEnabled == true
        enableAutoCloseSwitchRewarded.isEnabled =
            adCustomizationsManager.autoCloseSettings?.rewardedEnabled == true
        enableAutoCloseSwitchRewarded.isChecked =
            adCustomizationsManager.autoCloseSettings?.rewardedValue == true

        // Skip Offsets
        cbInputSkipOffset.isChecked =
            adCustomizationsManager.skipOffsetSettings?.html?.first == true
        htmlSkipOffsetInput.isEnabled =
            adCustomizationsManager.skipOffsetSettings?.html?.first == true
        htmlSkipOffsetInput.setText(adCustomizationsManager.skipOffsetSettings?.html?.second)

        cbInputVideoSkipOffset.isChecked =
            adCustomizationsManager.skipOffsetSettings?.video?.first == true
        videoSkipOffsetInput.isEnabled =
            adCustomizationsManager.skipOffsetSettings?.video?.first == true
        videoSkipOffsetInput.setText(adCustomizationsManager.skipOffsetSettings?.video?.second)

        cbInputRewardedSkipOffset.isChecked =
            adCustomizationsManager.skipOffsetSettings?.rewardedHtml?.first == true
        rewardedHtmlSkipOffsetInput.isEnabled =
            adCustomizationsManager.skipOffsetSettings?.rewardedHtml?.first == true
        rewardedHtmlSkipOffsetInput.setText(adCustomizationsManager.skipOffsetSettings?.rewardedHtml?.second)

        cbInputRewardedVideoSkipOffset.isChecked =
            adCustomizationsManager.skipOffsetSettings?.rewardedVideo?.first == true
        rewardedVideoSkipOffsetInput.isEnabled =
            adCustomizationsManager.skipOffsetSettings?.rewardedVideo?.first == true
        rewardedVideoSkipOffsetInput.setText(adCustomizationsManager.skipOffsetSettings?.rewardedVideo?.second)

        cbInputEndcardCloseButtonDelay.isChecked =
            adCustomizationsManager.skipOffsetSettings?.endCardCloseDelay?.first == true
        endCardCloseButtonDelayInput.isEnabled =
            adCustomizationsManager.skipOffsetSettings?.endCardCloseDelay?.first == true
        endCardCloseButtonDelayInput.setText(adCustomizationsManager.skipOffsetSettings?.endCardCloseDelay?.second)

        // Navigation
        cbInputNavigationMode.isChecked =
            adCustomizationsManager.navigationSettings?.enabled == true
        navigationModeInput.isEnabled = adCustomizationsManager.navigationSettings?.enabled == true
        navigationModeInput.setText(adCustomizationsManager.navigationSettings?.value)

        // Landing Page
        cbLandingPage.isChecked = adCustomizationsManager.landingPageSettings?.enabled == true
        landingPageSwitch.isEnabled = adCustomizationsManager.landingPageSettings?.enabled == true
        landingPageSwitch.isChecked = adCustomizationsManager.landingPageSettings?.value == true

        // Click Behaviour
        cbGroupClickBehaviour.isChecked =
            adCustomizationsManager.clickBehaviourSettings?.enabled == true
        clickBehaviourGroup.check(if (adCustomizationsManager.clickBehaviourSettings?.value == true) R.id.radio_creative else R.id.radio_action_button)
        requireView().findViewById<RadioButton>(R.id.radio_creative).isEnabled =
            adCustomizationsManager.clickBehaviourSettings?.enabled == true
        requireView().findViewById<RadioButton>(R.id.radio_action_button).isEnabled =
            adCustomizationsManager.clickBehaviourSettings?.enabled == true

        // Reduced Buttons
        cbEnableReducedButtons.isChecked =
            adCustomizationsManager.reducedButtonsSettings?.enabled == true
        enableReducedButtonsSwitch.isEnabled =
            adCustomizationsManager.reducedButtonsSettings?.enabled == true
        enableReducedButtonsSwitch.isChecked =
            adCustomizationsManager.reducedButtonsSettings?.value == true

        // Content Info
        cbContentInfoUrl.isChecked = adCustomizationsManager.contentInfoSettings?.urlEnabled == true
        contentInfoUrlInput.isEnabled =
            adCustomizationsManager.contentInfoSettings?.urlEnabled == true
        contentInfoUrlInput.setText(adCustomizationsManager.contentInfoSettings?.urlValue)

        cbContentInfoIconUrl.isChecked =
            adCustomizationsManager.contentInfoSettings?.iconUrlEnabled == true
        contentInfoIconUrlInput.isEnabled =
            adCustomizationsManager.contentInfoSettings?.iconUrlEnabled == true
        contentInfoIconUrlInput.setText(adCustomizationsManager.contentInfoSettings?.iconUrlValue)

        cbContentInfoIconClickAction.isChecked =
            adCustomizationsManager.contentInfoSettings?.iconClickActionEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_open).isEnabled =
            adCustomizationsManager.contentInfoSettings?.iconClickActionEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_content_info_icon_click_action_expand).isEnabled =
            adCustomizationsManager.contentInfoSettings?.iconClickActionEnabled == true
        contentInfoIconClickActionGroup.check(
            when (adCustomizationsManager.contentInfoSettings?.iconClickActionValue) {
                ContentInfoIconAction.EXPAND.action -> R.id.radio_content_info_icon_click_action_expand
                ContentInfoIconAction.OPEN.action -> R.id.radio_content_info_icon_click_action_open
                else -> R.id.radio_content_info_icon_click_action_expand
            }
        )

        cbContentInfoDisplay.isChecked =
            adCustomizationsManager.contentInfoSettings?.displayEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_content_info_display_inapp).isEnabled =
            adCustomizationsManager.contentInfoSettings?.displayEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_content_info_display_systembrowser).isEnabled =
            adCustomizationsManager.contentInfoSettings?.displayEnabled == true
        contentInfoDisplayGroup.check(
            when (adCustomizationsManager.contentInfoSettings?.displayValue) {
                ContentInfoDisplay.IN_APP.display -> R.id.radio_content_info_display_inapp
                ContentInfoDisplay.SYSTEM_BROWSER.display -> R.id.radio_content_info_display_systembrowser
                else -> R.id.radio_content_info_display_systembrowser
            }
        )

        // Close Button Delay
        cbInputCloseButtonDelay.isChecked =
            adCustomizationsManager.closeButtonSettings?.enabled == true
        closeButtonDelayInput.isEnabled =
            adCustomizationsManager.closeButtonSettings?.enabled == true
        closeButtonDelayInput.setText(adCustomizationsManager.closeButtonSettings?.value)

        // Countdown Style
        cbCountdownStyle.isChecked = adCustomizationsManager.countdownSettings?.enabled == true
        requireView().findViewById<RadioButton>(R.id.radio_countdown_style_pie_chart).isEnabled =
            adCustomizationsManager.countdownSettings?.enabled == true
        requireView().findViewById<RadioButton>(R.id.radio_countdown_style_timer).isEnabled =
            adCustomizationsManager.countdownSettings?.enabled == true
        requireView().findViewById<RadioButton>(R.id.radio_countdown_style_progress).isEnabled =
            adCustomizationsManager.countdownSettings?.enabled == true
        countdownStyleGroup.check(
            when (adCustomizationsManager.countdownSettings?.value) {
                CountdownStyle.PIE_CHART.name -> R.id.radio_countdown_style_pie_chart
                CountdownStyle.TIMER.name -> R.id.radio_countdown_style_timer
                CountdownStyle.PROGRESS.name -> R.id.radio_countdown_style_progress
                else -> R.id.radio_countdown_style_timer
            }
        )

        // Learn More
        cbBcLearnMoreSize.isChecked = adCustomizationsManager.learnMoreSettings?.sizeEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_learn_more_size_default).isEnabled =
            adCustomizationsManager.learnMoreSettings?.sizeEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_learn_more_size_medium).isEnabled =
            adCustomizationsManager.learnMoreSettings?.sizeEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_learn_more_size_large).isEnabled =
            adCustomizationsManager.learnMoreSettings?.sizeEnabled == true
        bcLearnMoreSizeGroup.check(
            when (adCustomizationsManager.learnMoreSettings?.sizeValue) {
                LearnMoreSize.DEFAULT.name -> R.id.radio_learn_more_size_default
                LearnMoreSize.MEDIUM.name -> R.id.radio_learn_more_size_medium
                LearnMoreSize.LARGE.name -> R.id.radio_learn_more_size_large
                else -> R.id.radio_learn_more_size_default
            }
        )

        cbBcLearnMoreLocation.isChecked =
            adCustomizationsManager.learnMoreSettings?.locationEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_learn_more_location_default).isEnabled =
            adCustomizationsManager.learnMoreSettings?.locationEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_learn_more_location_bottom_down).isEnabled =
            adCustomizationsManager.learnMoreSettings?.locationEnabled == true
        requireView().findViewById<RadioButton>(R.id.radio_learn_more_location_bottom_up).isEnabled =
            adCustomizationsManager.learnMoreSettings?.locationEnabled == true
        bcLearnMoreLocationGroup.check(
            when (adCustomizationsManager.learnMoreSettings?.locationValue) {
                LearnMoreLocation.DEFAULT.name -> R.id.radio_learn_more_location_default
                LearnMoreLocation.BOTTOM_DOWN.name -> R.id.radio_learn_more_location_bottom_down
                LearnMoreLocation.BOTTOM_UP.name -> R.id.radio_learn_more_location_bottom_up
                else -> R.id.radio_learn_more_location_default
            }
        )

        // Impression Tracking
        cbImpTracking.isChecked =
            adCustomizationsManager.impressionTrackingSettings?.enabled == true
        impTracking.isEnabled = adCustomizationsManager.impressionTrackingSettings?.enabled == true
        impTracking.setText(adCustomizationsManager.impressionTrackingSettings?.value)

        // Visibility Settings
        cbMinVisibilityTime.isChecked =
            adCustomizationsManager.visibilitySettings?.minTimeEnabled == true
        minVisibilityTime.isEnabled =
            adCustomizationsManager.visibilitySettings?.minTimeEnabled == true
        minVisibilityTime.setText(adCustomizationsManager.visibilitySettings?.minTimeValue)

        cbMinVisibilityPercent.isChecked =
            adCustomizationsManager.visibilitySettings?.minPercentEnabled == true
        minVisibilityPercent.isEnabled =
            adCustomizationsManager.visibilitySettings?.minPercentEnabled == true
        minVisibilityPercent.setText(adCustomizationsManager.visibilitySettings?.minPercentValue)

        // Custom CTA
        cbCustomCTAEnabled.isChecked = adCustomizationsManager.customCtaSettings?.enabled == true
        enableCustomCTASwitch.isEnabled = adCustomizationsManager.customCtaSettings?.enabled == true
        enableCustomCTASwitch.isChecked =
            adCustomizationsManager.customCtaSettings?.enabledValue == true

        cbInputCustomCTADelay.isChecked =
            adCustomizationsManager.customCtaSettings?.delayEnabled == true
        customCTADelay.isEnabled = adCustomizationsManager.customCtaSettings?.delayEnabled == true
        customCTADelay.setText(adCustomizationsManager.customCtaSettings?.delayEnabledValue)

        val selectedCustomCTAType = when (adCustomizationsManager.customCtaSettings?.typeValue) {
            0 -> R.id.radio_default
            1 -> R.id.radio_extended
            else -> R.id.radio_default
        }
        customCTATypeGroup.check(selectedCustomCTAType)

        // Custom fields from prefs
        customEndCardHTML.setText(prefs.getCustomEndCardHTML())
        bundleId.setText(prefs.getBundleId())
        customCTAIconURL.setText(prefs.getCustomCTAIconURL())
        customCTAAppName.setText(prefs.getCustomCTAAppName())
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

            if (cbInputNavigationMode.isChecked && navigationModeInput.text.toString().trim()
                    .isEmpty()
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

            if (cbInputCloseButtonDelay.isChecked && closeButtonDelayInput.text.toString().trim()
                    .isEmpty()
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

            if (cbContentInfoUrl.isChecked && !URLValidator.isValidURL(
                    contentInfoUrlInput.text.toString().trim()
                )
            ) {
                isWrongUrlUsed = true
                return false
            }

            if (cbContentInfoIconUrl.isChecked && !URLValidator.isValidURL(
                    contentInfoIconUrlInput.text.toString().trim()
                )
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

            val learnMoreSize = when (bcLearnMoreSizeGroup.checkedRadioButtonId) {
                R.id.radio_learn_more_size_default -> LearnMoreSize.DEFAULT
                R.id.radio_learn_more_size_medium -> LearnMoreSize.MEDIUM
                R.id.radio_learn_more_size_large -> LearnMoreSize.LARGE
                else -> LearnMoreSize.DEFAULT
            }

            val learnMoreLocation = when (bcLearnMoreLocationGroup.checkedRadioButtonId) {
                R.id.radio_learn_more_location_default -> LearnMoreLocation.DEFAULT
                R.id.radio_learn_more_location_bottom_down -> LearnMoreLocation.BOTTOM_DOWN
                R.id.radio_learn_more_location_bottom_up -> LearnMoreLocation.BOTTOM_UP
                else -> LearnMoreLocation.DEFAULT
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
                audioSettings = AudioSettings(
                    enabled = cbInitialAudio.isChecked, value = getAudioStateInt(initialAudioState)
                ), mraidSettings = MraidSettings(
                    expandEnabled = cbMraidExpand.isChecked,
                    expandValue = mraidExpandSwitch.isChecked
                ), autoCloseSettings = AutoCloseSettings(
                    interstitialEnabled = cbAutoClose.isChecked,
                    interstitialValue = enableAutoCloseSwitch.isChecked,
                    rewardedEnabled = cbAutoCloseRewarded.isChecked,
                    rewardedValue = enableAutoCloseSwitchRewarded.isChecked
                ), endCardSettings = EndCardSettings(
                    enabled = cbEnableEndcard.isChecked,
                    value = enableEndcardSwitch.isChecked,
                    customEnabled = cbEnableCustomEndcard.isChecked,
                    customValue = enableCustomEndcardSwitch.isChecked,
                    customDisplayEnabled = cbEnableCustomEndcardDisplay.isChecked,
                    customDisplayValue = customEndCardDisplay
                ), navigationSettings = NavigationSettings(
                    enabled = cbInputNavigationMode.isChecked,
                    value = navigationModeInput.text.toString()
                ), landingPageSettings = LandingPageSettings(
                    enabled = cbLandingPage.isChecked, value = landingPageSwitch.isChecked
                ), skipOffsetSettings = SkipOffsetSettings(
                    html = cbInputSkipOffset.isChecked to htmlSkipOffsetInput.text.toString(),
                    video = cbInputVideoSkipOffset.isChecked to videoSkipOffsetInput.text.toString(),
                    playable = false to "3",
                    rewardedHtml = cbInputRewardedSkipOffset.isChecked to rewardedHtmlSkipOffsetInput.text.toString(),
                    rewardedVideo = cbInputRewardedVideoSkipOffset.isChecked to rewardedVideoSkipOffsetInput.text.toString(),
                    endCardCloseDelay = cbInputEndcardCloseButtonDelay.isChecked to endCardCloseButtonDelayInput.text.toString()
                ), clickBehaviourSettings = ClickBehaviourSettings(
                    enabled = cbGroupClickBehaviour.isChecked, value = clickBehaviour
                ), contentInfoSettings = ContentInfoSettings(
                    urlEnabled = cbContentInfoUrl.isChecked,
                    urlValue = contentInfoUrlInput.text.toString(),
                    iconUrlEnabled = cbContentInfoIconUrl.isChecked,
                    iconUrlValue = contentInfoIconUrlInput.text.toString(),
                    iconClickActionEnabled = cbContentInfoIconClickAction.isChecked,
                    iconClickActionValue = contentInfoIconClickAction,
                    displayEnabled = cbContentInfoDisplay.isChecked,
                    displayValue = contentInfoDisplay
                ), closeButtonSettings = CloseButtonSettings(
                    enabled = cbInputCloseButtonDelay.isChecked,
                    value = closeButtonDelayInput.text.toString()
                ), countdownSettings = CountdownSettings(
                    enabled = cbCountdownStyle.isChecked, value = countdownStyle.name
                ),
                learnMoreSettings = LearnMoreSettings(
                    sizeEnabled = cbBcLearnMoreSize.isChecked,
                    sizeValue = learnMoreSize.name,
                    locationEnabled = cbBcLearnMoreLocation.isChecked,
                    locationValue = learnMoreLocation.name
                ),
                impressionTrackingSettings = ImpressionTrackingSettings(
                    enabled = cbImpTracking.isChecked, value = impTracking.text.toString()
                ), visibilitySettings = VisibilitySettings(
                    minTimeEnabled = cbMinVisibilityTime.isChecked,
                    minTimeValue = minVisibilityTime.text.toString(),
                    minPercentEnabled = cbMinVisibilityPercent.isChecked,
                    minPercentValue = minVisibilityPercent.text.toString()
                ), customCtaSettings = CustomCtaSettings(
                    enabled = cbCustomCTAEnabled.isChecked,
                    enabledValue = enableCustomCTASwitch.isChecked,
                    delayEnabled = cbInputCustomCTADelay.isChecked,
                    delayEnabledValue = customCTADelay.text.toString(),
                    typeValue = getCustomCtaTypeInt()
                ), reducedButtonsSettings = ReducedButtonsSettings(
                    enabled = cbEnableReducedButtons.isChecked,
                    value = enableReducedButtonsSwitch.isChecked
                )
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
                    requireActivity(), "Please fill mandatory fields above", Toast.LENGTH_SHORT
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