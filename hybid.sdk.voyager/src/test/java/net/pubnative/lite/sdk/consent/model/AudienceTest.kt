package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AudienceTest {

    private lateinit var audience1: Audience
    private lateinit var audience2: Audience
    private lateinit var audience3: Audience

    @Before
    fun setup() {
        audience1 = Audience(0, "audience_id1",
                "taxonomy2_ids1",
                "taxonomy3_ids1",
                11,
                12,
                10,
                "name_query1")

        audience2 = Audience(0, "audience_id1",
                "taxonomy2_ids1",
                "taxonomy3_ids1",
                11,
                12,
                10,
                "name_query1")

        audience3 = Audience(0, "audience_id1",
                "taxonomy2_ids3",
                "taxonomy3_ids3",
                111,
                1211,
                100,
                "name_query3")
    }

    @Test
    fun `test two Audiences are equal`() {
        Assert.assertEquals(audience1, audience2)
    }

    @Test
    fun `test two Audiences are not equal`() {
        Assert.assertNotEquals(audience1, audience3)
    }
}