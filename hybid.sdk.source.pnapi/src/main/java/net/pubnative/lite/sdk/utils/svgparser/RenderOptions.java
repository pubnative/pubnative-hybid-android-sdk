// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser;

import net.pubnative.lite.sdk.utils.svgparser.utils.RenderOptionsBase;

public class RenderOptions extends RenderOptionsBase
{
   public RenderOptions()
   {
      super();
   }

   public static RenderOptions  create()
   {
      return new RenderOptions();
   }

   public RenderOptions  css(String css)
   {
      return (RenderOptions) super.css(css);
   }

   public RenderOptions  css(CSS css)
   {
      return (RenderOptions) super.css(css);
   }
   public boolean hasCss()
   {
      return super.hasCss();
   }

   public RenderOptions  preserveAspectRatio(PreserveAspectRatio preserveAspectRatio)
   {
      return (RenderOptions) super.preserveAspectRatio(preserveAspectRatio);
   }
   public boolean hasPreserveAspectRatio()
   {
      return super.hasPreserveAspectRatio();
   }

   public RenderOptions  view(String viewId)
   {
      return (RenderOptions) super.view(viewId);
   }
   public boolean hasView()
   {
      return super.hasView();
   }
   public RenderOptions  viewBox(float minX, float minY, float width, float height)
   {
      return (RenderOptions) super.viewBox(minX, minY, width, height);
   }
   public boolean hasViewBox()
   {
      return super.hasViewBox();
   }
   public RenderOptions  viewPort(float minX, float minY, float width, float height)
   {
      return (RenderOptions) super.viewPort(minX, minY, width, height);
   }
   public boolean hasViewPort()
   {
      return super.hasViewPort();
   }

   public RenderOptions  target(String targetId)
   {
      return (RenderOptions) super.target(targetId);
   }
   public boolean hasTarget()
   {
      return super.hasTarget();
   }
}
