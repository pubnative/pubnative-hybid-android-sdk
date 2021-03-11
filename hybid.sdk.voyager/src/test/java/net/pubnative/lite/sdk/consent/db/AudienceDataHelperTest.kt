package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.Audience
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AudienceDataHelperTest {

    private lateinit var audience1: Audience
    private lateinit var audience2: Audience
    private lateinit var audience3: Audience

    lateinit var database: DatabaseHelper

    @Before
    fun setup() {

        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(Audience::class)

        audience1 = Audience(audience_id = "audience_id1",
                taxonomy2_ids = "taxonomy2_ids1",
                taxonomy3_ids = "taxonomy3_ids1",
                start_time = 11,
                end_time = 12,
                upper_limit = 10,
                name_query = "name_query1")

        audience2 = Audience(audience_id = "audience_id1",
                taxonomy2_ids = "taxonomy2_ids1",
                taxonomy3_ids = "taxonomy3_ids1",
                start_time = 11,
                end_time = 12,
                upper_limit = 10,
                name_query = "name_query1")

        audience3 = Audience(audience_id = "audience_id1",
                taxonomy2_ids = "taxonomy2_ids1",
                taxonomy3_ids = "taxonomy3_ids1",
                start_time = 1121,
                end_time = 1232,
                upper_limit = 1330,
                name_query = "name_query3")
    }


    @Test
    fun `test insert Audience then return count`() {
        Assert.assertEquals(database.count(Audience::class), 0)
        database.insert(audience1)
        Assert.assertEquals(database.count(Audience::class), 1)
        database.insert(audience2)
        Assert.assertEquals(database.count(Audience::class), 2)
    }

    @Test
    fun `test update Audience then return count`() {
        Assert.assertEquals(database.count(Audience::class), 0)
        database.insert(audience1)
        val items = database.get(Audience::class)
        items?.let {
            Assert.assertEquals(database.count(Audience::class), 1)
            items[0].name_query = "name_query_updated"
            database.update(items)
            Assert.assertEquals(database.count(Audience::class), 1)
        }
    }

    @Test
    fun `test delete Audience from database`() {
        Assert.assertEquals(database.count(Audience::class), 0)
        database.insert(audience1)
        Assert.assertEquals(database.count(Audience::class), 1)
        database.insert(audience2)
        Assert.assertEquals(database.count(Audience::class), 2)
        val items = database.get(Audience::class)
        if (items != null)
            database.delete(items)
        Assert.assertEquals(database.count(Audience::class), 0)
    }

    @Test
    fun `test fetch Audience from database`() {
        Assert.assertEquals(database.count(Audience::class), 0)
        database.insert(audience1)
        Assert.assertEquals(database.count(Audience::class), 1)
        database.insert(audience3)
        val items = database.get(Audience::class)
        Assert.assertEquals(items?.size, 2)
        Assert.assertEquals(items?.get(0)?.name_query, "name_query1")
        Assert.assertEquals(items?.get(1)?.name_query, "name_query3")
    }
}