package net.pubnative.lite.sdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.DisplayMetrics;

public enum DrawableResources {
    INTERSTITIAL_CLOSE_BUTTON_NORMAL("iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAMAAADVRocKAAAAUVBMVEUAAAD///////////////////////////////////////////////8AAAAQEBAgICAwMDBAQEBQUFBgYGBwcHCAgICfn5+/v7/Pz8/v7+////9QtCtmAAAADXRSTlMAECBQYHCAj5+/z9/v87rZqQAAAfxJREFUeAHslc2SgyAQhHeJ8UcZVBIJ+P4PuoWZA26t7ehySZV9DKn+GLoz+bp06dKlD5IqqqabWV1TFiqn+61m71Rdfct090rPG9KV+r99PUPVKpu9d9PImpzPg7hrNgnOGlrJWBf4UN/PXr9lh5elP2Vf/IVWnaoOX9/1tKne8RAnClXy7UeCGnmK8qj/O93wpF09wzvrM/5+IIEGDwnA3xkSyThAAO/vSCx3LIcb8IcEYZeUBv6QoJUI0CJ/TGhF+2Hpj6GDMkuX7sIHCgMd1hAkj8QNTX5fDwQbwiP5xYm6qpb9kPijceKlE8KyNZRkgDH1Z8KGf0oYBSOoXw0K8zZh4EOzbhIeoYpf6dcmawI66uMHFQRoTgDZgIOYgt5dEhYboY8tLwwYccBXxYOFnZg7jhgQgD/H3O10yOI4cfQW96iIxwYXBlfLxIMCltQTQQLwj/Lwj6fhCCAB+HMIDcx4IkzA/jTBlHkPQQL05320A8AE5C8HYALwxwCFAUyA/hjwnQ+Q4Yk+L+QsNf1pzw4NAIBhEAi6iqruP21lTCTnYIcEePih8VfBnx1/19xwuGVy0/exhQcvHx15+OXx3RcQXKF8CfQ11hdxjxI8DPE4xwMpj9Q8FPRY04NZj5Y9HN/x/kvifT9Q+InFj0Qzc52ZuW505vKqqqr6JLIRlkedBesAAAAASUVORK5CYII="),
    INTERSTITIAL_CLOSE_BUTTON_PRESSED("iVBORw0KGgoAAAANSUhEUgAAAGAAAABgCAMAAADVRocKAAAAY1BMVEUAAAAjHyD///93cXH///9bVlZ3cXF3cXFxbGx3cXFybGx3cXF3cXFzbW13cXF3cXF0bm53cXF3cXETEhIZGBggHh4mJCQsKioyMDA5NjY/PDxFQkJSTU1eWVlkX19xa2t3cXE1H1L7AAAAE3RSTlMACBAgIDBQYHiAiI+fp7/P19/vm4QQ3gAAAg9JREFUeAHsld1ygyAUhNumahKNjoBIIhHf/ylb8cwEO2XBn5vMuJcysx/HXY8fhw4dOvRGOmV5WQ+kurxmpz3d04K8XdVFutPdczZ4xPLtcyS3AeqWbLt98bLqddeSOt2/nhcbpjgzMjFaCT6TUNrQITuvvX5FDk/F/5V6DpOqVUOkdH3dcK8aTUOsKNSFbt9yqJamuCz1n9I1Dx7Uw0xZr/HvJY+Q7AEB+mvBoyQ0IID3r3m09LIcUugPCGlk/xn0BwQW9z1U2B8Rqqj9YPsjlgKE7dI58gUZyRdLmtlLwg11vq87gklzd764qK4mdj84/mAce2mHYLdGEgDY/0vr+hPB4+8SWvsHCiTwp0Fm8BMkHYp5k3AK+fCrZm4yI8CjZnyQQwCzCQAbcEApsOCSUMgIP1ZwYVBHDb4qHswEmlpTxIAA/CnmOtAhhePE0Svco2w8FrgwuFpiPMhgSXtUeYn8rcaVd/UCSooAEKA/hVDCjDuOCdifdzBl2kOQAP1pHwUAmAD8YwGYgP0x4JsAiED+GPDpAXztCNj+it4v5F1q+tOeHVoBAMIwEBUI3kOx/7TILsB3lx3aJBd+aPpV+GfH3zU3HG6Z3vR5bOHBy0dHHn55fPcFRFcoXwJ9jfVF3KMEDkM8zvFAyiM1DwU91vRg1qNlD8c93vcDhZ9Y+Eg0WvvMzHW/zlxeKaX0AEs5cjxqSuS5AAAAAElFTkSuQmCC");

    private final String encodedString;

    private Bitmap cachedBitmap;

    DrawableResources(final String encodedString) {
        this.encodedString = encodedString;
    }

    public Drawable createDrawable(final Context context) {
        Bitmap bitmap = getBitmap();
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        drawable.setTargetDensity(ViewUtils.asIntPixels(displayMetrics.xdpi, context));
        return drawable;
    }

    public Bitmap getBitmap() {
        if (cachedBitmap == null) {
            byte[] rawImageData = Base64.decode(encodedString, Base64.DEFAULT);
            cachedBitmap = BitmapFactory.decodeByteArray(rawImageData, 0, rawImageData.length);
        }
        return cachedBitmap;
    }

    public void clear() {
        cachedBitmap = null;
    }
}
