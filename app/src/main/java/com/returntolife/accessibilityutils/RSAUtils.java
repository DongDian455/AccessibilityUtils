package com.returntolife.accessibilityutils;

import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密工具类，实现公钥加密私钥解密和私钥解密公钥解密
 */
public class RSAUtils {

    /**
     * 公钥解密
     *
     * @param publicKeyText
     * @param text
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String publicKeyText, String text) throws Exception {
//        LogUtils.d("decryptByPublicKey publicKeyText="+publicKeyText+"--text="+text);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(android.util.Base64.decode(publicKeyText, android.util.Base64.DEFAULT));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(android.util.Base64.decode(text, Base64.DEFAULT));
        String str =  new String(result, StandardCharsets.UTF_8);
//        LogUtils.d("decryptByPublicKey str="+str);
        return str;
    }


}