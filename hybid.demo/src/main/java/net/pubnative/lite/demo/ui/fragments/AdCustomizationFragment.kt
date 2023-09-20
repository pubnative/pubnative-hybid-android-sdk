package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.CountdownStyle
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.InterstitialActionBehaviour
import net.pubnative.lite.sdk.vpaid.enums.AudioState

class AdCustomizationFragment : Fragment(R.layout.fragment_ad_customization) {

    private lateinit var initialAudioGroup: RadioGroup
    private lateinit var mraidExpandSwitch: SwitchCompat
    private lateinit var locationTrackingSwitch: SwitchCompat
    private lateinit var locationUpdatesSwitch: SwitchCompat
    private lateinit var customSkipOffsetDisabledSwitch: SwitchCompat
    private lateinit var autocloseSwitch: SwitchCompat
    private lateinit var autocloseSwitchRewarded: SwitchCompat
    private lateinit var enableEndcardSwitch: SwitchCompat
    private lateinit var htmlSkipOffsetInput: EditText
    private lateinit var videoSkipOffsetInput: EditText
    private lateinit var endCardCloseButtonDelayInput: EditText
    private lateinit var clickBehaviourGroup: RadioGroup
    private lateinit var settingManager: SettingsManager
    private lateinit var countdownStyleGroup: RadioGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        settingManager = SettingsManager.getInstance(requireContext())
        view.findViewById<Button>(R.id.button_save_settings).setOnClickListener {
            saveData()
            activity?.finish()
        }
        fillSavedValues()
    }

    private fun initViews() {
        initialAudioGroup = requireView().findViewById(R.id.group_initial_audio)
        mraidExpandSwitch = requireView().findViewById(R.id.check_mraid_expand)
        locationTrackingSwitch = requireView().findViewById(R.id.check_location_tracking)
        locationUpdatesSwitch = requireView().findViewById(R.id.check_location_updates)
        customSkipOffsetDisabledSwitch = requireView().findViewById(R.id.disable_custom_skip_offset)
        autocloseSwitch = requireView().findViewById(R.id.check_auto_close)
        autocloseSwitchRewarded = requireView().findViewById(R.id.check_auto_close_rewarded)
        enableEndcardSwitch = requireView().findViewById(R.id.check_enable_endcard)
        htmlSkipOffsetInput = requireView().findViewById(R.id.input_skip_offset)
        videoSkipOffsetInput = requireView().findViewById(R.id.input_video_skip_offset)
        endCardCloseButtonDelayInput =
            requireView().findViewById(R.id.input_endcard_close_button_delay)
        clickBehaviourGroup = requireView().findViewById(R.id.group_click_behaviour)
        countdownStyleGroup = requireView().findViewById(R.id.countdown_style)
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().adCustomizationSettings

        val selectedInitialAudio = when (settings?.initialAudioState) {
            0 -> R.id.radio_sound_default
            1 -> R.id.radio_sound_on
            2 -> R.id.radio_sound_mute
            else -> {
                R.id.radio_sound_default
            }
        }
        initialAudioGroup.check(selectedInitialAudio)

        val countdownStyle = when (settings?.countdownStyle) {
            CountdownStyle.PIE_CHART.id -> R.id.radio_countdown_style_pie_chart
            CountdownStyle.TIMER.id -> R.id.radio_countdown_style_timer
            CountdownStyle.PROGRESS.id -> R.id.radio_countdown_style_progress
            else -> {
                R.id.radio_countdown_style_pie_chart
            }
        }
        countdownStyleGroup.check(countdownStyle)

        mraidExpandSwitch.isChecked = settings?.mraidExpanded == true
        locationTrackingSwitch.isChecked = settings?.locationTracking == true
        locationUpdatesSwitch.isChecked = settings?.locationUpdates == true
        customSkipOffsetDisabledSwitch.isChecked = settings?.customSkipOffsetDisabled == true
        autocloseSwitch.isChecked = settings?.closeVideoAfterFinish == true
        autocloseSwitchRewarded.isChecked = settings?.closeVideoAfterFinishForRewardedVideo == true
        enableEndcardSwitch.isChecked = settings?.enableEndcard == true
        htmlSkipOffsetInput.setText(settings?.skipOffset.toString())
        videoSkipOffsetInput.setText(settings?.videoSkipOffset.toString())
        endCardCloseButtonDelayInput.setText(settings?.endCardCloseButtonDelay.toString())

        val selectedClickBehaviour = when (settings?.videoClickBehaviour) {
            true -> R.id.radio_creative
            false -> R.id.radio_action_button
            else -> R.id.radio_creative
        }

        customSkipOffsetDisabledSwitch.setOnCheckedChangeListener { _, isChecked ->

            onCustomSkipOffsetSwitchChanged(isChecked)
        }

        clickBehaviourGroup.check(selectedClickBehaviour)

        onCustomSkipOffsetSwitchChanged(customSkipOffsetDisabledSwitch.isChecked)
    }

    private fun onCustomSkipOffsetSwitchChanged(isChecked: Boolean) {
        if (isChecked) {
            htmlSkipOffsetInput.isEnabled = false
            videoSkipOffsetInput.isEnabled = false
            endCardCloseButtonDelayInput.isEnabled = false
        } else {
            htmlSkipOffsetInput.isEnabled = true
            videoSkipOffsetInput.isEnabled = true
            endCardCloseButtonDelayInput.isEnabled = true
        }
    }

    private fun saveData() {
        val initialAudioState = when (initialAudioGroup.checkedRadioButtonId) {
            R.id.radio_sound_default -> AudioState.DEFAULT
            R.id.radio_sound_on -> AudioState.ON
            R.id.radio_sound_mute -> AudioState.MUTED
            else -> AudioState.ON
        }

        val mraidExpand = mraidExpandSwitch.isChecked
        val locationTracking = locationTrackingSwitch.isChecked
        val locationUpdates = locationUpdatesSwitch.isChecked
        val customSkipOffsetDisabled = customSkipOffsetDisabledSwitch.isChecked
        val autoCloseVideo = autocloseSwitch.isChecked
        val autoCloseVideoRewarded = autocloseSwitchRewarded.isChecked
        val enableEndcard = enableEndcardSwitch.isChecked

        val skipOffset = htmlSkipOffsetInput.text.toString()
        val videoSkipOffset = videoSkipOffsetInput.text.toString()
        val endCardCloseButtonDelay = endCardCloseButtonDelayInput.text.toString()

        val videoClickBehaviour = when (clickBehaviourGroup.checkedRadioButtonId) {
            R.id.radio_creative -> InterstitialActionBehaviour.HB_CREATIVE
            R.id.radio_action_button -> InterstitialActionBehaviour.HB_ACTION_BUTTON
            else -> InterstitialActionBehaviour.HB_CREATIVE
        }

        val skipOffsetInt: Int
        val videoSkipOffsetInt: Int
        val endCardCloseButtonDelayInt: Int

        if (skipOffset.isEmpty() || videoSkipOffset.isEmpty()
            || skipOffset.contains(" ")
            || videoSkipOffset.contains(" ")
            || endCardCloseButtonDelay.isEmpty()
            || endCardCloseButtonDelay.contains(" ")
        ) {
            Toast.makeText(
                context,
                "Please make sure skipOffset, videoSkipOffset and endCardCloseButtonDelay values are correct.",
                Toast.LENGTH_LONG
            ).show()
            return
        } else {
            skipOffsetInt = if (TextUtils.isEmpty(skipOffset)) {
                0
            } else {
                skipOffset.toInt()
            }
            videoSkipOffsetInt = if (TextUtils.isEmpty(videoSkipOffset)) {
                0
            } else {
                videoSkipOffset.toInt()
            }
            endCardCloseButtonDelayInt = if (TextUtils.isEmpty(endCardCloseButtonDelay)) {
                0
            } else {
                endCardCloseButtonDelay.toInt()
            }
        }

        val countdownStyle = when (countdownStyleGroup.checkedRadioButtonId) {
            R.id.radio_countdown_style_pie_chart -> CountdownStyle.PIE_CHART
            R.id.radio_countdown_style_timer -> CountdownStyle.TIMER
            R.id.radio_countdown_style_progress -> CountdownStyle.PROGRESS
            else -> CountdownStyle.TIMER
        }

        settingManager.setInitialAudioState(getAudioStateInt(initialAudioState))
        settingManager.setMraidExpanded(mraidExpand)
        settingManager.setLocationTracking(locationTracking)
        settingManager.setLocationUpdates(locationUpdates)
        settingManager.setCustomSkipOffsetDisabled(customSkipOffsetDisabled)
        settingManager.setCloseVideoAfterFinish(autoCloseVideo)
        settingManager.setCloseVideoAfterFinishForRewardedVideo(autoCloseVideoRewarded)
        settingManager.setEnableEndcard(enableEndcard)
        settingManager.setSkipOffset(skipOffsetInt)
        settingManager.setVideoSkipOffset(videoSkipOffsetInt)
        settingManager.setEndCardCloseButtonDelay(endCardCloseButtonDelayInt)
        settingManager.setVideoClickBehaviour(getVideoClickBehaviourBoolean(videoClickBehaviour))
        settingManager.setCountdownStyle(countdownStyle.id)

        HyBid.setVideoAudioStatus(initialAudioState)
        HyBid.setMraidExpandEnabled(mraidExpand)
        HyBid.setLocationTrackingEnabled(locationTracking)
        HyBid.setLocationUpdatesEnabled(locationUpdates)
        HyBid.setCloseVideoAfterFinish(autoCloseVideo)
        HyBid.setCloseVideoAfterFinishForRewarded(autoCloseVideoRewarded)
        HyBid.setEndCardEnabled(enableEndcard)
        if (!customSkipOffsetDisabled) {
            HyBid.setHtmlInterstitialSkipOffset(skipOffsetInt)
            HyBid.setVideoInterstitialSkipOffset(videoSkipOffsetInt)
            HyBid.setEndCardCloseButtonDelay(endCardCloseButtonDelayInt)
        } else {
            HyBid.resetSkipOffsetValues()
        }
        HyBid.setInterstitialClickBehaviour(videoClickBehaviour)
        HyBid.setCountdownStyle(countdownStyle)
    }

    private fun getVideoClickBehaviourBoolean(interstitialActionBehaviour: InterstitialActionBehaviour): Boolean {
        return interstitialActionBehaviour == InterstitialActionBehaviour.HB_CREATIVE
    }

    private fun getAudioStateInt(audioState: AudioState): Int {
        return when (audioState) {
            AudioState.DEFAULT -> 0
            AudioState.ON -> 1
            AudioState.MUTED -> 2
        }
    }
}