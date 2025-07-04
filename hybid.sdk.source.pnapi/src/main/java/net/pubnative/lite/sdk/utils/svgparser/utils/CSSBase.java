// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.svgparser.utils;

public class CSSBase {
    protected CSSParser.Ruleset cssRuleset;

    protected CSSBase(String css) {
        this.cssRuleset = new CSSParser(CSSParser.Source.RenderOptions, null).parse(css);
    }
}
