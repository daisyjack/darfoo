package com.darfoo.darfoolauncher.support;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by zjh on 14-12-23.
 */
public class Cryptor {
    /**
     * 使用base64加密
     */
    public static String base64EncodeStr(String plainText){
        byte[] b = plainText.getBytes();
        Base64 base64 = new Base64();
        b = base64.encode(b);
        String s = new String(b);
        System.out.println("encode result -> " + s);
        return s;
    }

    /**
     * 使用base64解密
     */
    public static String base64DecodeStr(String encodeStr){
        byte[] b = encodeStr.getBytes();
        Base64 base64 = new Base64();
        b = base64.decode(b);
        String s = new String(b);
        System.out.println("decode result -> " + s);
        return s;
    }

    public static String encryptQiniuUrl(String originurl){
        return base64EncodeStr(base64EncodeStr(base64EncodeStr(originurl)));
    }

    public static String decryptQiniuUrl(String crypturl){
        return base64DecodeStr(base64DecodeStr(base64DecodeStr(crypturl)));
    }
}
