package net.pubnative.lite.sdk.vpaid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.pubnative.lite.sdk.vpaid.models.vast.CreativeExtension;
import net.pubnative.lite.sdk.vpaid.models.vast.Vast;
import net.pubnative.lite.sdk.vpaid.xml.Attribute;
import net.pubnative.lite.sdk.vpaid.xml.Tag;
import net.pubnative.lite.sdk.vpaid.xml.Text;
import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

@RunWith(RobolectricTestRunner.class)
public class XmlParserTest {

    public static class Item {
        @Attribute
        private String id;

        @Attribute
        private String note;

        @Text
        private String value;

        // Deliberately unannotated: must be silently skipped by the descriptor builder.
        private String notBoundToXml;

        public String getId() {
            return id;
        }

        public String getNote() {
            return note;
        }

        public String getValue() {
            return value;
        }

        public String getNotBoundToXml() {
            return notBoundToXml;
        }
    }

    public static class AllAttributeTypes {
        @Attribute
        private String stringValue;

        @Attribute
        private long primitiveLong;
        @Attribute
        private Long boxedLong;

        @Attribute
        private int primitiveInt;
        @Attribute
        private Integer boxedInt;

        @Attribute
        private byte primitiveByte;
        @Attribute
        private Byte boxedByte;

        @Attribute
        private double primitiveDouble;
        @Attribute
        private Double boxedDouble;

        @Attribute
        private float primitiveFloat;
        @Attribute
        private Float boxedFloat;

        @Attribute
        private boolean primitiveBoolean;
        @Attribute
        private Boolean boxedBoolean;

        public String getStringValue() {
            return stringValue;
        }

        public long getPrimitiveLong() {
            return primitiveLong;
        }

        public Long getBoxedLong() {
            return boxedLong;
        }

        public int getPrimitiveInt() {
            return primitiveInt;
        }

        public Integer getBoxedInt() {
            return boxedInt;
        }

        public byte getPrimitiveByte() {
            return primitiveByte;
        }

        public Byte getBoxedByte() {
            return boxedByte;
        }

        public double getPrimitiveDouble() {
            return primitiveDouble;
        }

        public Double getBoxedDouble() {
            return boxedDouble;
        }

        public float getPrimitiveFloat() {
            return primitiveFloat;
        }

        public Float getBoxedFloat() {
            return boxedFloat;
        }

        public boolean isPrimitiveBoolean() {
            return primitiveBoolean;
        }

        public Boolean getBoxedBoolean() {
            return boxedBoolean;
        }
    }

    public static class ConcurrencyProbe {
        @Attribute
        private String id;

        @Text
        private String value;

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }

    public static class CaseSensitiveAttribute {
        @Attribute("Case")
        private String value;

        public String getValue() {
            return value;
        }
    }

    public static class Container {
        @Attribute("name")
        private String label;

        @Tag("Item")
        private List<Item> items;

        @Tag("Single")
        private Item single;

        public String getLabel() {
            return label;
        }

        public List<Item> getItems() {
            return items;
        }

        public Item getSingle() {
            return single;
        }
    }

    @Test
    public void testXmlPullParserFactoryIsSingleton() throws Exception {
        Field factoryField = XmlParser.class.getDeclaredField("sParserFactory");
        assertTrue("Factory field must be static", Modifier.isStatic(factoryField.getModifiers()));
        factoryField.setAccessible(true);

        String xml = "<VAST version=\"2.0\"><Ad><InLine><AdSystem>Test</AdSystem></InLine></Ad></VAST>";
        XmlParser.parse(xml, Vast.class);
        Object factoryAfterFirstParse = factoryField.get(null);
        assertNotNull("Factory should be initialized after first parse", factoryAfterFirstParse);

        XmlParser.parse(xml, Vast.class);
        Object factoryAfterSecondParse = factoryField.get(null);
        assertSame("Factory instance must not be rebuilt per parse() call", factoryAfterFirstParse, factoryAfterSecondParse);
    }

