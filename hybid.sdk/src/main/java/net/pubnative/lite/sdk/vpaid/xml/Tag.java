package net.pubnative.lite.sdk.vpaid.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {

    String value() default "";

}
