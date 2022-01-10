/* 
 * Project Name : 
 * Project      : 
 * File Name    : 
 * Date	        : Jul 15, 2008
 * Version      : 1.0
 * Author       : 
 * Comment      :  
 */

package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0400100Bean extends CommBean{

	
	private String rootSpecNumber   = ""; //원거래전문번호
	private String mAccount      	= ""; //출금계좌번호 , 모계좌번호
	private String receiveAccount   = ""; //입금계좌번호
	private String amount           = ""; //의뢰금액
	private String receiveBankCode  = ""; //입금은행코드
	private String successAmount    = ""; //정상처리금액
	private String failureAmount    = ""; //처리불능금액
	private String divisionCount    = ""; //분할처리건수
	private String divisionNumber   = ""; //분할처리번호
	private String otherSpecNumber  = ""; //타행전문번호
	private String errorAmount      = ""; //입금불능통지금액
	private String errorCode        = ""; //에러코드
	private String receiveNewBankCode= "";//입금은행코드 3자리 
	private String extra	        = ""; //예비
	
	public FB0400100Bean(){
	}
	
	public FB0400100Bean(String transaction){
		this(transaction.getBytes());
	}
	
	public FB0400100Bean(byte[] transaction){
		super.rootTransaction = transaction;
		rootSpecNumber 	= CommonUtil.toString(transaction,0,6).trim();	//원거래전문번호
		mAccount		= CommonUtil.toString(transaction,6,15).trim();	//출금계좌번호
		receiveAccount	= CommonUtil.toString(transaction,21,15).trim();//입금계좌번호
		amount			= CommonUtil.toString(transaction,36,13).trim();//입금계좌번호
		receiveBankCode	= CommonUtil.toString(transaction,49,2).trim();	//입금은행코드
		successAmount	= CommonUtil.toString(transaction,51,13).trim();//정상처리금액
		failureAmount	= CommonUtil.toString(transaction,64,13).trim();//처리불능금액
		divisionCount	= CommonUtil.toString(transaction,77,2).trim();	//분할처리건수
		divisionNumber 	= CommonUtil.toString(transaction,79,2).trim();	//분할처리번호
		otherSpecNumber	= CommonUtil.toString(transaction,81,6).trim();	//타행전문번호
		errorAmount		= CommonUtil.toString(transaction,87,9).trim();	//입금불능통지금액
		errorCode		= CommonUtil.toString(transaction,96,3).trim();	//에러코드
		receiveNewBankCode	= CommonUtil.toString(transaction,99,3).trim();	//에러코드
		extra			= CommonUtil.toString(transaction,102,transaction.length - 102).trim();	//예비
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.zerofill(rootSpecNumber,6));
		transaction.append(CommonUtil.byteFiller(mAccount,15));
		transaction.append(CommonUtil.byteFiller(receiveAccount,15));
		transaction.append(CommonUtil.zerofill(amount,13));
		transaction.append(CommonUtil.byteFiller(receiveBankCode,2));
		transaction.append(CommonUtil.zerofill(successAmount,13));
		transaction.append(CommonUtil.zerofill(failureAmount,13));
		transaction.append(CommonUtil.byteFiller(divisionCount,2));
		transaction.append(CommonUtil.byteFiller(divisionNumber,2));
		transaction.append(CommonUtil.zerofill(otherSpecNumber,6));
		transaction.append(CommonUtil.zerofill(errorAmount,9));
		transaction.append(CommonUtil.byteFiller(errorCode,3));
		transaction.append(CommonUtil.zerofill(receiveNewBankCode,3));
		transaction.append(CommonUtil.byteFiller(extra,98));
		
		return transaction.toString();
	}

	public String getRootSpecNumber() {
		return rootSpecNumber;
	}

	public void setRootSpecNumber(String rootSpecNumber) {
		this.rootSpecNumber = rootSpecNumber;
	}

	public String getMAccount() {
		return mAccount;
	}

	public void setMAccount(String account) {
		mAccount = account;
	}

	public String getReceiveAccount() {
		return receiveAccount;
	}

	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReceiveBankCode() {
		return receiveBankCode;
	}

	public void setReceiveBankCode(String receiveBankCode) {
		this.receiveBankCode = receiveBankCode;
	}

	public String getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(String successAmount) {
		this.successAmount = successAmount;
	}

	public String getFailureAmount() {
		return failureAmount;
	}

	public void setFailureAmount(String failureAmount) {
		this.failureAmount = failureAmount;
	}

	public String getDivisionCount() {
		return divisionCount;
	}

	public void setDivisionCount(String divisionCount) {
		this.divisionCount = divisionCount;
	}

	public String getDivisionNumber() {
		return divisionNumber;
	}

	public void setDivisionNumber(String divisionNumber) {
		this.divisionNumber = divisionNumber;
	}

	public String getOtherSpecNumber() {
		return otherSpecNumber;
	}

	public void setOtherSpecNumber(String otherSpecNumber) {
		this.otherSpecNumber = otherSpecNumber;
	}

	public String getErrorAmount() {
		return errorAmount;
	}

	public void setErrorAmount(String errorAmount) {
		this.errorAmount = errorAmount;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getReceiveNewBankCode() {
		return receiveNewBankCode;
	}

	public void setReceiveNewBankCode(String receiveNewBankCode) {
		this.receiveNewBankCode = receiveNewBankCode;
	}
	
	
	
}
