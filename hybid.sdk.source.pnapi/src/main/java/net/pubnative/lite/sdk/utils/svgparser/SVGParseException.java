package net.pubnative.lite.sdk.utils.svgparser;

import org.xml.sax.SAXException;

public class SVGParseException extends SAXException
{
   public SVGParseException(String msg)
   {
      super(msg);
   }

   public SVGParseException(String msg, Exception cause)
   {
      super(msg, cause);
   }
}
