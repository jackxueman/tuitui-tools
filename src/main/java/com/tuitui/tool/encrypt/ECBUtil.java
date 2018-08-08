package com.tuitui.tool.encrypt;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ECBUtil {
	public static String sid = "e461359e119347b19f7e9c01440b752c";
	public static String key ="759aeac7f224b0163e6561fc8f76ec91";
	static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	static Cipher cipher;
	static SecretKey secretKey;
	static final String CIPHER_ALGORITHM_CBC = "AES/ECBUtil/PKCS7Padding";
	static{
		try {
			cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
			String hexKey  = getMD5Format(sid+key);
			System.out.println("密钥："+hexKey);
			byte[] bytes =hexStringToBytes(hexKey);

			String base = Base64.getEncoder().encodeToString(bytes);
			System.out.println("base64密钥："+base);
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			bytes = sha.digest(bytes);
			bytes = Arrays.copyOf(bytes, 16);
			base = Base64.getEncoder().encodeToString(bytes);
			System.out.println("sha1 base64密钥："+base);
			secretKey = new SecretKeySpec(bytes,"AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static byte[] enc(byte[] contents){
		try {
			//使用加密模式初始化 密钥
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encrypt = cipher.doFinal(contents);
			String base = Base64.getEncoder().encodeToString(encrypt);
			System.out.println("加密后："+base);
			return encrypt;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] dec(byte[] contents){
		try {
			//使用解密模式初始化 密钥
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decrypt = cipher.doFinal(contents);
			String base = Base64.getEncoder().encodeToString(decrypt);
			System.out.println("解密后:"+base);
			return decrypt;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (StringUtils.isEmpty(hexString)) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/***
	 *
	 * @Title: getMD5Format
	 * @Description: 计算MD5并转换为32字节明文显示串
	 * @author wujl
	 * @param data
	 * @return String 返回类型
	 */
	public static String getMD5Format(String data) {
		try {
			MessageDigest message = MessageDigest.getInstance("MD5");

			message.update(data.getBytes());
			byte[] b = message.digest();

			String digestHexStr = "";
			for (int i = 0; i < 16; i++) {
				digestHexStr += byteHEX(b[i]);
			}
			return digestHexStr;
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 *
	 * @Title: byteHEX
	 * @Description:
	 * @author wujl
	 * @param ib
	 * @return String 返回类型
	 */
	private static String byteHEX(byte ib) {
		char[] ob = new char[2];
		ob[0] = hexDigits[(ib >>> 4) & 0X0F];
		ob[1] = hexDigits[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static Map<String,String> getKey(String url)throws IOException{
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		Map<String,String> result= new HashMap<>();
		CloseableHttpResponse httpResponse = closeableHttpClient.execute(httpPost);
		try {
			HttpEntity entityResponse = httpResponse.getEntity();
			InputStream ins = entityResponse.getContent();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader reader = new BufferedReader(isr);
			String tmp = "";
			while((tmp=reader.readLine())!=null){
				System.out.println(tmp);
			}

			return result;
		} finally {
			httpResponse.close();
		}
	}
}
