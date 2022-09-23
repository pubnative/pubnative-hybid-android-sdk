package net.pubnative.lite.demo.ui.fragments.markup

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.network.PNHttpClient

class MarkupViewModel(application: Application) : AndroidViewModel(application) {

    private var customMarkup: MarkupType = MarkupType.CUSTOM_MARKUP
    private var customMarkupSize: MarkupSize = MarkupSize.BANNER

    private val _clipboard: MutableLiveData<String> = MutableLiveData()
    val clipboard: LiveData<String> = _clipboard

    private val _loadInterstitial: MutableLiveData<String> = MutableLiveData()
    val loadInterstitial: LiveData<String> = _loadInterstitial

    private val _adapterUpdate: MutableLiveData<String> = MutableLiveData()
    val adapterUpdate: LiveData<String> = _adapterUpdate

    private val _listVisibillity: MutableLiveData<Boolean> = MutableLiveData()
    val listVisibillity: LiveData<Boolean> = _listVisibillity

    fun pasteFromClipboard() {
        val clipboardText =
            ClipboardUtils.copyFromClipboard(getApplication<Application>().applicationContext)
        if (!TextUtils.isEmpty(clipboardText)) {
            _clipboard.value = clipboardText
        }
    }

    fun setMarkupType(customMarkup: MarkupType) {
        this.customMarkup = customMarkup
    }

    fun setMarkupSize(markupSize: MarkupSize) {
        this.customMarkupSize = markupSize
        _listVisibillity.value = customMarkupSize != MarkupSize.INTERSTITIAL
    }

    fun loadMarkup(markupText: String) {
        when (customMarkup) {
            MarkupType.CUSTOM_MARKUP -> {
                processMarkup(markupText)
            }
            MarkupType.URL -> {
                loadCreativeUrl(markupText)
            }
        }
    }

    private fun loadCreativeUrl(creativeURL: String) {

        PNHttpClient.makeRequest(getApplication<Application>().applicationContext,
            creativeURL,
            null,
            null,
            true,
            object : PNHttpClient.Listener {
                override fun onSuccess(response: String) {
                    Log.d("onSuccess", response)
                    processMarkup(response)
                }

                override fun onFailure(error: Throwable) {
                    Log.d("onFailure", error.toString())
                    Toast.makeText(
                        getApplication<Application>().applicationContext,
                        "Creative request failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun processMarkup(markup: String) {
        if (TextUtils.isEmpty(markup)) {
            Toast.makeText(
                getApplication<Application>().applicationContext,
                "Please input some markup",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (customMarkupSize == MarkupSize.INTERSTITIAL) {
                _loadInterstitial.value = markup
            } else {
                _adapterUpdate.value = markup
            }
        }
    }

    fun getMarkupSize(): MarkupSize {
        return customMarkupSize
    }
}