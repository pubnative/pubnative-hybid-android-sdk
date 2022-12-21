package net.pubnative.lite.demo.ui.fragments.markup

import android.app.Application
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.AdRequestRegistry

class MarkupViewModel(application: Application) : AndroidViewModel(application) {
    private val ADM_MACRO = "{[{ .Adm | base64EncodeString | safeHTML }]}"

    private var customMarkup: MarkupType = MarkupType.CUSTOM_MARKUP
    private var customMarkupSize: MarkupSize = MarkupSize.BANNER
    private var urWrap: Boolean = false
    private var urTemplate: String = ""

    private val _clipboard: MutableLiveData<String> = MutableLiveData()
    val clipboard: LiveData<String> = _clipboard

    private val _loadInterstitial: MutableLiveData<String> = MutableLiveData()
    val loadInterstitial: LiveData<String> = _loadInterstitial

    private val _creativeId: MutableLiveData<String> = MutableLiveData()
    val creativeId: LiveData<String> = _creativeId

    private val _adapterUpdate: MutableLiveData<String> = MutableLiveData()
    val adapterUpdate: LiveData<String> = _adapterUpdate

    private val _listVisibillity: MutableLiveData<Boolean> = MutableLiveData()
    val listVisibillity: LiveData<Boolean> = _listVisibillity

    private val _creativeIdVisibillity: MutableLiveData<Boolean> = MutableLiveData()
    val creativeIdVisibillity: LiveData<Boolean> = _creativeIdVisibillity

    fun pasteFromClipboard() {
        val clipboardText =
            ClipboardUtils.copyFromClipboard(getApplication<Application>().applicationContext)
        if (!TextUtils.isEmpty(clipboardText)) {
            _clipboard.value = clipboardText
        }
    }

    fun setURWrap(wrapInUR: Boolean) {
        this.urWrap = wrapInUR
    }

    fun setURTemplate(urTemplate: String) {
        this.urTemplate = urTemplate
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
                override fun onSuccess(
                    response: String?, headers: MutableMap<String?, MutableList<String>?>?
                ) {
                    AdRequestRegistry.getInstance().setLastAdRequest(creativeURL, response, 0)
                    Log.d("onSuccess", response ?: "")
                    if (!headers.isNullOrEmpty() && headers.containsKey("Creative_id")) {
                        val headerValues = headers["Creative_id"]
                        if (!headerValues.isNullOrEmpty()) {
                            processMarkup(response, headerValues.first())
                        } else {
                            processMarkup(response)
                        }
                    } else {
                        processMarkup(response)
                    }
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

    private fun processMarkup(markup: String?, creativeId: String = "") {
        if (creativeId.isNotEmpty()) {
            _creativeIdVisibillity.value = true
            _creativeId.value = creativeId
        } else {
            _creativeIdVisibillity.value = false
            _creativeId.value = ""
        }

        if (markup?.isEmpty() == true) {
            Toast.makeText(
                getApplication<Application>().applicationContext,
                "Please input some markup",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val renderMarkup =
                if (urWrap && urTemplate.isNotEmpty()) wrapInUR(markup!!) else markup!!
            if (customMarkupSize == MarkupSize.INTERSTITIAL) {
                _loadInterstitial.value = renderMarkup
            } else {
                _adapterUpdate.value = renderMarkup
            }
        }
    }

    fun getMarkupSize(): MarkupSize {
        return customMarkupSize
    }

    private fun wrapInUR(adm: String): String {
        val encodedAdm = Base64.encodeToString(adm.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        return urTemplate.replace(ADM_MACRO, encodedAdm, false)
    }
}