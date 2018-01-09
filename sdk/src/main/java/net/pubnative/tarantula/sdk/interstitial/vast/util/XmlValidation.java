package net.pubnative.tarantula.sdk.interstitial.vast.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class XmlValidation {
    private static String TAG = XmlValidation.class.getSimpleName();

    public static boolean validate(InputStream schemaStream, String xml) {
        VASTLog.i(TAG, "Beginning XSD validation.");
        SchemaFactory factory = new XMLSchemaFactory();
        Source schemaSource = new StreamSource(schemaStream);
        Source xmlSource = new StreamSource(new ByteArrayInputStream(xml.getBytes()));
        Schema schema;
        try {
            schema = factory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
        } catch (Exception e) {
            VASTLog.e(TAG, e.getMessage(), e);
            return false;
        }
        VASTLog.i(TAG, "Completed XSD validation..");
        return true;
    }
}
