package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.InterstitialActionBehaviour
import net.pubnative.lite.sdk.vpaid.enums.AudioState

class AdCustomizationFragment : Fragment()  {

    private lateinit var initialAudioGroup: RadioGroup
    private lateinit var mraidExpandSwitch: Switch
    private lateinit var locationTrackingSwitch: Switch
    private lateinit var locationUpdatesSwitch: Switch
    private lateinit var autocloseSwitch: Switch
    private lateinit var htmlSkipOffsetInput: EditText
    private lateinit var videoSkipOffsetInput: EditText
    private lateinit var clickBehaviourGroup: RadioGroup
    private lateinit var settingManager: SettingsManager



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(
        R.layout.fragment_ad_customization, container, false)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialAudioGroup = view.findViewById(R.id.group_initial_audio)
        mraidExpandSwitch = view.findViewById(R.id.check_mraid_expand)
        locationTrackingSwitch = view.findViewById(R.id.check_location_tracking)
        locationUpdatesSwitch = view.findViewById(R.id.check_location_updates)
        autocloseSwitch = view.findViewById(R.id.check_auto_close)
        htmlSkipOffsetInput = view.findViewById(R.id.input_skip_offset)
        videoSkipOffsetInput = view.findViewById(R.id.input_video_skip_offset)
        clickBehaviourGroup = view.findViewById(R.id.group_click_behaviour)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_settings).setOnClickListener {
            saveData()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()

        val selectedInitialAudio = when (settings.initialAudioState) {
            0 -> R.id.radio_sound_default
            1 -> R.id.radio_sound_on
            2 -> R.id.radio_sound_mute
            else -> {R.id.radio_sound_default}
        }
        initialAudioGroup.check(selectedInitialAudio)
        mraidExpandSwitch.isChecked = settings.mraidExpanded
        locationTrackingSwitch.isChecked = settings.locationTracking
        locationUpdatesSwitch.isChecked = settings.locationUpdates
        autocloseSwitch.isChecked = settings.closeVideoAfterFinish
        htmlSkipOffsetInput.setText(settings.skipOffset.toString())
        videoSkipOffsetInput.setText(settings.videoSkipOffset.toString())

        val selectedClickBehaviour = when (settings.videoClickBehaviour) {
            true -> R.id.radio_creative
            false -> R.id.radio_action_button
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
        val skipOffset = htmlSkipOffsetInput.text.toString()
        val videoSkipOffset = videoSkipOffsetInput.text.toString()
        val videoClickBehaviour = when (clickBehaviourGroup.checkedRadioButtonId) {
            R.id.radio_creative -> InterstitialActionBehaviour.HB_CREATIVE
            R.id.radio_action_button -> InterstitialActionBehaviour.HB_ACTION_BUTTON
            else -> InterstitialActionBehaviour.HB_CREATIVE
        }

        val skipOffsetInt : Int = skipOffset.toInt()
        val videoSkipOffsetInt : Int = videoSkipOffset.toInt()

        settingManager.setInitialAudioState(getAudioStateInt(initialAudioState))
        settingManager.setMraidExpanded(mraidExpand)
        settingManager.setLocationTracking(locationTracking)
        settingManager.setLocationUpdates(locationUpdates)
        settingManager.setCloseVideoAfterFinish(autoCloseVideo)
        settingManager.setSkipOffset(skipOffsetInt)
        settingManager.setVideoSkipOffset(videoSkipOffsetInt)
        settingManager.setVideoClickBehaviour(getVideoClickBehaviourBoolean(videoClickBehaviour))


        HyBid.setVideoAudioStatus(initialAudioState)
        HyBid.setMraidExpandEnabled(mraidExpand)
        HyBid.setLocationTrackingEnabled(locationTracking)
        HyBid.setLocationUpdatesEnabled(locationUpdates)
        HyBid.setCloseVideoAfterFinish(autoCloseVideo)
        HyBid.setHtmlInterstitialSkipOffset(skipOffsetInt)
        HyBid.setVideoInterstitialSkipOffset(videoSkipOffsetInt)
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