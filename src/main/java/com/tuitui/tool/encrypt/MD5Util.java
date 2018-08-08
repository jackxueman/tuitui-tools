package com.tuitui.tool.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * MD5 工具类
 * @author liujianxue
 * @email  1071935039@qq.com
 * @date 2018/1/8
 */
public class MD5Util {

    private MD5Util() {
    }

    /**
     * 生成uuid并转成16进制
     * @return
     */
    public static String generatorToken(){
        return generateValue(UUID.randomUUID().toString());
    }

    public static String toHexString(byte[] data) {
        // 16进制数码
        final char[] hexCode = "0123456789abcdef".toCharArray();

        if (data == null) {
            return null;
        }
        StringBuilder r = new StringBuilder(data.length * 2);
        byte[] arrayOfByte = data;
        int j = data.length;
        for (int i = 0; i < j; i++) {
            byte b = arrayOfByte[i];
            //取高4位
            r.append(hexCode[(b >> 4 & 0xF)]);
            //取低4位
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static String generateValue(String param) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(param.getBytes());
            byte[] messageDigest = algorithm.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密（32位）
     *
     * @param source 原串
     * @return 加密串
     */
    public static String encrypt32(String source) {
        String md5 = null;
        if (source != null) {
            try {
                md5 = encrypt32(source.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    /**
     * MD5加密（32位）
     *
     * @param source 原串
     * @return 加密串
     */
    private static String encrypt32(byte[] source) {
        String s = null;
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        final int temp = 0xf;
        final int arraySize = 32;
        final int strLen = 16;
        final int offset = 4;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte[] tmp = md.digest();
            char[] str = new char[arraySize];
            int k = 0;
            for (int i = 0; i < strLen; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> offset & temp];
                str[k++] = hexDigits[byte0 & temp];
            }
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
