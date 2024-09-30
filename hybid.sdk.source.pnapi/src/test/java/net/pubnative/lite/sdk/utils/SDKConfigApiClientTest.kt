package net.pubnative.lite.sdk.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import net.pubnative.lite.sdk.api.SDKConfigAPiClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SDKConfigApiClientTest {

    @Mock
    var context: Context? = null

    @Mock
    var connectivityManager: ConnectivityManager? = null

    @Mock
    var networkInfo: NetworkInfo? = null

    private val appTokenWithAtomEnabled = "543027b8e954474cbcd9a98481622a3b"
    private val appTokenWithAtomDisabled = "dde3c298b47648459f8ada4a982fa92d"
    private val appTokenEmptyValue = "emptyconfigtoken"
    private val appTokenWithNullValue = "nullconfigtoken"
    private val appTokenWithErrorValue = "errorconfigtoken"
    private val appTokenWithServerError = "servererrorconfigtoken"
    private val appTokenWithNotFound = "notfoundconfigtoken"
    private val appTokenWithNull = null

    private lateinit var sdkConfigApiClient: SDKConfigAPiClient

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        Mockito.`when`(context?.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        Mockito.`when`(context?.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(Mockito.mock(TelephonyManager::class.java))
        Mockito.`when`(connectivityManager?.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo?.isConnected).thenReturn(true)
        Mockito.`when`(networkInfo?.type).thenReturn(ConnectivityManager.TYPE_WIFI)
        Mockito.`when`(networkInfo?.state).thenReturn(NetworkInfo.State.CONNECTED)

        sdkConfigApiClient =
            SDKConfigAPiClient(context)
    }

    @Test
    fun `fetchConfig returns response with atom config api disabled when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithAtomDisabled)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api enabled when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithAtomEnabled)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    true
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api empty when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenEmptyValue)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api null when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithNullValue)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api error when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithErrorValue)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api server error when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithServerError)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api not found when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithNotFound)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }

    @Test
    fun `fetchConfig returns response with atom config api null value when network call succeeds`() =
        run {
            sdkConfigApiClient.setAppToken(appTokenWithNull)
            sdkConfigApiClient.fetchConfig { isAtomEnabled ->
                Assert.assertEquals(
                    isAtomEnabled,
                    false
                )
            }
        }
}