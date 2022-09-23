package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.InterstitialActionBehaviour
import net.pubnative.lite.sdk.vpaid.enums.AudioState

class AdCustomizationFragment : Fragment(R.layout.fragment_ad_customization)  {

    private lateinit var initialAudioGroup: RadioGroup
    private lateinit var mraidExpandSwitch: SwitchCompat
    private lateinit var locationTrackingSwitch: SwitchCompat
    private lateinit var locationUpdatesSwitch: SwitchCompat
    private lateinit var autocloseSwitch: SwitchCompat
    private lateinit var autocloseSwitchRewarded: SwitchCompat
    private lateinit var enableEndcardSwitch: SwitchCompat
    private lateinit var htmlSkipOffsetInput: EditText
    private lateinit var videoSkipOffsetInput: EditText
    private lateinit var endCardCloseButtonDelayInput: EditText
    private lateinit var clickBehaviourGroup: RadioGroup
    private lateinit var settingManager: SettingsManager
    private lateinit var feedbackFormSwitch: SwitchCompat
    private lateinit var feedbackFormUrlInput: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        settingManager = SettingsManager.getInstance(requireContext())
        view.findViewById<Button>(R.id.button_save_settings).setOnClickListener {
            saveData()
        }
        fillSavedValues()
    }

    private fun initViews(){
        initialAudioGroup = requireView().findViewById(R.id.group_initial_audio)
        mraidExpandSwitch = requireView().findViewById(R.id.check_mraid_expand)
        locationTrackingSwitch = requireView().findViewById(R.id.check_location_tracking)
        locationUpdatesSwitch = requireView().findViewById(R.id.check_location_updates)
        autocloseSwitch = requireView().findViewById(R.id.check_auto_close)
        autocloseSwitchRewarded = requireView().findViewById(R.id.check_auto_close_rewarded)
        enableEndcardSwitch = requireView().findViewById(R.id.check_enable_endcard)
        htmlSkipOffsetInput = requireView().findViewById(R.id.input_skip_offset)
        videoSkipOffsetInput = requireView().findViewById(R.id.input_video_skip_offset)
        endCardCloseButtonDelayInput = requireView().findViewById(R.id.input_endcard_close_button_delay)
        clickBehaviourGroup = requireView().findViewById(R.id.group_click_behaviour)
        feedbackFormSwitch = requireView().findViewById(R.id.feedback_form_switch)
        feedbackFormUrlInput = requireView().findViewById(R.id.feedback_form_url_input)
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().adCustomizationSettings

        val selectedInitialAudio = when (settings?.initialAudioState) {
            0 -> R.id.radio_sound_default
            1 -> R.id.radio_sound_on
            2 -> R.id.radio_sound_mute
            else -> {R.id.radio_sound_default}
        }
        initialAudioGroup.check(selectedInitialAudio)
        mraidExpandSwitch.isChecked = settings?.mraidExpanded == true
        locationTrackingSwitch.isChecked = settings?.locationTracking == true
        locationUpdatesSwitch.isChecked = settings?.locationUpdates == true
        autocloseSwitch.isChecked = settings?.closeVideoAfterFinish == true
        autocloseSwitchRewarded.isChecked = settings?.closeVideoAfterFinishForRewardedVideo == true
        enableEndcardSwitch.isChecked = settings?.enableEndcard == true
        htmlSkipOffsetInput.setText(settings?.skipOffset.toString())
        videoSkipOffsetInput.setText(settings?.videoSkipOffset.toString())
        endCardCloseButtonDelayInput.setText(settings?.endCardCloseButtonDelay.toString())
        feedbackFormSwitch.isChecked = settings?.feedbackEnabled == true
        feedbackFormUrlInput.setText(settings?.feedbackFormUrl)

        val selectedClickBehaviour = when (settings?.videoClickBehaviour) {
            true -> R.id.radio_creative
            false -> R.id.radio_action_button
            else ->
                R.id.radio_creative
        }

        clickBehaviourGroup.check(selectedClickBehaviour)
    }

    private fun saveData() {
        val initialAudioState = when (initialAudioGroup.checkedRadioButtonId) {
            R.id.radio_sound_default -> AudioState.DEFAULT
            R.id.radio_sound_on -> AudioState.ON
            R.id.radio_sound_mute -> AudioState.MUTED
            else -> AudioState.DEFAULT
        }

        val mraidExpand = mraidExpandSwitch.isChecked
        val locationTracking = locationTrackingSwitch.isChecked
        val locationUpdates = locationUpdatesSwitch.isChecked
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
        val feedbackFormEnabled = feedbackFormSwitch.isChecked
        val feedbackFormUrl = feedbackFormUrlInput.text.toString()

        val skipOffsetInt: Int
        val videoSkipOffsetInt: Int
        val endCardCloseButtonDelayInt: Int

        if (skipOffset.isEmpty() || videoSkipOffset.isEmpty() || skipOffset.contains(" ") ||
            videoSkipOffset.contains(" ") || endCardCloseButtonDelay.isEmpty() || endCardCloseButtonDelay.contains(" ")) {
            Toast.makeText(context, "Please make sure skipOffset, videoSkipOffset and endCardCloseButtonDelay values are correct.", Toast.LENGTH_LONG).show()
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

        if(feedbackFormEnabled && feedbackFormUrl.isBlank()){
            Toast.makeText(context, "Feedback can not be enabled with blank url", Toast.LENGTH_LONG).show()
            return
        }

        settingManager.setInitialAudioState(getAudioStateInt(initialAudioState))
        settingManager.setMraidExpanded(mraidExpand)
        settingManager.setLocationTracking(locationTracking)
        settingManager.setLocationUpdates(locationUpdates)
        settingManager.setCloseVideoAfterFinish(autoCloseVideo)
        settingManager.setCloseVideoAfterFinishForRewardedVideo(autoCloseVideoRewarded)
        settingManager.setEnableEndcard(enableEndcard)
        settingManager.setSkipOffset(skipOffsetInt)
        settingManager.setVideoSkipOffset(videoSkipOffsetInt)
        settingManager.setEndCardCloseButtonDelay(endCardCloseButtonDelayInt)
        settingManager.setVideoClickBehaviour(getVideoClickBehaviourBoolean(videoClickBehaviour))
        settingManager.setFeedbackFormEnabled(feedbackFormEnabled)
        settingManager.setFeedbackFormUrl(feedbackFormUrl)


        HyBid.setVideoAudioStatus(initialAudioState)
        HyBid.setMraidExpandEnabled(mraidExpand)
        HyBid.setLocationTrackingEnabled(locationTracking)
        HyBid.setLocationUpdatesEnabled(locationUpdates)
        HyBid.setCloseVideoAfterFinish(autoCloseVideo)
        HyBid.setCloseVideoAfterFinishForRewarded(autoCloseVideoRewarded)
        HyBid.setEndCardEnabled(enableEndcard)
        HyBid.setAdFeedbackEnabled(feedbackFormEnabled)
        HyBid.setContentInfoUrl(feedbackFormUrl)
        HyBid.setHtmlInterstitialSkipOffset(skipOffsetInt)
        HyBid.setVideoInterstitialSkipOffset(videoSkipOffsetInt)
        HyBid.setEndCardCloseButtonDelay(endCardCloseButtonDelayInt)
        HyBid.setInterstitialClickBehaviour(videoClickBehaviour)
    }

    private fun getVideoClickBehaviourBoolean(interstitialActionBehaviour: InterstitialActionBehaviour) : Boolean {
        return interstitialActionBehaviour == InterstitialActionBehaviour.HB_CREATIVE
    }

    private fun getAudioStateInt(audioState: AudioState) : Int {
        return when (audioState) {
            AudioState.DEFAULT -> 0
            AudioState.ON -> 1
            AudioState.MUTED -> 2
        }
    }
}