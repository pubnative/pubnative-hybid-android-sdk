package net.pubnative.lite.sdk.vpaid;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import net.pubnative.lite.sdk.vpaid.xml.XmlParser;

public class XmlParserTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface TestTag {
        String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface TestAttribute {
        String value() default "";
    }
    static class TestClass {
        @TestTag("tag1")
        private String tagField;

        @TestAttribute("attr1")
        private int attributeField;

        private String unannotatedField;
    }

    @Test
    public void testGetAnnotationWithCaching() throws Exception {
        Field cacheField = XmlParser.class.getDeclaredField("ANNOTATION_CACHE");
        cacheField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<Object, Map<Class<? extends Annotation>, Annotation>> cache =
                (Map<Object, Map<Class<? extends Annotation>, Annotation>>) cacheField.get(null);
        cache.clear();

        Method getAnnotationMethod = XmlParser.class.getDeclaredMethod(
                "getAnnotation",
                java.lang.reflect.AnnotatedElement.class,
                Class.class
        );
        getAnnotationMethod.setAccessible(true);

        Field tagField = TestClass.class.getDeclaredField("tagField");
        Field attrField = TestClass.class.getDeclaredField("attributeField");
        Field unannotatedField = TestClass.class.getDeclaredField("unannotatedField");

        TestTag tagAnnotation = (TestTag) getAnnotationMethod.invoke(
                null, tagField, TestTag.class);
        assertNotNull("Should find the TestTag annotation", tagAnnotation);
        assertEquals("tag1", tagAnnotation.value());

        TestAttribute attrAnnotation = (TestAttribute) getAnnotationMethod.invoke(
                null, attrField, TestAttribute.class);
        assertNotNull("Should find the TestAttribute annotation", attrAnnotation);
        assertEquals("attr1", attrAnnotation.value());

        assertEquals("Cache should have 2 elements", 2, cache.size());
        TestTag tagAnnotation2 = (TestTag) getAnnotationMethod.invoke(
                null, tagField, TestTag.class);
        assertNotNull("Should find annotation from cache", tagAnnotation2);
        assertSame("Second call should return cached instance", tagAnnotation, tagAnnotation2);
        TestAttribute wrongAnnotation = (TestAttribute) getAnnotationMethod.invoke(
                null, tagField, TestAttribute.class);
        assertNull("Should not find wrong annotation type", wrongAnnotation);
        TestAttribute wrongAnnotation2 = (TestAttribute) getAnnotationMethod.invoke(
                null, tagField, TestAttribute.class);
        assertNull("Should use cached negative result", wrongAnnotation2);

        TestTag missingAnnotation = (TestTag) getAnnotationMethod.invoke(
                null, unannotatedField, TestTag.class);
        assertNull("Should not find annotation on unannotated field", missingAnnotation);
        assertEquals("Cache should have 3 elements", 3, cache.size());
    }
}
