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
package net.pubnative.lite.sdk.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class PNCrypto {

    /**
     * Encrypts the given input string using SHA-1 algorithm
     *
     * @param input String to be encrypted
     * @return Encrypted string
     */
    public static String sha1(String input) {
        String result = "";
        if (!TextUtils.isEmpty(input)) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                byte[] bytes = input.getBytes("UTF-8");
                digest.update(bytes, 0, bytes.length);
                bytes = digest.digest();
                for (final byte b : bytes) {
                    stringBuilder.append(String.format("%02X", b));
                }
                result = stringBuilder.toString().toLowerCase(Locale.US);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Encrypts the given input string using md5 algorithm
     *
     * @param input String to be encrypted
     * @return Encrypted string
     */
    public static String md5(String input) {
        String result = "";

        if (!TextUtils.isEmpty(input)) {
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
                digest.update(input.getBytes());
                byte[] messageDigest = digest.digest();
                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte b : messageDigest) {
                    String h = Integer.toHexString(0xFF & b);
                    while (h.length() < 2) {
                        h = "0" + h;
                    }
                    hexString.append(h);
                }
                result = hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
