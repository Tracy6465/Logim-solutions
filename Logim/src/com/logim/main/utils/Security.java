package com.logim.main.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;



final public class Security {

	static int iterationCount = 1000;
	static int saltLength = 8; // bytes; 64 bits
	static int keyLength = 256;
	public static byte[] salt;
	public static byte[] iv;

	
	public byte[] fromHexString(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static SecretKey getKey(String password) {
		
		SecureRandom random = new SecureRandom();
		salt = new byte[saltLength];
		random.nextBytes(salt);
		SecretKey key= null;
		
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
				iterationCount, keyLength);

		SecretKeyFactory keyFactory=null;
		try {
			keyFactory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] keyBytes=new byte[32]; 
;
		try {
			keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 key= new SecretKeySpec(keyBytes, "AES");
		
	
		return key;
		
	
	}
	
	public static SecretKey getExistingKey(String password, byte[] salt)
	{
		SecretKey key= null;
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt,
				iterationCount, keyLength);
		SecretKeyFactory keyFactory=null;
		
		try {
			keyFactory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] keyBytes=new byte[32]; 
;
		try {
			keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 key= new SecretKeySpec(keyBytes, "AES");
		
	
		return key;
		
	}

	public static String cipher(String plaintext, SecretKey key) {
		SecureRandom random = new SecureRandom();
		Cipher cipher=null;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iv = new byte[cipher.getBlockSize()];
		random.nextBytes(iv);
		IvParameterSpec ivParams = new IvParameterSpec(iv);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] ciphertext=null;
		try {
			 ciphertext= cipher.doFinal(plaintext.getBytes("UTF-8"));
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Base64.encodeWebSafe(ciphertext,true);
	}

	public static String decrypt(byte[] salt,String ciphered,String password)
	{
		

		byte[] cipherBytes=new byte[32]; 

	
		String[] fields = ciphered.split("]");
		
		byte[] iv = new byte[32];
		try {
			iv = Base64.decodeWebSafe(fields[0]);
			
		} catch (Base64DecoderException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

			try {
				cipherBytes = Base64.decodeWebSafe(fields[1]);
			} catch (Base64DecoderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		
		// as above
		SecretKey key = getExistingKey(password,salt);

		Cipher cipher=null;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IvParameterSpec ivParams = new IvParameterSpec(iv);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] plaintext=new byte[32];

		try {

			plaintext = cipher.doFinal(cipherBytes);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String plainStr=null;
		try {
			plainStr = new String(plaintext , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plainStr;
	}
	
	
	
}
