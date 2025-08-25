// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.models.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void testSettersAndGetters() {
        String id = "user-123";
        String gender = "M";
        Integer yob = 1990;
        Geo geo = new Geo();
        geo.setCountry("USA");

        user.setId(id);
        user.setGender(gender);
        user.setYearOfBirth(yob);
        user.setGeo(geo);

        assertEquals(id, user.getId());
        assertEquals(gender, user.getGender());
        assertEquals(yob, user.getYearOfBirth());
        assertEquals(geo, user.getGeo());
        assertEquals("USA", user.getGeo().getCountry());
    }

    @Test
    public void testJsonRoundTrip_isConsistent() throws Exception {
        // This test ensures that a complex, nested object can be
        // serialized to JSON and then deserialized back into an identical object.

        // 1. Create the original object with all its nested dependencies
        Geo geo = new Geo();
        geo.setCountry("DE");
        geo.setCity("Rheine");

        Data data = new Data();
        data.setId("data-456");

        User originalUser = new User();
        originalUser.setId("user-abc");
        originalUser.setBuyeruid("buyer-xyz");
        originalUser.setYearOfBirth(1985);
        originalUser.setGender("F");
        originalUser.setGeo(geo);
        originalUser.setData(Arrays.asList(data));

        // 2. Convert the original object to JSON
        JSONObject jsonObject = originalUser.toJson();
        assertNotNull(jsonObject);
        assertEquals("user-abc", jsonObject.getString("id"));
        assertEquals(1985, jsonObject.getInt("yob"));
        assertEquals("DE", jsonObject.getJSONObject("geo").getString("country"));
        assertEquals("data-456", jsonObject.getJSONArray("data").getJSONObject(0).getString("id"));

        // 3. Convert the JSON back into a new object
        User restoredUser = new User(jsonObject);

        // 4. Assert that the new object has the same data as the original
        assertEquals(originalUser.getId(), restoredUser.getId());
        assertEquals(originalUser.getBuyeruid(), restoredUser.getBuyeruid());
        assertEquals(originalUser.getYearOfBirth(), restoredUser.getYearOfBirth());
        assertEquals(originalUser.getGender(), restoredUser.getGender());

        // Assert nested Geo
        assertNotNull(restoredUser.getGeo());
        assertEquals(originalUser.getGeo().getCountry(), restoredUser.getGeo().getCountry());
        assertEquals(originalUser.getGeo().getCity(), restoredUser.getGeo().getCity());

        // Assert nested List<Data>
        assertNotNull(restoredUser.getData());
        assertEquals(1, restoredUser.getData().size());
        assertEquals(originalUser.getData().get(0).getId(), restoredUser.getData().get(0).getId());
    }
}
