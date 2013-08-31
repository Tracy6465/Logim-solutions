package com.logim.main.camera;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Hashing {

	public static String bytesToHexString(byte[] b) {	
	   char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
		            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < b.length; j++) {
			buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
			buf.append(hexDigit[b[j] & 0x0f]);
		}
		return buf.toString();
	}

	public static String hashSha1(String stringToHash) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			//System.out.println("Message digest object info: ");
			//System.out.println("   Algorithm = " + md.getAlgorithm());
			//System.out.println("   Provider = " + md.getProvider());
			//System.out.println("   toString = " + md.toString());

			md.update(stringToHash.getBytes());
			byte[] output = md.digest();
			
			return Hashing.bytesToHexString(output);

		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static String hmacSha1(String value, String key) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            return Hashing.bytesToHexString(rawHmac);  
            
        } 
        catch (NoSuchAlgorithmException e) {
            return null;
        }
        catch (InvalidKeyException ex){
        	throw new RuntimeException(ex);
        }
    }
	
}