    @Test
    public void testParseUsesAttributeTagAndTextFields() throws Exception {
        String xml = "<Container name=\"root\">"
                + "<ITEM id=\"1\" note=\"a note\">first</ITEM>"
                + "<Unknown><Nested>ignored</Nested></Unknown>"
                + "<UnknownEmpty/>"
                + "<ITEM id=\"2\">second</ITEM>"
                + "<SINGLE id=\"9\">solo</SINGLE>"
                + "</Container>";

        Container container = XmlParser.parse(xml, Container.class);

        assertEquals("root", container.getLabel());

        assertNotNull(container.getItems());
        assertEquals(2, container.getItems().size());
        assertEquals("1", container.getItems().get(0).getId());
        assertEquals("first", container.getItems().get(0).getValue());
        assertEquals("a note", container.getItems().get(0).getNote());
        assertEquals("2", container.getItems().get(1).getId());
        assertEquals("second", container.getItems().get(1).getValue());
        assertNull("Attribute declared on the class but absent from this instance must stay unset",
                container.getItems().get(1).getNote());
        assertNull("Field with no matching annotation must stay unset",
                container.getItems().get(0).getNotBoundToXml());

        assertNotNull(container.getSingle());
        assertEquals("9", container.getSingle().getId());
        assertEquals("solo", container.getSingle().getValue());
    }

    @Test
    public void testAttributeMatchingIsCaseSensitiveUnlikeTagMatching() throws Exception {
        CaseSensitiveAttribute wrongCase = XmlParser.parse(
                "<CaseSensitiveAttribute case=\"lower\" />", CaseSensitiveAttribute.class);
        assertNull("Attribute lookup must be exact-case, unlike tag lookup", wrongCase.getValue());

        CaseSensitiveAttribute rightCase = XmlParser.parse(
                "<CaseSensitiveAttribute Case=\"exact\" />", CaseSensitiveAttribute.class);
        assertEquals("exact", rightCase.getValue());
    }

    @Test
    public void testDescriptorIsCachedPerClassNotPerParse() throws Exception {
        Field cacheField = XmlParser.class.getDeclaredField("DESCRIPTOR_CACHE");
        cacheField.setAccessible(true);
        Map<?, ?> cache = (Map<?, ?>) cacheField.get(null);

        String xml = "<Container name=\"root\"><ITEM id=\"1\">first</ITEM></Container>";

        XmlParser.parse(xml, Container.class);
        int sizeAfterFirstParse = cache.size();
        assertTrue("Descriptor cache should hold an entry per distinct class seen (Container, Item)",
                sizeAfterFirstParse >= 2);

        XmlParser.parse(xml, Container.class);
        assertEquals("Parsing the same classes again must not add new descriptor entries",
                sizeAfterFirstParse, cache.size());
    }

    @Test
    public void testParseAttributesCoercesAllSupportedTypes() throws Exception {
        String xml = "<AllAttributeTypes"
                + " stringValue=\"hello\""
                + " primitiveLong=\"123456789012\" boxedLong=\"987654321098\""
                + " primitiveInt=\"42\" boxedInt=\"7\""
                + " primitiveByte=\"5\" boxedByte=\"9\""
                + " primitiveDouble=\"3.14\" boxedDouble=\"2.71\""
                + " primitiveFloat=\"1.5\" boxedFloat=\"6.25\""
                + " primitiveBoolean=\"true\" boxedBoolean=\"false\""
                + " />";

        AllAttributeTypes result = XmlParser.parse(xml, AllAttributeTypes.class);

        assertEquals("hello", result.getStringValue());

        assertEquals(123456789012L, result.getPrimitiveLong());
        assertEquals(Long.valueOf(987654321098L), result.getBoxedLong());

        assertEquals(42, result.getPrimitiveInt());
        assertEquals(Integer.valueOf(7), result.getBoxedInt());

        assertEquals((byte) 5, result.getPrimitiveByte());
        assertEquals(Byte.valueOf((byte) 9), result.getBoxedByte());

        assertEquals(3.14, result.getPrimitiveDouble(), 0.0001);
        assertEquals(Double.valueOf(2.71), result.getBoxedDouble());

        assertEquals(1.5f, result.getPrimitiveFloat(), 0.0001f);
        assertEquals(Float.valueOf(6.25f), result.getBoxedFloat());

        assertTrue(result.isPrimitiveBoolean());
        assertEquals(Boolean.FALSE, result.getBoxedBoolean());
    }

