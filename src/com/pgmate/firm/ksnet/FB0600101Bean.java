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

public class FB0600101Bean extends CommBean {

	private String rootSpecNumber 	= "";	//원거래 전문번호
	private String senderAccount	= "";	//출금 계좌번호
	private String remainAccount	= "";	//입금 계좌번호
	private String amount			= "";	//금액
	private String commission		= "";	//수수료
	private String payNumber     	= "";	//지급번호
	private String transferTime		= "";   //이체시각(HHMMSS)
	private String resultCd			= "";	//처리결과
	private String bankCode2		= "";	//은행코드2
	private String nabbuNumber	 	= "";	//납부자번호
	private String tranType			= "";	//거래구분
	private String bankCode3		= "";	//은행코드3
	private String extra1			= "";	//접속은행예비 SPACE
	
	
	public FB0600101Bean(){	
	}
	
	public FB0600101Bean(String transaction){
		this(transaction.getBytes());
	}
	
	public FB0600101Bean(byte[] transaction){
		super.rootTransaction = transaction;
		rootSpecNumber 	= CommonUtil.toString(transaction,0,6).trim();		//원거래 전문번호
		senderAccount	= CommonUtil.toString(transaction,6,15).trim();		//출금 계좌번호
		remainAccount	= CommonUtil.toString(transaction,21,15).trim();	//입금 계좌번호
		amount			= CommonUtil.toString(transaction,36,13).trim();	//금액
		commission		= CommonUtil.toString(transaction,49,9).trim();		//수수료
		payNumber		= CommonUtil.toString(transaction,58,15).trim();	//지급번호
		transferTime	= CommonUtil.toString(transaction,73,6).trim();		//이체시각(HHMMSS)
		resultCd		= CommonUtil.toString(transaction,79,4).trim();		//처리결과
		bankCode2		= CommonUtil.toString(transaction,83,2).trim();		//은행코드2
		nabbuNumber 	= CommonUtil.toString(transaction,85,20).trim();	//납부자번호
		tranType		= CommonUtil.toString(transaction,105,2).trim();	//거래구분
		bankCode3		= CommonUtil.toString(transaction,107,3).trim();	//은행코드3
		extra1			= CommonUtil.toString(transaction,110,90).trim();	//예비
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.zerofill(rootSpecNumber,6));
		transaction.append(CommonUtil.byteFiller(senderAccount,15));
		transaction.append(CommonUtil.byteFiller(remainAccount,15));
		transaction.append(CommonUtil.zerofill(amount,13));
		transaction.append(CommonUtil.zerofill(commission,9));
		transaction.append(CommonUtil.byteFiller(payNumber,15));
		transaction.append(CommonUtil.byteFiller(transferTime,6));
		transaction.append(CommonUtil.byteFiller(resultCd,4));
		transaction.append(CommonUtil.byteFiller(bankCode2,2));
		transaction.append(CommonUtil.byteFiller(nabbuNumber,20));
		transaction.append(CommonUtil.byteFiller(tranType,2));
		transaction.append(CommonUtil.byteFiller(bankCode3,3));
		transaction.append(CommonUtil.byteFiller(extra1,90));
		
		return transaction.toString();
	}

	public String getRootSpecNumber() {
		return rootSpecNumber;
	}

	public void setRootSpecNumber(String rootSpecNumber) {
		this.rootSpecNumber = rootSpecNumber;
	}

	public String getSenderAccount() {
		return senderAccount;
	}

	public void setSenderAccount(String senderAccount) {
		this.senderAccount = senderAccount;
	}

	public String getRemainAccount() {
		return remainAccount;
	}

	public void setRemainAccount(String remainAccount) {
		this.remainAccount = remainAccount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getPayNumber() {
		return payNumber;
	}

	public void setPayNumber(String payNumber) {
		this.payNumber = payNumber;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public String getResultCd() {
		return resultCd;
	}

	public void setResultCd(String resultCd) {
		this.resultCd = resultCd;
	}

	public String getBankCode2() {
		return bankCode2;
	}

	public void setBankCode2(String bankCode2) {
		this.bankCode2 = bankCode2;
	}

	public String getNabbuNumber() {
		return nabbuNumber;
	}

	public void setNabbuNumber(String nabbuNumber) {
		this.nabbuNumber = nabbuNumber;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getBankCode3() {
		return bankCode3;
	}

	public void setBankCode3(String bankCode3) {
		this.bankCode3 = bankCode3;
	}

	public String getExtra1() {
		return extra1;
	}

	public void setExtra1(String extra1) {
		this.extra1 = extra1;
	}
}
