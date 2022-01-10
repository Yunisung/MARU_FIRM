/* 
 * Project Name : 
 * Project      : 
 * File Name    : FBHeaderBean.java
 * Date	        : Jul 15, 2008
 * Version      : 1.0
 * Author       : 
 * Comment      :  
 */

package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FBHeaderBean extends CommBean {
	
	private String identificationCode 	= "";		//식별코드
	private String companyCode			= "";		//업체코드
	private String bankCode				= "";		//은행코드
	private String specCode				= "";		//전문코드
	private String classificationCode	= "";		//구분코드
	private String frequency			= "";		//전문송신횟수('1' or 'Y')
	private String specNumber			= "";		//전문일련번호
	private String transactionTime		= "";		//YYYYMMDDHHMMSS
	private String ksnetResponseCode	= "";		//SPACE 응답코드
	private String bankResponseCode		= "";		//은행응답보드
	private String inquiryDay			= "";		//조회일자
	private String inqueryNumber		= "";		//조회번호
	private String bankSpecNumber		= "";		//은행전문번호
	private String newBankCode			= "";		//은행코드 3자리 
	private String extra				= "";		//예비
	
	
	public FBHeaderBean(){
		
	}
	
	public FBHeaderBean(String transaction){
		this(transaction.getBytes());
	}
	
	public FBHeaderBean(byte[] transaction){
		super.rootTransaction = transaction;
		
		identificationCode 	= CommonUtil.toString(transaction,0,9).trim();		//식별코드
		companyCode			= CommonUtil.toString(transaction,9,8).trim();		//업체코드
		bankCode			= CommonUtil.toString(transaction,17,2).trim();		//은행코드
		specCode			= CommonUtil.toString(transaction,19,4).trim();		//전문코드
		classificationCode	= CommonUtil.toString(transaction,23,3).trim();		//구분코드
		frequency			= CommonUtil.toString(transaction,26,1).trim();		//전문송신횟수('1' or 'Y')
		specNumber			= CommonUtil.toString(transaction,27,6).trim();		//전문일련번호
		transactionTime		= CommonUtil.toString(transaction,33,14).trim();	//YYYYMMDDHHMMSS
		ksnetResponseCode	= CommonUtil.toString(transaction,47,4).trim();		//SPACE 응답코드
		bankResponseCode	= CommonUtil.toString(transaction,51,4).trim();		//은행응답보드
		inquiryDay			= CommonUtil.toString(transaction,55,8).trim();		//조회일자
		inqueryNumber		= CommonUtil.toString(transaction,63,6).trim();		//조회번호
		bankSpecNumber		= CommonUtil.toString(transaction,69,15).trim();	//은행 전문번호 
		newBankCode			= CommonUtil.toString(transaction,84,3).trim();	//은행 전문번호
		extra				= CommonUtil.toString(transaction,87,13);			//예비
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(identificationCode,9));
		transaction.append(CommonUtil.byteFiller(companyCode,8));
		transaction.append(CommonUtil.byteFiller(bankCode,2));
		transaction.append(CommonUtil.byteFiller(specCode,4));
		transaction.append(CommonUtil.zerofill(classificationCode,3));
		transaction.append(CommonUtil.zerofill(frequency,1));
		transaction.append(CommonUtil.zerofill(specNumber,6));
		transaction.append(CommonUtil.byteFiller(transactionTime,14));
		transaction.append(CommonUtil.byteFiller(ksnetResponseCode,4));
		transaction.append(CommonUtil.byteFiller(bankResponseCode,4));
		transaction.append(CommonUtil.byteFiller(inquiryDay,8));
		transaction.append(CommonUtil.zerofill(inqueryNumber,6));
		transaction.append(CommonUtil.byteFiller(bankSpecNumber,15));
		transaction.append(CommonUtil.byteFiller(newBankCode,3));
		transaction.append(CommonUtil.byteFiller(extra,13));
		
		return transaction.toString();
	}

	public String getIdentificationCode() {
		return identificationCode;
	}

	public void setIdentificationCode(String identificationCode) {
		this.identificationCode = identificationCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getSpecCode() {
		return specCode;
	}

	public void setSpecCode(String specCode) {
		this.specCode = specCode;
	}

	public String getClassificationCode() {
		return classificationCode;
	}

	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getSpecNumber() {
		return specNumber;
	}

	public void setSpecNumber(String specNumber) {
		this.specNumber = specNumber;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getKsnetResponseCode() {
		return ksnetResponseCode;
	}

	public void setKsnetResponseCode(String ksnetResponseCode) {
		this.ksnetResponseCode = ksnetResponseCode;
	}

	public String getBankResponseCode() {
		return bankResponseCode;
	}

	public void setBankResponseCode(String bankResponseCode) {
		this.bankResponseCode = bankResponseCode;
	}

	public String getInquiryDay() {
		return inquiryDay;
	}

	public void setInquiryDay(String inquiryDay) {
		this.inquiryDay = inquiryDay;
	}

	public String getInqueryNumber() {
		return inqueryNumber;
	}

	public void setInqueryNumber(String inqueryNumber) {
		this.inqueryNumber = inqueryNumber;
	}

	public String getBankSpecNumber() {
		return bankSpecNumber;
	}

	public void setBankSpecNumber(String bankSpecNumber) {
		this.bankSpecNumber = bankSpecNumber;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getNewBankCode() {
		return newBankCode;
	}

	public void setNewBankCode(String newBankCode) {
		this.newBankCode = newBankCode;
	}
	
	
	
	
	
}
