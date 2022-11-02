/* 
 * Project Name : 
 * Project      : TrustMate_SPEC
 * File Name    : FB0600400Bean.java
 * Date	        : Jul 15, 2008
 * Version      : 1.0
 * Author       : 
 * Comment      :  
 */

package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class FB0600400Bean extends CommBean {

	private Logger logger = LoggerFactory.getLogger( getClass() );

	private String transactionDay 	= "";	//MMDD
	private String bankCode			= "";	//계좌은행
	private String account			= "";	//계좌번호
	private String name				= "";	//계좌성명
	private String socialNumber		= "";	//계좌의 주민번호
	private String socialCheck      = "";	//계좌의 주민번호 체크 여부 99:일반체크 88:두음법칙체크
	private String mAccount			= "";   //새마을 금고 이용할때 (은행과 직계약 일 경우) 모계좌번호
	private String newBankCode		= "";	//계좌은행코드3자리 

//	private String companySpace 	= "";	//회사사용정보 SPACE
//	private String hangulSpace		= "";	//한글사용정보 SPACE
//	private String bankSpace		= "";	//접속은행예비 SPACE

	//PYS: 하이픈전문에 추가된것
	private String amount			= ""; 	//금액
	private String dotcomSpace		= "";	//닷컴통장 조회 : 우리닷컴 통장조회시 'D'세팅, 우리은행 계약업체에 한함
	private String otherBankSpace	= "";	//당타행인증유형
	private String nhSpace			= "";	//농협계좌구분

	private String extra			= "";	//예비(개별부) SPACE

	public FB0600400Bean(){	
	}
	
	public FB0600400Bean(String transaction){
		this(transaction.getBytes(Charset.forName("euc-kr")));
	}
	
	public FB0600400Bean(byte[] transaction){
		super.rootTransaction = transaction;
		transactionDay 	= CommonUtil.toString(transaction,0,4).trim();		//MMDD
		bankCode		= CommonUtil.toString(transaction,4,2).trim();		//계좌은행
		account			= CommonUtil.toString(transaction,6,16).trim();		//계좌번호
		name			= CommonUtil.toString(transaction,22,22).trim();	//계좌성명
		socialNumber	= CommonUtil.toString(transaction,44,13).trim();	//계좌의 주민번호
		socialCheck		= CommonUtil.toString(transaction,57,2).trim();		//계좌의 주민번호 체크 여부 99:일반체크 88:두음법칙체크
		mAccount		= CommonUtil.toString(transaction,59,20).trim();	//새마을 금고 이용할때 (은행과 직계약 일 경우) 모계좌번호
		newBankCode		= CommonUtil.toString(transaction,79,3).trim();		//은행코드 3자리
		amount			= CommonUtil.toString(transaction, 82, 13).trim();	//금액
		dotcomSpace		= CommonUtil.toString(transaction, 95, 1).trim();//닷컴통장조회
		otherBankSpace  = CommonUtil.toString(transaction, 96, 1).trim();//당타행인증유형
		nhSpace			= CommonUtil.toString(transaction, 97, 1).trim();//농협계좌구분
		extra			= CommonUtil.toString(transaction,98,102).trim();	//예비(개별부) SPACE

//		companySpace 	= CommonUtil.toString(transaction,175,20).trim();	//회사사용정보 SPACE
//		hangulSpace		= CommonUtil.toString(transaction,195,1).trim();	//한글사용정보 SPACE
//		bankSpace		= CommonUtil.toString(transaction,196,4).trim();	//접속은행예비 SPACE

		log();
	}

	public void log() {
		logger.info("=================== 예금주 조회 결과 ===================");
		logger.info("transactionDay : {}", transactionDay);
		logger.info("bankCode : {}", bankCode);
		logger.info("account : {}", account);
		logger.info("name : {}", name);
		logger.info("socialNumber : {}", socialNumber);
		logger.info("socialCheck : {}", socialCheck);
		logger.info("mAccount : {}", mAccount);
		logger.info("newBankCode : {}", newBankCode);
		logger.info("amount : {}", amount);
		logger.info("dotcomSpace : {}", dotcomSpace);
		logger.info("otherBankSpace : {}", otherBankSpace);
		logger.info("nhSpace : {}", nhSpace);
		logger.info("extra : {}", extra);

	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.zerofill(transactionDay,4));
		transaction.append(CommonUtil.byteFiller(bankCode,2));
		transaction.append(CommonUtil.byteFiller(account,16));
		transaction.append(CommonUtil.byteFiller(name,22));
		transaction.append(CommonUtil.byteFiller(socialNumber,13));
		transaction.append(CommonUtil.byteFiller(socialCheck,2));
		transaction.append(CommonUtil.byteFiller(mAccount,20));
		transaction.append(CommonUtil.byteFiller(newBankCode,3));
		transaction.append(CommonUtil.byteFiller(dotcomSpace, 1));
		transaction.append(CommonUtil.byteFiller(otherBankSpace, 1));
		transaction.append(CommonUtil.byteFiller(nhSpace, 1));
		transaction.append(CommonUtil.byteFiller(extra,115));
//		transaction.append(CommonUtil.byteFiller(companySpace,20));
//		transaction.append(CommonUtil.byteFiller(hangulSpace,1));
//		transaction.append(CommonUtil.zerofill(bankSpace,4));
		
		return transaction.toString();
	}

	public String getTransactionDay() {
		return transactionDay;
	}

	public void setTransactionDay(String transactionDay) {
		this.transactionDay = transactionDay;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}

	public String getSocialCheck() {
		return socialCheck;
	}

	public void setSocialCheck(String socialCheck) {
		this.socialCheck = socialCheck;
	}

	public String getMAccount() {
		return mAccount;
	}

	public void setMAccount(String account) {
		mAccount = account;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

//	public String getCompanySpace() {
//		return companySpace;
//	}
//
//	public void setCompanySpace(String companySpace) {
//		this.companySpace = companySpace;
//	}
//
//	public String getHangulSpace() {
//		return hangulSpace;
//	}
//
//	public void setHangulSpace(String hangulSpace) {
//		this.hangulSpace = hangulSpace;
//	}
//
//	public String getBankSpace() {
//		return bankSpace;
//	}
//
//	public void setBankSpace(String bankSpace) {
//		this.bankSpace = bankSpace;
//	}

	public String getNewBankCode() {
		return newBankCode;
	}

	public void setNewBankCode(String newBankCode) {
		this.newBankCode = newBankCode;
	}

	public String getDotcomSpace() { return dotcomSpace; }

	public void setDotcomSpace(String dotcom) { this.dotcomSpace = dotcom; }

	public String getOtherBankSpace() { return otherBankSpace; }

	public void setOtherBankSpace(String otherbank) { this.otherBankSpace = otherbank; }

	public String getNhSpace() { return nhSpace; }

	public void setNhSpace(String nh) { this.nhSpace = nh; }
	
	
	
	
	
	
	
	
	
}
