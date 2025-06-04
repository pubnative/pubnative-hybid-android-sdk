// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.db;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class DBManagerTest {

    DBManager dbManager;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        dbManager = new DBManager(context);
        dbManager.open();
    }

    @Test
    public void insert() {
        for (int i = 0; i < 1000; i++) {
            dbManager.insert("4");
        }

        int imp_depth = dbManager.getImpressionDepth("4");
        Assert.assertEquals(imp_depth, 1000);

        for (int i = 0; i < 100; i++) {
            dbManager.insert("5");
        }

        imp_depth = dbManager.getImpressionDepth("5");
        Assert.assertEquals(imp_depth, 100);
    }

    @After
    public void tearDown() {
        dbManager.close();
    }
}
