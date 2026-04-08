// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.vpaid.xml;

import android.text.TextUtils;

import net.pubnative.lite.sdk.utils.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Simple XML parser.
 * <p>
 * <p><b>Note:</b> in current implementation tag names are case-insensitive.
 * For example &lt;Img/&gt;, &lt;IMG/&gt;, and &lt;img/&gt; are the same tag.
 * <p>Attributes are case-sensitive.
 * For example &lt;PIC width="7in"/&gt; and &lt;PIC WIDTH="6in"/&gt; are separate attributes.
 */
public class XmlParser {
    private static final Map<AnnotatedElement, Map<Class<? extends Annotation>,
            Annotation>> ANNOTATION_CACHE = new WeakHashMap<>();
    public static <T> T parse(String xml, Class<T> classOfT) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
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

    @SuppressWarnings("java:S3011") // Reflection accessibility bypass is required for XML parsing framework
    private static <T> void parseAttributes(XmlPullParser parser, T tagInstance) throws IllegalAccessException {
        for (Field field : tagInstance.getClass().getDeclaredFields()) {
            Attribute attribute = getAnnotation(field, Attribute.class);
            if (attribute == null) {
                continue;
            }
            String attributeValue = attribute.value();
            if (TextUtils.isEmpty(attributeValue)) {
                attributeValue = field.getName();
            }
            String value = parser.getAttributeValue(null, attributeValue);
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            // Accessibility bypass needed to set private/protected fields from XML attributes
            field.setAccessible(true);
            Class fieldClass = field.getType();
            if (fieldClass.equals(String.class)) {
                field.set(tagInstance, value);
            } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
                field.setLong(tagInstance, Long.parseLong(value));
            } else if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
                field.setInt(tagInstance, Integer.parseInt(value));
            } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
                field.setByte(tagInstance, Byte.parseByte(value));
            } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
                field.setDouble(tagInstance, Double.parseDouble(value));
            } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
                field.setFloat(tagInstance, Float.parseFloat(value));
            } else if (Boolean.class.equals(fieldClass) || boolean.class.equals(fieldClass)) {
                field.setBoolean(tagInstance, Boolean.parseBoolean(value));
            }
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

    @SuppressWarnings("java:S3011") // Reflection accessibility bypass is required for XML parsing framework
    private static <T> void parseText(XmlPullParser parser, T parent) throws Exception {
        if (parser.getEventType() != XmlPullParser.TEXT) {
            return;
        }
        Field textField = getFieldForText(parent);
        if (textField != null) {
            // Accessibility bypass needed to set private/protected text fields from XML content
            textField.setAccessible(true);
            textField.set(parent, parser.getText());
        }
        parser.next();
    }

    @SuppressWarnings({"unchecked", "java:S3011"}) // Reflection accessibility bypass is required for XML parsing framework
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
                // Accessibility bypass needed to set private/protected list fields from XML tags
                tagField.setAccessible(true);
                List list = (List) tagField.get(parent);
                if (list == null) {
                    list = new ArrayList();
                    tagField.set(parent, list);
                }
                list.add(tag);
            } else {
                Object tag = parseTag(parser, tagField.getType());
                // Accessibility bypass needed to set private/protected fields from XML tags
                tagField.setAccessible(true);
                tagField.set(parent, tag);
            }
        }
    }

    private static <T> Field getFieldForTag(T parent, String tagName) {
        for (Field field : parent.getClass().getDeclaredFields()) {
            Tag tagAnnotation = getAnnotation(field, Tag.class);
            if (tagAnnotation != null) {
                String tagValue = tagAnnotation.value();
                if (TextUtils.isEmpty(tagValue)) {
                    tagValue = field.getName();
                }
                if (tagValue.equalsIgnoreCase(tagName)) {
                    return field;
                }
            }
        }
        return null;
    }

    private static <T> Field getFieldForText(T parent) {
        for (Field field : parent.getClass().getDeclaredFields()) {
            Text textAnnotation = getAnnotation(field, Text.class);
            if (textAnnotation != null) {
                return field;
            }
        }
        return null;
    }

    private static void skipTag(XmlPullParser parser, String name, int depth) throws Exception {
        do {
            parser.next();
        } while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equalsIgnoreCase(name) || parser.getDepth() != depth);
        parser.next();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        try {
            Map<Class<? extends Annotation>, Annotation> elementCache = ANNOTATION_CACHE.get(element);
            if (elementCache != null) {
                Annotation cachedAnnotation = elementCache.get(annotationType);
                if (cachedAnnotation != null) {
                    return (T) cachedAnnotation;
                }
            }

            for (Annotation annotation : element.getDeclaredAnnotations()) {
                if (annotationType.isInstance(annotation)) {
                    if (elementCache == null) {
                        elementCache = new HashMap<>();
                        ANNOTATION_CACHE.put(element, elementCache);
                    }
                    elementCache.put(annotationType, annotation);
                    return (T) annotation;
                }
            }

            if (elementCache == null) {
                elementCache = new HashMap<>();
                ANNOTATION_CACHE.put(element, elementCache);
            }
            elementCache.put(annotationType, null);
        } catch (OutOfMemoryError e) {
            Logger.e(XmlParser.class.getSimpleName(), "Error getting annotation: " + e.getMessage());
        }
        return null;
    }

}
