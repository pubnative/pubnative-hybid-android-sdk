// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import net.pubnative.lite.sdk.utils.svgparser.utils.TextScanner;

import java.util.HashMap;
import java.util.Map;

public class PreserveAspectRatio
{
   private final Alignment  alignment;
   private final Scale      scale;

   private static final Map<String, Alignment> aspectRatioKeywords = new HashMap<>(10);

   public static final PreserveAspectRatio  UNSCALED = new PreserveAspectRatio(null, null);

   public static final PreserveAspectRatio  STRETCH = new PreserveAspectRatio(Alignment.none, null);

   public static final PreserveAspectRatio  LETTERBOX = new PreserveAspectRatio(Alignment.xMidYMid, Scale.meet);

   public static final PreserveAspectRatio  START = new PreserveAspectRatio(Alignment.xMinYMin, Scale.meet);

   public static final PreserveAspectRatio  END = new PreserveAspectRatio(Alignment.xMaxYMax, Scale.meet);

   public static final PreserveAspectRatio  TOP = new PreserveAspectRatio(Alignment.xMidYMin, Scale.meet);

   public static final PreserveAspectRatio  BOTTOM = new PreserveAspectRatio(Alignment.xMidYMax, Scale.meet);

   public static final PreserveAspectRatio  FULLSCREEN = new PreserveAspectRatio(Alignment.xMidYMid, Scale.slice);

   public static final PreserveAspectRatio  FULLSCREEN_START = new PreserveAspectRatio(Alignment.xMinYMin, Scale.slice);
   public enum Alignment
   {
      /** Document is stretched to fit both the width and height of the viewport. When using this Alignment value, the value of Scale is not used and will be ignored. */
      none,
      /** Document is positioned at the top left of the viewport. */
      xMinYMin,
      /** Document is positioned at the centre top of the viewport. */
      xMidYMin,
      /** Document is positioned at the top right of the viewport. */
      xMaxYMin,
      /** Document is positioned at the middle left of the viewport. */
      xMinYMid,
      /** Document is centred in the viewport both vertically and horizontally. */
      xMidYMid,
      /** Document is positioned at the middle right of the viewport. */
      xMaxYMid,
      /** Document is positioned at the bottom left of the viewport. */
      xMinYMax,
      /** Document is positioned at the bottom centre of the viewport. */
      xMidYMax,
      /** Document is positioned at the bottom right of the viewport. */
      xMaxYMax
   }

   public enum Scale
   {
      /**
       * The document is scaled so that it is as large as possible without overflowing the viewport.
       * There may be blank areas on one or more sides of the document.
       */
      meet,
      /**
       * The document is scaled so that entirely fills the viewport. That means that some of the
       * document may fall outside the viewport and will not be rendered.
       */
      slice
   }


   static {
      aspectRatioKeywords.put("none", Alignment.none);
      aspectRatioKeywords.put("xMinYMin", Alignment.xMinYMin);
      aspectRatioKeywords.put("xMidYMin", Alignment.xMidYMin);
      aspectRatioKeywords.put("xMaxYMin", Alignment.xMaxYMin);
      aspectRatioKeywords.put("xMinYMid", Alignment.xMinYMid);
      aspectRatioKeywords.put("xMidYMid", Alignment.xMidYMid);
      aspectRatioKeywords.put("xMaxYMid", Alignment.xMaxYMid);
      aspectRatioKeywords.put("xMinYMax", Alignment.xMinYMax);
      aspectRatioKeywords.put("xMidYMax", Alignment.xMidYMax);
      aspectRatioKeywords.put("xMaxYMax", Alignment.xMaxYMax);
   }

   PreserveAspectRatio(Alignment alignment, Scale scale)
   {
      this.alignment = alignment;
      this.scale = scale;
   }

   public static PreserveAspectRatio  of(String value)
   {
      try {
         return parsePreserveAspectRatio(value);
      } catch (SVGParseException e) {
         throw new IllegalArgumentException(e.getMessage());
      }
   }
   public Alignment  getAlignment()
   {
      return alignment;
   }

   public Scale  getScale()
   {
      return scale;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PreserveAspectRatio other = (PreserveAspectRatio) obj;
      return (alignment == other.alignment && scale == other.scale);
   }

   @Override
   public String toString()
   {
      return alignment + " " + scale;
   }

   private static PreserveAspectRatio  parsePreserveAspectRatio(String val) throws SVGParseException
   {
      TextScanner scan = new TextScanner(val);
      scan.skipWhitespace();

      String  word = scan.nextToken();
      if ("defer".equals(word)) {    // Ignore defer keyword
         scan.skipWhitespace();
         word = scan.nextToken();
      }

      Alignment  align = aspectRatioKeywords.get(word);
      Scale      scale = null;

      scan.skipWhitespace();

      if (!scan.empty()) {
         String meetOrSlice = scan.nextToken();
         switch (meetOrSlice) {
            case "meet":
               scale = Scale.meet; break;
            case "slice":
               scale = Scale.slice; break;
            default:
               throw new SVGParseException("Invalid preserveAspectRatio definition: " + val);
         }
      }
      return new PreserveAspectRatio(align, scale);
   }

}
