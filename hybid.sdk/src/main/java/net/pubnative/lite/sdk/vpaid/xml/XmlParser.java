// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.xml;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple XML parser.
 * <p>
 * <p><b>Note:</b> in current implementation tag names are case-insensitive.
 * For example &lt;Img/&gt;, &lt;IMG/&gt;, and &lt;img/&gt; are the same tag.
 * <p>Attributes are case-sensitive.
 * For example &lt;PIC width="7in"/&gt; and &lt;PIC WIDTH="6in"/&gt; are separate attributes.
 */
public class XmlParser {
    private static final ConcurrentHashMap<Class<?>, Descriptor> DESCRIPTOR_CACHE = new ConcurrentHashMap<>();

    private static volatile XmlPullParserFactory sParserFactory;

    private static XmlPullParserFactory getParserFactory() throws XmlPullParserException {
        XmlPullParserFactory factory = sParserFactory;
        if (factory == null) {
            synchronized (XmlParser.class) {
                factory = sParserFactory;
                if (factory == null) {
                    factory = XmlPullParserFactory.newInstance();
                    sParserFactory = factory;
                }
            }
        }
        return factory;
    }

    public static <T> T parse(String xml, Class<T> classOfT) throws Exception {
        XmlPullParser parser = getParserFactory().newPullParser();
        parser.setInput(new StringReader(xml));
        parser.next();
        return parseTag(parser, classOfT);
    }

    private static <T> T parseTag(XmlPullParser parser, Class<T> classOfT) throws Exception {
        T tagInstance = classOfT.newInstance();
        parseAttributes(parser, tagInstance);
        parser.next();
        parseElements(parser, tagInstance);
        parser.next();
        return tagInstance;
    }

    private static <T> void parseAttributes(XmlPullParser parser, T tagInstance) throws IllegalAccessException {
        for (AttributeField attributeField : getDescriptor(tagInstance.getClass()).attributeFields) {
            String value = parser.getAttributeValue(null, attributeField.name);
            Field field = attributeField.field;
            bindAttributeFieldValue(tagInstance, field, value);
        }
    }

