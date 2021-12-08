// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.sdk.mraid.internal;

import android.util.Base64;

import net.pubnative.lite.sdk.mraid.Assets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by erosgarciaponte on 05.01.18.
 */

public class MRAIDHtmlProcessor {
    public static String processRawHtml(String rawHtml) {
        StringBuffer processedHtml = new StringBuffer(rawHtml);

        // Remove the mraid.js script tag.
        // We expect the tag to look like this:
        // <script src='mraid.js'></script>
        // But we should also be to handle additional attributes and whitespace
        // like this:
        // <script type = 'text/javascript' src = 'mraid.js' > </script>

        // Remove the mraid.js script tag.
        String regex = "<script\\s+[^>]*\\bsrc\\s*=\\s*([\"'])mraid\\.js\\1[^>]*>\\s*</script>\\n*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(processedHtml);

        // Add html, head, and/or body tags as needed.
        boolean hasHtmlTag = rawHtml.contains("<html");
        boolean hasHeadTag = rawHtml.contains("<head");
        boolean hasBodyTag = rawHtml.contains("<body");

        String ls = System.getProperty("line.separator");

        if (!hasHtmlTag) {
            if (!hasBodyTag) {
                processedHtml.insert(0, "<body><div id='hybid-ad' align='center'>" + ls);
                processedHtml.append("</div></body>");
            }
            if (!hasHeadTag) {
                processedHtml.insert(0, "<head>" + ls + "</head>" + ls);
            }
            // In the !hasBody && hasHead case, we end up with <body><head></head></body>
            // but that was a terrible case to begin with

            processedHtml.insert(0, "<html>" + ls);
            processedHtml.append(ls).append("</html>");
        } else if (!hasHeadTag) {
            // html tag exists, head tag doesn't, so add it
            regex = "<html[^>]*>";
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(processedHtml);
            int idx = 0;
            while (matcher.find(idx)) {
                processedHtml.insert(matcher.end(), ls + "<head>" + ls + "</head>");
                idx = matcher.end();
            }
        }

        String str = Assets.mraidJS;
        byte[] mraidjsBytes = Base64.decode(str, Base64.DEFAULT);
        String mraidJs = new String(mraidjsBytes);
        String mraidTag = "<script>" + ls + mraidJs + ls + "</script>";

        String omsdkStr = net.pubnative.lite.sdk.viewability.Assets.omsdkjs;
        byte[] omsdkBytes = Base64.decode(omsdkStr, Base64.DEFAULT);
        String omSdk = new String(omsdkBytes);
        String omsdkTag = "<script>" + ls + omSdk + ls + "</script>";

        String scalingStr = Assets.scaling_script;
        byte[] scalingBytes = Base64.decode(scalingStr, Base64.DEFAULT);
        String scaling = new String(scalingBytes);
        String scalingTag = "<script>" + ls + scaling + ls + "</script>";

        // Add meta and style tags to head tag.
        regex = "<head[^>]*>";
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(processedHtml);
        int idx = 0;
        if (matcher.find(idx)) {
            String metaTag = "<meta name='viewport' content='width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no' />";
            String styleTag =
                    "<style>" + ls +
                            "body { margin:0; padding:0;}" + ls +
                            "*:not(input) { -webkit-touch-callout:none; -webkit-user-select:none; -webkit-text-size-adjust:none; }" + ls +
                            "</style>";
            processedHtml.insert(matcher.end(), ls + metaTag + ls + styleTag + ls
                    + mraidTag + ls + omsdkTag + ls + scalingTag);
        }

        return processedHtml.toString();
    }
}
