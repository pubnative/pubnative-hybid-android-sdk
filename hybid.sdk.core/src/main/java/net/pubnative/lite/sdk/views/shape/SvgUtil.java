// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.views.shape;

import android.content.Context;

import net.pubnative.lite.sdk.views.shape.path.parser.IoUtil;
import net.pubnative.lite.sdk.views.shape.path.parser.PathInfo;
import net.pubnative.lite.sdk.views.shape.path.parser.SvgToPath;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SvgUtil {
    private static final Map<Integer, PathInfo> PATH_MAP = new ConcurrentHashMap<Integer, PathInfo>();

    public static PathInfo readSvg(Context context, int resId) {
        PathInfo pathInfo = PATH_MAP.get(resId);
        if (pathInfo == null) {
            InputStream is = null;
            try {
                is = context.getResources().openRawResource(resId);
                pathInfo = SvgToPath.getSVGFromInputStream(is);
                PATH_MAP.put(resId, pathInfo);
            } finally {
                IoUtil.closeQuitely(is);
            }
        }

        return pathInfo;
    }
}
