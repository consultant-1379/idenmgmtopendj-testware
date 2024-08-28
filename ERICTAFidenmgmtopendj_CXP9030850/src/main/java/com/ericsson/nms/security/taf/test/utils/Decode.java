/*  ------------------------------------------------------------------------------
 *  ******************************************************************************
 *  * COPYRIGHT Ericsson 2016
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  ******************************************************************************
 *  ------------------------------------------------------------------------------
 */
package com.ericsson.nms.security.taf.test.utils;

import java.io.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Decode {

    public static final String __OBFUSCATE = "OBF:";
    public static final String __CRYPT = "CRYPT:";
    private static final String CRYPT_ALGORITHM = "AES";
    private static final byte[] NON_SECRET_KEY = { -50, 50, -16, -26, -99, -61, 94, 45, 26, 75, -39, -44, -48, -75, 40, 4 };
    private static final String CIPHER_STRING = "AES/CBC/PKCS5Padding";
    private String password;
    private String keystoreType;
    private String cryptoAlias;
    private KeyStore keyStore;
    private InputStream in;
    private char[] clearPassword;
    private Key key;

    public void setKeystoreType(final String kt) {
        this.keystoreType = kt;
    }

    public void setCryptoAlias(final String ca) {
        this.cryptoAlias = ca;
    }

    public void setKeystorePassword(final String pass) {
        this.password = pass;
    }

    public char[] unfold(final String s) throws GeneralSecurityException {
        char[] passwordCopy = null;
        if (null != s) {
            if (s.startsWith(__CRYPT)) {
                passwordCopy = decrypt(s).toCharArray();
            } else if (s.startsWith(__OBFUSCATE)) {
                passwordCopy = deobfuscate(s).toCharArray();
            } else {
                passwordCopy = s.toCharArray();
            }
        }
        return passwordCopy;
    }

    public String deobfuscate(String s) {
        if (s.startsWith(__OBFUSCATE)) {
            s = s.substring(__OBFUSCATE.length());
        }

        final byte[] b = new byte[s.length() / 2];
        int l = 0;
        for (int i = 0; i < s.length(); i += 4) {
            final String x = s.substring(i, i + 4);
            final int i0 = Integer.parseInt(x, 36);
            final int i1 = (i0 / 256);
            final int i2 = (i0 % 256);
            b[l++] = (byte) ((i1 + i2 - 254) / 2);
        }

        return new String(b, 0, l);
    }

    public String decrypt(String s) throws GeneralSecurityException {
        if (s.startsWith(__CRYPT)) {
            s = s.substring(__CRYPT.length());
        }

        // Instantiate the cipher
        final Cipher cipher = Cipher.getInstance(CRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(NON_SECRET_KEY, CRYPT_ALGORITHM));
        return new String(cipher.doFinal(hexStringToByteArray(s)));
    }

    private byte[] hexStringToByteArray(final String hex) {
        final byte rc[] = new byte[hex.length() / 2];
        for (int i = 0; i < rc.length; i++) {
            final String h = hex.substring(i * 2, i * 2 + 2);
            final int x = Integer.parseInt(h, 16);
            rc[i] = (byte) x;
        }
        return rc;
    }

    public String decode(final String vector, final String password, final String keystoreJecksFile) {
        String data = "";
        try {
            in = new FileInputStream(keystoreJecksFile);
            keyStore = KeyStore.getInstance(keystoreType);
            clearPassword = unfold(password);
            keyStore.load(in, clearPassword);
            key = keyStore.getKey(cryptoAlias, clearPassword);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (key == null) {
            final String msg = "Encryption key not found";
            throw new NullPointerException(msg);
        }

        try {
            final byte[] iv = Base64.decodeBase64(vector);
            final byte[] encData = Base64.decodeBase64(password);
            final Cipher symmetric = Cipher.getInstance(CIPHER_STRING);
            symmetric.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            final byte[] encryptedData = symmetric.doFinal(encData);
            data = new String(encryptedData);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return data;
    }

}