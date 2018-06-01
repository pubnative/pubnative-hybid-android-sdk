//
// Copyright (c) 2016, PubNative, Nexage Inc.
// All rights reserved.
// Provided under BSD-3 license as follows:
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice, this
// list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// Neither the name of Nexage, PubNative nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package net.pubnative.lite.sdk.vast.processor;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vast.VASTParser;
import net.pubnative.lite.sdk.vast.model.VASTModel;
import net.pubnative.lite.sdk.vast.model.VAST_DOC_ELEMENTS;
import net.pubnative.lite.sdk.vast.util.XmlTools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * This class is responsible for taking a VAST 2.0 XML file, parsing it,
 * validating it, and creating a valid VASTModel object corresponding to it.
 * 
 * It can handle "regular" VAST XML files as well as VAST wrapper files.
 */
public final class VASTProcessor {

    private static final String TAG = VASTProcessor.class.getName();

    // Maximum number of VAST files that can be read (wrapper file(s) + actual
    // target file)
    private static final int     MAX_VAST_LEVELS  = 5;
    private static final boolean IS_VALIDATION_ON = false;

    private VASTMediaPicker mediaPicker;
    private VASTModel vastModel;
    private StringBuilder mergedVastDocs = new StringBuilder(500);

    public VASTProcessor(VASTMediaPicker mediaPicker) {

        this.mediaPicker = mediaPicker;
    }

    public VASTModel getModel() {

        return vastModel;
    }

    public int process(String xmlData) {

        Logger.d(TAG, "process");
        vastModel = null;
        InputStream is = null;

        try {
            is = new ByteArrayInputStream(xmlData.getBytes(Charset.defaultCharset().name()));
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, e.getMessage(), e);
            return VASTParser.ERROR_XML_PARSE;
        }

        int error = processUri(is, 0);

        try {
            is.close();
        } catch (IOException e) { }

        if (error != VASTParser.ERROR_NONE) {

            return error;
        }

        Document mainDoc = wrapMergedVastDocWithVasts();
        vastModel = new VASTModel(mainDoc);

        if (mainDoc == null) {

            return VASTParser.ERROR_XML_PARSE;
        }

        if (!VASTModelPostValidator.validate(vastModel, mediaPicker)) {

            return VASTParser.ERROR_POST_VALIDATION;
        }

        return VASTParser.ERROR_NONE;
    }

    private Document wrapMergedVastDocWithVasts() {

        Logger.d(TAG, "wrapmergedVastDocWithVasts");
        mergedVastDocs.insert(0, "<VASTS>");
        mergedVastDocs.append("</VASTS>");

        String merged = mergedVastDocs.toString();
        Logger.d(TAG, "Merged VAST doc:\n" + merged);

        Document doc = XmlTools.stringToDocument(merged);
        return doc;

    }
    private int processUri(InputStream is, int depth) {

        Logger.d(TAG, "processUri");

        if (depth >= MAX_VAST_LEVELS) {

            String message = "VAST wrapping exceeded max limit of " + MAX_VAST_LEVELS + ".";
            Logger.e(TAG, message);
            return VASTParser.ERROR_EXCEEDED_WRAPPER_LIMIT;
        }

        Document doc = createDoc(is);

        if (doc == null) {

            return VASTParser.ERROR_XML_PARSE;
        }

        merge(doc);

        // check to see if this is a VAST wrapper ad
        NodeList uriToNextDoc = doc.getElementsByTagName(VAST_DOC_ELEMENTS.vastAdTagURI.getValue());

        if (uriToNextDoc == null || uriToNextDoc.getLength() == 0) {

            // This isn't a wrapper ad, so we're done.
            return VASTParser.ERROR_NONE;

        } else {

            // This is a wrapper ad, so move on to the wrapped ad and process
            // it.
            Logger.d(TAG, "Doc is a wrapper. ");
            Node node = uriToNextDoc.item(0);
            String nextUri = XmlTools.getElementValue(node);
            Logger.d(TAG, "Wrapper URL: " + nextUri);
            InputStream nextInputStream = null;

            try {

                URL nextUrl = new URL(nextUri);
                nextInputStream = nextUrl.openStream();

            } catch (Exception e) {

                Logger.e(TAG, e.getMessage(), e);
                return VASTParser.ERROR_XML_OPEN_OR_READ;
            }

            int error = processUri(nextInputStream, depth + 1);
            try {

                nextInputStream.close();

            } catch (IOException e) {
            }
            return error;
        }
    }

    private Document createDoc(InputStream is) {

        Logger.d(TAG, "About to create doc from InputStream");
        try {

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            Logger.d(TAG, "Doc successfully created.");
            return doc;

        } catch (Exception e) {

            Logger.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    private void merge(Document newDoc) {

        Logger.d(TAG, "About to merge doc into main doc.");

        NodeList nl = newDoc.getElementsByTagName("VAST");

        Node newDocElement = nl.item(0);

        String doc = XmlTools.xmlDocumentToString(newDocElement);
        mergedVastDocs.append(doc);

        Logger.d(TAG, "Merge successful.");
    }
}
