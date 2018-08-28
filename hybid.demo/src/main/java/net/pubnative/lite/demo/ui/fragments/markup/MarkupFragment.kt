package net.pubnative.lite.demo.ui.fragments.markup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.ClipboardUtils

class MarkupFragment : Fragment() {
    private lateinit var markupInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var markupList: RecyclerView
    private var selectedSize: Int = R.id.radio_size_banner

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_markup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markupInput = view.findViewById(R.id.input_markup)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        markupList = view.findViewById(R.id.list_markup)

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            loadMarkup()
        }

        adSizeGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedSize = checkedId
            updateListVisibility()
        }
    }

    private fun updateListVisibility() {
        if (selectedSize == R.id.radio_size_banner || selectedSize == R.id.radio_size_medium) {
            markupList.visibility = View.VISIBLE
        } else {
            markupList.visibility = View.GONE
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(activity!!)
        if (!TextUtils.isEmpty(clipboardText)) {
            markupInput.setText(clipboardText)
        }
    }

    private fun loadMarkup() {
        val markup = markupInput.text.toString()
        if (TextUtils.isEmpty(markup)) {
            Toast.makeText(activity, "Please input some markup", Toast.LENGTH_SHORT).show()
        } else {

        }
    }
}