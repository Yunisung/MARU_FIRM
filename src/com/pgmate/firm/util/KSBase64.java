/* 
 * Project      : TrustMate_FB_2.1
 * File Name    : net.trustmate.ksnet.util.KSBase64.java
 * Date         : 2011. 4. 25.
 * Version      : 2.1
 * Author       : ginaida@trustmate.net
 * Comment      : FirmBanking System
 */

package com.pgmate.firm.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class KSBase64 {

	/**
	 * 6비트 변환 테이블
	 */
	protected static final char[] alphabet = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0 to 7
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 8 to 15
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 16 to 23
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 24 to 31
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 32 to 39
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 40 to 47
		'w', 'x', 'y', 'z', '0', '1', '2', '3', // 48 to 55
		'4', '5', '6', '7', '8', '9', '+', '/'  // 56 to 63
	};

	/**
	 * 알파벳 역변환 테이블
	 */
	protected static final int[] decodeTable = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 0 to 9
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10 to 19
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20 to 29
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 30 to 39
		-1, -1, -1, 62, -1, -1, -1, 63, 52, 53, // 40 to 49
		54, 55, 56, 57, 58, 59, 60, 61, -1, -1, // 50 to 59
		-1, -1, -1, -1, -1,  0,  1,  2,  3,  4, // 60 to 69
		 5,  6,  7,  8,  9, 10, 11, 12, 13, 14, // 70 to 79
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, // 80 to 89
		25, -1, -1, -1, -1, -1, -1, 26, 27, 28, // 90 to 99
		29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // 100 to 109
		39, 40, 41, 42, 43, 44, 45, 46, 47, 48, // 110 to 119
		49, 50, 51                              // 120 to 122
	};

    public static char[] encode(String s) {
		return encode(s.getBytes());
    }

    /**
     * convert bytes into a BASE64 encoded string
     */
    public static char[] encode(byte[] bytes)
    {
		int sixbit;

		// 전체 문자열 길이는 3바이트를 6비트씩 표현하므로 3으로 나눈 다음
		// 4를 곱하면 된다. 3으로 나누어 떨어지지 않으면 4 바이트가 추가된다.
		// (Base64는 뒷 부분을 =으로 채워 전체 길이를 4의 배수로 만든다.)
		char[] output = new char[((bytes.length - 1) / 3 + 1) * 4];

		int outIndex = 0;
		int i = 0;

		while ((i + 3) <= bytes.length)
		{
			// 3 바이트(24비트)를 6비트씩 자른 다음
			// 해당하는 값을 알파벳 테이블에서 찾는다.

			// 첫번째 6비트 (6비트)
			sixbit = (bytes[i] & 0xFC) >> 2;
			output[outIndex++] = alphabet[sixbit];

			// 두번째 6비트 (2비트, 4비트)
			sixbit = ((bytes[i] & 0x3) << 4) + ((bytes[i + 1] & 0xF0) >> 4);
			output[outIndex++] = alphabet[sixbit];

			// 세번째 6비트 (4비트, 2비트)
			sixbit = ((bytes[i + 1] & 0xF) << 2) + ((bytes[i + 2] & 0xC0) >> 6);
			output[outIndex++] = alphabet[sixbit];

			// 네번째 6비트 (6비트)
			sixbit = bytes[i + 2] & 0x3F;
			output[outIndex++] = alphabet[sixbit];

			i += 3;
		}

		if (bytes.length - i == 2)  // 2 바이트가 남은 경우 처리
		{
			// 2 바이트(16비트)를 6비트씩 자른 다음
			// 해당하는 값을 알파벳 테이블에서 찾는다.

			// 첫번째 6비트 (6비트)
			sixbit = (bytes[i] & 0xFC) >> 2;
			output[outIndex++] = alphabet[sixbit];

			// 두번째 6비트 (2비트, 4비트)
			sixbit = ((bytes[i] & 0x3) << 4) + ((bytes[i + 1] & 0xF0) >> 4);
			output[outIndex++] = alphabet[sixbit];

			// 세번째 6비트 (4비트, 0비트)
			sixbit = (bytes[i + 1] & 0xF) << 2;
			output[outIndex++] = alphabet[sixbit];

			// 네번째 6비트 (남는 부분은 =으로 채운다.)
			output[outIndex++] = '=';
		} else
		if (bytes.length - i == 1)  // 1바이트가 남은 경우 처리
		{
			// 1 바이트(8비트)를 6비트씩 자른 다음
			// 해당하는 값을 알파벳 테이블에서 찾는다.

			// 첫번째 6비트 (6비트)
			sixbit = (bytes[i] & 0xFC) >> 2;
			output[outIndex++] = alphabet[sixbit];

			// 두번째 6비트 (2비트, 0비트)
			sixbit = (bytes[i] & 0x3) << 4;
			output[outIndex++] = alphabet[sixbit];

			// 세번째 6비트 (남는 부분은 =으로 채운다.)
			output[outIndex++] = '=';
			// 네번째 6비트 (남는 부분은 =으로 채운다.)
			output[outIndex++] = '=';
		}

		return output;
	}

	public static char[] b2cs(byte[] ebytes)
	{
		char[] echars = new char[ebytes.length];
		for(int i=0; i<echars.length; i++) echars[i] = (char)ebytes[i];

		return echars;
	}

	public static byte[] c2bs(char[] echars)
	{
		byte[] ebytes = new byte[echars.length];
		for(int i=0; i<echars.length; i++) ebytes[i] = (byte)echars[i];

		return ebytes;
	}
	
	/**
	 * Base64로 인코딩된 문자열을 원래의 바이트 배열로 되돌린다.
	 */
	public static byte[] decode(byte[] ebytes)
	{
		return decode(b2cs(ebytes));
	}

	public static byte[] decode(String encoded)
	{
		return decode(encoded.toCharArray());
	} 

	public static byte[] decode(char[] echars)
	{
		byte[] decoded = null;
		int decodedLength = (echars.length / 4 * 3);
		int invalid = 0;

		if (echars.length % 4 != 0)
		{
		    System.err.println("It's not BASE64 encoded string.");
		    return null;
		}
		if (echars[echars.length - 2] == '=')  // 마지막 2 바이트가 무효
		{
		    invalid = 2;
		} else
		if (echars[echars.length - 1] == '=')  // 마지막 1 바이트가 무효
		{
		    invalid = 1;
		}
		decodedLength -= invalid;
		decoded = new byte[decodedLength];

		int i = 0, di = 0;
		int sixbit0, sixbit1, sixbit2, sixbit3;

		for (; i < echars.length - 4; i += 4)
		{
			sixbit0 = decodeTable[echars[i    ]];
			sixbit1 = decodeTable[echars[i + 1]];
			sixbit2 = decodeTable[echars[i + 2]];
			sixbit3 = decodeTable[echars[i + 3]];

			decoded[di++] = (byte) ((sixbit0 << 2) + ((sixbit1 & 0x30) >> 4));
			decoded[di++] = (byte) (((sixbit1 & 0xF) << 4) + ((sixbit2 & 0x3C) >> 2));
			decoded[di++] = (byte) (((sixbit2 & 0x3) << 6) + sixbit3);
		}

		// 마지막 사이클
		switch (invalid)
		{
			case 0 : // 3 바이트 모두 유효
				sixbit0 = decodeTable[echars[i    ]];
				sixbit1 = decodeTable[echars[i + 1]];
				sixbit2 = decodeTable[echars[i + 2]];
				sixbit3 = decodeTable[echars[i + 3]];

				decoded[di++] = (byte) ((sixbit0 << 2) + ((sixbit1 & 0x30) >> 4));
				decoded[di++] = (byte) (((sixbit1 & 0xF) << 4) + ((sixbit2 & 0x3C) >> 2));
				decoded[di++] = (byte) (((sixbit2 & 0x3) << 6) + sixbit3);
				break;

			case 1 : // 2 바이트만 유효
				sixbit0 = decodeTable[echars[i    ]];
				sixbit1 = decodeTable[echars[i + 1]];
				sixbit2 = decodeTable[echars[i + 2]];

				decoded[di++] = (byte) ((sixbit0 << 2) + ((sixbit1 & 0x30) >> 4));
				decoded[di++] = (byte) (((sixbit1 & 0xF) << 4) + ((sixbit2 & 0x3C) >> 2));
				break;

			case 2 : // 1 바이트만 유효
				sixbit0 = decodeTable[echars[i    ]];
				sixbit1 = decodeTable[echars[i + 1]];

				decoded[di++] = (byte) ((sixbit0 << 2) + ((sixbit1 & 0x30) >> 4));
				break;
		}

		// assert decoded.length == di;
		return decoded;
	}

	public static byte[] url64_decode(byte[] ebytes)
	{

		try {
			char[] echars =  b2cs(URLDecoder.decode(new String(ebytes), "ksc5601").getBytes());
			return KSBase64.decode(echars);
			}catch(java.io.UnsupportedEncodingException ue){}
		return null;

	}
		
	public static byte[] url64_encode(byte[] ebytes)
	{
		char[] echars = KSBase64.encode(ebytes);
		try {
		return URLEncoder.encode(new String(echars), "ksc5601").getBytes();
		}catch(java.io.UnsupportedEncodingException ue){}
		
	return null;
	}
}