    @Test
    public void testTagFieldsStayNullWhenTheirTagNeverAppears() throws Exception {
        Container empty = XmlParser.parse("<Container name=\"root\"></Container>", Container.class);
        assertNull("List-typed tag field must stay null, not an empty list, when no child ever matches it",
                empty.getItems());
        assertNull("Single-typed tag field must stay null when its tag never appears",
                empty.getSingle());
    }

    @Test
    public void testLastOccurrenceWinsWhenASingleValuedTagIsDuplicated() throws Exception {
        Container duplicateSingle = XmlParser.parse(
                "<Container name=\"root\"><SINGLE id=\"1\">a</SINGLE><SINGLE id=\"2\">b</SINGLE></Container>",
                Container.class);
        assertEquals("A single-valued tag field repeated in the document keeps the last occurrence",
                "2", duplicateSingle.getSingle().getId());
    }

    @Test
    public void testCreativeExtensionTextAroundNestedTagInPrettyPrintedXml() throws Exception {
        // Real class mixing @Attribute + @Tag + @Text; documents that text after the last
        // child tag wins (see getText()'s defensive .trim()), not accumulated text.
        String xml = "<CreativeExtension type=\"verveCTA\">\n"
                + "    real text before\n"
                + "    <VerveCTAButton>\n"
                + "        <HTMLResource><![CDATA[<html></html>]]></HTMLResource>\n"
                + "    </VerveCTAButton>\n"
                + "    trailing text after\n"
                + "</CreativeExtension>";

        CreativeExtension extension = XmlParser.parse(xml, CreativeExtension.class);

        assertEquals("verveCTA", extension.getType());
        assertNotNull(extension.getVerveCTAButton());
        assertNotNull(extension.getVerveCTAButton().getHtmlResource());
        assertEquals("<html></html>", extension.getVerveCTAButton().getHtmlResource().getText());
        assertEquals("trailing text after", extension.getText());
    }

    @Test(timeout = 5000)
    public void testConcurrentFirstAccessToNewClassProducesConsistentResults() throws Exception {
        String xml = "<ConcurrencyProbe id=\"42\">payload</ConcurrencyProbe>";
        int threadCount = 20;
        CountDownLatch startGate = new CountDownLatch(1);
        Thread[] threads = new Thread[threadCount];
        ConcurrencyProbe[] results = new ConcurrencyProbe[threadCount];
        Throwable[] failures = new Throwable[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            threads[index] = new Thread(() -> {
                try {
                    startGate.await();
                    results[index] = XmlParser.parse(xml, ConcurrencyProbe.class);
                } catch (Throwable t) {
                    failures[index] = t;
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        startGate.countDown();
        for (Thread thread : threads) {
            thread.join();
        }

        for (int i = 0; i < threadCount; i++) {
            assertNull("Thread " + i + " must not throw when racing to build the descriptor for the first time",
                    failures[i]);
            assertNotNull("Thread " + i + " must produce a result", results[i]);
            assertEquals("42", results[i].getId());
            assertEquals("payload", results[i].getValue());
        }

        Field cacheField = XmlParser.class.getDeclaredField("DESCRIPTOR_CACHE");
        cacheField.setAccessible(true);
        Map<?, ?> cache = (Map<?, ?>) cacheField.get(null);
        assertTrue("Descriptor cache must hold an entry for the class every thread just raced to parse",
                cache.containsKey(ConcurrencyProbe.class));
    }
}
