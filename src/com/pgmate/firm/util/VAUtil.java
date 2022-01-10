package com.pgmate.firm.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * @author Administrator
 *
 */
public class VAUtil {

	/**
	 * 0 : 정상,입금 가능,수취조회 정상 응답
	 * 1 : 미등록 상점인 경우 ( 업체 관련 없음 )
	 * 2 : 발급 원거래 없음 ( 가상계좌 발급이 없는 경우 )
	 * 3 : 거래 금액 오류 ( 입금 금액 불일치 등 )
	 * 4 : 마감시간 초과 또는 거래시간 초과
	 * 5 : 입금자 불일치
	 * 6 : 계좌 오류
	 * 7 : 수취 조회 응답 오류
	 * 8 : 발급 취소 또는 발급 원거래 없음
	 * 9 : 기타 오류
	 *
	 * @param type
	 * @param bankCode
	 * @return
	 */
	public static String vaResponseCode(String type,String bankCode){
		if(type.equals("0")){
			return "0000";
		}else if(type.equals("1")){ //미등록 상점인 경우
			return notRegistMerchant(bankCode);
		}else if(type.equals("2")){ //발급 원거래 없음
			return notPublish(bankCode);
		}else if(type.equals("3")){ //거래금액 오류
			return differentAmount(bankCode);
		}else if(type.equals("4")){ //마감 시간 초과 또는 거래시간 초과
			return impossibleTime(bankCode);
		}else if(type.equals("5")){ //입금자 불일치
			return differentPeople(bankCode);
		}else if(type.equals("6")){ //계좌 오류
			return notRegistAccount(bankCode);
		}else if(type.equals("7")){ //수취 조회 응답 오류
			return responseError(bankCode);
		}else if(type.equals("8")){ //발급 취소 또는 발급 원거래 없음
			return publishCancle(bankCode);
		}else if(type.equals("9")){ //기타 오류
			return otherError(bankCode);
		}else{
			return otherError(bankCode);
		}
	}
	/**
	 * 미등록상점인경우 코드 값.
	 */
	public static String notRegistMerchant(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "814";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V818";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "9999";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V818";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "534";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "024";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "404";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "0279";
		}else{
			return "9999";
		}

	}

	/**
	 * 발급원거래없음
	 * @param bankCode
	 * @return
	 */
	public static String notPublish(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "416";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V786";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K402";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V786";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "534";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "031";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "404";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "3500";
		}else{
			return "9999";
		}

	}

	/**
	 * 거래금액 오류
	 * @param bankCode
	 * @return
	 */
	public static String differentAmount(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "415";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V713";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K409";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V713";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "518";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "022";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "419";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "0240";
		}else{
			return "9999";
		}
	}

	/**
	 * 마감시간 초과 999	V151	9999	V151	510	032	663
	 * @param bankCode
	 * @return
	 */
	public static String impossibleTime(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "413";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V151";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K114";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V151";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "510";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "032";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "663";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "9999";
		}else{
			return "9999";
		}
	}


	/**
	 * 입금자 불일치 999	V405	9999	V405	599	026	489
	 * @param bankCode
	 * @return
	 */
	public static String differentPeople(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "310";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V405";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K417";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V405";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "599";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "026";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "489";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "9999";
		}else{
			return "9999";
		}
	}

	/**
	 * 계좌오류 838	V817	9999	V817	534	030	404
	 */
	public static String notRegistAccount(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "838";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V817";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K402";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V817";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "534";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "030";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "404";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "0013";
		}else{
			return "9999";
		}
	}


	/**
	 * 수취조회에 대한 응답 오류 999	V783	9999	V783	599	019	555
	 * @param bankCode
	 * @return
	 */
	public static String responseError(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "413";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V783";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K418";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V783";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "599";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "019";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "555";
		}else{
			return "9999";
		}
	}

	/**
	 * 발급 취소 또는 발급 원거래 없음 601	V786	9999	V786	599	025	451
	 * @param bankCode
	 * @return
	 */
	public static String publishCancle(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "601";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V786";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K402";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V786";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "599";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "025";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "451";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "3606";
		}else{
			return "9999";
		}
	}


	/**
	 * 기타 오류 999	V140	9999	V141	599	099	451
	 * @param bankCode
	 * @return
	 */
	public static String otherError(String bankCode){
		if(bankCode.equals("04") || bankCode.equals("004")){
			return "401";
		}else if(bankCode.equals("11") || bankCode.equals("12") || bankCode.equals("011") || bankCode.equals("012")){
			return "V140";
		}else if(bankCode.equals("20") || bankCode.equals("020")){
			return "K999";
		}else if(bankCode.equals("21") || bankCode.equals("26") || bankCode.equals("88") ||bankCode.equals("021") ||bankCode.equals("026")||bankCode.equals("088")){
			return "V141";
		}else if(bankCode.equals("23") || bankCode.equals("023")){
			return "599";
		}else if(bankCode.equals("71") || bankCode.equals("071")){
			return "099";
		}else if(bankCode.equals("81") || bankCode.equals("081")){
			return "451";
		}else if(bankCode.equals("27") || bankCode.equals("027")){
			return "9999";
		}else{
			return "9999";
		}
	}

	// 문자열 인코딩을 고려해서 문자열 자르기
    public static String cut2(String parameterName, int maxLength) {
        int DB_FIELD_LENGTH = maxLength;
 
        Charset ecuCharset = Charset.forName("EUC-KR");
        CharsetDecoder cd = ecuCharset.newDecoder();
 
        try {
            byte[] sba = parameterName.getBytes("EUC-KR");
            if(sba.length > DB_FIELD_LENGTH) {
	            // Ensure truncating by having byte buffer = DB_FIELD_LENGTH
	            ByteBuffer bb = ByteBuffer.wrap(sba, 0, DB_FIELD_LENGTH); // len in [B]
	            CharBuffer cb = CharBuffer.allocate(DB_FIELD_LENGTH); // len in [char] <= # [B]
	            // Ignore an incomplete character
	            cd.onMalformedInput(CodingErrorAction.IGNORE);
	            cd.decode(bb, cb, true);
	            cd.flush(cb);
	            parameterName = new String(cb.array(), 0, cb.position());
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("### 지원하지 않는 인코딩입니다." + e);
        }
 
        return parameterName;
    }
}
