import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import junit.framework.TestCase.assertEquals
import net.pubnative.lite.demo.util.convertPxToDp
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class HelpersTest {

    @Test
    fun testConvertDpToPx() {
        val context = mock(Context::class.java)
        val metrics = mock(DisplayMetrics::class.java)
        metrics.density = 1.0f
        val resources = mock(Resources::class.java)
        `when`(context.resources).thenReturn(resources)
        `when`(resources.displayMetrics).thenReturn(metrics)

        val px = 100f
        val expectedDp = 100 / metrics.density

        val actualDp = convertPxToDp(context, px)

        assertEquals(expectedDp, actualDp.toFloat())
    }

    @Test
    fun testConvertPxToDp() {
        val context = mock(Context::class.java)
        val metrics = mock(DisplayMetrics::class.java)
        metrics.density = 1.0f
        val resources = mock(Resources::class.java)
        `when`(context.resources).thenReturn(resources)
        `when`(resources.displayMetrics).thenReturn(metrics)

        val px = 100f
        val expectedDp = 100 / metrics.density

        val actualDp = convertPxToDp(context, px)

        assertEquals(expectedDp, actualDp.toFloat())
    }
}