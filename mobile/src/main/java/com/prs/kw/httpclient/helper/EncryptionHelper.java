package com.prs.kw.httpclient.helper;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by pranjal on 26/6/15.
 */
public class EncryptionHelper {

    public static final String SECRET_KEY = "@31!&8";
    public static String TAG = "EncryptionHelper";

    private static SecretKeySpec sks = null;
    private static SecretKeySpec getSecretKey(){
        // Set up secret key spec for 128-bit AES encryption and decryption
        if(sks == null) {
            try {
                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
                sr.setSeed(Base64.encode(SECRET_KEY.getBytes(),Base64.DEFAULT));
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init(128, sr);
                sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sks;
    }

    static boolean dummy = true;
    public static String encrypt(String input) {

        if(dummy)
            return input;

        Cipher c = null;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encodedBytes = c.doFinal(input.getBytes());
            byte[] base64EncodedStr = Base64.encode(encodedBytes,Base64.DEFAULT);
            return URLEncoder.encode(new String(base64EncodedStr), "utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String input) {

        if(dummy)
            return input;

        Cipher c = null;
        try {
            c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] base64DecodedStr = Base64.decode(URLDecoder.decode(new String(input), "utf-8"),Base64.DEFAULT);
            byte[] decodedBytes = c.doFinal(base64DecodedStr);
            return new String(decodedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
