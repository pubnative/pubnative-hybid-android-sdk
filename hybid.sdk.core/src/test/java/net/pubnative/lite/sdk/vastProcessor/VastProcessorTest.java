package net.pubnative.lite.sdk.vastProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Activity;

import net.pubnative.lite.sdk.testing.VastResponseTestUtils;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.models.vpaid.AdSpotDimensions;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.response.VastProcessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

@RunWith(RobolectricTestRunner.class)
public class VastProcessorTest {

    private String successResponse;
    private String response_without_media_files;
    private String response_without_ad;
    private String response_without_impression;
    private String response_empty_tag;
    private String response_invalid;
    private String response_invalid_with_empty_error;

    VastProcessor vastProcessor;

    @Mock
    private VastProcessor.Listener listener;

    private Activity activity;

    @Before
    public void setUp() {
        initMocks(this);

        ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        activity = activityController.get();

        vastProcessor = new VastProcessor(activity.getApplicationContext(), new AdSpotDimensions(2183, 1080));

        successResponse = VastResponseTestUtils.getSuccessVastResponse();
        response_without_media_files = VastResponseTestUtils.getVastResponseWithoutMediaFiles();
        response_without_ad = VastResponseTestUtils.getVastResponseWithoutAd();
        response_empty_tag = VastResponseTestUtils.getVastResponseEmptyTag();
        response_invalid = VastResponseTestUtils.getInvalidVastResponse();
        response_invalid_with_empty_error = VastResponseTestUtils.getInvalidVastResponseWithEmptyError();
        response_without_impression = VastResponseTestUtils.getVastResponseWithoutImpression();
    }

    @Test
    public void onAdSuccessResponse() {
        vastProcessor.parseResponse(successResponse, listener);
        verify(listener).onParseSuccess(any(AdParams.class), any(String.class));
    }

    @Test
    public void onAdResponseWithoutAd() {
        vastProcessor.parseResponse(response_without_ad, listener);
        verify(listener).onParseError(any(PlayerInfo.class));
    }

    @Test
    public void onAdResponseWithoutMediaFiles() {
        vastProcessor.parseResponse(response_without_media_files, listener);
        verify(listener).onParseSuccess(any(AdParams.class), any(String.class));
    }

    @Test
    public void onAdResponseWithoutCreatives() {
        vastProcessor.parseResponse(response_without_media_files, listener);
        verify(listener).onParseSuccess(any(AdParams.class), any(String.class));
    }

    @Test
    public void onAdResponseEmptyTag() {
        vastProcessor.parseResponse(response_empty_tag, listener);
        verify(listener).onParseError(any(PlayerInfo.class));
    }

    @Test
    public void onAdResponseParseError() {
        vastProcessor.parseResponse(response_invalid, listener);
        verify(listener).onParseError(any(PlayerInfo.class));
    }

    @Test
    public void onAdResponseParseErrorEmptyError() {
        vastProcessor.parseResponse(response_invalid_with_empty_error, listener);
        verify(listener).onParseError(any(PlayerInfo.class));
    }

    @Test
    public void onAdResponseParseEmptyImpression() {
        vastProcessor.parseResponse(response_without_impression, listener);
        verify(listener).onParseSuccess(any(AdParams.class), any(String.class));
    }
}