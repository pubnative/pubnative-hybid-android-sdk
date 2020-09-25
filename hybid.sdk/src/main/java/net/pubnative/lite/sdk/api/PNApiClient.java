// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.AdRequestRegistry;
import net.pubnative.lite.sdk.utils.PNApiUrlComposer;

import org.json.JSONObject;

/**
 * Created by erosgarciaponte on 17.01.18.
 */

public class PNApiClient {

    public interface AdRequestListener {
        void onSuccess(Ad ad);

        void onFailure(Throwable exception);
    }

    public interface TrackUrlListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    public interface TrackJSListener {
        void onSuccess();

        void onFailure(Throwable throwable);
    }

    private Context mContext;
    private String mApiUrl = HyBid.BASE_URL;

    String getApiUrl() {
        return mApiUrl;
    }

    void setApiUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mApiUrl = url;
        }
    }

    public PNApiClient(Context context) {
        this.mContext = context;
    }

    public void getAd(AdRequest request, final AdRequestListener listener) {
        final String url = getAdRequestURL(request);
        if (url == null) {
            if (listener != null) {
                listener.onFailure(new Exception("PNApiClient - Error: invalid request URL"));
            }
        } else {
            final long initTime = System.currentTimeMillis();

            PNHttpClient.makeRequest(mContext, url, null, null, new PNHttpClient.Listener() {
                @Override
                public void onSuccess(String response) {
                    response = "{\n" +
                            "  \"status\": \"ok\",\n" +
                            "  \"ads\": [\n" +
                            "    {\n" +
                            "      \"assetgroupid\": 15,\n" +
                            "      \"assets\": [\n" +
                            "        {\n" +
                            "          \"type\": \"vast2\",\n" +
                            "          \"data\": {\n" +
                            "            \"vast2\": \"\\u003cVAST version=\\\"2.0\\\"\\u003e\\u003cAd id=\\\"223626102\\\"\\u003e\\u003cInLine\\u003e\\u003cAdTitle\\u003ePubNative Mobile Video\\u003c/AdTitle\\u003e\\u003cDescription\\u003eAdvanced Mobile Monetization\\u003c/Description\\u003e\\u003cError\\u003ehttps://got.pubnative.net/video/event?err=[ERRORCODE]\\u0026amp;t=U7UoKRwzVhMwrnbO9RagUREJcI--ZwAC6viDPbl5KhvGxLEyA9HR4hk7SnKS519akFGT9xE8fxvF2KYH9PcND9KCi4lkwbstVFjQIyll2uWU2h013oC0vyY8ZhlrGqxrBsKWgNhRK_0hDLr1LaN0v78kScEYQ_IQv-WD8nOULRrWMzqNnRPHvnPG9TdrOV_1rmfLjMOPuJnAIF31cc4t2G8BbAXScphG_xLepCSVAdyOYre8H_Orsv-kbKf6SQPyNLgb2NsQssMPIykNG0qbr7IOwcJ19dHAk-2qWXg2PYWAnxgC3ydY3kQIYxz65rfcZM10ugfcvOD2213JFKPDVV_YmJx3uYWLuZgY-5j6IHQhmtbuTJnr7DzmHpFdovCOXxwXZWJHUPb1qHnoyJaIUC-APq8QMCPIkRYv84lqZkYWLqVyozvmObYFbUStY4JPUu0zMZy-Ywu_2ml8XI2kp-WP0G_lXVNx8Qao5zwB_mBKs5mpkn6jwcRwfcv7tJXuljASsZNBI3BmG3QI7kE_bM15_XKk8_rKZq6ffDgpGt_usw3oKpCCprHhu1ccDBwEjDHkgFKqmct-C9Ep_CQ0jkpAVS7b3QsIKffJ4loMG_9_nWGLWhOa7ETkoyIfsWmHm3d3llPAj0gxKXwghVeT2zrFKOt0pr3vlHm1qit8EA28I-S4xlBZ34R2FD8cvYv0iNeVh77mu75ixGqSXfcWKx-Rww3Cnib8ZfpMcwSykgDga9irGm8Fr46Gx5klxVmwwNesLz2-OiXe4VCvPeqJ-mEJmmmqdESFFVFN5k0IoAGd_BSXso4Ce4TjH1FGCe0rcYRLo7Uge0mfi_Pznzvifd7lMToS6hwEQxzev1Aa17_zAP5AdlLrPIg4G22z6tC-Z6IzzzGI37FHF2E5Rv5Yg3M7Vlz_eS3ktN4v3JsNNr5oYSHNqqzY4CWg5bqRu7eg49RdkDrrSacxYf3HFcI7zpObYTRd5xOMAxSI7QIltN6CEHtoZuAzq9kW7RtGVpaxmsg6O2dHfEp21q7im5cJ2eeHwXimxygYM46CmJ1UN17lbtEeWRCFO5erhslR6XD3F494jJsEaS7MW8XecZN-g8-5bSmThM83tgjTKsLufY3EGd-f0H7x-7cgtbR_6HJF-WXYIgcOKL_7XKCohLhmGFOSVX9ap5sVMxMXkxlkj1SFCK--dQmWg5TwofV06SaWuqy2cqMdMiDvoONSCJjgpxm5Im03ELMQoE-k9lboQAXGghjzN4W9t-8moR5aTvW6ZedZQZDEoD8ydhdL3AeVLUQRmsiOIxYQcA\\u003c/Error\\u003e\\u003cAdSystem version=\\\"2.0\\\"\\u003ePubNative\\u003c/AdSystem\\u003e\\u003cImpression\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/iurl?app_id=1036637\\u0026amp;beacon=vast1\\u0026amp;p=0.033222222\\u003c/Impression\\u003e\\u003cImpression\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp//v1/tracker/iurl?app_id=1036637\\u0026amp;beacon=vast2\\u0026amp;p=0.033222222\\u003c/Impression\\u003e\\u003cCreatives\\u003e\\u003cCreative sequence=\\\"1\\\"\\u003e\\u003cCompanionAds\\u003e\\u003cCompanion width=\\\"480\\\" height=\\\"320\\\"\\u003e\\u003cHTMLResource\\u003e\\u0026lt;img src=\\u0026#34;https://cdn.pubnative.net/widget/v3/assets/easyfiles_320x480.jpg\\u0026#34;\\u0026gt;\\u003c/HTMLResource\\u003e\\u003cCompanionClickThrough\\u003ehttps://pubnative.net\\u003c/CompanionClickThrough\\u003e\\u003cTrackingEvents\\u003e\\u003cTracking event=\\\"creativeView\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/companionView\\u003c/Tracking\\u003e\\u003c/TrackingEvents\\u003e\\u003c/Companion\\u003e\\u003c/CompanionAds\\u003e\\u003c/Creative\\u003e\\u003cCreative sequence=\\\"1\\\"\\u003e\\u003cLinear skipoffset=\\\"00:00:03\\\"\\u003e\\u003cDuration\\u003e00:00:14\\u003c/Duration\\u003e\\u003cTrackingEvents\\u003e\\u003cTracking event=\\\"start\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/videoStart\\u003c/Tracking\\u003e\\u003cTracking event=\\\"midpoint\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/midPoint\\u003c/Tracking\\u003e\\u003cTracking event=\\\"midpoint\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/midPoint2\\u003c/Tracking\\u003e\\u003cTracking event=\\\"firstQuartile\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/firstQuartile\\u003c/Tracking\\u003e\\u003cTracking event=\\\"firstQuartile\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/firstQuartile2\\u003c/Tracking\\u003e\\u003cTracking event=\\\"thirdQuartile\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/thirdQuartile\\u003c/Tracking\\u003e\\u003cTracking event=\\\"thirdQuartile\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/thirdQuartile\\u003c/Tracking\\u003e\\u003cTracking event=\\\"complete\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/complete\\u003c/Tracking\\u003e\\u003cTracking event=\\\"complete\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/complete2\\u003c/Tracking\\u003e\\u003cTracking event=\\\"mute\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/mute\\u003c/Tracking\\u003e\\u003cTracking event=\\\"pause\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/pause\\u003c/Tracking\\u003e\\u003cTracking event=\\\"fullscreen\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/fullscreen\\u003c/Tracking\\u003e\\u003cTracking event=\\\"fullscreen\\\"\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/fullscreen2\\u003c/Tracking\\u003e\\u003cTracking event=\\\"creativeView\\\"\\u003ehttps://got.pubnative.net/video/event?t=CetXhg8qnGlia6cUTj7TSZLLpVUsCD5EiUapBTlJwJPpX1ovMECWk9_n95aRScNnVciB6vBmN_slrwiBSLjOfJjERdPXDRKlMcWGr8Z5tysGXgOWvd31c2MffydGVFip3kQWSMi0auUf4RcfPygUg3YqQycIKtAf2R1KZaU51-q-5BQS8Nou7dstpLrZBd3HB1zEjMCq3qK-fUbAR3MRLkwzwWwI0UDn85u9gsABo8ZSVCwhyy20m63NU8tq4_krO6K65DwOu8QkPIxf66vkkPvfjjB6Zu9GRQEeFXzldoVtfPJsY-1dSrdLOwygTmZ1qT3tIPzUyX8LHmFWMUPkpdmMjPCnzNcWUdXAOmGJHxFST4Ir0-B5L2ny_ay8U8ov1WQeFyZ08wDejc1dPDHg6FpFg-BapNW_-vv1sg2vGamz0RWOVuESrm-nDTDjsdMLgPG0YUAnCcG1tuJZpe_E6xfp5sbVyH38pOWlVqul3Rpk7iiVzRQ6cdCcWmM6fCu-YI79LTkhnIACItWmwZ1fcn1cc6A7ar9clZJn1qql07_v4Qoo-Wi3lnYNniRsW2Xw3HnfbUfF3Ok7GuCHMvmmzj-CZSf23Nl6r-EMIE8TZ9soQyGiSi2liwnomkLnBhIP-sjGKrholnPejyEeNxEatJRqXgkoAYZvPT9f-cYMKlK1zomz1qGbxswOjFJO3IA3HmiqdU8YhSh2XOOnrMoHkBnyirmTMtwH3XIEMorzMA2BLXp8AvxU17LnWk3DRrn-eXBGn_qK-loVE_-6L-YEL1aUyjf8UANmPRdG2NL-aU1Eu81DRPyFjOodR7MFwZ3EX3SlRvZxZdgaMNUH84oRbdeMaC51q5SS_ls1YrMVj3bV1INi2Pp-jvmKUvTQ8yp5XDdVKVt1Jz1Wc4cvOnLja4_WEk35mvBWzd6QCH4Ca3A_2iMN6XG_JC8JPrc3gyfNN6vg78-rE_06MGpVPo1ozpF5Ov7toRGeF82TrIQdhRl_jUGQS7NwkvVJG5nWjBiQow4M9rw9z7aV4Nr9JCXxddAdN1Uflzx9vNhH1oWI9BE24NySm0sj4nlZFCEiTon6MObv5MGCdRkNUoowLQ47g9PYKqBwm24je34uUxRFYBy37pCNH7P-R0jwMD0kn6nvEa7WBGtjd-tucB2YyPo-GoHsNrsudEZ_Bf9Cb3FeDJnDTN3pECiT8Eh0nbikvlaQyNTbWtXoC2w87pMDC7OMqyRbUKu4uGytJAOTAxxFDDoKa5BFWCSyvdNE7NF-oSLIQDDMQbtoqInRyuJOCf5CyYesoVaGkD7Nk1MVY3XJdyg\\u003c/Tracking\\u003e\\u003cTracking event=\\\"start\\\"\\u003ehttps://got.pubnative.net/video/event?t=sKQ5_cqg5u1zCto6D65D79nNnaa3M2wYTCpLFFn8a1JSYzgq4IqXT6pnfLJ0crpfh3rjqbK1vlB_giC9PoS29Ch91ugm0dj4HOXQGamTDG-6Ej6o4D2LysCaSJIvtVAcWcuw5yDrHU3emLaw6SJIn7ahI5AyecAd7_ruO8jYbCG7kAq38avStykIXmn49K-jJRzi41uSyEHragyIQK1ITJa-3zm5Y9YUz15QylFlBhKqPVc9wnQ8-A_n5E64S0fTcM2dyhifp12EU8yb3j1mxy_6jyw3pDqjwY5aM7IGWa-VuDsvwDZuzBMC8vIkw2UGdOJiWecGieTlALN-0iKF0LSV8jTdyy6XgNElWBtniSC7qeGWAjG9ndZJToX1K_8H1BYmA9I28q0qe1kiDdLW3ybEpuH2WU1bRF3jLwWmFO34troyw4fHUBz8uXCIHyjgIfxwA8M1PMzJRov5dl0xaizRKSTsLn-Q8S82g33EfaAh1stA1T28ORJ0iEf86hNt1tRQCNYTkCQlZVRJMbSNoNuopcXHiQMqhog8JJ7pL0YaHa8OLiPEZ7fE71iXw6xp3gSUHXRAmksZmhhXlCE11LlqF5HJMoQ-2T_q0kBwZkn0OzNje1SyCnu2EQVBnf7_ZIR3bzukeVtNc1RKGlYKWcROfn1q1aVu6TNeGquNH_4zgQUCxwRmR7y3-p2Paa_J0VJXzduZMvG5tmLgQDJYeF9deUYk7o6oObf-jt8-c6_AU1cJybSb8ch-n0-Ul6JvIBjY9lNwzZlK2ElIew5fEt2PFIwYMOOM_DBsNeNqdaMY5gwhfhPpl1nB6VnC1yciVUKuZ-7uC-G7H7bYCtYS2D8wUdpmht3uqZp5bJQNKTVL6haG0gR7OFx_Oz-V3IsSmdtxiv1LrUr27HTft7OovbjHIK_MQMEr7c1fU67AN-sQzAYNgWcjy4ZVUu2dRJ4rTojtVBnBTKfyB7DreGmS5DgdF1AsCOvU-kzsSw_fuacN6e6vvtQWu-0TtN8rEn6JPVx7t4moF_P56nPcbDwRpYA8wzfI_-ocFgyVAGm-slVxFVDChtVI8876wzS2KUBpXevW2X8QciPTqFFjrtijDCXp3AYYLN35uMMW-OAb7Prshq05WLWBgE652Qy92DSXvfH4YTistzddGh-L4tRMq-ix8lGI56xt4qAvZ4e6pgH2iDG3kzyb1culghaOqBIEsjVniK-u7smFEhhdoPhNZEFW1AM5917BVVQncjhbCapdUcFMnJH2mSc8kOrzt1rAjdkPBPWrP8HlJBMp2A5I2cR491NFJhHXAQ\\u003c/Tracking\\u003e\\u003cTracking event=\\\"midpoint\\\"\\u003ehttps://got.pubnative.net/video/event?t=LyNiGVtCxZ4kblLLWVNE5WTH5TmRozv833-gId87qQ7_9YJJ-g5m52H_7vFp40thF1ywc8DOHY-xHSBJVeSG0vMxRXwCInT9NWEvsNZaFiKC1eVcKk9SuIJVs-ttlVYbvIYxAMQe9rPI9LTYLEHbE5qwZ5MXKu-7YJCZwh1b7fdzP4SkmvLB-3tLNH4cxF8MAXvAasg4zDWPpr2iJyd67hW2xqfcMivfYdMDFBRrxSx_2A2z4C_ydXj6JrRoRGv8ozFpGViwN7QKcaB_CvCrvxi7KPjSjdNUWWRKB099WEtMVFkScarY6obO9x2RGTVSN6vN6vw8PIg-0WC6sjKU49RWQxxXKv8RVIHy2jw5Ca1ti7g0HKu06Ln9B05EWg9WKF_a2dnRc_BI6LMnIWyNEk6RtJyNuhp5Egm2CB-jGA6Vqrq67eRG7WIjO_tfkqbesdOo2fl89fDzhQKhCpqPD71X5d7hGJS1UQ7bihRqJsEls8XT2zXgyyqd8tawTl8BKG1jcrdyQUv9_X-zDWxjGxdFul_Ds4NwtRa--iacCib6-dqaawNtWANEONlH8RtXX_ff73_KNzV8BISA5FNbWW5OXSWxV8Tsb1BvFREwyVEQC7NcqvXDhrKAxyhwCX3ZPA3Vt4MWmWxGbW98-xA4ZBHQNkovfn-8db5lN5KGG6KqCbXNIamIfYx59daDUqA1G6w55_VVeLjoHfn2uJXGglbBtNZvSecYvR6YiUHF4UuEesti9nvpMzy3AEZy-HFxQvTFXgU0QQSi3xDMpF1Cc78We0RLbKjV1huUNx08zRR7rieKfQI9Wf3qXxCCqFR_oGXyv5MqMUBc9XgxQmDBFPYvKa7DM2p4EjkazEwWO8F4q4R6hbWLrCzt-f08eU1ivX5izUQmvwONHbk9Qbo9yJUD2ni6Ek-Uy78ieGPYVNlfGEZs_xKllbsn21rkZvmrN39Z8-yI1NvqprHzu3GdTITbE86nzPTDqYSnYiBcYNCiROt1iIfKbZdWkrvl4yR1eUj27DPV3Hw04IGu8IWJICPwJJtgFK7hTgougHtO-Ol-xUV5fqwVym5mOpO5MYAOQPbNiUtIfLmRzHXeKJbserxWkowiD9O829iEGu2OoNkzeky3xUhhgFTJ2_-8WWHxQmqp5KLm7X2UuMYHudYORHGQpCdKFXJ54B6BF73vmuPbvp70L9_IZ6cN2F6UyMHVWtRYGEhVuisjOK-MHU6EA5cJrvC_EAsHmajRVDA0j-kKC7_I2ZckBcGQD8HGcWQkADPGTsOMWwIv-yatUgYQ4UjeHqZARPN8RGB_RQ\\u003c/Tracking\\u003e\\u003cTracking event=\\\"firstQuartile\\\"\\u003ehttps://got.pubnative.net/video/event?t=hC9twl8DOLcRrjHO1sHtJKozAlB14GX0TkZLJQ8ZGROLpbfL-uCDkjc6DIH7F2WukGwA-uVbPsGm6BETacvEIL6jwd9XXp0ovzIKQ-aOntta2eF6jGbSUsnZ-QwAyzV_KiU0NKpXnj_Nd6UG3P5tXRyjUCKugeRbGR2_F8xqxpgHlcS4Ifrw5KFTfy2OLWpiCbu2BakdF6sb-VqZ0_a1p3JyDnlSgTGtl-y8NqzVnvlbIt3nP0yyLA8BnBQZlh1gqPeoqQrxunya8H24wGaqWRzpiAKeyXOhlyPMJIiFE8sjVKyRdUB_VdIqn7crOITvm6FMlrkD58HuHmohWRFJHcUUVxvy9n4DwQze3PxOa_MM1fwPjY4gU90fyJSwouLsElfYyFWHOOPovOZCpLZmTMSIzQDZZ4kM-wlVbX_yecUUCGLBMZ4k1jUmXazBUaJJAMVwf4dYGWzMLblhBy0geoP_6PtW9Tb0PcaP4lI_vXqTcJ7mDOfdf5KMnW9xpGs_fcp1m7Eg-e_EEp-Bs70l0MmYVJU0sUQ_7VCMSB2rbDzslJhLNADShdVpqjUx665Bs6_dRYu8Y3NBBQ2RwmhhZSyw3XvqzeQ358WyAEQ7hECcAKqv_mGRI5RETTE_uAMUiyV1SgW9ScJjfxBe6ZxzJL0H0t5x3x6pJRLvA7dSH0u33FkzRl8WJ980ybhE3ROmRTH6uOlXTchG6HlGVxwxUddN7O3JPwW7nBQJ3uguXhbLnJ4wnx_jRQ0CSHShjaAJvT2J5m44ZbgVL3cx5whZzVFRkdgeMTuHvbOJsEZyvLZxiN_urmZR5wx-dZ9-Yl6FA7oaDytpF3RhQsW12wCpMSWOahogb-gs3e8ypJ82tXMKkIzGDEhCc6vYj0bCUOODyAJ6fEYvYF666uTGMGXRyqHO1ZyYmsslDKJ1oz4NPN48kH5grdOmW6uZUSMVo-uTZXClzv4j4gyMAY-7jTjoUeP9VjWKFyF9vYzNWOXop7D_SVx766XI_KuRFM9RnXnujAMIM9UoRp_byK6VDAceh6Ngynv64icca1_lxMmogP8jOfKhUS9Vf6axTz6I7c2liJFvfy0DlYZZLN-WMtlQTq3BffZyRaK_d_Q6S9ugPt965Jn788Pa-jUJeb3ol4MSapT67eNWjFH6lcLlemCJfi1CJ4aVQI4oinXTyMMLSSVka5igECD0Mhd4v0BimrjOn0EDpu0yCflMoGaMG0ay5j_5uUlA79-SNGlyNbJgzu1waLbwj_xl9sXDse07vmA43DXtC5NmLulTKzv4r-avo03S3B3SJC9O7KTHjcDLBS8Z\\u003c/Tracking\\u003e\\u003cTracking event=\\\"thirdQuartile\\\"\\u003ehttps://got.pubnative.net/video/event?t=ybLnL9lDREM3_XOy8FRhn7a3rdZkMQL7mavivUeW-c2JTSulRaG0RWMyEjUm98e5sRvhQp-OkbQ44EEhcVKFxUsrthmMTpTYkBLi7Jk4RLKRay4QliUdmmi9Av9sgMAWx8w9DAUfa0rjKKMB1kd4IjrxXVl12qk3scafDXRh6HQf_1OsM9Ie-VVH9F9WOgYLNAwU2LUCGAG2ht89_p7Rq2TmTJMqqhIMoWrzw8oJ9ZHCwI2FwSba2XoFPReqUIAvc5DHYSJ0YmlBysoVGlCGLBmb0SGPbO-n1jE945239Ya80o6cpcUpvzW4nTyGE4KZNPlxVMGLWjKz6bl0Ytzf9FpBjyzKMmCyiQnaOxF3V3uk19A3GFQMgaN2iEjT_TwxEmS1bmcdaY5EVleLOTpiOnAZXBSfJCrt_fMGYz8G6E-fvWanbHBDi6vlzw_s9Qj2KzF3aN9u1gOpY_9BpFGLLyIXOTesRe17xWbHGd5uXF1DZobc7oCXMCK9wXAh_rLDcqgS4qGWWgpNxAzVcghQ9SUbylVR0mrywTOCKKJhIKMemkOdhCVZhl0spYsa0hN8SSUEBxOOA8MYzH627IQ4gZiDs9UM4X1J-uL1Zo7q6-ZiXxKAr4BbjXP_wtHMldt090V-QFKQshbWGaz1OQsk4HR9V8t3xM8rxuODFXKC1frHnLbEvPgOCFHho5QKWvYoKFokcW2aQXdGHmCApBSNM-TY2m_qmExI0ubRvgzhbwbDpxpM24dMIQ79O5yEXlIxWNXieTSaPVm6dVdjC6pVsN0gO1htzyk7Fu4na-XajDrT85zdV1Bzrv8VPm-DRIB5c2NWlp5aU4ZwyZhKV9SIt26mWWb6DTzFjUJcFEAFWXCOaVYJQSTXEdn5DgrNGOvFba0RiMqiyju2GmsgMQJGDRmr5QbwA8cgEUMHUT-N5oOqbo8R4g8ZcMrYx3mEVv5Wt2dUi_ZAZ49KJ87T2614x4eiva3lkTi8lU6vA6C8rlqX32_bce8aZKofIKkYoHaZfvTWcOqaw7QqOvK2mK41NQZsIwRYUg70fOxigyYQ2dnit4yv9JqAtzrF7odGAXmUL0hiPoS201WRkyV4J-ZOb9dhX5bkAKPj0rviOW_U9dVF2Y6T2RjqMOuxi-bhTK-zdjFHj2ekKpa0etweSkSC2Lw18H06Vr9mermCBz-FP0fcwP_1vhQ9YX0YfggVTca2bHFVieDXVbh7ys5Cb2bKe_StFtE0aYSBMUvZZYEZW7W_a2V1rywwtqNomqFw_0jnMb2YYO3hTmgynJ0Gm5FmsRFIIMKVzo4XV-Lg0YHaecGT\\u003c/Tracking\\u003e\\u003cTracking event=\\\"complete\\\"\\u003ehttps://got.pubnative.net/video/event?t=aPy1qJ7eQvyLYxvDy_PF6C3GLeLQis400_a8YGQACgLQDNhWofeQdvZoSIOB_CLs2mB9-eK2FloBNghK0lJSCiBtxGqz4yQt-jNFihh36iVYfWGXdaaZNM2_hcoIUrYjG-l0ZtfOtQhtQwysXItzJUGVo10TmEvTd1F2g60gnLdBJYc89wJulknBR3rZ51DESdtKL6vco9Ue7QR5HejzJwwGW6U5T6WW9xnstBHvsdcA8pXG71qmetJYKrjgpAtXzuTHp-JXjGdAO6sPjqclLhG4TqolM7TN03qU17pFvWu_QRE60kSqsAsewVCzM4r3Vs2JBadFvr3nzyERHEg9wAW7kL2OsbZ48Np6O2R6UBpdC5z2c8cdsdqAfFTNSBgSw6IxA47074B6H8xHVN0EPUnLxXHTQY6YI1XDSATmg8YeANXOkx_-GZdidpU1umX-PO7wScpKqMhsUNvrSNQTHG0o03Mb5uoIFCvaeM6jxnt4fEcj1TOEiZbuntbrCj0V85yX9QkjyQnHle-SgGReeJgmn6pdtcQzTToiiTcZV5f82cPt6FrbHFPR91sl_aaGsRGCPvYCDpetr3CILRaerVxka3evXZb50N8HAEdrA6ew0v_1lQ3dqOdIUy8KYRqgDVNjBszyyPlQGMMONJhy9aG_h0ZtDPW5Fex4cz4wXHdt0XVCup4YYoz3OgPxtYgfipfe43ajsGZTk3zi52OFM36MutjCp4beLKQIM0GnAT5YroDQPTVkWqBWcVp2kg3wXSbuItXiwl33AqH2QhyUQMmNw6HR4yHWDHU_Y0S9Ok7gEURwFbHZuKlChRwwI39Ir_GDX73YYEwRuwhTpwdYflzg94X0s7074twG43HlG7POW74wVzj0KAAjMszQWbSrLNl5fxcgD5q4gDafwdBh4BO21i5B-PqjJ89UQFSgLgJFiuMeppgwKoTSqCmgc9MLudyXNN7E-QuqUS2mKqNsJra8DE1aEja4nW1pdAyblpW-FMauLmh0JA5lpcTID74i0rVwY9rT4xlf7l3xf87fiRWf0E4uSswInfxu8OjUjuzXitiRh_jIm-UXQChUAkIqrT7G9bzKEJUS7WNvW5PN2AsDeG7tthmRKC7iYk0y3dLiSc6sNjn-VcK4kfTs1Ai0qDYaeme_rxYnTz-K6cdIkvSygo5bQ4skB5O-VreTkXrbRV13OLvodDCDETwNPfGdYdmYQPC1-NelA1IoylKOPqzyewCB8mk1zMwV1E8xA7tMJppJS0AGOX8ub9NqTbL715Nj1Lvi08BgwltiOwlH1ZaJ6eNc6igKsj2OZA\\u003c/Tracking\\u003e\\u003cTracking event=\\\"mute\\\"\\u003ehttps://got.pubnative.net/video/event?t=2uck0HMckFDhI0ezGIMhwsEmA9vVqphXIcHNWM8FK0gi-fP2KNb26V_hdcwcHiUBrKZsvvn-B6jP134i3PJ6qDpDLuMCePx6alJ6zO107TTji9sAL4S0i-Ii7uVHRL2dLlTEA1NNhU3LzY--V1wvBLGBojqPv65U6SnZZQhXuDpGE7DcaUPNkAlgPbjGCbIov5XeKmVAm8Pmv89CUb7RmvFcnYzJyIF3d_LwrocWtTZP_UTjR_0aOgMCnLZYxiOkHdvZ9HwRpVN30ZQVgBbKYrmTnt8NalaBDt2pBW8aZlhh_6oX37Wv9_Pd-4xH3lOXpgRwXINNYNt_UCUykbU-Lw3RcQWs1IkMK5ayROrBnGWjEQTbl_RBXNU3sLk0cCZ940ZGrxUAznWxm5ABNXVIDPDTAcNWTx6vclAQwAB5BpC5D8Z-ZGs8VxpjAICmtwfi1xxefHZHrP5m5wiL1RbuWnGB6E7wgYUGiKV_P1Ptdo09hTTlA6qtHQ0lcKjqay5LuX1LIx0JTRSJGOUf3GBmP2UekvGCUhwcwOQGG6CWSJTfCtjd46hrm1adBigy10F2oJbVIRTKP-1TTBtm6k8ZnILKFzNq9jH39JgltQvOZUUEdW28FEqbIQcdLrJ8QGNMeSI2EP0m4MWgHN-2Ma5PuI_AWHI8iICGx0jc4YS_kEXSwFVm-mMpMihaYBzb3-elWL7hgjSZGsZsuLoeJthGfoRWXcoAq0IS5BEHOkVSpkJFzCQc9fyQ-DkyBEiE5w4zwKkjS_DbUbvEAjmYiR2JeHPCJw7hnJ85LIr_Qy_FqIDiDDnj67bXmQA3f0yI70oE7aOmyyXKyrGqbjzfFuwcKSlbprTpGSJeK0LTjvzYP-j8myEP7WDHSVmhCGFzzdb5QZpfs1CT0JQY__QxBBJW79OybURcrr8e0nFYvkDKE66Zx2A_AJ6Oa5SgYqBBE2caR6wSOnQHLazqZW7rEIg-cxaAkL2GlgCX03EJ_Bj-5QYWxPOyflh_KCqhOC067OKBAvbPi23S_5OsTVL7CX9bwgFAGfdT8whoQdaZ5kO-eX8Gn4lMVv52fEGD2zMNRgYOgtUTu_6vtnlGJsUIS1BEf5dN0jaSOtvSmzBaUmSP8nx7IchcsUhabb2wx5-HYSxOfKS64OQdSqZJ9LmiX7gNJpa_ZrAfRrbTDVFWAhNbAy4aB-mxPr8jjQ7a1y5SDOwojExDrdmVXx2lAPuzG2USjsKwTZf9D6FqUOGoLj2COUbLT1vXUNk9UMvwqFptqnoMK9lMnvT_EZ2QhgQa_pPYPO5omG1Wy6hr\\u003c/Tracking\\u003e\\u003cTracking event=\\\"unmute\\\"\\u003ehttps://got.pubnative.net/video/event?t=GQgJ7EkrN3WW1nvrlgIuSbuR8U9P8EN4Z9yMzEeQjMnB-zDGnJKtaz1_LBBJXtOYkY6nDvbAB-r6ztS8gOvh6aCDyg5iQn6HwudzTWlQ5E4KIVDJuFJfRs8bPxrPvAFGOTeBnZsPWtL_vws51Y5bSQh1iBpqJ50f8-TwqX-VsMpq_CvEXqrDzVYqlVbP8AIb1mD6BPhp0wOc9sliOSvZbqMeTaiZb9RC7psHO7d0gw63nF5DwKLktOKuCR7EqfblbkcPak5M7xriA1GrsXVXjZ4ge_D5Clxb6G2DYVN53qz81qQQ36M3GioSc_k5Lo8n7Jr_VEwGhyp_N988eA0gL5D9V5J0DcjzVQ4b_66LyVAOUk9sKb9qbFhwe0GbLbj8kpIziTwyz6dHz2SypS93MW-ejD4KPLdciu1_SSeSMfQv7dTAtQ9d3T5EVj85Xu_tEg__hOpuUwWSDqRshBssGo3APJIAEEHVdWrK5Hyb2cFmPuM-Aaw-Q3xebYi6EAV1xEVzBR9vrF2yaff2hnWmBeVjggjfeEhLN38DE2qcV6ka8XxBUSSGJiV1aE3dyr-d8fdJgdujQW5VUejOdvoLKZkVSq5cckklufP56rXyi2RDQ-XhoxVMDCNiXL1j0F7AbQ0MbxonvRmoDCRUspHey8NaU0c8idp7f0Z1wmGGt9NimlwPe5DZD1bP3YtsSc5UDNyRRZRgciSsi9biPV3F40v96KiqgncYktqLaB9xoTpAx8aKF7MZJvlq6KrmwIsQqbCrUfETbyvqF_QfWf3ineePAgUikyUpyMjO3OtvwZNfBlhceCxQ5vAbThRp2X_fsby01xVMT1lDvoR2GNFGmp344q4e-uA6YtbjaPXiPQC50ajw0UBDPOpCurmCD20A0vyhzPhnq5HSxytG-LwaIrLHAtRrj6gcG_Vk2B2du0i7rzQ18bpFizZWK8gZa7DeHFPeNdMW2xhB_AzmDEhybyXCyAF59t_7yXHh2KqAJLXPkx5syeQHs-TXPGXDn80bAOKZIupxTmNMq_7WG6W2F0Q1XrYMIkSkIVM2sj4dRKF9I3Vk5KQG74kTNUxbdMIwhlfdEzZPp0Bdb2AUQoLn4OBWmPZt3vjKqnx4vBlli0xDyT2kaky337qdePQUqNDjPnjlMAPaVbr-oCRAhDVUHeJd0Lz2POxqOuuA0Jj4sw0om2XY-FDhbxn7mGTi7K4xjSJR9WQXIBJIZ6IXQSm8RKf_juIzbro4695DRUCQiaf6gJHwtgjAcEzTWZ_KjMTBl97HHylypqujQ82Rw6kwublXxI8U63Ljmwk\\u003c/Tracking\\u003e\\u003cTracking event=\\\"pause\\\"\\u003ehttps://got.pubnative.net/video/event?t=BevLHNuRzFiNAv8nqpkb7N27yTTvNnWQ5wvNDEkBGgAD_VLAUH-IZQCFVE0yGj8BkIexQIgGL8WzoHA-puOv7fJXqNfeSpEF7B_kkZOWfhfEf705rXuZIkHrgzp1jkXTlaTdkggip8sZiCUcE23woEDt4Mkhf8X0SKtss2JyhOEJG6btlWYKxV8_shTobro1OXEcLK69j6vsFugz28h9CUMOkNgDJaKo5LRwoSbCvOtydSJNv3mVYXZF2BYu9CPNv4oeVfNQObzECDS7clqJmu1FEebYYg97qToHuYFdarfBn6zDOXe97QANLPPSHYz3lNg8qlzoS_Y1-z0UADjfqs0O1ZTb9SoOsseFHcsYQGHvEF8gNeeWk5hiyJ0wkhTfbHOO2tvFV6UMzakLi7WTGNRp91fttG2cptTRplH_S_fVDkUeWrYGXZPJh0S8aTeaZPOsYHfRxgRq7fMC6kpkcF7zEwobRpiEBaMV1S8Mtu7LRjFh9wMRcn0oQgoGib4dOl1yyBZWesWp7bu3sMBhavcSI7SlRbt3bchqlJCMKkhqnGp4z3tWt68VyCSPMeGFNLSB91U4snQph2YKqtFyk1ExVaFMzR1eUrhpXDEm0HkHn4cQeTZ-PEympedh9Kke1IvsAzWyKmC4lAc04vaokQ7QimM2fzWMAowLw9dsLHxK119EHijmGFrXkUT_kCedtLnCuGSfQldO_luBKEW2RKUVRc9PM0SaY-oB-AFhoqCGcIX9bggI5Vo8tYC8DlrXlDYplgKxvy6ESswoDPDaBExURWz7KOkerYY1vADsTIc7WWz1ZHhqiMGcCBdOv6pILAk6xxFnLIgooZkUnmTjh_nC_6RA1UtGXuQ5Oi_nMCl00CE_z1dt2782tIRs41h8NXlohNZt61YZvBftsyzaDOrPHFo1qpW7BVAcT54KgCDwSD0XHTvuYhX3kipKLa6Pwnys8o3PQmdKchzZDqDxDc0T_PTSQPYfu6o6v84aOWUXqwcsoARZRosxQYRxGFI1L4v3AbmIHKmq0GxO3BOJMcC11AW18bjWMPj_ZsiSEeEPEJG4TtIdfkOQySiRZEWXhY_Z92qC92ikP-grqVr4PwzJp3Kuomusiy_pSOz0wWNoxW9Lu9PC8j6pPnnuvJHSgJZ_yajHeohCY526F9lGGhh2xlr7zpXRqK_gMJ2TZfR5kP814Cz930iUd6vJRLO3XxHgmOvJG45S_58oDqYqU_dVTYsoe9hMk36Daszd6QHfDhb-wEWG01T8YKZ1is1zS_bqdUm2MaMteiXIsQSX3Q3Zb5UxfpWwEQ\\u003c/Tracking\\u003e\\u003cTracking event=\\\"rewind\\\"\\u003ehttps://got.pubnative.net/video/event?t=ctxnQW8kDZX52VS0RzE6GawxjvL-tX37P4z8TsXLCWx5BOYbBovRA4TNk2Jsua5MadmpGBoPuRIYG7UW1-5gkx0CqnnV1ba570UqHNBUBZRI9QrVqvrhK3awvkBC1vNVoXPBqRxszAAn6y6wSBLEQnnufZVV_u0D9MHzmtkvFt1_bdXKAyNVQ3lNZkg7GCQr3CFsC1GG_eMF1wpB_lQSyJT0hrns-tYSABVyYCk8k2-aVrj129vXABnMdDNIhvVZ2_XtVWeF_OlnULr_ZmL6GvcCao3HkKWmAG3dhdlyB3d1plkzokJ-uW0kLbiKMVLj6TDkLVm9xMGZ-0JLm6jgvzlF98h8YOvNsD3alxQKwHxFBIK038XH0vbKCkqbR6yR8hp8LQ7BMfMvLAnaTFbwHDyufVuJdNVei2Z3yJLTD2r1oODm5YGfQ6-aK2WJXOaHIUoWUFSKJ3dNcygxbD1WiPgsVgcCcGVMybXMzveqO-QyO_NxHWUGyawYRG1bny9u54HiWFtTHpw8ZQ0fgSYUd800g1_vPhwwJ-nVI9wM-CN9HjGgj-BZ-Obd97pgosnmOtVZ6OORmy0Tw_bqJXwtd9pM1tjpyzRfB_EuN6YrWJOedtv3Bh8KO43cVE4WNWGpWvYGUWK968TwnYWhwAbcY6A5nBA9Q-h7gIp-lkoZo1-_Xe87lOBrvpEmX97dnxMmSfVURXpuLvDelhHwRHsRKmpObo-H8MupUWq2v8OO2PBxiytbjaxf5N5UAmkDsiYKr7kg5ATs1pQpOEgaO1biNtW8PaScQfEAQevfmrnsGhCCLei564ZRPSg6Yc4etQID3arXxoA_8vbprFQifT1-IqPlM_lkt7LsdPP7SQEZC6vEaLS342Mx0MHt_Wh8RscjrS2nzN1gIPkCVRcuq-1HWds4sFwBSEiD3k4tgRSa0EyUyUVfdeAVoPfoudnUercLAiCivzatVRYwLG7BqkCmpAOcY7wBpjPrWR6q_Ok8XykpDqUzjwSRfUxcAMfIIaCIkBL685G4SR_AcB0TCfUZoGbxoHF_xKeA0R8H2E9XXwig-IXNgXzySa6SSy5I-Y-3mJaV0E-Cn1Z2T_T71So_uL5gQroUN9fxseAOphCwNO7QsDv3801jdSWjWIktzL-evqItAoPczhewl5QKWHS4cdOz68nKczaESVbRtiAcU57qkPuRL8kaEw0If10tT6_aXwqN9lCCiqEzLDZhaqSssVTMw3CmQD_cdpnzYkTgJ1R4s7xagn9-eYXqReGsr7vZ2g4Qd0czirSRxnratEO7qYklzPgOloSUf0k\\u003c/Tracking\\u003e\\u003cTracking event=\\\"resume\\\"\\u003ehttps://got.pubnative.net/video/event?t=ZyAZj0BKnfeUOwfvNln5ifIfBZB_QSu29Vy_pLYzN_537LX6ksVWUtWASCBLUWEiskNJVLekL-huhbpGzjZD7FZs5zMqsL8XSn-At5cKoTlisWa2hZFOgGrKwKw5Tde7CNR55ng3V_FfGRh47CfA9A4tHndMEefzZOsiEzwlr23xloZIfWm2wvExnLC2haIh3mhHssUKP968DFJneWBPwf7vjC2Nvppqm7kC6dOXQxebSsYMOo_lqktAQIvJYNBwwiiiuUENO-hUhKMvelq4tsFav1pYAZygN8yxmhJmKUs9ARyojbXpLfvMJIUF1xEWhwlkkQhGGS8s0w6Gb_3V58wVUXSZ_0ZreilDmUGfv_QzhUuTcQkcTWKozflDS98klDSuUwzmLKDpOVMhNre78Vm99Yp-2GHWrN3bquYsjZMH3L6ZmlkRQuzzKD4hER19LkjVbStiBXGaycAl2ygQNGt5IjDA7VnQYCTr2Cs8iY39ofU6jP9FmLik1t9Pl7LUwM24rcejo7LTvsciIC5Aic9RjQ46wavT-q4mX3YPT1W5kzcf9Hm6cLvf8_IBY1vRQPBzlyaxjvNY6JBqZ_Xaxa9zJKrbXRMzckJm88ypIibMVsD8MTGlju9h21RyDAHeWhddxB1MyWn3o18sUX-f2NBdVhwU_LRoHYJ9A9OiFfucxa0IWopoNuOWlUXlWdUcLpdI60G7q3djdQ8gm1m2tJfdWbkzav_hS6sPyIqjMwMFKfHhE41ag-eprNtrriukrLxSijIrimHvgCXC5oLGbh29XhfvkixNfRl3NAGZmUoRbX39-65hJK9IcEM367ZuW02YuOgOJAim0elc419eAhWr4_LxvBx5Erjl1RLqN1l_OMnGUr9rQ8v2Ae9td9wbQcTdbEsfs22UPlO-Q80hqZP-mGHqXfh02SvC26H6cxpd8OjBhXYTiYaY9JSdsrJKFHS3e7Kz9ZLth6BCIRvHhLIY8QXYQwzm2fikMtdYpPyE0KOq0Qq7qB_3pHohEWPvC3kuaSpmYiN3z8WZbIUZbjaBl1N-DuHZfDLsPagiKDy2XENFQZBJI-TSjSbiRzw8qQ1Q_YlOz1ld82irMBlj46gJ9LOLhA4yXZt-CFhGfPtWh93LYFmyrJGsKAn_mH-lcWYitV3gsmfSptGQ8J5DBOGCXgKlR1IoJTnqLSkcOIKgNv2OclwlPQvx9WbfRV7vyfvYw2T0KjGY-TdzBNyTOMN8wWuiYYjlqjEb9brAn3ID_JvBIWQDsvLIwR_S8QBsOnYN1mg_WWhdFOkvu5QvFPQsjc9OMdSowOk\\u003c/Tracking\\u003e\\u003cTracking event=\\\"fullscreen\\\"\\u003ehttps://got.pubnative.net/video/event?t=XVWFUJAUK5_MY8iK7UgRXSL5om4bxy8ahA3wKAbRLnyO4aekSEYyPoTZEemLuBGHuidw2VM6I8z4btf0quqB2AwZxJ18Wa9dJGU2Gv8vBihrUgx8sp34ytnKZfhbLUpFJrNgcbh9UENdj6XEBwYfb6hwAqElxl_kfZwMEWhn97UU6mgnQC2mQ2AE5iQeeuFkkR-2RQQSRiOunuVnO_Stw_lUL7nM38fsFjsPwAF8v4NFa-HRl4Ko6BFR7yX0hx6Oe3Z9jF6FUULwfP-784B3ShVxMd0wETl-laCMJvE6S7gbWd3t6-WCS3P7FC1uOyIV5K4KmAPIugjBbZkweZMu9v4WTH-ap6JuhtABo7qwH2bp5odBiYfvKkEqXvdjshOwrhi6EEZf8E-9M1j_tHCQ-0bykAtG6VfCiy9lvTPi-PmcbkYPUrJbf7cbmv7nqP4_KwAIVpz1alomK_2DEPY-0ZyIzZozg9DXES4xq5SJmpppen1guCTTnUPTPJN94iuET8UzBIDXrPGP8BsLoYAjcLfvx4QzpqaMisEa2DdaUVawaLXYOF5UCfYvIEoND829FIZ-0pLBoJuNsZ1ayq1sFapbGC6jTPqSqieQ-y8LhKSfagk4t2IvLpDw7OEpzpV89O2YDFzrXLwPm_drW0dKtPgvWrc7q9iZNineqif5ogE0T6HlWQIRax5Lt2lMFcNkXli_OMvRa6t56su3yjN6ZnTOkSGJwsY5teFFcJ6-WGkXexgU7SksdXhkVW-tmKWlS0AkGQ-dEgEVsR8o3WFIJMb08nfTQOlI_bMAsJxBErEhawMxMkOAUSxoIOBuoR0i_UKKjlWP7ny2NmGH1CHV-FdK-rmsLkniJptvUSsoKXcXxtZfcFYwevwq1C-VENNPTbXKuMGLcE6-uBUBhoNF8n8rmarELoOn-nCunUChs9FMG1bWh74QnZckDowL_RwE20rGtJ_I9NV2EG3g8-zd3hSh1OGTUrNs8LMIh6hA6K7IocSxlN4dqkGoxwrSd5neMbYTs6qyu0tx4Wp1x11l27NXRM8pkX_VC4R_lJamr-bqzrqKH_Rb9iLFA3mXjEFm1bKBN9GodrDWhkgfAo0gTkwCaINl-cO-rQt-iuXKFxsm_axGmBMlgZ7cG5RnJK6n3hZg80UzkMd9eaHPGhkL8xemK2fIAGt8hcqyQk9HQDlxgv_c9Jaiie_nxHT1SSa9dayyHnKkaAOGDGotHfSscq5CJwHBy0vYfD2jAjSDsG7yx0SGhihzHgve7XfJdgwnXBe8v1K7aHlAQTAXSfTZgN9h5t6C5MZNgPiei08g\\u003c/Tracking\\u003e\\u003cTracking event=\\\"expand\\\"\\u003ehttps://got.pubnative.net/video/event?t=av5WrKUmbS-gnps9gll3LHs7rQgXLHvzJps6_--0tejty-MqeckUkZSrDqvIcwTzbxRa6RprZ8wAkeo1-ZMEpLSVGxWDIuZ17TtSpJuS-TVAZkca0GFC3YTnphskzuYReHqTgKHM0ogk4LfIaB_oMptTs270BDwoRw0ZlCw6HGngzUhtrijlHdu2VPDbDOStULNd5Mwcl5Q1KOKoGLaH-Ru50QD-dVyKQu7U_bi0hcvITQrdFedMygzvAJURUBff0mUmfMH9W4LTVA0eTaAgpPWenFiGXjVmbn3A18yUPESLCTo_2pfqZvw3-6zLn7K8zuwrqOo5z59i9xu2Zn42NjLG3E2en9vJueb41ficAYg0Dd64VJ4Nz3F58X6kwmRTHuM5eQuPaXw4JrU33te3kziqJ6eOm9bJ5Lram3ZnxQgpj15anv96mzMYB__IZZMhSIULkJSJif5epE1-igt-6UkRhvF1hZ2PcSTbtr_9Q0HsdgLsQgNmZTRWwxLZcIvtcEnAVSLi-qmqTvHdp30Zca7A3elwHAtAkAdZT4MPllsoGOZzKpqvZAsGk-BmkOQy_4y6U8t7scYvYfA5yY6UWiGwtNKz4x2eoUSy0vrq6mhqIR9_I9TTQHSMqDcQXU4tgULWDDjANXktHTM3diHu985DntQNKPQDwA0R86APoAkFss0HcuYuSBTuIVT_i4lqdrE9z8OrHLQRgwpRvx21M-jdXr2Wg76c1iH5tcmqDRUQ0H92kOezxAgzQ30mblQEWRNo78pLfz-orhCW17wnWboG79fTCxHCf53q1dY5JKQpdlEJVAx9gfb2qBsnp2aTXvIMtvsRqOd8kYwpaFpYgl1iphhEYUn1sJWQRm6f4-Jyp7EHcpVlr4wx77pVpqVg4Y3qqUxj7wys1iKlLIp4U-lgXNbARom-6Cn2gWhm8KD9zXJ0qQVdYKpCt4IC4iGR20Wle1jrJqSpYgPRdSBW33i2nQQ8GEjCNFEMF3nqSysO0G5godwnzhndcmr5ycu2rTy0siZjKyXryNQz-IOxSpzKk3r1wSCXNMM0SHVhtwsGREsaHKdwa4AjqXQEsHXluWUcI4KnidyNz8zuQE5SnqhlFhAg5BkLZ90V-arnGZFi-d4QJxG5W_1VEsSxrTBuolwinsw51aR_tu06PdTHQEhSoLNg6o9jm1n-L3hBJEUwOetaARURnAu_vK1PjaOWTmYuwenKLxP2xYwqTZh5C74LzVyRQYYIyz4IdPeY62LYWUsYebp98XmansgGfblOf3b1mERgIVGFIafsBg1Tt6Hkc9VTTsuNRvc\\u003c/Tracking\\u003e\\u003cTracking event=\\\"collapse\\\"\\u003ehttps://got.pubnative.net/video/event?t=1-KQLwcTrYFzsl-XAIr_59Rf7ATehb7-h3dZ9i4nQAY-ZMfJGwVa53yTtvvXZSPtLN_rntoa2Vq7tIRT3KAQg9o9URijqF6No2g2QmgEA4lZKF4trMcdcyvl1QzJR0HeORQuIczqHHZI-40HRmhq-sv7HXf4-lVYwj8agvkIy3oOVTdQFMDkQy2eFQcLxSruzZntjeNFguccq8NYVVqM0G9llQ---fmQqIIwmH78RxLfDPpWobVEqlAFxy-qJRtSSvuoIWtPAzdF52fwt5uNnGkZaA6RwtEbVEPEQdwdLP2i_86PURT1BDegGkwX46P3z3yPpb_SxvItI-P8cFwDDy13K8nvo6_1TOIQLYLnZvzmeURr6z2yYFXgyIOUssDRsyStnAzFSRae3Qhzt5YkxRtMkbpdnX5l0EhMRK0ss8VaaU4KEM5xIM7IWfxb2TFlH0_pSuja_TeRkqA_BkAIDPrVV0cc4s8U6N338o_li1duYACf_P0t7WA1qfQp3FpRi95TjoUQPJhGvg4l5RuY3UDSTkAjaeZ3EPAeEJW0gFhihLcoqMnW_Q2fiFWlygg9vk2WwT1ihjH1HCN408PwTO9t64WWtr-aAwHJjHVDjdyOm5Y7pvanoCU0avUpYwlrfECH9iMYQ7o8H4J-I1MAMOzS8rYvnLtY4BrGKsb6ALxcp95Je4hu7vTeDAH-MZuhqGxUuQjAaK8T58AKNMccP7_81f23tYFUFZ3wp_Ra7A1yxQcYyK20FOBbCsZlFsQuPK6g3EhsRwdMkWskgva2awxPsk2dKBtqt1rPdmP37qz1XTwcOTuYbcCgpxohNLnwnVwVhqAs8KDaaNQw_bcyT9BvH8FsQfvW1llEPL6_T4XF4VYDlvKuhttdOoBe1cyaDfef73E44l5j55mDcaea-Krkn6Cc-QGeWb0UcexzXl73EUiuV6sBp4WjAWOphUNkdo24gBtlyquIplmrAadK766jfFJZy2-p8u5UrevDcR2LqgmftAeDBhi_9ZIoLaNlgJQbiQ0e5cXB-TFvL1TvY8gQ1C_apT6un4jvKxyGuatc5GIkz7aRKOytEfzi6IIwWlUO5rewsLcXyi1oVJaT8ZQZOj4Z-v2hHtFhG7GTgJb0mCNGLbd9IJ_9ffj0FHGG5deycJvrioEZ4j-FC_aKRuChtbSdlmKTz-nFg7_JG_4y2RBcgttROmHpNVPiItKc7rf0W-hF6vnIo5ckOC7JuUUIsaFq-pGRZwXRzRp6qLsW58EdwqaWNc7QNiIOaJhCkNJ_sNzpUVtxJF5lsv3Qli785LXiMlTUi576Ag\\u003c/Tracking\\u003e\\u003cTracking event=\\\"acceptInvitation\\\"\\u003ehttps://got.pubnative.net/video/event?t=8zLeArqckbzw8TWWrfVdClrUdHjS3vvvo0Hz5m-xOeqfBTR-yRRIBIXNYl5RI1hv7i4tbi1YYuD4uiosCcNzVSub9vNeBG9CaV2ngaf3YkUf-Scq2Zkul7Iz3zwyaB3a8kNNiXtgfeLagk_V3nZTrnpqNoI-LN6ReuWYT8NHPdT7TbBxQoCJt1T9vz0wykbJmTs3FnnBFJj-SHRy60Ui5HyKagpawn7Uxs_0zCY6IE2gRXD74Iuk9GvR8Lm3IzU1Pvlb8EdhZQW8m0ARJMIl4YvWUVUQO1iaZam7spObefvh0Zk8n1Gi_UT4ToTTFhIrlIDPiaMSWxviwqJ1m7uhoa6G1zFsiiZJpX1JkHXib6AXprIKPknSZmzDTcbO3fISnksY49YY7DybnuzoZ4Qa_uVA5Au15HpZKevZdRFLj1vFWZ9pbzAkgY8yIaXHwlGHDuI5W-Ti0ehU0DmoaoZHhszV9DFuUpJBc5xTwyFkwedRKoqRiadAn7ISO0MczTj9GR-sVXi-zaHQDJomswtfKAjovxnGdkmpC3-ZEnF-5ZF19wpMFbxqaWGDNXTdljn5kYCatotmu2q9ae6XpbNAnZ1vqV1PVGNOkLs_wN6bPtea4nDLqzfOcKF0CmSoNJB53QaswQvoejROeKFHhvTZpcEfu4aBQQhXEGHXQi76pJBjW_w79runmtYKvL0LQuQBdMttyFisY7Q7Ck8AfhRwMo845gWtrtaYzBryoLPKOZJFaRrAX2gByeF4hLW2Ws1OwpUvzthHtGjpx6VOTp4LUlbEct09TKbNC10i-kqAaHUvbSu94M-nQ65GVK6ga1Bla_N1tD4l7wm6_mIWC3NXOvX_dEnVpoJKQ56MoDMFpNvqacg_fwu7pfRTg9P3mP51PhynskPKaE719iNpU6tH_3bXRy3sOn9PEvGSlQ3lA6Vizr8yF8r4sITFLQv1ZccSOXs-koNF1vA-3pFXZcniH-Hdi613U0BYz6P3lzaYrmOzLiaYGgAUmm8nHM6i8Yex4rW_v7Nn7aG2dRrW0UTLBByVx2k62YFM9nrx46kYXIjg1kY5J_HEmzCUI7DXCGzvrC3sOoINs9Y-Whf8uQlfCffXzWIpz8VKG80V9oCcroTF6lWMREbfMMD5mXBadVpLf2Fy26xTLNBiBmaKdIWC3n_2b1vp0nmWVBHCVe9xO1Ynxe9Tv1yLRGeoPygaFfiVlyDofHFiGj4dYqI1ftk9IeCzy-313NGh9BxO04LeakiqoLP0gLmdsprnbO9M5N_2wTJDSy1rTx-wj-NSvZHsOLGRmH4lWAQqB3txvrLvt-gUbcu7\\u003c/Tracking\\u003e\\u003cTracking event=\\\"close\\\"\\u003ehttps://got.pubnative.net/video/event?t=vM-TdgjPCkRxCOAI41VdnBTVEXXITXLXX9KmTzc090XZYEaZcE9MWm6zB3DmYcEV5DGZ3-Ezfi9e6ytkSPDK5P4m1P-A82AXue4Pa-JslmKmSIrdCZtDpsV1uCzFrMoVUNojiyqgsAzc_Td1-zvEd6iIuErklrZ3dgA5rs-MTBC8SSh7RsgjJQkZSZ2C3KIaD7lYBqxjrD9IcCpt4PY3kGzY0BKIN2xxHWzmd_--PPgdRDUV6koU03kVj0XuIOnF3WnifbgeS6zrozWuNgobwWok2SLH2cdvFDCGrmg6v5ZVyzZdigJtdNiIwWmtoZGbAmChEAWdGvmNCNKFFEvB_uJIibMvEupcagQ3QUeKU8NijF0zc4Fj2r_K3-tGsbtynT-yS3U_0FVIrcWs5INGon7zu5vBtnHvDld_EVnhi9TZAuI6A_O9ZofIOyqGZ3rNSoVQvbgArklO9aEJ6yaLtcNfM9mY0LHN7fX3kYfy1E6X2harmo76_SLHcf71ZKPHI9lAMaiZ_cmqrlchFuzgK4AyE6FiGvKCtwRmLKoeX-tUbjm67sAWGG12NNPeoH6i_Gr2MBp1TqkzpJBkIRhPn2x8g8mSAKldMzHKWk6gh3bGXFI_W8I_GXj_0Bba2UY9N0q9YV6hqPYpd8tuiWQYcmSBVzie-pfBrOn7NMEs9sO5eOYfOKBw6hiBCzWrDxaYAhi4krmsy2qsbC4g8sDtN9Ni_1e0jcwQY8U0l006FOfKg1XqPo0DscgnsZeRRga5jf7ej0yo-Nx_rMAvyPUzQvlj5GHi6jJ_Jem3bVdnehUqFm_TNQifBnW4aAo5y_27d-RoAmH1yypWzlEpOhDTf_BN6_usL5_IJxDS3GpReXEry9kqz1ewkEv17ahBUGPXQnKELGiCvsqpKs7y1AG8NPFBbFt0x9m-OCPuKmCT882H6BvKXglf0KIUee5Fqe9irXQrKN8B16ENIqIzMRINUsCv7JiMN8t24vZsGqx5IvT-9sijyE1jkHOcuOGw4RG3YTIf95AFoJ6wNJpaCqxkmLdzHnx1gTxwHNAtHZ-97IAfKSUQofdwDG-RPWtvCUYcrZu9xvBd8l7qnA5qliyzX1eh3dDbVz3B94XFZepYvCpWiYvqyTtE4wnf97x7ukgJfqUT9HzBLYjKYjHf9-06wN35nDciFUwuJRTyuSvrA-4ZG4qEapZ1SLuAL9zGRBv1OmORo_mQmrvD3eAcdT2imjYhA8puT1tSjIU_761_J9K2VRfvOHIdPZXQsDTaX8rcfq7m__lGIGUzBRKiuVO_tK3E2SxOMduRDA\\u003c/Tracking\\u003e\\u003c/TrackingEvents\\u003e\\u003cVideoClicks\\u003e\\u003cClickThrough\\u003ehttps://pubnative.net\\u003c/ClickThrough\\u003e\\u003cClickTracking\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/clickTracking\\u003c/ClickTracking\\u003e\\u003cClickTracking\\u003ehttps://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/clickTracking2\\u003c/ClickTracking\\u003e\\u003cClickTracking\\u003ehttps://got.pubnative.net/click/rtb?aid=1036637\\u0026amp;t=X1nXwH1iiV2gZ_Q_vAaGruDrH56Um3WiI_Nyuf92FHCkivVVzpRUP6NdGaxNajOGhZccvRWpmC2DMAVYVIdgXKXrrhw2Fv1rsV7XSLhe19XLypn-uZRPcogFiGCuDYTStDQ41RtbCo6DBiBkjceqHXybPXicdpzm-RKxCCbB-QYNh3pnmOBqJhpC3hZ7Wos7qrvmq1Un86-tN9fESa6285gW48rSTWXWBVtNbVofI9ccf7lPabZF-OvvfqsGhFwCtBlgWLCuudMs3EN9LB9XvQLob3wLd3SwUnJsP-BlSa4fTjhwd7l3qfoQERykhjgSV68ldbzepS7vtHb5ALQoPgtykUlRNjuBSf3Nv2d4OfJCQWJh2EOzNrmLdYw-itlKkDxg5MqAUJl4OFyYFX1_I3o-5wQRLOJZbQfJtOkjjk-fk-oIM0dN9JTixQUazj9_ZBPsRn1IxjPyc2U6JybWVOxGlJZvmqerrKu9RalYIb9_KL8j01t9P6xhQsvMKI61d_gyyIcPzSpp3q5F-r6wMV9-wQRxIaqrEeqe-tyswBDpZ1eycVCKYopEHVj-K48XUuALXSMP0wn5CcOikVw6Xw2Tk_6-1zOOADVUTaAVjDyIMdtaShd643SsgdaDgJ5CBwatFnQIIRyWgp2Z12kfNUscdt4-_UyqSX3fECJq5ZFKS7NOnhU6WhugaKsU3JmwrMKxltKVtz0xjXGuVDVgiIpJ7ugng7vZobUaONIIJ-pFpJiOV7Pwn0RNB-4dT5FDnJpVsPFzWR65lyVaTtkD580j2F8blX3RbfwsueHyFbZr0DjZEYpB5APLf-aRvC6h2RRkTOuN2IxCYBEzURjpZVg2tlB5HVjm2X0WR1niENCdedH8QO22GX4LHP-rfu4uA-k8AXYJKtjTmf0FBieap0BuizbpnRp5gU1XmyzKZG5-LjDZqN-GGd94AuJnl80Mv95pTkR_66FvwRPGNZwqmXnpGv8MxscW4dAREfkYTdXwM8xXMh-XQTaKtb1CzfiW2dWCBssu1oQ5UHyhSVZxGbEE68eWd0CSC78V9YM\\u003c/ClickTracking\\u003e\\u003c/VideoClicks\\u003e\\u003cMediaFiles\\u003e\\u003cMediaFile id=\\\"1\\\" delivery=\\\"progressive\\\" type=\\\"video/mp4\\\" bitrate=\\\"457\\\" width=\\\"1280\\\" height=\\\"720\\\"\\u003ehttps://pubnative-assets.s3.amazonaws.com/static/PNVideoMockup.mp4\\u003c/MediaFile\\u003e\\u003c/MediaFiles\\u003e\\u003c/Linear\\u003e\\u003c/Creative\\u003e\\u003c/Creatives\\u003e\\u003c/InLine\\u003e\\u003c/Ad\\u003e\\u003c/VAST\\u003e\"\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ],\n" +
                            "      \"meta\": [\n" +
                            "        {\n" +
                            "          \"type\": \"points\",\n" +
                            "          \"data\": {\n" +
                            "            \"number\": 23\n" +
                            "          }\n" +
                            "        },\n" +
                            "        {\n" +
                            "          \"type\": \"revenuemodel\",\n" +
                            "          \"data\": {\n" +
                            "            \"text\": \"cpm\"\n" +
                            "          }\n" +
                            "        },\n" +
                            "        {\n" +
                            "          \"type\": \"contentinfo\",\n" +
                            "          \"data\": {\n" +
                            "            \"link\": \"https://pubnative.net/content-info\",\n" +
                            "            \"icon\": \"https://cdn.pubnative.net/static/adserver/contentinfo.png\",\n" +
                            "            \"text\": \"Learn about this ad\"\n" +
                            "          }\n" +
                            "        },\n" +
                            "        {\n" +
                            "          \"type\": \"creativeid\",\n" +
                            "          \"data\": {\n" +
                            "            \"text\": \"test_creative\"\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ],\n" +
                            "      \"beacons\": [\n" +
                            "        {\n" +
                            "          \"type\": \"impression\",\n" +
                            "          \"data\": {\n" +
                            "            \"url\": \"https://backend.europe-west4gcp0.pubnative.net/mockdsp/v1/tracker/nurl?app_id=1036637\\u0026p=0.033222222\"\n" +
                            "          }\n" +
                            "        },\n" +
                            "        {\n" +
                            "          \"type\": \"impression\",\n" +
                            "          \"data\": {\n" +
                            "            \"url\": \"https://got.pubnative.net/impression?aid=1036637\\u0026t=Bg9C1qaIAjHk-CFLirncW0xCRwGVy6YdnGUon2NFEz0WW-cViKoY3EUcAqZiI7n4k0BTH-y0SJDQYDQOt-zUNm1xim1KyulXhIjqtC76dNBqAR_VpHg4wDJoJpmEGEOYZGsovGF_7k-vslCWeaC09xAQQL9MO7u-mEd2mzPCd14UI1hRs3YOYUXuA3k-12Puqm8FF-GWsgcn4DVyOfNueWBPPCELDchKm5b--DAW-5dvzGs_JVCgfnb4Cl2wNz2CyoGQ_fYJkUuJh5Lg3yOwt5WwOAuQv7FAmC10BM5rnLpkny32x6YRJA5KdQBBL6vk-iq76NW91Rg0zDqHUk9uAX5QpbfSQZa6v4P8utu-Sn3S0yt9kdIHy8BXBIRH_gncyPoMEmysQ8XY4yAt4GGAFu42wOAn_v0dSP0XhtjUeW5e2kSM2F5W_mVYuizV4pmEnumhhGBM9O9b-9rfMszCos0XHP3TMrc18xmb9SeRFI59jwtHyxu3QF3GiHJenA6HrpXnISU7AbxV-lDOynUqnHXAzipZIIZd_UGYndFvQANXMiPmb1ltBT4dfCF1s5G-HKh6ZiPhHeCdgCdZTPgHXdha9v7kxZcyUn4yW-imahlG0MxFPWt58jrCGE6soSBufbYadoeI-VgzMwxYyyronz4ZrO1xGMORCuzy-bNMRFSMlPMHAao6I4iMJlypVLHj-hn6yLsmYg9OI-q7GFZVE-CHmhS7kliO0VHwaw8tvce_269kPaPJRpxZJfkkPQ2_RISBYGgWojcyBzaS1ESLXJ7YeZ-sNERIY7kau8pg47-vRr6vg6xR9ctIKd7kFfDZvyJ0Uj15IPA5C-knXP3R8s1lfZsvYTBKecn_qsSvhh_lQclI7_uy-_94dqnwvtiT4IZ7JmgTGr2wzr2MyBNSYvSg7DVGuRuxbXfao1QMSJYImQApi_wEEn1tuN6EYPxrgBy2SAprxp2d_FXU5Umx0Yt3aHjeMt1rQRsojjd6jYcqi9tVMrrgIW8PPhDMwOCguuY8c7laOVsVIHzuLDw9gqkpvYjKIQL7sNqM0a48e9sLhnrAaESZtxFhGZhn6lgmphFrW4OiPsIEM47tG3DhnswOJBycl-32j7oNdl5y8BwVVmbMcpvQ8iDaWd7uM2-n2X-pYnbDdDFk0dOsMUmU7UjB4wSvAEQLDRvQb0Y69gx9KEK6oIDWzAHtRkar8PB5Ho2jSCkxTU33q_x9NDh5LQznaDUYiDrZWpEdQSxn4IIxclJvJbnPc7k9wVPKp4Nd1RXiydRozpmXPfaM12Trr3o\"\n" +
                            "          }\n" +
                            "        },\n" +
                            "        {\n" +
                            "          \"type\": \"click\",\n" +
                            "          \"data\": {\n" +
                            "            \"url\": \"https://got.pubnative.net/click/rtb?aid=1036637\\u0026t=wFB1WozahFpi-8emYKH84LGtAYYREN92GFTA4_ERx78dQiCZeagXiEqo3IFTojc7tjohMbRbtOvQqiblPXeL9cIG7UUj1csAlNv0MY0_W8HvLwouQVKntmwZxH4U-IfEiaz_uIeC_79TTGyOiE8t4FszWl0S7lDAkEpTv2dUDh6xDNc84FE4AIGd_INr-gWnLHdL_0rMZlinPrHcOSFITu0TTZqMyI3Msk_nylhm9vWNBvXxFEQPV4JX4U-zS-slHFZc1xlck3PTrSFJO5tOY1w02wEgo7sjxPtYqmVqXm4_lQQZcoKAPZRwZpudPRfUJpsM0bdzpTKNJUuRz_EeKAZAgR8UJiGchAyeZ8i4aOtsMzPPMnxeMvqysh5KOis3xGLt8KRfcZnlS5BBRTihSPckR02M5TfK6Ahdl658ZGZkKuggspC7GImzw4Q0cFI7lP2RD7xAL3FbWdDmfeGLDS3UT9NVbIiamxE6vdLq94cVN07LfFT3uYfEkoHFMO6ZkXgfU9kZH6O_ZF8HFHfPVQcxx2PVwiaGi8L5SE-H_qhwQZ2ujQ7sXA29EVk8VJAddLsIWUqSspXh9v7Ba1RtfCWCzt7TzgJMNAyLEkS-JRv3Ok53jstzJfMhqK4YjF4xNay7HOy-pb_6-oOuSIIqD03A4AIsC1hXGZ78RhTH5VpBnNB85m2ucFgWUGps9vt9nJqMoMkckyt_o9hzkIOyOPXIF24PYiE_kBi4hzl3jDvV8woS1805ap5ypqh2cgrHQr6ar24YO1j8jvBWqwqlvu5LtzZlKQdOqbQdv9jPanRd3pElzddw-tuLaD_8yF9HfYhiDYx3WOco9Q2p6ptD3V2u6nf6Xki_Znf1NWBBlBoxSihQ6ub2rhuZnVDj2xpas67bgyCy2BmT-i_Z1up3VZgVWMyyvFQkRjZnbiStZqdHHOUBwG6a5Qxj46k4aRsyyws_PbIkbhhdPY66Nxop3ZPO5SnVyC3U5R5KvdTLi89ETyzI5CTH-vbmMnFDi6fl9u-dCNyV3KtQvvPQ75dJupP4S6wUgLruqwao3RQ\"\n" +
                            "          }\n" +
                            "        }\n" +
                            "      ]\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}\n";
                    registerAdRequest(url, response, initTime);
                    processStream(response, listener);
                }

                @Override
                public void onFailure(Throwable error) {
                    registerAdRequest(url, error.getMessage(), initTime);

                    if (listener != null) {
                        listener.onFailure(error);
                    }
                }
            });
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void trackUrl(String url, final TrackUrlListener listener) {
        PNHttpClient.makeRequest(mContext, url, null, null, false, true, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                if (listener != null) {
                    listener.onFailure(error);
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void trackJS(String js, final TrackJSListener listener) {
        if (TextUtils.isEmpty(js)) {
            if (listener != null) {
                listener.onFailure(new Exception("Empty JS tracking beacon"));
            }
        } else {
            WebView webView = new WebView(mContext);
            webView.getSettings().setJavaScriptEnabled(true);

            String processedJS = processJS(js);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                webView.loadUrl("javascript:" + processedJS);
            } else {
                webView.evaluateJavascript(processedJS, null);
            }

            if (listener != null) {
                listener.onSuccess();
            }
        }
    }

    private String processJS(String js) {
        String scriptOpen = "<script>";
        String scriptClose = "</script>";

        String processed = js.replace(scriptOpen, "");
        processed = processed.replace(scriptClose, "");

        return processed;
    }

    protected String getAdRequestURL(AdRequest adRequest) {
        return PNApiUrlComposer.buildUrl(mApiUrl, adRequest);
    }

    protected void processStream(String result, AdRequestListener listener) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(result));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response cannot be parsed", error);
        }
        if (parseException != null) {
            listener.onFailure(parseException);
        } else if (apiResponseModel == null) {
            listener.onFailure(new Exception("PNApiClient - Parse error"));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                listener.onSuccess(apiResponseModel.ads.get(0));
            } else {
                listener.onFailure(new Exception("HyBid - No fill"));
            }
        } else {
            // STATUS 'ERROR'
            listener.onFailure(new Exception("HyBid - Server error: " + apiResponseModel.error_message));
        }
    }

    private void registerAdRequest(String url, String response, long initTime) {
        AdRequestRegistry.getInstance().setLastAdRequest(url, response, System.currentTimeMillis() - initTime);
    }
}
