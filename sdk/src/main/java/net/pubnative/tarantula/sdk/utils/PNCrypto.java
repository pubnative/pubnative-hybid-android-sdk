package net.pubnative.tarantula.sdk.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class PNCrypto {

    private static final String TAG = PNCrypto.class.getSimpleName();

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
                byte messageDigest[] = digest.digest();
                // Create Hex String
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++) {
                    String h = Integer.toHexString(0xFF & messageDigest[i]);
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
