package com.parserlabs.commons.cipher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AES {

	private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
	private static IvParameterSpec ivParameterSpec;
	private static SecretKeySpec secretKey;

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
			cipher.init(2, secretKey, ivParameterSpec);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));

			return new String(original);
		} catch (Exception e) {
			log.error("Error while decrypting: {}", e.toString());
		}
		return null;
	}

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
			cipher.init(1, secretKey, ivParameterSpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			log.error("Error while encrypting: {}", e.toString());
		}
		return null;
	}

	public static void setKey(String cipherKey) {
		MessageDigest sha = null;
		try {
			byte[] key = cipherKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-256");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
			ivParameterSpec = new IvParameterSpec(key);
		} catch (NoSuchAlgorithmException e) {
			log.error("unsupported algoritm exception occured:", e);
		}
	}
}