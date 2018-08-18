package com.thebrownarrow.baseutils.util;

import android.content.Context;
import android.content.res.Resources;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author The Brown Arrow.
 * Email - thebrownarrow@gmail.com
 */
public class AppUtils {

    public static String md5(final String string) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(string.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
