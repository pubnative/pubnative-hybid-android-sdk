package net.pubnative.tarantula.sdk.network;

import android.os.Handler;

import net.pubnative.tarantula.sdk.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by erosgarciaponte on 24.01.18.
 */
@RunWith(RobolectricTestRunner.class)
public class TarantulaHttpRequestTest {
    @Test
    public void start_withNullContext_callbacksFail() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        TarantulaHttpRequest.Listener listener = mock(TarantulaHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(null, "url", listener);
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void start_withNullUrl_callbacksFail() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        TarantulaHttpRequest.Listener listener = mock(TarantulaHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(RuntimeEnvironment.application.getApplicationContext(), null, listener);
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void start_withEmptyUrl_callbacksFail() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        TarantulaHttpRequest.Listener listener = mock(TarantulaHttpRequest.Listener.class);
        request.mHandler = new Handler();
        request.start(RuntimeEnvironment.application.getApplicationContext(), "", listener);
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
    }

    @Test
    public void invokeFinish_withNullListener_pass() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeFinish("result");
    }

    @Test
    public void invokeFail_withNullListener_pass() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        request.mListener = null;
        request.mHandler = new Handler();
        request.invokeFail(mock(Exception.class));
    }

    @Test
    public void invokeLoad_WithValidListener_callbackAndNullsListener() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        TarantulaHttpRequest.Listener listener = mock(TarantulaHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeFinish("result");
        verify(listener).onPNHttpRequestFinish(eq(request), eq("result"));
        assertThat(request.mListener).isNull();
    }

    @Test
    public void invokeFail_withValidListener_callbackAndNullsListener() {

        TarantulaHttpRequest request = spy(TarantulaHttpRequest.class);
        TarantulaHttpRequest.Listener listener = mock(TarantulaHttpRequest.Listener.class);
        request.mListener = listener;
        request.mHandler = new Handler();
        request.invokeFail(mock(Exception.class));
        verify(listener).onPNHttpRequestFail(eq(request), any(Exception.class));
        assertThat(request.mListener).isNull();
    }
}
