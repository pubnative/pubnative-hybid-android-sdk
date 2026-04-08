// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.mraid.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.util.Base64;
import java.lang.reflect.Method;
import net.pubnative.lite.sdk.viewability.Assets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MRAIDHtmlProcessorTest {

    @Test
    public void processRawHtml_whenNoHtmlTags_wrapsWithHtmlHeadAndBody() {
        String rawHtml = "<div id='ad'>Hello</div>";
        String processedHtml = MRAIDHtmlProcessor.processRawHtml(rawHtml);

        assertTrue(processedHtml.startsWith("<html>"));
        assertTrue(processedHtml.contains("<head>"));
        assertTrue(processedHtml.contains("<body><div id='hybid-ad' align='center'>"));
        assertTrue(processedHtml.contains("<div id='ad'>Hello</div>"));
        assertTrue(processedHtml.contains("</div></body>"));
        assertTrue(processedHtml.endsWith("</html>"));
    }

    @Test
    public void processRawHtml_whenOnlyHtmlTagExists_addsHeadTag() {
        String rawHtml = "<html><body>Ad Content</body></html>";
        String processedHtml = MRAIDHtmlProcessor.processRawHtml(rawHtml);

        assertTrue(processedHtml.contains("<head>"));
        assertTrue(processedHtml.contains("<body>Ad Content</body>"));
    }

    @Test
    public void processRawHtml_withExistingHead_injectsScriptsAndTags() {
        String rawHtml = "<html><head><title>Original Title</title></head><body>Ad</body></html>";
        String processedHtml = MRAIDHtmlProcessor.processRawHtml(rawHtml);

        assertTrue(processedHtml.contains("<meta name='viewport' content='width=device-width, initial-scale=1.0"));
        assertTrue(processedHtml.contains("<style>"));
        assertTrue(processedHtml.contains("body { margin:0; padding:0;}"));
        assertTrue(processedHtml.contains("</style>"));

        String mraidJs = new String(Base64.decode(net.pubnative.lite.sdk.mraid.Assets.mraidJS, Base64.DEFAULT));
        assertTrue(processedHtml.contains(mraidJs));

        String omSdkJs = new String(Base64.decode(Assets.OMSDKJS, Base64.DEFAULT));
        assertTrue(processedHtml.contains(omSdkJs));

        String scalingJs = new String(Base64.decode(net.pubnative.lite.sdk.mraid.Assets.scaling_script_minified, Base64.DEFAULT));
        assertTrue(processedHtml.contains(scalingJs));

        assertTrue(processedHtml.contains("<title>Original Title</title>"));
    }

    @Test
    public void processRawHtml_withExistingMraidJsTag_removesItBeforeInjecting() {
        String oldMraidTag = "<script src='mraid.js'></script>";
        String rawHtml = "<html><head>" + oldMraidTag + "</head><body>Ad</body></html>";
        String processedHtml = MRAIDHtmlProcessor.processRawHtml(rawHtml);

        assertFalse(processedHtml.contains(oldMraidTag));

        String mraidJs = new String(Base64.decode(net.pubnative.lite.sdk.mraid.Assets.mraidJS, Base64.DEFAULT));
        assertTrue(processedHtml.contains(mraidJs));
    }

    @Test
    public void removeAllScripts_removesScriptTags() throws Exception {
        Method method = MRAIDHtmlProcessor.class.getDeclaredMethod("removeAllScripts", String.class);
        method.setAccessible(true);

        String htmlWithScript = "<html><script>alert('hello');</script><body>Content</body></html>";
        String result = (String) method.invoke(null, htmlWithScript);

        assertEquals("<html><body>Content</body></html>", result);
    }
}