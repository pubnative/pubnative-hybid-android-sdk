package net.pubnative.lite.sdk.utils;

import android.os.Handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class UrlDrillerTest {
    @Test
    public void creates() {

        UrlDriller opener = new UrlDriller();
        assertThat(opener).isNotNull();
    }

    @Test
    public void invokeRedirectCallbackWithListener() {

        UrlDriller driller = spy(UrlDriller.class);
        UrlDriller.Listener drillerListener = spy(UrlDriller.Listener.class);

        driller.mHandler = new Handler();
        driller.setListener(drillerListener);
        driller.invokeRedirect("");

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(drillerListener, times(1)).onURLDrillerRedirect(anyString());
    }

    @Test
    public void invokeFinishCallbackWithListener() {

        UrlDriller driller = spy(UrlDriller.class);
        UrlDriller.Listener drillerListener = spy(UrlDriller.Listener.class);

        driller.mHandler = new Handler();
        driller.setListener(drillerListener);
        driller.invokeFinish("");

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(drillerListener, times(1)).onURLDrillerFinish(anyString());
    }

    @Test
    public void invokeFailCallbackWithListener() {

        UrlDriller driller = spy(UrlDriller.class);
        UrlDriller.Listener drillerListener = spy(UrlDriller.Listener.class);

        driller.mHandler = new Handler();
        driller.setListener(drillerListener);
        driller.invokeFail("", mock(Exception.class));

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(drillerListener, times(1)).onURLDrillerFail(anyString(), any(Exception.class));
    }

    @Test
    public void invokeCallbacksWithoutListener() {

        UrlDriller driller = spy(UrlDriller.class);

        driller.mHandler = new Handler();

        driller.invokeFail("", mock(Exception.class));
        driller.invokeFinish("");
        driller.invokeRedirect("");
        driller.invokeStart("");
    }

    @Test
    public void invokeStartCallbackWithListener(){
        UrlDriller driller = spy(UrlDriller.class);
        UrlDriller.Listener drillerListener = spy(UrlDriller.Listener.class);

        driller.mHandler = new Handler();
        driller.setListener(drillerListener);
        driller.invokeStart("");

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(drillerListener, times(1)).onURLDrillerStart(anyString());
    }

    @Test
    public void failOnEmptyURL(){

        UrlDriller driller = spy(UrlDriller.class);
        UrlDriller.Listener drillerListener = spy(UrlDriller.Listener.class);

        driller.mHandler = new Handler();
        driller.setListener(drillerListener);
        driller.drill("");

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(drillerListener, times(1)).onURLDrillerFail(anyString(), any(IllegalArgumentException.class));
    }

    @Test
    public void failOnNullURL(){

        UrlDriller driller = spy(UrlDriller.class);
        UrlDriller.Listener drillerListener = spy(UrlDriller.Listener.class);

        driller.mHandler = new Handler();
        driller.setListener(drillerListener);
        driller.drill(null);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(drillerListener, times(1)).onURLDrillerFail(anyString(), any(IllegalArgumentException.class));
    }
}
