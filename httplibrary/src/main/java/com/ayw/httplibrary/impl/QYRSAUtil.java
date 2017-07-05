package com.ayw.httplibrary.impl;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

public class QYRSAUtil {

    private static final String KEY_ALGORITHM = "RSA";

    public static String sign(String privateKey, String val) {
        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(restorePrivateKey(Base64.decode(privateKey, Base64.DEFAULT)));
            byte[] data = val.getBytes();
            sign.update(data);
            byte[] signature = sign.sign();
            String signString = new String(Base64.encode(signature, Base64.DEFAULT));
            return signString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}