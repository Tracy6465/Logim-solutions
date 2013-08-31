package com.logim.main.utils;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by IntelliJ IDEA.
 * User: Family
 * Date: 1/21/12
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class AES128 {
    private final String characterEncoding = "UTF-8";
    private final String cipherTransformation = "AES/CBC/PKCS7Padding";
    private final String aesEncryptionAlgorithm = "AES";
    public static byte[] iv;

public  byte[] decrypt(byte[] cipherText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
{
    Cipher cipher = Cipher.getInstance(cipherTransformation);
    SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
    cipherText = cipher.doFinal(cipherText);
    return cipherText;
}

public  String decrypt(byte[] cipherText, SecretKey key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
{
    Cipher cipher = Cipher.getInstance(cipherTransformation);
   // SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
    cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
    cipherText = cipher.doFinal(cipherText);
    return new String(cipherText, characterEncoding);
}

public byte[] encrypt(byte[] plainText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
{
    Cipher cipher = Cipher.getInstance(cipherTransformation);
    SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
    plainText = cipher.doFinal(plainText);
    return plainText;
}


public String encrypt(String plainText, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
{
    byte[] plainTextbytes= null;
	try {
		plainTextbytes = plainText.getBytes(characterEncoding);
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	SecureRandom random = new SecureRandom();
    Cipher cipher = Cipher.getInstance(cipherTransformation);
    //SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
	iv = new byte[cipher.getBlockSize()];
	random.nextBytes(iv);
	IvParameterSpec ivParams = new IvParameterSpec(iv);
   // IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
    cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
    plainTextbytes = cipher.doFinal(plainTextbytes);

    return Base64.encodeWebSafe(plainTextbytes,true);
}

private byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
    byte[] keyBytes= new byte[16];
    byte[] parameterKeyBytes= key.getBytes(characterEncoding);
    System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
    return keyBytes;
}

/// <summary>
/// Encrypts plaintext using AES 128bit key and a Chain Block Cipher and returns a base64 encoded string
/// </summary>
/// <param name="plainText">Plain text to encrypt</param>
/// <param name="key">Secret key</param>
/// <returns>Base64 encoded string</returns>
public String encrypt(String plainText, String key) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
    byte[] plainTextbytes = plainText.getBytes(characterEncoding);
    byte[] keyBytes = getKeyBytes(key);
    return Base64.encodeWebSafe(encrypt(plainTextbytes,keyBytes,keyBytes), true);
    //return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.DEFAULT);
}

/// <summary>
/// Decrypts a base64 encoded string using the given key (AES 128bit key and a Chain Block Cipher)
/// </summary>
/// <param name="encryptedText">Base64 Encoded String</param>
/// <param name="key">Secret Key</param>
/// <returns>Decrypted String</returns>
public String decrypt(String encryptedText, String key) throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
   // byte[] cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT);
	byte[] cipheredBytes=null;
	try {
		cipheredBytes = Base64.decodeWebSafe(encryptedText);
	} catch (Base64DecoderException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    byte[] keyBytes = getKeyBytes(key);
    return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
}
}