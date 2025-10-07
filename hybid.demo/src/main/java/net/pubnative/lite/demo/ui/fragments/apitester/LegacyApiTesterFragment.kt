// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.apitester

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.AdCustomizationActivity
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.LegacyApiAdapter
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize.*
import net.pubnative.lite.demo.ui.fragments.markup.MarkupType
import net.pubnative.lite.demo.util.Destroyable
import net.pubnative.lite.demo.viewmodel.AdCustomizationViewModel
import net.pubnative.lite.demo.viewmodel.ApiTesterViewModel
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
import net.pubnative.lite.sdk.utils.Logger

class LegacyApiTesterFragment : Fragment(R.layout.fragment_legacy_api_tester),
    OnLogDisplayListener {

    private lateinit var viewModel: ApiTesterViewModel
    private lateinit var adCustomizationViewModel: AdCustomizationViewModel

    private lateinit var responseInput: EditText
    private lateinit var oRTBBodyInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var responseSourceGroup: RadioGroup
    private lateinit var enableAdCustomisationCheckbox: CheckBox
    private lateinit var markupList: RecyclerView
    private lateinit var loadButton: MaterialButton
    private lateinit var showButton: MaterialButton
    private lateinit var customizeButton: MaterialButton
    private lateinit var oRTBLayout: RelativeLayout
    private lateinit var adCustomizationLayout: LinearLayout

    private val adapter = LegacyApiAdapter(this)

    private var interstitial: HyBidInterstitialAd? = null
    private var rewardedAd: HyBidRewardedAd? = null

    private val TAG = LegacyApiTesterFragment::class.java.simpleName

    private var adCustomisationEnabled: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModels()
        initViews(view)
        initObservers()
        setListeners()
    }


    private fun loadInterstitial(ad: Ad?) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                displayLogs()
                showButton.isEnabled = true
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
                displayLogs()
                showButton.isEnabled = false
            }

            override fun onInterstitialImpression() {
                Logger.d(TAG, "onInterstitialImpression")
            }

            override fun onInterstitialClick() {
                Logger.d(TAG, "onInterstitialClick")
            }

            override fun onInterstitialDismissed() {
                Logger.d(TAG, "onInterstitialDismissed")
                showButton.isEnabled = false
            }
        }

        interstitial = HyBidInterstitialAd(requireActivity(), interstitialListener)
        interstitial?.prepareAd(ad)
    }

    private fun initViewModels() {
        viewModel = ViewModelProvider(this)[ApiTesterViewModel::class.java]
        adCustomizationViewModel = ViewModelProvider(this)[AdCustomizationViewModel::class.java]
    }

    private fun initViews(view: View) {
        responseInput = view.findViewById(R.id.input_response)
        oRTBBodyInput = view.findViewById(R.id.input_ortb_body)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        responseSourceGroup = view.findViewById(R.id.group_response_source)
        markupList = view.findViewById(R.id.list_markup)
        markupList.isNestedScrollingEnabled = false
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        oRTBLayout = view.findViewById(R.id.layout_ortb_body)
        adCustomizationLayout = view.findViewById(R.id.ad_customisation_layout)
        enableAdCustomisationCheckbox = view.findViewById(R.id.cb_enable_customization)
        customizeButton = view.findViewById(R.id.customize_button)

        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter

        responseInput.setText("{\n" +
                "  \"status\": \"ok\",\n" +
                "  \"ads\": [\n" +
                "    {\n" +
                "      \"link\": \"vrvdl://navigate?deeplinkUrl=lazada%3A%2F%2Fid%2Fweb%2Fwww%2Fmarketing%2Fgateway%2Findex.html%3Fnull%26dsource%3Dsml%26exlaz%3De_VstumeukfBDGip8qo24MCZxWHrQEPErDLVXf0F6NCPjCXvTKdNAtBYJmlT48KeoRp83TTGt9l6hbTkAZBDtR5Jegp%25252BSr4Ou1D3WnASIas1Y%25253D%26rta_token%3D609c5528-47b1-4ddf-ac93-ddb67c28059e%26rta_event_id%3D0b85895816376598112263997%26os%3DAndroid%26gps_adid%3D083d2639-b0c1-48b9-9d32-8637f4f5701f%26idfa%3D__idfa__%26idfv%3D__idfv__%26bundle_id%3Dcom.tempo.video.edit%26device_model%3DCPH2185%26device_make%3DOPPO%26ip%3D__ip__%26sub_id1%3D110000000%26sub_id2%3DUDffqymfz2m3su3jRz6AI3%26sub_id3%3D10000003&fallbackUrl=https%3A%2F%2Fc.lazada.co.id%2Ft%2Fc.0ym6jY%3Frta_token%3D8363d79b-5e2e-4388-95b5-28b11804ad56%26rta_event_id%3D2101483316635640206655934%26os%3DAndroid%26gps_adid%3D81f0f170-5860-4a18-b11c-4e457da2604b%26imei%3D__imei__%26android_id%3D__android_id__%26idfa%3D__idfa__%26idfv%3D__idfv__%26bundle_id%3Dcom.yydlsggj.moneyquiz%26device_model%3Dv2149%26device_make%3Dvivo%26sub_id1%3D102%26sub_id2%3D2e50854e5bcf482cbfcbf054f6626578_2022091905_1005%26sub_id3%3D1005%26sub_id4%3D__sub_id4__%26sub_id5%3D__sub_id5__\",\n" +
                "      \"assetgroupid\": 15,\n" +
                "      \"assets\": [\n" +
                "        {\n" +
                "          \"type\": \"vast2\",\n" +
                "          \"data\": {\n" +
                "            \"vast2\": \"<VAST version=\\\"2.0\\\"><Ad id=\\\"223626102\\\"><InLine><AdTitle>Verve Group Mobile Video</AdTitle><Description>Advanced Mobile Monetization</Description><Error><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&err=[ERRORCODE]&et=error&pub_app_id=HxX29Nj62vlNWW5mch-UYgq7x3JxoVU]]></Error><AdSystem version=\\\"1.0\\\">PubNative</AdSystem><Impression><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/iurl?app_id=1036637&beacon=vast1&p=0.03475]]></Impression><Impression><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp//v1/tracker/iurl?app_id=1036637&beacon=vast2&p=0.03475]]></Impression><Creatives><Creative sequence=\\\"1\\\"><CompanionAds><Companion width=\\\"320\\\" height=\\\"480\\\"><HTMLResource><![CDATA[<script src=\\\"mraid.js\\\"></script><style id=\\\"stl\\\">html,body,#ctr,#bgblr{margin:0;padding:0;width:100%!;(string=v2)height:100%!;(MISSING)overflow:hidden;border:none;position:absolute;top:0;left:0;}body{background:black;}</style><div style=\\\"display:none\\\" id=\\\"adm\\\">PGEgdGFyZ2V0PSJfYmxhbmsiIGhyZWY9Imh0dHBzOi8vcGxheS5nb29nbGUuY29tL3N0b3JlL2FwcHMvZGV0YWlscz9pZD1uZXQucHVibmF0aXZlLmVhc3lzdGVwcyI+PGltZyBzcmM9Imh0dHBzOi8vY2RuLnB1Ym5hdGl2ZS5uZXQvd2lkZ2V0L3YzL2Fzc2V0cy92Ml8zMDB4MjUwLmpwZyIgd2lkdGg9IjMwMCIgaGVpZ2h0PSIyNTAiIGJvcmRlcj0iMCIgYWx0PSJBZHZlcnRpc2VtZW50IiAvPjwvYT4</div><div id=\\\"bgblr\\\" style=\\\"z-index:0;background:center/cover;-webkit-filter:saturate(0.5) blur(15px);filter:saturate(0.5) blur(15px);\\\"></div><iframe id=\\\"ctr\\\" frameborder=\\\"0\\\" scrolling=\\\"no\\\" width=\\\"100%!\\\"(MISSING) height=\\\"100%!\\\"(MISSING)></iframe><script type=\\\"text/javascript\\\">(function(){\\nfunction MRAID(){function k(b){try{if(\\\"object\\\"===typeof b.mraid&&b.mraid.getState)var g=b.mraid}catch(l){}return b.parent!==b?k(b.parent)||g:g}var h=window,e=k(h)||{ntfnd:!0},c=\\\"{offsetX:0,offsetY:0,x:0,y:0,width:\\\"+h.innerWidth+\\\",height:\\\"+h.innerHeight+\\\",useCustomClose:!1}\\\",L=\\\"addEventListener\\\",V=\\\"isViewable\\\",d=this,f={removeEventListener:0,open:\\\"window.top.open(a)\\\",close:0,unload:0,useCustomClose:0,expand:0,playVideo:0,resize:0,storePicture:0,createCalendarEvent:0,supports:\\\"{sms:!1,tel:!1,calendar:!1,storePicture:!1,inlineVideo:!1,orientation:!1,vpaid:!1,location:!1}\\\",\\nVERSIONS:{},STATES:{LOADING:\\\"loading\\\",DEFAULT:\\\"default\\\"},PLACEMENTS:{},ORIENTATIONS:{},FEATURES:{},EVENTS:{READY:\\\"ready\\\",ERROR:\\\"error\\\"},CLOSEPOSITIONS:{}};c={Version:'\\\"2.0\\\"',PlacementType:'\\\"unknown\\\"',OrientationProperties:\\\"{allowOrientationChange:!1}\\\",CurrentAppOrientation:'{orientation:\\\"\\\"}',CurrentPosition:c,DefaultPosition:c,State:'\\\"default\\\"',ExpandProperties:c,MaxSize:c,ScreenSize:c,ResizeProperties:c,Location:\\\"{}\\\"};d._L=[];d[L]=function(b,g){\\\"ready\\\"===b||\\\"viewableChange\\\"===b?d._L.push({c:g,a:!0}):\\n\\\"stateChange\\\"===b&&d._L.push({c:g,a:\\\"default\\\"});e[L]&&e[L].apply(h,arguments)};for(var a in c)f[\\\"get\\\"+a]=c[a],f[\\\"set\\\"+a]=\\\"undefined\\\";for(a in f)e[a]?d[a]=e[a]:(d[a]=f[a]?\\\"object\\\"===typeof f[a]?f[a]:new Function(\\\"return \\\"+f[a]):function(){},e[a]=d[a]);d[V]=function(){return!!e.ntfnd||e[V]()===true||e[V]()===\\\"true\\\"};d.getState=function(){return e.ntfnd||d[V]()&&e.getState()===\\\"loading\\\"?\\\"default\\\":e.getState()};return e.ntfnd?(setTimeout(function(){d._L.forEach(function(b){b.c.call(window,b.a)})},1),d.ntfnd=!0,d):e}\\nfunction P(a){return decodeURIComponent(escape(atob(a)))}var D=document,PC=!0,CR=!1,Q=\\\"querySelector\\\",E=\\\"replace\\\",M=\\\"mraid\\\",T=\\\"addEventListener\\\",ctr=D[Q](\\\"#ctr\\\"),A=P(D[Q](\\\"#adm\\\").innerText),O=JSON.parse(P(\\\"eyJwdWJBcHBJRCI6MTAzNjYzNywicHViSUQiOjEwMDYxOTEsImRzcElEIjo2NCwiZHNwTmFtZSI6IiIsImltcElEIjoiM2FhYjRhYTctNDZjMS00YjgyLTk3N2EtZGJlYzJiMDY5ZGVhIiwiY3JpZCI6InRlc3RfY3JlYXRpdmUiLCJwdWJOYW1lIjoiIiwiaXNNUkFJRCI6ZmFsc2UsImlzSFRNTDUiOmZhbHNlfQ\\\")),F=!1,CF=!1,L=\\\"<\\\",R=[[P(\\\"PHNjcmlwdFtePl0qIHNyYz1bJyJdP21yYWlkLmpzWyciXT9bXj5dKj48L3NjcmlwdD4=\\\"),\\\"\\\"],[P(\\\"W+KAmOKAmV0=\\\"),\\\"'\\\"]],B=[\\\"aHR0cHM6Ly9iYWNrZW5kLmV1cm9wZS13ZXN0NGdjcDAucHVibmF0aXZlLm5ldC9tb2NrZHNwL3YxL3RyYWNrZXIvbnVybD9hcHBfaWQ9MTAzNjYzNyZwPTAuMDMzMjIyMjIy\\\"],CB=[],i;for(i in B)B[i]=P(B[i])[E](P(\\\"JHtBVUNUSU9OX1BSSUNFfQ==\\\"),\\\"0.03475\\\");for(i in CB)CB[i]=P(CB[i]);\\nfunction RN(){if(ctr&&A){window[M]=new MRAID;for(r in R)A=A[E](new RegExp(R[r][0],\\\"g\\\"),R[r][1]);A=L+\\\"style>html,body{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;}\\\"+L+\\\"/style>\\\"+L+'script type=\\\"text/javascript\\\">'+MRAID.toString()+\\\"window.mraid=new MRAID();\\\"+L+\\\"/script>\\\"+A;if(O.aqh&&O.aqf){var a=function(b){return(b||\\\"\\\")[E](P(\\\"JWN1c3RfaW1wIQ==\\\"),O.impID)[E](P(\\\"JURFTUFORF9JRCE=\\\"),O.dspID)[E](P(\\\"JWRzcE5hbWUh\\\"),O.dspName)[E](P(\\\"JURFTUFORF9DUkVBVElWRV9JRCE=\\\"),O.crid)[E](P(\\\"JVBVQkxJU0hFUl9JRCE=\\\"),\\nO.pubID)[E](P(\\\"JXB1Yk5hbWUh\\\"),O.pubName)[E](P(\\\"JSVXSURUSCUl\\\"),(ctr.contentWindow||window).innerWidth)[E](P(\\\"JSVIRUlHSFQlJQ==\\\"),(ctr.contentWindow||window).innerHeight)};O.aqh=a(O.aqh);O.aqf=a(O.aqf);A=O.aqh+A+O.aqf}A=L+\\\"!DOCTYPE html>\\\\n\\\"+A;window[M][T](\\\"ready\\\",MR);window[M][T](\\\"viewableChange\\\",MV);CR&&FA();MV();PC&&setTimeout(EX,1)}}function FA(){if(!F){for(var i in B)(new Image).src=B[i];F=!0}}function MR(){window[M][T](\\\"viewableChange\\\",MV);MV()}\\nfunction MV(){if(\\\"true\\\"===window[M].isViewable()||!0===window[M].isViewable()){CR||FA();PC||setTimeout(EX,1);setInterval(FS,100)}}\\nfunction FS(){var a=ctr.contentWindow;if(a){var b=window.innerWidth,h=window.innerHeight,g=ctr.style,k=!1,l=10,m=10,e=a.document.querySelectorAll(\\\"body,div,span,p,section,article,a,img,canvas,video,iframe\\\");for(a=0;a<e.length;a++){var c=e[a].offsetWidth;var d=e[a].offsetHeight;if((c===320&&d===480||c===480&&d===320||c===300&&d===250||(c===300||c===320)&&d===50&&h===50&&c!==b)&&!k){k=!0;var f=d*b/c>h?h/d:b/c;g.width=c+\\\"px\\\";g.height=d+\\\"px\\\";g.transform=\\\"scale(\\\"+f+\\\",\\\"+f+\\\")\\\";d*b/\\nc>h?(g.top=(f-1)*d/2+\\\"px\\\",g.left=(b-c)/2+\\\"px\\\"):(g.top=(h-d)/2+\\\"px\\\",g.left=(f-1)*c/2+\\\"px\\\")}f=(e[a].style.backgroundImage||\\\"\\\").match(P(\\\"XnVybFwoIihodHRwLispIlwpJA==\\\"));if(k&&(\\\"IMG\\\"===e[a].nodeName&&e[a].src||f&&f[1])&&c>l&&d>m){l=c;m=d;var n=e[a].src?e[a].src:f[1]}}n&&(document[Q](\\\"#bgblr\\\").style.backgroundImage=\\\"url(\\\"+n+\\\")\\\")}}\\nfunction EX(){if(A){try{var a=ctr.contentWindow,b=a.document;b.open();a[T](\\\"click\\\",function(e){if(!CF){for(var i in CB)(new Image).src=CB[i];CF=true}},true);a[T](\\\"load\\\",FS);b.write(A);b.close()}catch(h){}A=\\\"\\\"}}RN();\\n})();</script>]]></HTMLResource><CompanionClickThrough><![CDATA[https://verve.com]]></CompanionClickThrough><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=7Q8QwUnjUxhP8FDi9Jp8aSxc6mkf9YiBeN6saRHzSmEoiFt2bSOQC82rUDQ27O_ULojkHj1lPWB9DuShI-N-JA-NHEjcdQUDTjyTZCIDlUB6HzY6oNHWHH0KAEbFG8ahjImk6WsXEe-Jiqh4sGcVU6XqjlVdRmg]]></CompanionClickTracking><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=TR8L74euNvkMzUgb62Cq-Oipron24VK8IACIggLKR-CjlPwbko66hCDiJ71UyH_ieAe8fzdbkTlGcZ9MWLZJza9_ekSMLbwlKmo8tEkd0-z3y6BvIBNU2QRMH87QwGQJG51q3YBCDUCD5kRELwSuz3S_Dlo7pQv4_tU89Xmulrz3Kx020fXIsYVOM-N9CBPbsL9PYF7OVzCZ5VEdAgYFfQcwdOIiwrFIAUquz_GnUru834OIPrKqNF06yahlrUKoqdfYgnn3QsmI5ME4Ntyxlzbtdsfw7WkQcWovvw9i97xIHuSjCPUNz2MxiCsEbeQHoFamvs4lhw4zdWZdgLCtebMLZ8PdjkY3iAQttsPYdw8NRFZOna84xd5wioJRD61VD1aM0I_Py5_9V4SGAx9lZr-n2rZNIjfqPFdVW-Lxg10uz3BFA8DzfhMEq00REl0bKbQkqPExrc02uyszqXnwR_sLg72WZsW9HPZ0lck7Df6pM6AbiDJKo5hzq3LKoaiL5ghZQ1NI5rw_Cy91qdVfk4MS3J7UYRgDsEgsTTz5Ia0FJzsZYszUYfeFO05oDhd8h8uA_Bi4eaTMgxxnUJNJZn-93p3xx_0pjQRfeMkDf5x44K_o58lyHq_uRFjLSmxTAmPjGBKtll5MnkFwbthGAI7SHLbMaBwETFoy4HikHKvmpeT8vNaKdufNzYAfj_gaEcwNqZdtE5e__LYse7KCooZsQEmQE7v5hwhrClxsCnZ5g4UFaeJJF4Zz0lkn94ldVyG2bEaNGFSVGEIRUT6hgF6kraIGW9ZozZA7gOS92cqPPOqWVfwMUTN375Xc7I1Kfb8oj4ltKPpXWTbV1NEL-1RB3G4-lIUV5hSKgOV-snYWyFi6A6i3m2bIW_ne0NzhmSy89ttWN1T6AYfQEpyFbMc-glhIP8BOKDUim_Gw9JLvxeOxS4rJiOOJ9iC3gPVZ-mveVvg554PZ_9v88KwVjFdrLEspaoTrNethYUHn0jJvWMDwchLXbezNcwSnNDLr279VmZOeK9CWYDBJkTNhr6i7Cqmo0D-SXGSWI3n3nT8O9y72_MOSAlSSO28zT0dc-_tpcaIrVtI0cIl94S_kOBpfTIE9MJgCq08ViCxFHTNQ6-jtsneLDCN69uIxDueEx-4lv0M6AHFeu2dtrFewOtveNM8YC5z3NZ6EUnwbejBEWOUz6OUhyN0kT0MPorex8izuX-CHMqXtdp4QTY9-6ScVpFNfuyV2quK39NKgXg5RxTNDir4rs-n4I4zUrxAHPKiacfssJur05fF2X-wA0_3Ez6lOpW7aGXaswiPR_0AkHpS2ZmtO5Nf4UN74eaXduo_xgdymmCe8c6IMfBVg5JGE9zmZMdyaiz6lHwR3XCBlKBiQsR3Myw8yJ637L6wnX_lIGNPe]]></CompanionClickTracking><TrackingEvents><Tracking event=\\\"creativeView\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/companionView]]></Tracking><Tracking event=\\\"creativeView\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=0qnd9aM6pkMn0Yi5jnrB_y0fBS-MZnxadz03e5CZVXIYIkmQqRvKNCFah5gySm_BLftacLgap0EYvTjrtexG5sy3EpJXwcmx45CY1iWEDvKhXD9oVprLwK9fw-bzqcGqU7gGEF5dgWhNRf-wCn1dzSnwYc98JSk]]></Tracking></TrackingEvents></Companion><Companion width=\\\"480\\\" height=\\\"320\\\"><HTMLResource><![CDATA[<script src=\\\"mraid.js\\\"></script><style id=\\\"stl\\\">html,body,#ctr,#bgblr{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;position:absolute;top:0;left:0;}body{background:black;}</style><div style=\\\"display:none\\\" id=\\\"adm\\\">PGEgdGFyZ2V0PSJfYmxhbmsiIGhyZWY9Imh0dHBzOi8vcGxheS5nb29nbGUuY29tL3N0b3JlL2FwcHMvZGV0YWlscz9pZD1uZXQucHVibmF0aXZlLmVhc3lzdGVwcyI+PGltZyBzcmM9Imh0dHBzOi8vY2RuLnB1Ym5hdGl2ZS5uZXQvd2lkZ2V0L3YzL2Fzc2V0cy92Ml8zMDB4MjUwLmpwZyIgd2lkdGg9IjMwMCIgaGVpZ2h0PSIyNTAiIGJvcmRlcj0iMCIgYWx0PSJBZHZlcnRpc2VtZW50IiAvPjwvYT4</div><div id=\\\"bgblr\\\" style=\\\"z-index:0;background:center/cover;-webkit-filter:saturate(0.5) blur(15px);filter:saturate(0.5) blur(15px);\\\"></div><iframe id=\\\"ctr\\\" frameborder=\\\"0\\\" scrolling=\\\"no\\\" width=\\\"100%!\\\"(MISSING) height=\\\"100%!\\\"(MISSING)></iframe><script type=\\\"text/javascript\\\">(function(){\\nfunction MRAID(){function k(b){try{if(\\\"object\\\"===typeof b.mraid&&b.mraid.getState)var g=b.mraid}catch(l){}return b.parent!==b?k(b.parent)||g:g}var h=window,e=k(h)||{ntfnd:!0},c=\\\"{offsetX:0,offsetY:0,x:0,y:0,width:\\\"+h.innerWidth+\\\",height:\\\"+h.innerHeight+\\\",useCustomClose:!1}\\\",L=\\\"addEventListener\\\",V=\\\"isViewable\\\",d=this,f={removeEventListener:0,open:\\\"window.top.open(a)\\\",close:0,unload:0,useCustomClose:0,expand:0,playVideo:0,resize:0,storePicture:0,createCalendarEvent:0,supports:\\\"{sms:!1,tel:!1,calendar:!1,storePicture:!1,inlineVideo:!1,orientation:!1,vpaid:!1,location:!1}\\\",\\nVERSIONS:{},STATES:{LOADING:\\\"loading\\\",DEFAULT:\\\"default\\\"},PLACEMENTS:{},ORIENTATIONS:{},FEATURES:{},EVENTS:{READY:\\\"ready\\\",ERROR:\\\"error\\\"},CLOSEPOSITIONS:{}};c={Version:'\\\"2.0\\\"',PlacementType:'\\\"unknown\\\"',OrientationProperties:\\\"{allowOrientationChange:!1}\\\",CurrentAppOrientation:'{orientation:\\\"\\\"}',CurrentPosition:c,DefaultPosition:c,State:'\\\"default\\\"',ExpandProperties:c,MaxSize:c,ScreenSize:c,ResizeProperties:c,Location:\\\"{}\\\"};d._L=[];d[L]=function(b,g){\\\"ready\\\"===b||\\\"viewableChange\\\"===b?d._L.push({c:g,a:!0}):\\n\\\"stateChange\\\"===b&&d._L.push({c:g,a:\\\"default\\\"});e[L]&&e[L].apply(h,arguments)};for(var a in c)f[\\\"get\\\"+a]=c[a],f[\\\"set\\\"+a]=\\\"undefined\\\";for(a in f)e[a]?d[a]=e[a]:(d[a]=f[a]?\\\"object\\\"===typeof f[a]?f[a]:new Function(\\\"return \\\"+f[a]):function(){},e[a]=d[a]);d[V]=function(){return!!e.ntfnd||e[V]()===true||e[V]()===\\\"true\\\"};d.getState=function(){return e.ntfnd||d[V]()&&e.getState()===\\\"loading\\\"?\\\"default\\\":e.getState()};return e.ntfnd?(setTimeout(function(){d._L.forEach(function(b){b.c.call(window,b.a)})},1),d.ntfnd=!0,d):e}\\nfunction P(a){return decodeURIComponent(escape(atob(a)))}var D=document,PC=!0,CR=!1,Q=\\\"querySelector\\\",E=\\\"replace\\\",M=\\\"mraid\\\",T=\\\"addEventListener\\\",ctr=D[Q](\\\"#ctr\\\"),A=P(D[Q](\\\"#adm\\\").innerText),O=JSON.parse(P(\\\"eyJwdWJBcHBJRCI6MTAzNjYzNywicHViSUQiOjEwMDYxOTEsImRzcElEIjo2NCwiZHNwTmFtZSI6IiIsImltcElEIjoiM2FhYjRhYTctNDZjMS00YjgyLTk3N2EtZGJlYzJiMDY5ZGVhIiwiY3JpZCI6InRlc3RfY3JlYXRpdmUiLCJwdWJOYW1lIjoiIiwiaXNNUkFJRCI6ZmFsc2UsImlzSFRNTDUiOmZhbHNlfQ\\\")),F=!1,CF=!1,L=\\\"<\\\",R=[[P(\\\"PHNjcmlwdFtePl0qIHNyYz1bJyJdP21yYWlkLmpzWyciXT9bXj5dKj48L3NjcmlwdD4=\\\"),\\\"\\\"],[P(\\\"W+KAmOKAmV0=\\\"),\\\"'\\\"]],B=[\\\"aHR0cHM6Ly9iYWNrZW5kLmV1cm9wZS13ZXN0NGdjcDAucHVibmF0aXZlLm5ldC9tb2NrZHNwL3YxL3RyYWNrZXIvbnVybD9hcHBfaWQ9MTAzNjYzNyZwPTAuMDMzMjIyMjIy\\\"],CB=[],i;for(i in B)B[i]=P(B[i])[E](P(\\\"JHtBVUNUSU9OX1BSSUNFfQ==\\\"),\\\"0.03475\\\");for(i in CB)CB[i]=P(CB[i]);\\nfunction RN(){if(ctr&&A){window[M]=new MRAID;for(r in R)A=A[E](new RegExp(R[r][0],\\\"g\\\"),R[r][1]);A=L+\\\"style>html,body{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;}\\\"+L+\\\"/style>\\\"+L+'script type=\\\"text/javascript\\\">'+MRAID.toString()+\\\"window.mraid=new MRAID();\\\"+L+\\\"/script>\\\"+A;if(O.aqh&&O.aqf){var a=function(b){return(b||\\\"\\\")[E](P(\\\"JWN1c3RfaW1wIQ==\\\"),O.impID)[E](P(\\\"JURFTUFORF9JRCE=\\\"),O.dspID)[E](P(\\\"JWRzcE5hbWUh\\\"),O.dspName)[E](P(\\\"JURFTUFORF9DUkVBVElWRV9JRCE=\\\"),O.crid)[E](P(\\\"JVBVQkxJU0hFUl9JRCE=\\\"),\\nO.pubID)[E](P(\\\"JXB1Yk5hbWUh\\\"),O.pubName)[E](P(\\\"JSVXSURUSCUl\\\"),(ctr.contentWindow||window).innerWidth)[E](P(\\\"JSVIRUlHSFQlJQ==\\\"),(ctr.contentWindow||window).innerHeight)};O.aqh=a(O.aqh);O.aqf=a(O.aqf);A=O.aqh+A+O.aqf}A=L+\\\"!DOCTYPE html>\\\\n\\\"+A;window[M][T](\\\"ready\\\",MR);window[M][T](\\\"viewableChange\\\",MV);CR&&FA();MV();PC&&setTimeout(EX,1)}}function FA(){if(!F){for(var i in B)(new Image).src=B[i];F=!0}}function MR(){window[M][T](\\\"viewableChange\\\",MV);MV()}\\nfunction MV(){if(\\\"true\\\"===window[M].isViewable()||!0===window[M].isViewable()){CR||FA();PC||setTimeout(EX,1);setInterval(FS,100)}}\\nfunction FS(){var a=ctr.contentWindow;if(a){var b=window.innerWidth,h=window.innerHeight,g=ctr.style,k=!1,l=10,m=10,e=a.document.querySelectorAll(\\\"body,div,span,p,section,article,a,img,canvas,video,iframe\\\");for(a=0;a<e.length;a++){var c=e[a].offsetWidth;var d=e[a].offsetHeight;if((c===320&&d===480||c===480&&d===320||c===300&&d===250||(c===300||c===320)&&d===50&&h===50&&c!==b)&&!k){k=!0;var f=d*b/c>h?h/d:b/c;g.width=c+\\\"px\\\";g.height=d+\\\"px\\\";g.transform=\\\"scale(\\\"+f+\\\",\\\"+f+\\\")\\\";d*b/\\nc>h?(g.top=(f-1)*d/2+\\\"px\\\",g.left=(b-c)/2+\\\"px\\\"):(g.top=(h-d)/2+\\\"px\\\",g.left=(f-1)*c/2+\\\"px\\\")}f=(e[a].style.backgroundImage||\\\"\\\").match(P(\\\"XnVybFwoIihodHRwLispIlwpJA==\\\"));if(k&&(\\\"IMG\\\"===e[a].nodeName&&e[a].src||f&&f[1])&&c>l&&d>m){l=c;m=d;var n=e[a].src?e[a].src:f[1]}}n&&(document[Q](\\\"#bgblr\\\").style.backgroundImage=\\\"url(\\\"+n+\\\")\\\")}}\\nfunction EX(){if(A){try{var a=ctr.contentWindow,b=a.document;b.open();a[T](\\\"click\\\",function(e){if(!CF){for(var i in CB)(new Image).src=CB[i];CF=true}},true);a[T](\\\"load\\\",FS);b.write(A);b.close()}catch(h){}A=\\\"\\\"}}RN();\\n})();</script>]]></HTMLResource><CompanionClickThrough><![CDATA[https://verve.com]]></CompanionClickThrough><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=BcDkq1Z_P5PNQqUbcnmv30uDC2gs0v2rg8mZUjbO724VSlIk0Z6TjdXJrbndn9Sb3jJFCwxNxe0yEvQs7414Ddd4YPrnIgOq1itV93wFiVyFvFVJN1osQhPrhbtEG5AEfqToWkUcfBFcc02drHU1VkH2jSuYf0I]]></CompanionClickTracking><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=oFYakWE5M_7SE8r6qCwSs_-I2X3BdNQU5Nw2AREPgT5aG3iDcGjA6-e8dhzQTL8xpEytaPQf-iDlYqMx5ubk6JkPHJwt4eAYRNmER8Jz_FX4Svt1PUdhcDMtxwKYC5tEjq3G7d55uhCSWctRqNAg8sfnMbd4bkJoZPazZ9EsvtZLNZMPxTPxSZnChv_sJEDHD1GHIt7_WXbpX7mZKbUJjuVwfx4FmmSxzLUIU-DFYltRFePwEKOJmTIvtgpip-V2NwF9xUHVOcDdYpTUjll2jqY-WIedINhJbmchuUeWH4zyCUXbkmwQ0ErrJruCy9VWuye7zsBSDST2dCtCfO0AjtGmYi5XyUkouTQTZUH4a2HbCWuNFc_nOwqCM2WIldvRQZrz3mcgTYZfbvLwa4WfdTc6thgS3lML6pujU2eOEFUroGD6hW0-JDsaY9JwTix-6c71H_5wKKI_8BcbfVrS4g6bZWzihCzkwyMRhmFsbdoQuwkmr5LUZd_NxQsoTOZEIk1k3kvFWcUuGYh3qClYmI_Lu2oNv3bYLbWxJxFV3dqB7GP549nA4ryujkNRHdh8WdrE2Xa_0BzG8m_uAkG2ZMSqD_1p4K08MJYu5e-IziPfaIAqXyjeMskmiVFSiOmkp2lY-EhxyqGDu4wo0ovyegCJcdVT3eWejudSDt9mHkW_Lg06v-3P5f7AJFU5zpQpgBik8BOrMCvW2rLv2Y1yTBf0E4iLJduuxiq8qhqBYdNw6JZ_WMMmFEThLVquKDdvEQa9kBpjoPQWnVuZuPu9nkVT4UlW6MZtSDpsZPTFQIebfoqv7dr6a6HJsSQU1Qh6-gN6azTrttVEGO0m5EnIuInYXI8uGJhBkQXcRC3iVKfkHkpjXbagT-10yk2tiadcI1mCy7hRwPzLOIfdJ_6C6mex4KVSKKG2En7gDG93gz-9YqWRhJMsmbDSpVjZSEco9EknV3x6e4w26oW_waV0zkklgEzIRQOfMYXOFxnqEqwjSd6e0OciNedMQN6QohIUDZa1It1TPpl-lKmL-mPebrWWQpq3B3HqJ_mMW7fuGxXMpOP3AQ72wQ6qfQ9EBqXugz4aVklFWXgFVbgkbAr3FUHUJT9ZzkiZY60nCjCqCUn9jitfCSoMxQ2cAE-qLVVYZde4ZRqLZvkWjE9ef2FrZo9npeJIrCRpxmjULYLjtKEtoBy02qryAVy77TRl_2qaBBO3lSA27rhF92HWGE7PH83uPPbkRPSSl0ibFwqWugFEvlww2t1G0eyOatmZnVklXGDvHTSsE0V1YUYE8HCYNe7WP2Biq5uz7xxqvNm83rUl1pos3C85S-M_iDs2dFOd8Yt2DW-EVGffhvu-IirJeGgm_ixRTHWavJqjuQKw4rnDSmj4EFG3yZnOhbaUrF0YiYJKL36T]]></CompanionClickTracking><TrackingEvents><Tracking event=\\\"creativeView\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/companionView]]></Tracking><Tracking event=\\\"creativeView\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=PyCZ3YsC_2KG_8DabDcyPTb1g3B1tSBG3_OQZxJRVH4sw-W7mvDQPcK8Iku4_4ROwsdYrOOEGxcT_kPyCnjNCbZ3uYG4P9LYnPvuMWpomV0r2l4XfNelOsvpVleVg9y8Cy0u46pHTcCog3mr7owSkNe5w4llj7g]]></Tracking></TrackingEvents></Companion><Companion width=\\\"768\\\" height=\\\"1024\\\"><HTMLResource><![CDATA[<script src=\\\"mraid.js\\\"></script><style id=\\\"stl\\\">html,body,#ctr,#bgblr{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;position:absolute;top:0;left:0;}body{background:black;}</style><div style=\\\"display:none\\\" id=\\\"adm\\\">PGEgdGFyZ2V0PSJfYmxhbmsiIGhyZWY9Imh0dHBzOi8vcGxheS5nb29nbGUuY29tL3N0b3JlL2FwcHMvZGV0YWlscz9pZD1uZXQucHVibmF0aXZlLmVhc3lzdGVwcyI+PGltZyBzcmM9Imh0dHBzOi8vY2RuLnB1Ym5hdGl2ZS5uZXQvd2lkZ2V0L3YzL2Fzc2V0cy92Ml8zMDB4MjUwLmpwZyIgd2lkdGg9IjMwMCIgaGVpZ2h0PSIyNTAiIGJvcmRlcj0iMCIgYWx0PSJBZHZlcnRpc2VtZW50IiAvPjwvYT4</div><div id=\\\"bgblr\\\" style=\\\"z-index:0;background:center/cover;-webkit-filter:saturate(0.5) blur(15px);filter:saturate(0.5) blur(15px);\\\"></div><iframe id=\\\"ctr\\\" frameborder=\\\"0\\\" scrolling=\\\"no\\\" width=\\\"100%!\\\"(MISSING) height=\\\"100%!\\\"(MISSING)></iframe><script type=\\\"text/javascript\\\">(function(){\\nfunction MRAID(){function k(b){try{if(\\\"object\\\"===typeof b.mraid&&b.mraid.getState)var g=b.mraid}catch(l){}return b.parent!==b?k(b.parent)||g:g}var h=window,e=k(h)||{ntfnd:!0},c=\\\"{offsetX:0,offsetY:0,x:0,y:0,width:\\\"+h.innerWidth+\\\",height:\\\"+h.innerHeight+\\\",useCustomClose:!1}\\\",L=\\\"addEventListener\\\",V=\\\"isViewable\\\",d=this,f={removeEventListener:0,open:\\\"window.top.open(a)\\\",close:0,unload:0,useCustomClose:0,expand:0,playVideo:0,resize:0,storePicture:0,createCalendarEvent:0,supports:\\\"{sms:!1,tel:!1,calendar:!1,storePicture:!1,inlineVideo:!1,orientation:!1,vpaid:!1,location:!1}\\\",\\nVERSIONS:{},STATES:{LOADING:\\\"loading\\\",DEFAULT:\\\"default\\\"},PLACEMENTS:{},ORIENTATIONS:{},FEATURES:{},EVENTS:{READY:\\\"ready\\\",ERROR:\\\"error\\\"},CLOSEPOSITIONS:{}};c={Version:'\\\"2.0\\\"',PlacementType:'\\\"unknown\\\"',OrientationProperties:\\\"{allowOrientationChange:!1}\\\",CurrentAppOrientation:'{orientation:\\\"\\\"}',CurrentPosition:c,DefaultPosition:c,State:'\\\"default\\\"',ExpandProperties:c,MaxSize:c,ScreenSize:c,ResizeProperties:c,Location:\\\"{}\\\"};d._L=[];d[L]=function(b,g){\\\"ready\\\"===b||\\\"viewableChange\\\"===b?d._L.push({c:g,a:!0}):\\n\\\"stateChange\\\"===b&&d._L.push({c:g,a:\\\"default\\\"});e[L]&&e[L].apply(h,arguments)};for(var a in c)f[\\\"get\\\"+a]=c[a],f[\\\"set\\\"+a]=\\\"undefined\\\";for(a in f)e[a]?d[a]=e[a]:(d[a]=f[a]?\\\"object\\\"===typeof f[a]?f[a]:new Function(\\\"return \\\"+f[a]):function(){},e[a]=d[a]);d[V]=function(){return!!e.ntfnd||e[V]()===true||e[V]()===\\\"true\\\"};d.getState=function(){return e.ntfnd||d[V]()&&e.getState()===\\\"loading\\\"?\\\"default\\\":e.getState()};return e.ntfnd?(setTimeout(function(){d._L.forEach(function(b){b.c.call(window,b.a)})},1),d.ntfnd=!0,d):e}\\nfunction P(a){return decodeURIComponent(escape(atob(a)))}var D=document,PC=!0,CR=!1,Q=\\\"querySelector\\\",E=\\\"replace\\\",M=\\\"mraid\\\",T=\\\"addEventListener\\\",ctr=D[Q](\\\"#ctr\\\"),A=P(D[Q](\\\"#adm\\\").innerText),O=JSON.parse(P(\\\"eyJwdWJBcHBJRCI6MTAzNjYzNywicHViSUQiOjEwMDYxOTEsImRzcElEIjo2NCwiZHNwTmFtZSI6IiIsImltcElEIjoiM2FhYjRhYTctNDZjMS00YjgyLTk3N2EtZGJlYzJiMDY5ZGVhIiwiY3JpZCI6InRlc3RfY3JlYXRpdmUiLCJwdWJOYW1lIjoiIiwiaXNNUkFJRCI6ZmFsc2UsImlzSFRNTDUiOmZhbHNlfQ\\\")),F=!1,CF=!1,L=\\\"<\\\",R=[[P(\\\"PHNjcmlwdFtePl0qIHNyYz1bJyJdP21yYWlkLmpzWyciXT9bXj5dKj48L3NjcmlwdD4=\\\"),\\\"\\\"],[P(\\\"W+KAmOKAmV0=\\\"),\\\"'\\\"]],B=[\\\"aHR0cHM6Ly9iYWNrZW5kLmV1cm9wZS13ZXN0NGdjcDAucHVibmF0aXZlLm5ldC9tb2NrZHNwL3YxL3RyYWNrZXIvbnVybD9hcHBfaWQ9MTAzNjYzNyZwPTAuMDMzMjIyMjIy\\\"],CB=[],i;for(i in B)B[i]=P(B[i])[E](P(\\\"JHtBVUNUSU9OX1BSSUNFfQ==\\\"),\\\"0.03475\\\");for(i in CB)CB[i]=P(CB[i]);\\nfunction RN(){if(ctr&&A){window[M]=new MRAID;for(r in R)A=A[E](new RegExp(R[r][0],\\\"g\\\"),R[r][1]);A=L+\\\"style>html,body{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;}\\\"+L+\\\"/style>\\\"+L+'script type=\\\"text/javascript\\\">'+MRAID.toString()+\\\"window.mraid=new MRAID();\\\"+L+\\\"/script>\\\"+A;if(O.aqh&&O.aqf){var a=function(b){return(b||\\\"\\\")[E](P(\\\"JWN1c3RfaW1wIQ==\\\"),O.impID)[E](P(\\\"JURFTUFORF9JRCE=\\\"),O.dspID)[E](P(\\\"JWRzcE5hbWUh\\\"),O.dspName)[E](P(\\\"JURFTUFORF9DUkVBVElWRV9JRCE=\\\"),O.crid)[E](P(\\\"JVBVQkxJU0hFUl9JRCE=\\\"),\\nO.pubID)[E](P(\\\"JXB1Yk5hbWUh\\\"),O.pubName)[E](P(\\\"JSVXSURUSCUl\\\"),(ctr.contentWindow||window).innerWidth)[E](P(\\\"JSVIRUlHSFQlJQ==\\\"),(ctr.contentWindow||window).innerHeight)};O.aqh=a(O.aqh);O.aqf=a(O.aqf);A=O.aqh+A+O.aqf}A=L+\\\"!DOCTYPE html>\\\\n\\\"+A;window[M][T](\\\"ready\\\",MR);window[M][T](\\\"viewableChange\\\",MV);CR&&FA();MV();PC&&setTimeout(EX,1)}}function FA(){if(!F){for(var i in B)(new Image).src=B[i];F=!0}}function MR(){window[M][T](\\\"viewableChange\\\",MV);MV()}\\nfunction MV(){if(\\\"true\\\"===window[M].isViewable()||!0===window[M].isViewable()){CR||FA();PC||setTimeout(EX,1);setInterval(FS,100)}}\\nfunction FS(){var a=ctr.contentWindow;if(a){var b=window.innerWidth,h=window.innerHeight,g=ctr.style,k=!1,l=10,m=10,e=a.document.querySelectorAll(\\\"body,div,span,p,section,article,a,img,canvas,video,iframe\\\");for(a=0;a<e.length;a++){var c=e[a].offsetWidth;var d=e[a].offsetHeight;if((c===320&&d===480||c===480&&d===320||c===300&&d===250||(c===300||c===320)&&d===50&&h===50&&c!==b)&&!k){k=!0;var f=d*b/c>h?h/d:b/c;g.width=c+\\\"px\\\";g.height=d+\\\"px\\\";g.transform=\\\"scale(\\\"+f+\\\",\\\"+f+\\\")\\\";d*b/\\nc>h?(g.top=(f-1)*d/2+\\\"px\\\",g.left=(b-c)/2+\\\"px\\\"):(g.top=(h-d)/2+\\\"px\\\",g.left=(f-1)*c/2+\\\"px\\\")}f=(e[a].style.backgroundImage||\\\"\\\").match(P(\\\"XnVybFwoIihodHRwLispIlwpJA==\\\"));if(k&&(\\\"IMG\\\"===e[a].nodeName&&e[a].src||f&&f[1])&&c>l&&d>m){l=c;m=d;var n=e[a].src?e[a].src:f[1]}}n&&(document[Q](\\\"#bgblr\\\").style.backgroundImage=\\\"url(\\\"+n+\\\")\\\")}}\\nfunction EX(){if(A){try{var a=ctr.contentWindow,b=a.document;b.open();a[T](\\\"click\\\",function(e){if(!CF){for(var i in CB)(new Image).src=CB[i];CF=true}},true);a[T](\\\"load\\\",FS);b.write(A);b.close()}catch(h){}A=\\\"\\\"}}RN();\\n})();</script>]]></HTMLResource><CompanionClickThrough><![CDATA[https://verve.com]]></CompanionClickThrough><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=BjOxhb60pXgB_D26uBhujvy7CHrIbtT1DC4Wngi4eAreGqaKaewBz8AovMHv4RkARsEU9FuT_mmrT8ZDr8cIYNtAb-IL3113feLlIi13DtU-zIIVrqt6d1R5MfPSftCKJ9r1JwoQ5bQ49IS2lY1jn7yt3pRBPps]]></CompanionClickTracking><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=9MjOfmWMh9ry5A_2VtuPARMkFX-93OJKSKFBasT4Z79wDyRO_1CuHSbVVKjYXcOzouAI07LKX0vcTsiaRXeQhqckki4svks1Ot06WthQJUdftD2Mp3U3Btp7f2q114L5A5oKQ--aXD8lhPDkNXWvbmGBZb4UGTPoTiFusLBPozp6rJSStmbgV0P3Jfg3ANYlRRYXHjnha2qCyoUlgD2i1O3IR2s8VIjdbdIn1et29CWf4piwhKA_2A-XFsPnmVWs_h54xvBBAjcv5l_ONsODt8NxGWKuuVX4qCf2bJpK1FzS4U9vUUFvmKiRNqUexrX2RsnVwQh-rhdAVEx2ALduJeVb5VLnFcLxsXIoTatJfrYS_Tj5mjnLFu7mcSuFzdX0aDsvNMoEpg2xlxcTevD9v3sGufZom9pB233k1dP4TCY_5oJf55WUINQccQJh6eyeMybez4PuyV-nh_8ml6ZZ3jwBaDlEWy-QUSdG-6Ljqni09byDQUAnB9bvoL8MlqDCa18Aa49hPKxKUlqRF23QbTTqqgq2w5Yp6UO-ygIb-ZgpYs1fq9KzYh31TRPm5-d6LzFVMbyRGBMEQ_xzIMOF7cbnQPfWOjLJdGjyDTyj3sImE7-B_7u9KLQk6fPNDrMR8QL7cJ3C8hnq9IMPiUMQryAlc2HdLnFV2GnXlyKITtm80ezPkaiV7VhkOh9wpZo1jVgQr9d1aNHbRYRPNOvA1tJNOdO9stMqZCIG5iMUVhsah_LWnrZwxM05FVe0BJ7yKOsobkJrWbVvcbali8HJG7eJqEy0H1OnSfHTpjlGvcGRCQUd2a26x2mmSIr6YpZqyntykA-S_6T-7qjlC8q-sz7dQDFwKgDLkZFFzZcRRfmPfLGl4YVFk6pxMtXG70hdC8f8P2KlKu-FXTCQ5AkHGE6gEaVw8z8mQxrpR5KQK41Jq4JUCcKeUE1FVzTuFmJeJWMF8ZqXCCWe5BBgUMEvJ1A_GseQppU4Q-5ajsWkE-zbB4HyVHkHxP_xx9WMxeiW9mfmywXPQD5xqKLcXX5iA4Altkax8AjALVWOg4mMz4X9CVrMtCdfDYPmhsx3XO0KcpDLGtEJrAXrs23nw0QbJrVjtOA0M-8l28vbnb7LURjDUYrUT-Nu-RC9J0rbMqVFzyKyNSyUd79Le-eRzx1lq-fnSkmVy6haZ4QdsVspaSBUYajGI3tDu7Nit_BAoCh0rdw_JpRehS_kBSqAzmX3IwNfxMQiXwJZPACiNZWTbD83HrHRCZrdwGhVQnr9hrsZnLgn1c87jGoAs1LFrfHBSkGAW2sFcPru8EBkKaPJaQY0cWdAGV5CIqsW9nysrRLqwWm5aVJdwbV70hsKVW68XWgQubcrIN53d2EMpatvhAq5vqj28xkqKBk1QLoQHlSjHpqe6RM5]]></CompanionClickTracking><TrackingEvents><Tracking event=\\\"creativeView\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/companionView]]></Tracking><Tracking event=\\\"creativeView\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=GJcv-mfK3AM9fvCuWzUv5C4eVs_iov7iQ1BY8sHYq4z0knOdXYzXDY3l5OB64gCzYXXfl82yqjZmUlgYSV_grDxviyPyS4ylY_eiCEKpjzfdBHEgA0k3u3h1rn5NdFkPan5HliRQvVm2WRIChulOarlS1X5d68o]]></Tracking></TrackingEvents></Companion><Companion width=\\\"1024\\\" height=\\\"768\\\"><HTMLResource><![CDATA[<script src=\\\"mraid.js\\\"></script><style id=\\\"stl\\\">html,body,#ctr,#bgblr{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;position:absolute;top:0;left:0;}body{background:black;}</style><div style=\\\"display:none\\\" id=\\\"adm\\\">PGEgdGFyZ2V0PSJfYmxhbmsiIGhyZWY9Imh0dHBzOi8vcGxheS5nb29nbGUuY29tL3N0b3JlL2FwcHMvZGV0YWlscz9pZD1uZXQucHVibmF0aXZlLmVhc3lzdGVwcyI+PGltZyBzcmM9Imh0dHBzOi8vY2RuLnB1Ym5hdGl2ZS5uZXQvd2lkZ2V0L3YzL2Fzc2V0cy92Ml8zMDB4MjUwLmpwZyIgd2lkdGg9IjMwMCIgaGVpZ2h0PSIyNTAiIGJvcmRlcj0iMCIgYWx0PSJBZHZlcnRpc2VtZW50IiAvPjwvYT4</div><div id=\\\"bgblr\\\" style=\\\"z-index:0;background:center/cover;-webkit-filter:saturate(0.5) blur(15px);filter:saturate(0.5) blur(15px);\\\"></div><iframe id=\\\"ctr\\\" frameborder=\\\"0\\\" scrolling=\\\"no\\\" width=\\\"100%!\\\"(MISSING) height=\\\"100%!\\\"(MISSING)></iframe><script type=\\\"text/javascript\\\">(function(){\\nfunction MRAID(){function k(b){try{if(\\\"object\\\"===typeof b.mraid&&b.mraid.getState)var g=b.mraid}catch(l){}return b.parent!==b?k(b.parent)||g:g}var h=window,e=k(h)||{ntfnd:!0},c=\\\"{offsetX:0,offsetY:0,x:0,y:0,width:\\\"+h.innerWidth+\\\",height:\\\"+h.innerHeight+\\\",useCustomClose:!1}\\\",L=\\\"addEventListener\\\",V=\\\"isViewable\\\",d=this,f={removeEventListener:0,open:\\\"window.top.open(a)\\\",close:0,unload:0,useCustomClose:0,expand:0,playVideo:0,resize:0,storePicture:0,createCalendarEvent:0,supports:\\\"{sms:!1,tel:!1,calendar:!1,storePicture:!1,inlineVideo:!1,orientation:!1,vpaid:!1,location:!1}\\\",\\nVERSIONS:{},STATES:{LOADING:\\\"loading\\\",DEFAULT:\\\"default\\\"},PLACEMENTS:{},ORIENTATIONS:{},FEATURES:{},EVENTS:{READY:\\\"ready\\\",ERROR:\\\"error\\\"},CLOSEPOSITIONS:{}};c={Version:'\\\"2.0\\\"',PlacementType:'\\\"unknown\\\"',OrientationProperties:\\\"{allowOrientationChange:!1}\\\",CurrentAppOrientation:'{orientation:\\\"\\\"}',CurrentPosition:c,DefaultPosition:c,State:'\\\"default\\\"',ExpandProperties:c,MaxSize:c,ScreenSize:c,ResizeProperties:c,Location:\\\"{}\\\"};d._L=[];d[L]=function(b,g){\\\"ready\\\"===b||\\\"viewableChange\\\"===b?d._L.push({c:g,a:!0}):\\n\\\"stateChange\\\"===b&&d._L.push({c:g,a:\\\"default\\\"});e[L]&&e[L].apply(h,arguments)};for(var a in c)f[\\\"get\\\"+a]=c[a],f[\\\"set\\\"+a]=\\\"undefined\\\";for(a in f)e[a]?d[a]=e[a]:(d[a]=f[a]?\\\"object\\\"===typeof f[a]?f[a]:new Function(\\\"return \\\"+f[a]):function(){},e[a]=d[a]);d[V]=function(){return!!e.ntfnd||e[V]()===true||e[V]()===\\\"true\\\"};d.getState=function(){return e.ntfnd||d[V]()&&e.getState()===\\\"loading\\\"?\\\"default\\\":e.getState()};return e.ntfnd?(setTimeout(function(){d._L.forEach(function(b){b.c.call(window,b.a)})},1),d.ntfnd=!0,d):e}\\nfunction P(a){return decodeURIComponent(escape(atob(a)))}var D=document,PC=!0,CR=!1,Q=\\\"querySelector\\\",E=\\\"replace\\\",M=\\\"mraid\\\",T=\\\"addEventListener\\\",ctr=D[Q](\\\"#ctr\\\"),A=P(D[Q](\\\"#adm\\\").innerText),O=JSON.parse(P(\\\"eyJwdWJBcHBJRCI6MTAzNjYzNywicHViSUQiOjEwMDYxOTEsImRzcElEIjo2NCwiZHNwTmFtZSI6IiIsImltcElEIjoiM2FhYjRhYTctNDZjMS00YjgyLTk3N2EtZGJlYzJiMDY5ZGVhIiwiY3JpZCI6InRlc3RfY3JlYXRpdmUiLCJwdWJOYW1lIjoiIiwiaXNNUkFJRCI6ZmFsc2UsImlzSFRNTDUiOmZhbHNlfQ\\\")),F=!1,CF=!1,L=\\\"<\\\",R=[[P(\\\"PHNjcmlwdFtePl0qIHNyYz1bJyJdP21yYWlkLmpzWyciXT9bXj5dKj48L3NjcmlwdD4=\\\"),\\\"\\\"],[P(\\\"W+KAmOKAmV0=\\\"),\\\"'\\\"]],B=[\\\"aHR0cHM6Ly9iYWNrZW5kLmV1cm9wZS13ZXN0NGdjcDAucHVibmF0aXZlLm5ldC9tb2NrZHNwL3YxL3RyYWNrZXIvbnVybD9hcHBfaWQ9MTAzNjYzNyZwPTAuMDMzMjIyMjIy\\\"],CB=[],i;for(i in B)B[i]=P(B[i])[E](P(\\\"JHtBVUNUSU9OX1BSSUNFfQ==\\\"),\\\"0.03475\\\");for(i in CB)CB[i]=P(CB[i]);\\nfunction RN(){if(ctr&&A){window[M]=new MRAID;for(r in R)A=A[E](new RegExp(R[r][0],\\\"g\\\"),R[r][1]);A=L+\\\"style>html,body{margin:0;padding:0;width:100%!;(MISSING)height:100%!;(MISSING)overflow:hidden;border:none;}\\\"+L+\\\"/style>\\\"+L+'script type=\\\"text/javascript\\\">'+MRAID.toString()+\\\"window.mraid=new MRAID();\\\"+L+\\\"/script>\\\"+A;if(O.aqh&&O.aqf){var a=function(b){return(b||\\\"\\\")[E](P(\\\"JWN1c3RfaW1wIQ==\\\"),O.impID)[E](P(\\\"JURFTUFORF9JRCE=\\\"),O.dspID)[E](P(\\\"JWRzcE5hbWUh\\\"),O.dspName)[E](P(\\\"JURFTUFORF9DUkVBVElWRV9JRCE=\\\"),O.crid)[E](P(\\\"JVBVQkxJU0hFUl9JRCE=\\\"),\\nO.pubID)[E](P(\\\"JXB1Yk5hbWUh\\\"),O.pubName)[E](P(\\\"JSVXSURUSCUl\\\"),(ctr.contentWindow||window).innerWidth)[E](P(\\\"JSVIRUlHSFQlJQ==\\\"),(ctr.contentWindow||window).innerHeight)};O.aqh=a(O.aqh);O.aqf=a(O.aqf);A=O.aqh+A+O.aqf}A=L+\\\"!DOCTYPE html>\\\\n\\\"+A;window[M][T](\\\"ready\\\",MR);window[M][T](\\\"viewableChange\\\",MV);CR&&FA();MV();PC&&setTimeout(EX,1)}}function FA(){if(!F){for(var i in B)(new Image).src=B[i];F=!0}}function MR(){window[M][T](\\\"viewableChange\\\",MV);MV()}\\nfunction MV(){if(\\\"true\\\"===window[M].isViewable()||!0===window[M].isViewable()){CR||FA();PC||setTimeout(EX,1);setInterval(FS,100)}}\\nfunction FS(){var a=ctr.contentWindow;if(a){var b=window.innerWidth,h=window.innerHeight,g=ctr.style,k=!1,l=10,m=10,e=a.document.querySelectorAll(\\\"body,div,span,p,section,article,a,img,canvas,video,iframe\\\");for(a=0;a<e.length;a++){var c=e[a].offsetWidth;var d=e[a].offsetHeight;if((c===320&&d===480||c===480&&d===320||c===300&&d===250||(c===300||c===320)&&d===50&&h===50&&c!==b)&&!k){k=!0;var f=d*b/c>h?h/d:b/c;g.width=c+\\\"px\\\";g.height=d+\\\"px\\\";g.transform=\\\"scale(\\\"+f+\\\",\\\"+f+\\\")\\\";d*b/\\nc>h?(g.top=(f-1)*d/2+\\\"px\\\",g.left=(b-c)/2+\\\"px\\\"):(g.top=(h-d)/2+\\\"px\\\",g.left=(f-1)*c/2+\\\"px\\\")}f=(e[a].style.backgroundImage||\\\"\\\").match(P(\\\"XnVybFwoIihodHRwLispIlwpJA==\\\"));if(k&&(\\\"IMG\\\"===e[a].nodeName&&e[a].src||f&&f[1])&&c>l&&d>m){l=c;m=d;var n=e[a].src?e[a].src:f[1]}}n&&(document[Q](\\\"#bgblr\\\").style.backgroundImage=\\\"url(\\\"+n+\\\")\\\")}}\\nfunction EX(){if(A){try{var a=ctr.contentWindow,b=a.document;b.open();a[T](\\\"click\\\",function(e){if(!CF){for(var i in CB)(new Image).src=CB[i];CF=true}},true);a[T](\\\"load\\\",FS);b.write(A);b.close()}catch(h){}A=\\\"\\\"}}RN();\\n})();</script>]]></HTMLResource><CompanionClickThrough><![CDATA[https://verve.com]]></CompanionClickThrough><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=1P30ZFbMLkkL--iX6OJYlhgeOf55DMDMuhMeRWN-v456Ohp80ZCkxgwrKDlv2FzedgRUek7Azu2PeJj2ivMqx8F8u6IUSqfrGyFv16S7BrGpGvBIofvDAAAtqjx8agxTJR2AQCimACYQdhurP2nI1XKOMT4-yQk]]></CompanionClickTracking><CompanionClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=bPICAD2nNpwh-N6wnkT9vyX8lbyDDwYGpD5TYBTwjN1IlLU3gdM0wGgVUDjaH5jOH6fR-_ONNGdPz0Mn_bORPFYTg5Wkj1dHbuehjpetKMMyDU6Hh48xoD5ZSsOXBml6GHVJldmyIsxFDr_EUGWGuz5cEBTdjiD0pbG_nq8F7YTdXLkKt0VyM1RiHizFawuHiJaHNTyOaA0OPhc1XvcIv2hKX-dHiEg4cblZRHFM9XEoIpMvpS2Szg4XQOTNqAOB5xSGlvj6Xa2uSfO1l7dvT9dGGRyapODa32K9td1HSzkZBMYpMkdIEdN2Ze9fO2qUQHJag3x-5tLAmCE9Z7toCWma0z18yreOz4EwYkPdD4TSAJy7zn_HtBLbYtt44Xy5nccIcU0eql5j0J_6k7ir0T_qGoS3KwJpZMklgYieDHfnAmmnrhOKhO_qKPawGN4pC0CMlG14Ly7pkD3t0vDbixALOCyYojQdGrSoUX3XoJO61MosdNQdF1ZbXWrAlxOCKgdQm5cSyUm2l1HBvqnkkw4uwqiNzzIxqAttkLLYrVu0zqEl6DMfpNxdtSR4DvIIQjdS5X1aWzfXtIdKtcMOgpLTQ6cVbadIWiDUZ33Ijb2eM9nlH-BBxzJeKk-hoAVj54AXFWYp8nZJYdkAHF6h5RmrnDlMom_LjpOagfjJjILqgPqGExTD64Z4o6XpYvFjMeM2rM4fWZkLjx8rbO3bBOvHefvHb_-M0Rymtx7HV4JCa2wx0O5JXMfhRhQDU0NeRkYYb8AtAukFS0CSnJoZWNsXWNqkZWbHmQjCVMXUWcULHaWnrtA0cr_FdAJJ-sps-xZpkk2PI9AgZaNgL7LHxV_VGi7ajzLxmHmrgqEWSVS80JMP4sVv_cW6GMIPF4XbRDP5-90bBW-_P3c_w3fhctrPGADrf_v1UfluJEXiiwf-gyLmiWDaMXeTTyYcJiM4Aa_5NxEZEFyUZQ4mKsWpCog7UxXV438OiAHdfQcnUxsfdcfbTEWNWPL2gEc_jc05w0roD73jaa6KEmB47kyWYsYWMusJp9oQ8Lo_ZRiEpF8SgI7IQ-OZhab0pSYRDRmTokfQTfNEJZ-nvfZNE0EulvJokIDUc93f6pko72MAFVaW3WzUoY0v6tWIQyTmp_-8ARRlLHVdsM4qW8jDtNeW1p_sK7AuRUbZkP63kD4lgi_QU_NNwDyMN6ghkpdlqEcdMmDWu_ucaY603KOQPzG4fvWzNdAIRb5NOrvZIV7HDXR5HV8TVFr3FxInetFmb4WQXemyvgSQdCcur6ulIrsFJ9AmBBsUpPIhn8gMj6c9bEL2MUyz44BuD7W6rrd_ATUz4EAVVez6OVZeqwriYFm5A3X3-eRFGhvX8VHRHXru6lDxt858_uZTQbmx5GaxRWtR4cBC7QMK]]></CompanionClickTracking><TrackingEvents><Tracking event=\\\"creativeView\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/companionView]]></Tracking><Tracking event=\\\"creativeView\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/companion/event?t=j5W9FLPgNKBjZoHSZ7V2oGCgkCmNFEBNdeCQoutjVDUD0yfeeQVPiJkDLtyb_pq8ymMmTATthY7q2t8ShANHi40VoC-v8UCUp_5HXOnqizcu1NUC8cjuzEgPQgPBjwMAH-DEGz07XjiCY2x-UvoaaTrBkukmPEM]]></Tracking></TrackingEvents></Companion></CompanionAds></Creative><Creative sequence=\\\"1\\\"><Linear><Duration>00:00:14</Duration><TrackingEvents><Tracking event=\\\"start\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/videoStart]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/midPoint]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/midPoint2]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/firstQuartile]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/firstQuartile2]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/thirdQuartile]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/thirdQuartile]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/complete]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/complete2]]></Tracking><Tracking event=\\\"mute\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/mute]]></Tracking><Tracking event=\\\"pause\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/pause]]></Tracking><Tracking event=\\\"fullscreen\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/fullscreen]]></Tracking><Tracking event=\\\"fullscreen\\\"><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/fullscreen2]]></Tracking><Tracking event=\\\"creativeView\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=creativeView&pub_app_id=Tbh_64WVnhJNrBYWjJGH9U0Kq6Uq0lU]]></Tracking><Tracking event=\\\"start\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=start&pub_app_id=8sU2_OuC5NppqvYzshdseW7-ZVU1m8A]]></Tracking><Tracking event=\\\"midpoint\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=midpoint&pub_app_id=pHYxrpAJjB6lZ72wc08hHEA9uTYAcL0]]></Tracking><Tracking event=\\\"firstQuartile\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=firstQuartile&pub_app_id=LMDoLp7If04YUjz4KvyjXFiAKA_z2Qc]]></Tracking><Tracking event=\\\"thirdQuartile\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=thirdQuartile&pub_app_id=Jaof2iNqgYOjRVbFQKVBDEu8SRE-GnM]]></Tracking><Tracking event=\\\"complete\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=complete&pub_app_id=OVubypCF6asOFQfr3-bUPv9wgVHl_Gs]]></Tracking><Tracking event=\\\"mute\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=mute&pub_app_id=JHwISVQXClAl87qea9wu75fW39orUfo]]></Tracking><Tracking event=\\\"unmute\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=unmute&pub_app_id=HqxZxgMIVSvcJoisN1CtApGTUobAmNU]]></Tracking><Tracking event=\\\"pause\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=pause&pub_app_id=YRsiUjC7jKmWLRYFvcapk3BHeldqWso]]></Tracking><Tracking event=\\\"rewind\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=rewind&pub_app_id=rnh5689GYWARCBnX2OJvBzu-Ke8I2JU]]></Tracking><Tracking event=\\\"resume\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=resume&pub_app_id=gVW_3JHHfkcZ_kzBBYME2ChwwDrtj9o]]></Tracking><Tracking event=\\\"fullscreen\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=fullscreen&pub_app_id=wC3g1wMumk2YIujJ5kMIGt5Js33breE]]></Tracking><Tracking event=\\\"expand\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=expand&pub_app_id=MC8UdCjWnx70oc98hqpuxYWfdo41ygQ]]></Tracking><Tracking event=\\\"collapse\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=collapse&pub_app_id=PzfjuGD0vXdkVrK6SFkyt4QOfnp61-Y]]></Tracking><Tracking event=\\\"acceptInvitation\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=acceptInvitation&pub_app_id=TIfiwhIJnQXp83YAHDll61hFD3KpkaQ]]></Tracking><Tracking event=\\\"close\\\"><![CDATA[http://got.us-east4gcp1.pubnative.net/v2/video/event?t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&et=close&pub_app_id=J1Ae0wTGy1GNmYniQoyR-WjgPyYaEYo]]></Tracking></TrackingEvents><VideoClicks><ClickThrough><![CDATA[https://verve.com]]></ClickThrough><ClickTracking><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/clickTracking]]></ClickTracking><ClickTracking><![CDATA[http://backend.us-east4gcp1.pubnative.net/mockdsp/v1/tracker/clickTracking2]]></ClickTracking><ClickTracking><![CDATA[http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=nnGd6braOFyjAshzqqi3a6EauBcZo5LNhxF-uMtZBsLI1S4HG_KbS3T7sfb-ZDL8Y9jWDId03VcI9NUC1uOMXUejKdmFo-nH8f3BXk8rSEEtN2U-KhLbx3JDuK1l8HNCIHA-PdwIEwHdmL5T5gHpQyc4EpUqGc8SUM4fwpo7_sXGrVUjiJHfecRSeHaRKuTHzZ2TchRDJcyKtz6Q6WRE0Z-evA5D38575bbcZ2rPEL3YEsp_xaRrDuBzD4aqiF_27-SWPFOqdm4Es6aGJ5b9D_WfTwxqlKGktwSvgflF-f4D2EkEW__nCPKfpUKdgesoWnCh09dMGxExAFTofaRvicu6WHVWvzl-NCYP9OTkN2kt1LX0Lsolzsj3r0xrFP85U-8LAEYNO-Wo8txU1H2ucL3_qCM3KZmFfgvzfdvVpshWvYUL6CAbkxUNy9QqPt2TlE0gxcF71_SQyXZS_4DiDw8feKE7oftwemB8Vo7mob7xvDPvReCZ50EiM6RaZumqLMuDQvD17JHIgMFgTjKbGuerhPtKhO1lye2cCjIr_3ViQhL78mW8qCI0hDswCfixLu4Z1Mun2hST0N_fbhRwThMCF_CzNXLO86H0d7_ELRLPMKJPvYaLzWI6R3NvkRCFhVOZmJR38UPUsWbe0cd6O9pwQuclC7rd4bcbDMv0t0aeDOAzVN8W42i8TOuatOU1npBoZJCtloF9grIXx_mrYL5l-iOg49xSub0KtuwE9jbpwPKBvGLAIBEya2D7HzGW4tjUWN5q1g-vtgj5cOEYCP_ojOBaHVqX1uUvnFKdFMCc5CENrRCWPkxjC2WPzBbEV25cJDwmsNMfw7WtQl_rIylhKDJ-nKSmRx-GuyIed4FNLFyrvohvjVPpyg33yzueYSBaG0XffsnEIXVGAFt66VxmZsT5RdhrnYyMHfvTa0pp-83rhoPFpqELXDfPOQN2pfRplHM_5Af6YBGG6CTscnOoJb6_RHYqsuw8WJtMCtK72igqOOMyGRpRfHl8A0aMogn7Qutz0HcmC04Z8C-ZYLNUWNLDjEoF7SoYXpdVYj-UkjqFhWJphu4NqADuxX1314ESORr-rbktIuG19KOLb0fBnxJYqiMfvb-_h-_uSPiZhdZfzU8lyWlWyiXhyntT0xnld37t1hSJWL94496BHQuaLt09K-q0THUESZaHhkWYgGNz3ZEYqgTQgwDgBOI2rJl5_RfDl-ycE-rfPxsdgzXLM-6ZPV9xK45AStXPekP1lmrd7FKqc7jZyIetWn61y7FdoLVOLMel9e8rrDttUqweHdQWLVVrBJH92x6GhCb8L_RdZtyn6HqpfMYCoC6Ey3RDU0ZN6-n-NZTsvrZNwNZ2O5ykaY98tD2vZkRWHRopjD5RNJKpeoVkUocX4JcE1M4LwEN8]]></ClickTracking></VideoClicks><MediaFiles><MediaFile id=\\\"1\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" bitrate=\\\"100\\\" width=\\\"320\\\" height=\\\"480\\\"><![CDATA[https://pubnative-assets.s3.amazonaws.com/widget/v3/assets/320x480.mp4]]></MediaFile><MediaFile id=\\\"2\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" bitrate=\\\"100\\\" width=\\\"480\\\" height=\\\"320\\\"><![CDATA[https://pubnative-assets.s3.amazonaws.com/widget/v3/assets/480x320.mp4]]></MediaFile><MediaFile id=\\\"3\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" bitrate=\\\"100\\\" width=\\\"768\\\" height=\\\"1024\\\"><![CDATA[https://pubnative-assets.s3.amazonaws.com/widget/v3/assets/768x1024.mp4]]></MediaFile><MediaFile id=\\\"4\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" bitrate=\\\"100\\\" width=\\\"1024\\\" height=\\\"768\\\"><![CDATA[https://pubnative-assets.s3.amazonaws.com/widget/v3/assets/1024x768.mp4]]></MediaFile></MediaFiles></Linear></Creative></Creatives></InLine></Ad></VAST>\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"custom_endcard\",\n" +
                "          \"data\": {\n" +
                "            \"html\": \"\\n<!DOCTYPE html>\\n<html lang=\\\"en\\\">\\n<head>\\n  <meta charset=\\\"UTF-8\\\" />\\n  <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1.0\\\"/>\\n  <title>Mobile Interface</title>\\n  <style>\\n    * {\\n      margin: 0;\\n      padding: 0;\\n      box-sizing: border-box;\\n    }\\n    body, html {\\n      height: 100%;\\n      font-family: Arial, sans-serif;\\n      overflow: hidden;\\n    }\\n    .container {\\n      width: 100%;\\n      height: 100vh;\\n      background: rgba(0, 0, 0, 0.5);\\n      position: relative;\\n      display: flex;\\n      flex-direction: column;\\n      justify-content: space-between;\\n      align-items: center;\\n    }\\n    .top-controls {\\n      position: absolute;\\n      top: 20px;\\n      right: 20px;\\n      display: flex;\\n      gap: 15px;\\n      z-index: 10;\\n    }\\n    .control-btn {\\n      width: 50px;\\n      height: 50px;\\n      border-radius: 50%;\\n      border: 2px solid rgba(255, 255, 255, 0.7);\\n      background: rgba(255, 255, 255, 0.2);\\n      backdrop-filter: blur(10px);\\n      cursor: pointer;\\n      transition: all 0.3s ease;\\n      display: flex;\\n      align-items: center;\\n      justify-content: center;\\n      color: white;\\n      font-size: 20px;\\n      font-weight: bold;\\n    }\\n    .control-btn:hover {\\n      background: rgba(255, 255, 255, 0.3);\\n      border-color: rgba(255, 255, 255, 0.9);\\n      transform: scale(1.05);\\n    }\\n    .control-btn:active {\\n      transform: scale(0.95);\\n    }\\n    .close-btn::before {\\n      content: '✕';\\n      font-size: 18px;\\n    }\\n    .replay-center-btn {\\n      position: absolute;\\n      top: 42%;\\n      left: 50%;\\n      transform: translate(-50%, -50%);\\n      width: 80px;  \\n      height: 80px;\\n      border-radius: 50%;\\n      border: none;\\n      background: rgba(255, 255, 255, 0.2);\\n      backdrop-filter: blur(10px);\\n      cursor: pointer;\\n      display: flex;\\n      align-items: center;\\n      justify-content: center;\\n      transition: all 0.3s ease;\\n      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);\\n      z-index: 10;\\n    }\\n    .replay-center-btn::before {\\n      content: \\\"\\\";\\n      padding: 6px;\\n      display: block;\\n      width: 36px;\\n      height: 36px;\\n      background-repeat: no-repeat;\\n      background-position: center;\\n      background-size: contain;\\nbackground-image: url('data:image/svg+xml;utf8,<svg xmlns=\\\"http://www.w3.org/2000/svg\\\" viewBox=\\\"174 423 80 80\\\" fill=\\\"none\\\"><rect x=\\\"174.5\\\" y=\\\"423.5\\\" width=\\\"80\\\" height=\\\"80\\\" rx=\\\"40\\\" fill=\\\"white\\\"  fill-opacity=\\\"0.0\\\"/><path d=\\\"M239.318 444.581L235.793 447.337C230.987 441.194 223.512 437.25 215.118 437.25C200.624 437.25 188.893 448.969 188.874 463.469C188.855 477.981 200.612  489.75 215.118 489.75C226.449 489.75 236.105 482.563 239.78 472.494C239.874 472.231 239.737 471.937 239.474 471.85L235.93 470.631C235.807 470.589 235.672 470.597 235.554 470.653C235.436  470.709 235.344 470.809 235.299 470.931C235.187 471.244 235.062 471.556 234.93 471.862C233.849 474.425 232.299 476.725 230.324 478.7C228.365 480.663 226.045 482.229 223.493 483.312C220.849  484.431 218.03 485 215.13 485C212.224 485 209.412 484.431 206.768 483.312C204.213 482.233 201.892 480.667 199.937 478.7C197.972 476.741 196.408 474.419 195.33 471.862C194.212 469.212  193.643 466.4 193.643 463.494C193.643 460.587 194.212 457.775 195.33 455.125C196.412 452.562 197.962 450.263 199.937 448.288C201.912 446.313 204.212 444.763 206.768 443.675C209.412 442.556  212.23 441.988 215.13 441.988C218.037 441.988 220.849 442.556 223.493 443.675C226.048 444.754 228.368 446.321 230.324 448.288C230.943 448.906 231.524 449.562 232.062 450.25L228.299  453.188C228.225 453.245 228.168 453.323 228.136 453.411C228.103 453.499 228.096 453.595 228.116 453.687C228.136 453.779 228.181 453.864 228.246 453.931C228.312 453.999 228.395 454.046  228.487 454.069L239.462 456.756C239.774 456.831 240.08 456.594 240.08 456.275L240.13 444.969C240.124 444.556 239.643 444.325 239.318 444.581V444.581Z\\\" fill=\\\"white\\\"/></svg>');   \\n    }\\n    .replay-center-btn:hover {\\n      background: rgba(255, 255, 255, 0.3);\\n      transform: translate(-50%, -50%) scale(1.05);\\n    }\\n    .replay-center-btn:active {\\n      transform: translate(-50%, -50%) scale(0.95);\\n    }\\n    .learn-more-btn {\\n      position: absolute;\\n      bottom: 40%;\\n      left: 50%;\\n      transform: translateX(-50%);\\n      background: rgba(17, 24, 39, 0.9);\\n      color: white;\\n      border: 2px solid rgba(255, 255, 255, 0.6);\\n      padding: 15px 40px;\\n      border-radius: 25px;\\n      font-size: 18px;\\n      font-weight: bold;\\n      cursor: pointer;\\n      transition: all 0.3s ease;\\n      box-shadow: 0 4px 15px rgba(17, 24, 39, 0.4);\\n      z-index: 10;\\n      backdrop-filter: blur(10px);\\n    }\\n    .learn-more-btn:hover {\\n      transform: translateX(-50%) translateY(-2px);\\n      box-shadow: 0 6px 20px rgba(17, 24, 39, 0.6);\\n      background: rgba(17, 24, 39, 1);\\n      border-color: rgba(255, 255, 255, 0.8);\\n    }\\n    .learn-more-btn:active {\\n      transform: translateX(-50%) translateY(0);\\n    }\\n    .learn-more-btn::after {\\n      content: '→';\\n      margin-left: 8px;\\n      font-size: 16px;\\n    }\\n    .domain-text {\\n      position: absolute;\\n      bottom: 35%;\\n      left: 50%;\\n      transform: translateX(-50%);\\n      color: white;\\n      font-size: 12px;\\n      font-weight: normal;\\n      z-index: 10;\\n      opacity: 0.8;\\n    }\\n    .ripple {\\n      position: absolute;\\n      border-radius: 50%;\\n      background: rgba(255, 255, 255, 0.6);\\n      transform: scale(0);\\n      animation: ripple 0.6s linear;\\n      pointer-events: none;\\n    }\\n    @keyframes ripple {\\n      to {\\n        transform: scale(4);\\n        opacity: 0;\\n      }\\n    }\\n    /* Responsive Styles */\\n    @media (max-width: 768px) {\\n      .replay-center-btn {\\n        width: 70px;\\n        height: 70px;\\n      }\\n      .replay-center-btn::before {\\n        width: 32px;\\n        height: 32px;\\n      }\\n      .learn-more-btn {\\n        padding: 12px 30px;\\n        font-size: 16px;\\n      }\\n      .domain-text { font-size: 10px; }\\n    }\\n\\n    @media (max-width: 480px) {\\n      .replay-center-btn {\\n        width: 60px;\\n        height: 60px;\\n      }\\n      .replay-center-btn::before {\\n        width: 28px;\\n        height: 28px;\\n      }\\n      .learn-more-btn {\\n        padding: 10px 25px;\\n        font-size: 14px;\\n      }\\n      .domain-text { font-size: 9px; }\\n    }\\n  </style>\\n</head>\\n<body>\\n  <a href=\\\"https://customendcard.verve.com/click\\\" class=\\\"cta-button\\\" target=\\\"_blank\\\">\\n  <div class=\\\"container\\\">\\n    <div class=\\\"top-controls\\\">\\n      <!-- Uncomment if you want a close button -->\\n      <!-- <button class=\\\"control-btn close-btn\\\" onclick=\\\"handleClose(this)\\\"></button> -->\\n    </div>\\n    <!-- Centered Replay Button -->\\n    <button class=\\\"replay-center-btn\\\" onclick=\\\"handleReplay(this)\\\"></button>\\n    <!-- Learn More Button -->\\n    <button class=\\\"learn-more-btn\\\" onclick=\\\"handleLearnMore(this)\\\">Learn More</button>\\n    <!-- Domain Text -->\\n    <div class=\\\"domain-text\\\" id=\\\"domain-text\\\"></div>\\n  </div>\\n\\n  <script>\\n    function handleReplay(btn) {\\n      addRipple(btn);\\n      window.open('https://customendcard.verve.com/replay', '_blank');\\n    }\\n\\n    function handleClose(btn) {\\n      addRipple(btn);\\n    }\\n\\n    function handleLearnMore(btn) {\\n      addRipple(btn);\\n    }\\n\\n    function addRipple(button) {\\n      const ripple = document.createElement('span');\\n      const rect = button.getBoundingClientRect();\\n      const size = Math.max(rect.width, rect.height);\\n      const x = rect.width / 2 - size / 2;\\n      const y = rect.height / 2 - size / 2;\\n\\n      ripple.style.width = ripple.style.height = size + 'px';\\n      ripple.style.left = x + 'px';\\n      ripple.style.top = y + 'px';\\n      ripple.classList.add('ripple');\\n\\n      button.appendChild(ripple);\\n\\n      setTimeout(() => ripple.remove(), 600);\\n    }\\n\\n    document.addEventListener('keydown', (e) => {\\n      if (e.key.toLowerCase() === 'r') {\\n        handleReplay(document.querySelector('.replay-center-btn'));\\n      }\\n      if (e.key === 'Enter' || e.key === ' ') {\\n        handleLearnMore(document.querySelector('.learn-more-btn'));\\n      }\\n    });\\n\\n    document.getElementById(\\\"domain-text\\\").textContent = \\\"www.verve.com\\\";\\n  </script>\\n  </a>\\n</body>\\n</html>\\n\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"meta\": [\n" +
                "        {\n" +
                "          \"type\": \"contentinfo\",\n" +
                "          \"data\": {\n" +
                "            \"link\": \"http://pubnative.net/content-info\",\n" +
                "            \"icon\": \"http://cdn.pubnative.net/static/adserver/contentinfo.png\",\n" +
                "            \"text\": \"Learn about this ad\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"creativeid\",\n" +
                "          \"data\": {\n" +
                "            \"text\": \"test_creative\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"campaignid\",\n" +
                "          \"data\": {\n" +
                "            \"text\": \"test_campaign\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"bundleid\",\n" +
                "          \"data\": {\n" +
                "            \"text\": \"\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"adexperience\",\n" +
                "          \"data\": {\n" +
                "            \"text\": \"brand\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"points\",\n" +
                "          \"data\": {\n" +
                "            \"number\": 10\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"revenuemodel\",\n" +
                "          \"data\": {\n" +
                "            \"text\": \"cpm\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"remoteconfigs\",\n" +
                "          \"data\": {\n" +
                "            \"jsondata\": {\n" +
                "              \"AutostorekitOverrideWithClick\": true,\n" +
                "              \"SKOverlayOverrideWithClick\": {\n" +
                "                \"enabled\": true,\n" +
                "                \"values\": {\n" +
                "                  \"autoclose\": 0,\n" +
                "                  \"click\": 1,\n" +
                "                  \"delay\": 8,\n" +
                "                  \"dismissible\": 1,\n" +
                "                  \"endcarddelay\": 1,\n" +
                "                  \"pos\": 0,\n" +
                "                  \"present\": 1\n" +
                "                }\n" +
                "              },\n" +
                "              \"SKOverlayenabled\": true,\n" +
                "              \"atom_enabled\": true,\n" +
                "              \"audiostate\": \"default\",\n" +
                "              \"bc_endcard_close_delay\": 5,\n" +
                "              \"bc_hide_controls\": true,\n" +
                "              \"bc_learn_more_location\": \"default\",\n" +
                "              \"bc_learn_more_size\": \"default\",\n" +
                "              \"bc_rewarded_video_skip_offset\": 30,\n" +
                "              \"bc_video_skip_offset\": 8,\n" +
                "              \"close_inter_after_finished\": false,\n" +
                "              \"close_reward_after_finished\": false,\n" +
                "              \"content_info_display\": \"inapp\",\n" +
                "              \"content_info_icon_click_action\": \"expand\",\n" +
                "              \"content_info_icon_url\": \"https://cdn.pubnative.net/static/adserver/contentinfo.png\",\n" +
                "              \"content_info_url\": \"https://feedback.verve.com/index.html\",\n" +
                "              \"creative_autostorekit\": true,\n" +
                "              \"custom_cta_enabled\": true,\n" +
                "              \"custom_endcard_display\": \"extension\",\n" +
                "              \"custom_endcard_enabled\": true,\n" +
                "              \"endcard_close_delay\": 2,\n" +
                "              \"endcardenabled\": true,\n" +
                "              \"fullscreen_clickability\": false,\n" +
                "              \"html_skip_offset\": 5,\n" +
                "              \"imp_tracking\": \"viewable\",\n" +
                "              \"landing_page\": true,\n" +
                "              \"min_visible_percent\": 0,\n" +
                "              \"min_visible_time\": 0,\n" +
                "              \"navigation_mode\": \"internal\",\n" +
                "              \"pc_endcard_close_delay\": 2,\n" +
                "              \"pc_endcardenabled\": true,\n" +
                "              \"pc_html_skip_offset\": 8,\n" +
                "              \"pc_reduced_icon_sizes\": true,\n" +
                "              \"pc_rewarded_html_skip_offset\": 30,\n" +
                "              \"pc_rewarded_video_skip_offset\": 30,\n" +
                "              \"pc_sdk_autostorekit\": true,\n" +
                "              \"pc_skoverlayenabled\": true,\n" +
                "              \"pc_video_skip_offset\": 8,\n" +
                "              \"rewarded_html_skip_offset\": 30,\n" +
                "              \"rewarded_video_skip_offset\": 30,\n" +
                "              \"sdk_autostorekit\": true,\n" +
                "              \"sdk_autostorekit_delay\": 0,\n" +
                "              \"video_skip_offset\": 5\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"remoteconfigsdebug\",\n" +
                "          \"data\": {\n" +
                "            \"jsondata\": {\n" +
                "              \"configids\": [\n" +
                "                10,\n" +
                "                23,\n" +
                "                25,\n" +
                "                35,\n" +
                "                36,\n" +
                "                49,\n" +
                "                52,\n" +
                "                53,\n" +
                "                54,\n" +
                "                57,\n" +
                "                63,\n" +
                "                65,\n" +
                "                69,\n" +
                "                72\n" +
                "              ],\n" +
                "              \"sliceids\": [\n" +
                "                74,\n" +
                "                1336,\n" +
                "                1363,\n" +
                "                1691,\n" +
                "                1725,\n" +
                "                1911,\n" +
                "                2033,\n" +
                "                2034,\n" +
                "                2035,\n" +
                "                2138,\n" +
                "                2330,\n" +
                "                2379,\n" +
                "                3297,\n" +
                "                4046\n" +
                "              ]\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"beacons\": [\n" +
                "        {\n" +
                "          \"type\": \"impression\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/v2/impression?aid=1036637&t=H4YLfoaPgWnPLR4Bke1bfAWkUEofE6RK0QKAwDs&px=1\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"impression\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"https://got.pubnative.net/click/rtb\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"companion_ad_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/companion/event?t=fQ8FakWsxg51tIJKNkOhRIDIZKsn-BBueVbWblxvf_4J4rLv_SJnGaKFml4jPY1XBlg9YSmIUGV6bBth0Ph7kZyMw3DqHYL51PjKBH17SWhZYffOKAwKhUDopoepAAWpFgvF1Sjxp_rSO1vbPAdNYwga33QU&err=[ERRORCODE]&et=[EVENTTYPE]\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"companion_ad_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=CSUIkzdfUQRD_sSUmYpusmY9zW6VUVw5so_PDuHmjqUPmLeBLLuBa95uZ4XP9youFA-ZGXjalhq1anNSPb29TvFbUm8s4pT-5UO3P1_vSHbA1aDJzjU8Zk1RMEBXjxV7Oc0CPQMWmHxh1_0KtUWgezCNJOaaxw3JivwfPD5VIcQf08E_vvQLVBvx7vhawc7o-cWnNW5z5D6qr08STPGzUMux29963gPPd0bjqn5z8ZfXLtw4ATy8vr9RbKs1DulCVXFlx32m7Y2oB9VnpwAbPSdFHQvvXzhS1rBg5EmBCuB081doVD94dVzYx4pW2v6rltPqeq0Gpyj4ymmnkY2RWz_zFn3vUZfCsyLGyCOCmYd6QNAiOdSdXsLhGAbK2gcKB-fzJkKH3U6uSUfrJRpzSpeKnFGwlwR29hVYtK0CG2l0IXO_UTanEBeHvQeLlQkRXAZbT27vqGM_YhMvpGWTYk7KZVUmXidZWCDAd_zLfdvAZ6d1VXWkVcZ8Yy0wZGdzM7WFqf6mTvox0nE1vAwtyjAxUgmbntmPdnRzIeoSagvH6r0BzMqN6SLtQZ88N2wNUUq7Uf6-tDQ8uz0LCzNrilFfHpPx89PNthRd8nE3w7e-tLCdP21JNa0sxiNMwvgHwIpBG4Rs0wZXMrR4dgLVTKLrAiCxbAYDlt11nSauwdvAWthjfzJ8XovV0Xh3Y2cRJCqJpVNPIwDnItI0IRiwYG6idUqBaD4LQjnvbt1N6-sT5rxmwvbDE_dYq-03YZz2khXGrT-ZPlIblEEztM-7sRPEbLdcyWn51r4qG5xdVBXRWqEIYWMiQkXk35HqNx_-AnETFwDIBjCcIfNF0W8sO2L9iJjt3LgFVpUmtO-k76V6TGBu56QdjMU00ksbAUTnNt4OW_ftE1WKG2F6AuRqTVihFpcB3DXtmHmpofpyV2qk2ikjT2p2a4T2kjgVsCyjJpLndvhkx4RfRap2qCafWU8UJGgxI81huNU_5eJhGpFP2gMA-lPesdCdfyyom_kU2I84i465jBuhQ9udQGiGcXKxv3tAmK5DmoLczAUOgXlLQMVVO2Cf5x5dYqix8gzQ8_67jgLd6YtAEOakv4kMbtFKYFc94cD5Ba-rNkIOQvAU9tfM_UreIeW5yN7daNCby7GLlGlU4I_eMItQSzhiU9rkqyd3XOu_stEUq3-O4R-ML089dm-gGAaUz1roNRpOuPWj9NCwWQTaVTB4YgsXV2MHP11gTcheQj_HT0i-XXdmVIcyGzTZa_7gsMW9zxTxa5PdJ6AdzDFCfSNR7wEWJEC62p_3PMo-fFmnVE5j_FWqK_bIBmr98QROV1ZTxsYCX_Gx6wN4gJUL53JVD0dOEFenhUHQVtGARocnShmtpBZOYtahQyP9n-YzqOYX4ZcD8-TAZXIq&et=[EVENTTYPE]&err=[ERRORCODE]\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"sdk_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/sdk/event?t=Yvc0u-eUuO-11VOCuE5rPS8WZX-hWAzs7U3wbF_H0zFDmue3pVd0zO7w71tngDCEwNsAmfY2hI5OFlvbDi_Hd5Iw_5Q3mpiCS9vRvhNP4ErVXUHCe4SGSBwaCgK1vhg46ge1Lxzh77bqnoGWIxDctINEvdRY&err=[ERRORCODE]&et=[EVENTTYPE]\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"custom_endcard_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/custom-endcard/event?t=gE0O1mBMstJN84YV0zGtRZFU5GDGWRbbjme2aHwC7wxKSwK7YvBKyH99Av2k7j4kKHcY_IJTY0GV0La_5Bynl_mBQdl2_o0j-d4fV8xfioIORpOEzAHgCvypyYLh4KZaRJYXkW34kxL0_0XrDz9eceBuUYHs&err=[ERRORCODE]&et=[EVENTTYPE]\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"custom_endcard_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/click/rtb?aid=1036637&t=e9AaxbF9bYX4DSjwhuzqPgC2p7afFnmO3VsYgLlRzsLzyLCWgCKf848u27nmAcgBmn0a3fR26o0Qn_6iM41eBxdktUkiUdEW7sW4LkJ8iUqmAvw1sawFKPmI_eua_nbBf0AYImpRaz5oFhbxeawNHkjZ_ecifd6Ff_Ek2NYxWA9YHKIm6xlrjaegbRGc2iM9gdvyihKnoI9Y26Z2E836X65jUf88i-GfIHZ1LOd1VBwBo-kU6N3-0HFX_jy1lQsbv22vuNcGuToddco2JUN7_7bkYgjO4cUqaSiJGbTI95KSBZQkXyXXG2ValjVTN1iIGdi4lIp1ZCpwYF14-HpinW6QuBlw89YP2p-vNzWa3UTuEEZYet546Bs0YihiiJfmXh3z6gIcqxSI6esLEdhH26zhvEiyUVe7WukK_a9Poz1wW5yCcUvjS2awrdQ2cv6yb1uXuFYJHqn5j4Uu-zHbY5On4i5EaBVxY_ONqdd_xk_QzsNwYOQDOwwQDeybMKGF5l_4LgTZUiyMlEeBLU_5cZ1yFr2kUM_JxFVVXBVlOEw1kDZ_Uv3sN0LCbJ4H-mEeiRbRfO8TsNTMyat8aTwtM4Ga6P-2fZ1GPwipwNtBcJSeMrPC9wDx4cZmo22R1ToTZrhfUSvrsbkr4wng436chfVNp0L-XmMX7UPM7NZ7jXRXVsd5laX_cH-GwoNF8JvJqXzZzl9r3WzXvzLbIxr8Zj6i8mhMbWJeNyPWzE_0RaF3suJR3EOvzRMWR-zn94NiwAAvFHxD06MM9tJ2JamFPa1yCveD6NBt0wNGeoEGgUvmftbHS36JogedHtlKdg8R1kiF6FnRO1t8qVNBmmMH4xFs5f-qxPxc-B3_eDg9Wl6-b52kE39Ge7krCH3n6NeQV7fvI75hkQ5VUnglf26Jz-Q_RmwGR8JcKuXGG_pPR8di3JEuRCg3y3Hx0setXSa2gjfc-JV9iDfMTm89Xoy-IujR5Pasl2USRvQUz6ee8knvbdRZDF4E1yQNbjLWRq72SGhxbnHs7JblN0w7D19uNbI-lGB2IAapTiWFA8Pcw2Tqa0X4TApzO-KVB0X51jXrbswNUIKksFjXEFvh_SwaloL6ayAFdmN7un1IdM8_92t7C2iYiJcL8i4XS-xZBNPUOp5N1NneyeIOf1czzzzpkM6MtIHFUlEWDsqLq6bNsqO-AlTcT5-PiSJsFgcb3WIcwzEL5YauhI1tGaWyg4xDTuXCZXP59hYaG6GUxHn_4P85iSquueAlsb9RYgTl5peysuhQGLPIwkVJu1R0s1mcapztzfwtN1x2cfz11E2hDsW5G3SwTL-KiwYJY_48cTMqlX0mBI8aMtB4bttgbnq35e5S9AGAWDIODPtTMrljR-l6YRmPLCUy-LSHTvBr680ENKXVscQW&et=[EVENTTYPE]&err=[ERRORCODE]\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"autostorekit_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/auto-storekit/event?t=9Y0x_bCfrnRvWRiVuvyGv0LXjbctWV1UcE-3EiVCBL-g4WR5IQLCGRm8IaLpJW07lKGBZLKtlfK29m3zWhZ7iTGmlq1o9ZdHmxJmUDvqvi7Md0oXOpKoRNVw4aV2j6hoxwc2rpBUeCx4m72OvCjiSEwWAy2f&err=[ERRORCODE]&et=[EVENTTYPE]&oto=[ONTOPOF]\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"skoverlay_event\",\n" +
                "          \"data\": {\n" +
                "            \"url\": \"http://got.us-east4gcp1.pubnative.net/sk-overlay/event?t=T8PT7_LLioMMWfDjTgCcKRqZYWisjtt-j24D6itF63mY2iHrsn9c_bhiawJ7FBdtK-quKyOl-Gn5JaWyn703MLRTJVwztiFiJf1Q84TlCXtJm3LftlNjEtnPVLLblI3Bxszwur5ZUr5D-kyiYU6kMFSbNNax&err=[ERRORCODE]&et=[EVENTTYPE]&oto=[ONTOPOF]\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}")
    }

    private fun initObservers() {
        viewModel.clipboard.observe(viewLifecycleOwner) {
            responseInput.setText(it)
        }

        viewModel.clipboardBody.observe(viewLifecycleOwner) {
            oRTBBodyInput.setText(it)
        }

        viewModel.listVisibility.observe(viewLifecycleOwner) {
            if (it) showRecyclerView()
            else hideAndCleanRecyclerView()
        }

        viewModel.showButtonVisibility.observe(viewLifecycleOwner) {
            if (it) showButton.visibility = View.VISIBLE
            else showButton.visibility = View.GONE
        }

        viewModel.loadInterstitial.observe(viewLifecycleOwner) {
            loadInterstitial(it)
        }

        viewModel.loadRewarded.observe(viewLifecycleOwner) {
            loadRewarded(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.adapterUpdate.observe(viewLifecycleOwner) {
            adapter.refreshWithAd(it, viewModel.getAdSize())
        }

        adCustomizationViewModel.onAdLoaded.observe(viewLifecycleOwner) { ad ->
            viewModel.handleAdResult(ad)
        }

        adCustomizationViewModel.onAdLoadFailed.observe(viewLifecycleOwner) {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setListeners() {

        view?.findViewById<ImageButton>(R.id.button_paste_clipboard)?.setOnClickListener {
            viewModel.pasteFromClipboard()
        }

        view?.findViewById<ImageButton>(R.id.button_paste_clipboard_body)?.setOnClickListener {
            viewModel.pasteFromClipboardBody()
        }

        loadButton.setOnClickListener {
            cleanLogs()
            if (adCustomisationEnabled && viewModel.getMarkupType() != MarkupType.ORTB_BODY && viewModel.getAdSize() != NATIVE) {
                loadCustomizedAd()
            } else {
                loadAd()
            }
        }

        showButton.setOnClickListener {
            when (viewModel.getAdSize()) {
                INTERSTITIAL -> {
                    interstitial?.show()
                }

                REWARDED -> {
                    rewardedAd?.show()
                }

                else -> {}
            }
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_size_banner -> {
                    viewModel.setAdSize(BANNER)
                }

                R.id.radio_size_medium -> {
                    viewModel.setAdSize(MEDIUM)
                }

                R.id.radio_size_leaderboard -> {
                    viewModel.setAdSize(LEADERBOARD)
                }

                R.id.radio_size_native -> {
                    viewModel.setAdSize(NATIVE)
                }

                R.id.radio_size_interstitial -> {
                    viewModel.setAdSize(INTERSTITIAL)
                }

                R.id.radio_size_rewarded -> {
                    viewModel.setAdSize(REWARDED)
                }
            }
            showAdCustomisationLayoutIfAvalable()
            showButton.isEnabled = false
        }

        responseSourceGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_markup -> {
                    showORTBEditText(false)
                    viewModel.setMarkupType(MarkupType.CUSTOM_MARKUP)
                }

                R.id.radio_url -> {
                    showORTBEditText(false)
                    viewModel.setMarkupType(MarkupType.URL)
                }

                R.id.radio_ortb -> {
                    showORTBEditText(true)
                    viewModel.setMarkupType(MarkupType.ORTB_BODY)
                }
            }
            showAdCustomisationLayoutIfAvalable()
        }

        customizeButton.setOnClickListener {
            if (adCustomisationEnabled) {
                val intent = Intent(context, AdCustomizationActivity::class.java)
                startActivity(intent)
            }
        }

        enableAdCustomisationCheckbox.setOnCheckedChangeListener { compoundButton, isChecked ->
            adCustomisationEnabled = isChecked
            customizeButton.isEnabled = adCustomisationEnabled
        }
    }

    private fun loadRewarded(ad: Ad?) {
        rewardedAd?.destroy()

        val rewardelListener = object : HyBidRewardedAd.Listener {

            override fun onRewardedLoaded() {
                Logger.d(TAG, "onRewardedLoaded")
                displayLogs()
                showButton.isEnabled = true
            }

            override fun onRewardedLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onRewardedLoadFailed", error)
                displayLogs()
                showButton.isEnabled = false
            }

            override fun onRewardedOpened() {
                Logger.d(TAG, "onRewardedOpened")
            }

            override fun onRewardedClosed() {
                Logger.d(TAG, "onRewardedClosed")
                showButton.isEnabled = false
            }

            override fun onRewardedClick() {
                Logger.d(TAG, "onRewardedClick")
            }

            override fun onReward() {
                Logger.d(TAG, "onReward")
            }
        }

        rewardedAd = HyBidRewardedAd(requireActivity(), rewardelListener)
        rewardedAd?.prepareAd(ad)
    }

    private fun loadAd() {
        showButton.isEnabled = false
        viewModel.loadApiAd(responseInput.text.toString(), oRTBBodyInput.text.toString())
    }

    private fun loadCustomizedAd() {

        showButton.isEnabled = false
        var isReworded = false;
        val adSize: AdSize?

        when (viewModel.getAdSize()) {
            BANNER -> {
                adSize = AdSize.SIZE_300x50
            }

            MEDIUM -> {
                adSize = AdSize.SIZE_300x250
            }

            LEADERBOARD -> {
                adSize = AdSize.SIZE_728x90
            }

            INTERSTITIAL -> {
                adSize = AdSize.SIZE_INTERSTITIAL
            }

            REWARDED -> {
                adSize = AdSize.SIZE_INTERSTITIAL; isReworded = true
            }

            NATIVE -> {
                adSize = null
            }
        }

        if (viewModel.getMarkupType() == MarkupType.CUSTOM_MARKUP) {
            adCustomizationViewModel.loadCustomizedAd(
                responseInput.text.toString(),
                adSize,
                isReworded,
                "",
                Constants.AdmType.API_V3
            )
        } else if (viewModel.getMarkupType() == MarkupType.URL) {
            adCustomizationViewModel.loadCustomizedAdFromUrl(
                responseInput.text.toString(),
                adSize,
                isReworded,
                "",
                Constants.AdmType.API_V3
            )
        }
    }

    override fun onDestroy() {
        interstitial?.destroy()
        rewardedAd?.destroy()
        cleanRecyclerView()
        super.onDestroy()
    }

    override fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    private fun cleanLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.clearEventList()
            activity.clearTrackerList()
            activity.clearRequestUrlString()
            activity.notifyAdCleaned()
        }
    }

    private fun showORTBEditText(rtbEditTextEnabled: Boolean) {
        if (rtbEditTextEnabled) {
            oRTBLayout.visibility = View.VISIBLE
        } else {
            oRTBLayout.visibility = View.GONE
        }
    }

    private fun showAdCustomisationLayoutIfAvalable() {
        if (viewModel.getMarkupType() != MarkupType.ORTB_BODY && viewModel.getAdSize() != NATIVE) {
            adCustomizationLayout.visibility = View.VISIBLE
        } else {
            adCustomizationLayout.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        adCustomizationViewModel.refetchAdCustomisationParams()
    }

    private fun hideAndCleanRecyclerView() {
        markupList.visibility = View.GONE

        cleanRecyclerView()
    }

    private fun cleanRecyclerView() {
        for (i in 0 until markupList.childCount) {
            val childView = markupList.getChildAt(i)
            val holder = markupList.getChildViewHolder(childView)

            (holder as? Destroyable)?.destroy()
        }
    }

    private fun showRecyclerView() {
        markupList.visibility = View.VISIBLE
    }
}