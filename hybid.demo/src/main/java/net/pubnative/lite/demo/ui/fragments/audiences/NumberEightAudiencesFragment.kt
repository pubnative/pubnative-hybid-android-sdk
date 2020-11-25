package net.pubnative.lite.demo.ui.fragments.audiences

import ai.numbereight.audiences.Audiences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R

class NumberEightAudiencesFragment : Fragment() {

    private lateinit var audiencesView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_numbereight_audiences, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audiencesView = view.findViewById(R.id.view_audiences)

        getAudiences()
    }

    fun getAudiences() {
        val audiences = Audiences.currentMemberships

        audiencesView.text = audiences.toString()
    }


}