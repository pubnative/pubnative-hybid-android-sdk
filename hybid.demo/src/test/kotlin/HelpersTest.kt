import android.content.Context
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertEquals
import net.pubnative.lite.demo.util.convertDpToPx
import net.pubnative.lite.demo.util.convertPxToDp
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HelpersTest {

    @Test
    fun testConvertDpToPx() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.resources.displayMetrics.density = 1.0f
        val dp = 100f
        val expected = (dp * context.resources.displayMetrics.density).toInt()
        assertEquals(expected, convertDpToPx(context, dp))
    }

    @Test
    fun testConvertPxToDp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.resources.displayMetrics.density = 1.0f
        val px = 100f
        val expected = (px / context.resources.displayMetrics.density).toInt()
        assertEquals(expected, convertPxToDp(context, px))
    }
}
