package com.pgmate.firm.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.pgmate.lib.util.lang.CommonUtil;

/**
 * @author Administrator
 *
 */
public class FirmUtil {

	public static String MONITOR 	= "MONITOR";
	public static String INTERNAL 	= "INTER";
	public static String KSNET		= "KSNET";
	
	
	public static String changeCharset(String str, String charset) {
		if(str == null) return "";
        try {
            byte[] bytes = str.getBytes(charset);
            return new String(bytes, charset);
        } catch(UnsupportedEncodingException e) { }//Exception
        return "";
    }
	
	
	public static String convertBankType(String str){
		
		byte[] srcByte = str.getBytes();
		byte[] destByte = new byte[srcByte.length+2];
		destByte[0] = 0x0E;
		destByte[srcByte.length+1] = 0x0F;
		
		
		
		CommonUtil.arrayCopy(destByte,1, srcByte, srcByte.length);
		return CommonUtil.toString(destByte);
	}
	
	
	public static byte[] udecode_3des(byte[] b_key, byte[] ebytes) throws NoSuchAlgorithmException, InvalidKeyException,
	IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, IOException {
		SecretKeySpec skeySpec = new SecretKeySpec(b_key, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] b_emsg = KSBase64.url64_decode(ebytes);
		byte[] b_dmsg = cipher.doFinal(b_emsg);
		
		return b_dmsg;
	}
		
	public static byte[] uencode_3des(byte[] b_key, byte[] pbytes) throws NoSuchAlgorithmException, InvalidKeyException,
			IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, IOException {
		SecretKeySpec skeySpec = new SecretKeySpec(b_key, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] b_emsg = cipher.doFinal(pbytes);
		byte[] b_bmsg = KSBase64.url64_encode(b_emsg);
		
		return b_bmsg;
	}

}
