/* 
 * Project Name : 
 * Project      : 
 * File Name    : FB0600100Bean.java
 * Date	        : Jul 15, 2008
 * Version      : 1.0
 * Author       : 
 * Comment      :  
 */

package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0600100Bean extends CommBean {

	private String rootSpecNumber	= "";	//원거래전문번호
	private String mAccount 		= "";	//지급 계좌번호(모계좌)
	private String receiveAccount	= "";	//입금계좌
	private String amount			= "";	//출금금액(이체금액)
	private String commission		= "";	//수수료
	private String payNumber		= "";	//지급번호 (SPACE)
	private String trasferTime		= "";	//이체시각(HHMMSS)
	private String result			= "";	//처리결과
	private String receiveBankCode	= "";	//입금은행코드
	private String extra			= "";	//예비 제일(송금이체 결과조회시 30설정)
	private String receiveNewBankCode= "";	//입금은행코드 3자리
	private String extra1			= "";	
	
	public FB0600100Bean(){	
	}
	
	public FB0600100Bean(String transaction){
		this(transaction.getBytes());
	}
	
	public FB0600100Bean(byte[] transaction){
		super.rootTransaction = transaction;	
		rootSpecNumber	= CommonUtil.toString(transaction,0,6).trim();	//원거래전문번호
		mAccount 		= CommonUtil.toString(transaction,6,15).trim();	//지급 계좌번호(모계좌)
		receiveAccount	= CommonUtil.toString(transaction,21,15).trim();//입금계좌
		amount			= CommonUtil.toString(transaction,36,13).trim();//출금금액(이체금액)
		commission		= CommonUtil.toString(transaction,49,9).trim();	//수수료
		payNumber		= CommonUtil.toString(transaction,58,15).trim();//지급번호 (SPACE)
		trasferTime		= CommonUtil.toString(transaction,73,6).trim();	//이체시각(HHMMSS)
		result			= CommonUtil.toString(transaction,79,4).trim();	//처리결과
		receiveBankCode	= CommonUtil.toString(transaction,83,2).trim();	//입금은행코드
		extra			= CommonUtil.toString(transaction,85,2).trim();
		receiveNewBankCode=CommonUtil.toString(transaction,87,3).trim();
		extra1=CommonUtil.toString(transaction,90,transaction.length-90).trim();
		
	}
	
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.zerofill(rootSpecNumber,6));
		transaction.append(CommonUtil.zerofill(mAccount,15));
		transaction.append(CommonUtil.zerofill(receiveAccount,15));
		transaction.append(CommonUtil.zerofill(amount,13));
		transaction.append(CommonUtil.zerofill(commission,9));
		transaction.append(CommonUtil.zerofill(payNumber,15));
		transaction.append(CommonUtil.zerofill(trasferTime,6));
		transaction.append(CommonUtil.byteFiller(result,4));
		transaction.append(CommonUtil.byteFiller(receiveBankCode,2));
		transaction.append(CommonUtil.byteFiller(extra,2));
		transaction.append(CommonUtil.byteFiller(receiveNewBankCode,3));
		transaction.append(CommonUtil.byteFiller(extra,110));
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

	public String getReceiveBankCode() {
		return receiveBankCode;
	}

	public void setReceiveBankCode(String receiveBankCode) {
		this.receiveBankCode = receiveBankCode;
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

	public String getTrasferTime() {
		return trasferTime;
	}

	public void setTrasferTime(String trasferTime) {
		this.trasferTime = trasferTime;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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

	public String getExtra1() {
		return extra1;
	}

	public void setExtra1(String extra1) {
		this.extra1 = extra1;
	}
	
	
	
	
	
	
}
