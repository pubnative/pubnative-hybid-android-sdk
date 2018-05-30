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

package net.pubnative.lite.sdk.vast.model;

import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vast.util.XmlTools;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class VASTModel implements Serializable {

    public static String EXTENSION_POSTVIEW_BANNER = "PN-Postview-Banner";

    private static String TAG = VASTModel.class.getName();

    private static final long serialVersionUID = 4318368258447283733L;

    private transient Document vastsDocument;
    private String pickedMediaFileURL = null;

    // Tracking XPATH
    private static final String XPATH_INLINE_LINEAR_TRACKING     = "/VASTS/VAST/Ad/InLine/Creatives/Creative/Linear/TrackingEvents/Tracking";
    private static final String XPATH_INLINE_NONLINEAR_TRACKING  = "/VASTS/VAST/Ad/InLine/Creatives/Creative/NonLinearAds/TrackingEvents/Tracking";
    private static final String XPATH_WRAPPER_LINEAR_TRACKING    = "/VASTS/VAST/Ad/Wrapper/Creatives/Creative/Linear/TrackingEvents/Tracking";
    private static final String XPATH_WRAPPER_NONLINEAR_TRACKING = "/VASTS/VAST/Ad/Wrapper/Creatives/Creative/NonLinearAds/TrackingEvents/Tracking";

    private static final String XPATH_COMBINED_TRACKING = XPATH_INLINE_LINEAR_TRACKING + "|" + XPATH_INLINE_NONLINEAR_TRACKING + "|" + XPATH_WRAPPER_LINEAR_TRACKING + "|" + XPATH_WRAPPER_NONLINEAR_TRACKING;

    // Direct items XPATH
    private static final String XPATH_MEDIA_FILE   = "//MediaFile";
    private static final String XPATH_DURATION     = "//Duration";
    private static final String XPATH_VIDEO_CLICKS = "//VideoClicks";
    private static final String XPATH_IMPRESSION   = "//Impression";
    private static final String XPATH_ERROR        = "//Error";
    private static final String XPATH_BANNER       = "//Extension[@type='{EXTENSION_TYPE}']/Banner";

    private static final String EXTENSION_TYPE_KEY = "{EXTENSION_TYPE}";

    public VASTModel(Document vasts) {

        this.vastsDocument = vasts;
    }

    public Document getVastsDocument() {

        return vastsDocument;
    }

    public HashMap<TRACKING_EVENTS_TYPE, List<String>> getTrackingUrls() {

        Logger.d(TAG, "getTrackingUrls");

        List<String> tracking;

        HashMap<TRACKING_EVENTS_TYPE, List<String>> trackings = new HashMap<TRACKING_EVENTS_TYPE, List<String>>();

        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            NodeList nodes = (NodeList) xpath.evaluate(XPATH_COMBINED_TRACKING, vastsDocument, XPathConstants.NODESET);
            Node node;
            String trackingURL;
            String eventName;
            TRACKING_EVENTS_TYPE key;

            if (nodes != null) {

                for (int i = 0; i < nodes.getLength(); i++) {

                    node = nodes.item(i);
                    NamedNodeMap attributes = node.getAttributes();

                    eventName = (attributes.getNamedItem("event")).getNodeValue();

                    try {

                        key = TRACKING_EVENTS_TYPE.valueOf(eventName);

                    } catch (IllegalArgumentException e) {

                        Logger.w(TAG, "Event:" + eventName + " is not valid. Skipping it.");
                        continue;
                    }

                    trackingURL = XmlTools.getElementValue(node);

                    if (trackings.containsKey(key)) {

                        tracking = trackings.get(key);
                        tracking.add(trackingURL);

                    } else {

                        tracking = new ArrayList<String>();
                        tracking.add(trackingURL);
                        trackings.put(key, tracking);
                    }
                }
            }

        } catch (Exception e) {

            Logger.e(TAG, e.getMessage(), e);
            return null;
        }

        return trackings;
    }

    public List<VASTMediaFile> getMediaFiles() {

        Logger.d(TAG, "getMediaFiles");

        ArrayList<VASTMediaFile> mediaFiles = new ArrayList<VASTMediaFile>();

        XPath xpath = XPathFactory.newInstance().newXPath();

        try {

            NodeList nodes = (NodeList) xpath.evaluate(XPATH_MEDIA_FILE, vastsDocument, XPathConstants.NODESET);
            Node node;
            VASTMediaFile mediaFile;
            String mediaURL;
            Node attributeNode;

            if (nodes != null) {

                for (int i = 0; i < nodes.getLength(); i++) {

                    mediaFile = new VASTMediaFile();
                    node = nodes.item(i);
                    NamedNodeMap attributes = node.getAttributes();

                    attributeNode = attributes.getNamedItem("apiFramework");
                    mediaFile.setApiFramework((attributeNode == null) ? null : attributeNode.getNodeValue());

                    attributeNode = attributes.getNamedItem("bitrate");
                    mediaFile.setBitrate((attributeNode == null) ? null : new BigInteger(attributeNode.getNodeValue()));

                    attributeNode = attributes.getNamedItem("delivery");
                    mediaFile.setDelivery((attributeNode == null) ? null : attributeNode.getNodeValue());

                    attributeNode = attributes.getNamedItem("height");
                    mediaFile.setHeight((attributeNode == null) ? null : new BigInteger(attributeNode.getNodeValue()));

                    attributeNode = attributes.getNamedItem("id");
                    mediaFile.setId((attributeNode == null) ? null : attributeNode.getNodeValue());

                    attributeNode = attributes.getNamedItem("maintainAspectRatio");
                    mediaFile.setMaintainAspectRatio((attributeNode == null) ? null : Boolean.valueOf(attributeNode.getNodeValue()));

                    attributeNode = attributes.getNamedItem("scalable");
                    mediaFile.setScalable((attributeNode == null) ? null : Boolean.valueOf(attributeNode.getNodeValue()));

                    attributeNode = attributes.getNamedItem("type");
                    mediaFile.setType((attributeNode == null) ? null : attributeNode.getNodeValue());

                    attributeNode = attributes.getNamedItem("width");
                    mediaFile.setWidth((attributeNode == null) ? null : new BigInteger(attributeNode.getNodeValue()));

                    mediaURL = XmlTools.getElementValue(node);
                    mediaFile.setValue(mediaURL);

                    mediaFiles.add(mediaFile);
                }
            }

        } catch (Exception e) {

            Logger.e(TAG, e.getMessage(), e);
            return null;
        }

        return mediaFiles;
    }

    public String getDuration() {

        Logger.d(TAG, "getDuration");

        String duration = null;

        XPath xpath = XPathFactory.newInstance().newXPath();

        try {

            NodeList nodes = (NodeList) xpath.evaluate(XPATH_DURATION, vastsDocument, XPathConstants.NODESET);
            Node node;

            if (nodes != null) {

                for (int i = 0; i < nodes.getLength(); i++) {

                    node = nodes.item(i);
                    duration = XmlTools.getElementValue(node);
                }
            }

        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
            return null;
        }

        return duration;
    }

    public String getExtensionURL(String type) {

        String result = null;

        String xPath      = XPATH_BANNER.replace(EXTENSION_TYPE_KEY, type);
        List<String> extensions = getListFromXPath(xPath);

        // We will use the first banner seen of this type
        if (extensions.size() > 0) {

            result = extensions.get(0);
        }

        return result;
    }

    public VideoClicks getVideoClicks() {

        Logger.d(TAG, "getVideoClicks");

        VideoClicks videoClicks = new VideoClicks();

        XPath xpath = XPathFactory.newInstance().newXPath();

        try {

            NodeList nodes = (NodeList) xpath.evaluate(XPATH_VIDEO_CLICKS, vastsDocument, XPathConstants.NODESET);
            Node node;

            if (nodes != null) {

                for (int i = 0; i < nodes.getLength(); i++) {

                    node = nodes.item(i);

                    NodeList childNodes = node.getChildNodes();

                    Node child;
                    String value = null;

                    for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {

                        child = childNodes.item(childIndex);
                        String nodeName = child.getNodeName();

                        if (nodeName.equalsIgnoreCase("ClickTracking")) {

                            value = XmlTools.getElementValue(child);
                            videoClicks.getClickTracking().add(value);

                        } else if (nodeName.equalsIgnoreCase("ClickThrough")) {

                            value = XmlTools.getElementValue(child);
                            videoClicks.setClickThrough(value);

                        } else if (nodeName.equalsIgnoreCase("CustomClick")) {

                            value = XmlTools.getElementValue(child);
                            videoClicks.getCustomClick().add(value);
                        }
                    }
                }
            }

        } catch (Exception e) {

            Logger.e(TAG, e.getMessage(), e);
            return null;
        }

        return videoClicks;
    }

    public List<String> getImpressions() {

        Logger.d(TAG, "getImpressions");

        List<String> list = getListFromXPath(XPATH_IMPRESSION);

        return list;
    }

    public List<String> getErrorUrl() {

        Logger.d(TAG, "getErrorUrl");

        List<String> list = getListFromXPath(XPATH_ERROR);

        return list;
    }

    private List<String> getListFromXPath(String xPath) {

        Logger.d(TAG, "getListFromXPath");

        ArrayList<String> list = new ArrayList<String>();

        XPath xpath = XPathFactory.newInstance().newXPath();

        try {

            NodeList nodes = (NodeList) xpath.evaluate(xPath, vastsDocument, XPathConstants.NODESET);
            Node node;

            if (nodes != null) {

                for (int i = 0; i < nodes.getLength(); i++) {

                    node = nodes.item(i);
                    list.add(XmlTools.getElementValue(node));
                }
            }

        } catch (Exception e) {

            Logger.e(TAG, e.getMessage(), e);
            return null;
        }

        return list;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {

        Logger.d(TAG, "writeObject: about to write");
        oos.defaultWriteObject();

        String data = XmlTools.xmlDocumentToString(vastsDocument);
        // oos.writeChars();
        oos.writeObject(data);
        Logger.d(TAG, "done writing");
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {

        Logger.d(TAG, "readObject: about to read");
        ois.defaultReadObject();

        String vastString = (String) ois.readObject();
        Logger.d(TAG, "vastString data is:\n" + vastString + "\n");

        vastsDocument = XmlTools.stringToDocument(vastString);

        Logger.d(TAG, "done reading");
    }

    public String getPickedMediaFileURL() {

        return pickedMediaFileURL;
    }

    public void setPickedMediaFileURL(String pickedMediaFileURL) {

        this.pickedMediaFileURL = pickedMediaFileURL;
    }
}
