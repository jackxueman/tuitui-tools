package com.tuitui.tool.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密
 *
 * @author liujianxue
 * @email  1071935039@qq.com
 * @date 2018/1/9
 */
public final class AesUtil {
    private static Logger logger = LoggerFactory.getLogger(AesUtil.class);

    private AesUtil() {}

    public static AesUtil getInstance() {
        return Nested.instance;
    }

    /**
     * 加密
     *
     * @param key     密钥
     * @param vector  迁移量
     * @param content 待加密内容
     * @return
     * @throws Exception
     */
    public String encrypt(String content, String key, String vector) throws Exception {
        logger.debug("=== AesUtil encrypt  content is {}, key is {}. vector is {}===", content, key, vector);
        if (key == null || key.length() != 16) {
            return null;
        }

        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * 解密
     *
     * @param key     密钥
     * @param vector  迁移量
     * @param content 待加密内容
     * @return
     * @throws Exception
     */
    public String decrypt(String key, String vector, String content) throws Exception {
        logger.debug("=== AesUtil decrypt  content is {}, key is {}. vector is {}===", content, key, vector);
        if (key == null || key.length() != 16) {
            return null;
        }

        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted1 = Base64.decodeBase64(content);
        byte[] original = cipher.doFinal(encrypted1);
        String originalStr = new String(original, "UTF-8");
        logger.debug("=== AesUtil decrypt  originalStr is {}===", originalStr);
        return originalStr;
    }

    /**
     * 内部静态类，用于保证线程安全
     */
    private static class Nested {
        private static AesUtil instance = new AesUtil();
    }
}