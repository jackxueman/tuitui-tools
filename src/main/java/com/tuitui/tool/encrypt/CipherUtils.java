package com.tuitui.tool.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class CipherUtils {

    public static Cipher newCipher(String passwd, int mode) {
        try {
            byte[] raw = HexConvertUtil.toBytes(passwd);
            byte[] bytes = Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(raw), 16);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, new SecretKeySpec(bytes, "AES"));
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("Cipher Exception!", e);
        }
    }
}

