// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import android.graphics.Picture;

import net.pubnative.lite.sdk.utils.svgparser.utils.SVGBase;

import java.io.InputStream;

public class SVG
{
   private static final String  VERSION = "1.5";

   private SVGBase base;

   // Users should use one of the getFromX() methods to create an instance of SVG
   private SVG(SVGBase base)
   {
      this.base = base;
   }

   public static SVG  getFromInputStream(InputStream is) throws SVGParseException
   {
      return new SVG(SVGBase.getFromInputStream(is));
   }

   public static SVG  getFromString(String svg) throws SVGParseException
   {
      return new SVG(SVGBase.getFromString(svg));
   }

   public Picture  renderToPicture()
   {
      return base.renderToPicture(null);
   }

   public Picture  renderToPicture(int widthInPixels, int heightInPixels)
   {
      return renderToPicture(widthInPixels, heightInPixels, null);
   }

   public Picture  renderToPicture(int widthInPixels, int heightInPixels, RenderOptions renderOptions)
   {
      return base.renderToPicture(widthInPixels, heightInPixels, renderOptions);
   }
}
