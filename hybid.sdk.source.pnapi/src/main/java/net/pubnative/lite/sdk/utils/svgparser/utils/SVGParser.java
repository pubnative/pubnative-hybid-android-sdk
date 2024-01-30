package net.pubnative.lite.sdk.utils.svgparser.utils;

import net.pubnative.lite.sdk.utils.svgparser.SVG;
import net.pubnative.lite.sdk.utils.svgparser.SVGExternalFileResolver;
import net.pubnative.lite.sdk.utils.svgparser.SVGParseException;

import java.io.InputStream;

interface SVGParser
{
    /**
     * Try to parse the stream contents to an {@link SVG} instance.
     */
    SVGBase parseStream(InputStream is) throws SVGParseException;

    /**
     * Tells the parser whether to allow the expansion of internal entities.
     * An example of a document containing an internal entities is:
     */
    SVGParser setInternalEntitiesEnabled(boolean enable);

    /**
     * Register an {@link SVGExternalFileResolver} instance that the parser should use when resolving
     * external references such as images, fonts, and CSS stylesheets.
     */
    SVGParser setExternalFileResolver(SVGExternalFileResolver fileResolver);
}