    private static void bindAttributeFieldValue(Object tagInstance, Field field, String value) throws IllegalAccessException {
        if (TextUtils.isEmpty(value)) {
            return;
        }

        Class fieldClass = field.getType();
        Object coercedValue = null;
        if (fieldClass.equals(String.class)) {
            coercedValue = value;
        } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
            coercedValue = Long.parseLong(value);
        } else if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
            coercedValue = Integer.parseInt(value);
        } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
            coercedValue = Byte.parseByte(value);
        } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
            coercedValue = Double.parseDouble(value);
        } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
            coercedValue = Float.parseFloat(value);
        } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)) {
            coercedValue = Boolean.parseBoolean(value);
        }

        if (coercedValue != null) {
            field.set(tagInstance, coercedValue);
        }
    }

    private static <T> void parseElements(XmlPullParser parser, T tagInstance) throws Exception {
        while (parser.getEventType() == XmlPullParser.START_TAG
                || parser.getEventType() == XmlPullParser.TEXT) {
            if (parser.getEventType() == XmlPullParser.TEXT) {
                parseText(parser, tagInstance);
            } else {
                parseSubTag(parser, tagInstance);
            }
        }
    }

    private static <T> void parseText(XmlPullParser parser, T parent) throws Exception {
        if (parser.getEventType() != XmlPullParser.TEXT) {
            return;
        }
        Field textField = getFieldForText(parent);
        if (textField != null) {
            textField.set(parent, parser.getText());
        }
        parser.next();
    }

    @SuppressWarnings("unchecked")
    private static <T> void parseSubTag(XmlPullParser parser, T parent) throws Exception {
        String tagName = parser.getName();
        int tagDepth = parser.getDepth();

        Field tagField = getFieldForTag(parent, tagName);
        if (tagField == null) {
            skipTag(parser, tagName, tagDepth);
        } else {
            if (List.class.isAssignableFrom(tagField.getType())) {
                ParameterizedType listGenericType = (ParameterizedType) tagField.getGenericType();
                Class<?> listGenericClass = (Class<?>) listGenericType.getActualTypeArguments()[0];
                Object tag = parseTag(parser, listGenericClass);
                List list = (List) tagField.get(parent);
                if (list == null) {
                    list = new ArrayList();
                    tagField.set(parent, list);
                }
                list.add(tag);
            } else {
                Object tag = parseTag(parser, tagField.getType());
                tagField.set(parent, tag);
            }
        }
    }

    private static <T> Field getFieldForTag(T parent, String tagName) {
        return getDescriptor(parent.getClass()).tagFieldsByName.get(tagName.toLowerCase(Locale.ROOT));
    }

    private static <T> Field getFieldForText(T parent) {
        return getDescriptor(parent.getClass()).textField;
    }

    private static void skipTag(XmlPullParser parser, String name, int depth) throws Exception {
        do {
            parser.next();
        } while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equalsIgnoreCase(name) || parser.getDepth() != depth);
        parser.next();
    }

    private static Descriptor getDescriptor(Class<?> clazz) {
        Descriptor descriptor = DESCRIPTOR_CACHE.get(clazz);
        if (descriptor == null) {
            Descriptor newDescriptor = Descriptor.build(clazz);
            Descriptor existing = DESCRIPTOR_CACHE.putIfAbsent(clazz, newDescriptor);
            descriptor = existing != null ? existing : newDescriptor;
        }
        return descriptor;
    }

    /**
     * Which fields carry {@link Attribute}/{@link Tag}/{@link Text} is a fixed property of a
     * model class, not of a parsed instance, so it is resolved once per class and cached here
     * instead of being rediscovered by reflection on every parse.
     */
    private static final class Descriptor {
        final List<AttributeField> attributeFields;
        final Map<String, Field> tagFieldsByName;
        final Field textField;

        private Descriptor(List<AttributeField> attributeFields, Map<String, Field> tagFieldsByName, Field textField) {
            this.attributeFields = attributeFields;
            this.tagFieldsByName = tagFieldsByName;
            this.textField = textField;
        }

        @SuppressWarnings("java:S3011") // Reflection accessibility bypass is required for XML parsing framework
        private static Descriptor build(Class<?> clazz) {
            List<AttributeField> attributeFields = new ArrayList<>();
            Map<String, Field> tagFieldsByName = new HashMap<>();
            Field textField = null;

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                AttributeField attributeField = resolveAttributeField(field);
                if (attributeField != null) {
                    attributeFields.add(attributeField);
                }

                String tagKey = resolveTagKey(field);
                if (tagKey != null && !tagFieldsByName.containsKey(tagKey)) {
                    tagFieldsByName.put(tagKey, field);
                }

                if (textField == null && isTextField(field)) {
                    textField = field;
                }
            }

            return new Descriptor(attributeFields, tagFieldsByName, textField);
        }

        private static AttributeField resolveAttributeField(Field field) {
            Attribute attribute = field.getAnnotation(Attribute.class);
            if (attribute == null) {
                return null;
            }
            String name = TextUtils.isEmpty(attribute.value()) ? field.getName() : attribute.value();
            return new AttributeField(field, name);
        }

        private static String resolveTagKey(Field field) {
            Tag tag = field.getAnnotation(Tag.class);
            if (tag == null) {
                return null;
            }
            String name = TextUtils.isEmpty(tag.value()) ? field.getName() : tag.value();
            return name.toLowerCase(Locale.ROOT);
        }

        private static boolean isTextField(Field field) {
            return field.getAnnotation(Text.class) != null;
        }
    }

    private static final class AttributeField {
        final Field field;
        final String name;

        private AttributeField(Field field, String name) {
            this.field = field;
            this.name = name;
        }
    }
}
