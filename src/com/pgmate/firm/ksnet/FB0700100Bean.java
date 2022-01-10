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

public class FB0700100Bean extends CommBean {

	private String account				= "";	//업체 계좌 (모계좌)
	private String reqCount				= "";	//당행이체 총 건수
	private String reqAmount			= "";	//당행이체 총 금액
	private String successCount			= "";	//당행이체 정상 건수
	private String successAmount		= "";	//당행이체 정상 금액
	private String failCount			= "";	//당행이체 불능 건수
	private String failAmount			= "";	//당행이체 불능 금액
	private String fee					= "";	//당행이체 수수료
	private String otherReqCount		= "";	//타행이체 총 건수
	private String otherReqAmount		= "";	//타행이체 총 금액
	private String otherSuccessCount	= "";	//타행이체 정상 건수
	private String otherSuccessAmount	= "";	//타행이체 정상 금액
	private String otherFailCount		= "";	//타행이체 불능 건수
	private String otherFailAmount		= "";	//타행이체 불능 금액
	private String otherFee				= "";	//타행이체 수수료
	private String extra				= "";
	
	public FB0700100Bean(){	
	}
	
	public FB0700100Bean(String transaction){
		this(transaction.getBytes());
	}
	
	public FB0700100Bean(byte[] transaction){
		super.rootTransaction = transaction;	
		account				= CommonUtil.toString(transaction,0,15);	//업체 계좌 (모계좌)
		reqCount			= CommonUtil.toString(transaction,15,5);	//당행이체 총 건수
		reqAmount			= CommonUtil.toString(transaction,20,13);	//당행이체 총 금액
		successCount		= CommonUtil.toString(transaction,33,5);	//당행이체 정상 건수
		successAmount		= CommonUtil.toString(transaction,38,13);	//당행이체 정상 금액
		failCount			= CommonUtil.toString(transaction,51,5);	//당행이체 불능 건수
		failAmount			= CommonUtil.toString(transaction,56,13);	//당행이체 불능 금액
		fee					= CommonUtil.toString(transaction,69,9);	//당행이체 수수료
		otherReqCount		= CommonUtil.toString(transaction,78,5);	//타행이체 총 건수
		otherReqAmount		= CommonUtil.toString(transaction,83,13);	//타행이체 총 금액
		otherSuccessCount	= CommonUtil.toString(transaction,96,5);	//타행이체 정상 건수
		otherSuccessAmount	= CommonUtil.toString(transaction,101,13);	//타행이체 정상 금액
		otherFailCount		= CommonUtil.toString(transaction,114,5);	//타행이체 불능 건수
		otherFailAmount		= CommonUtil.toString(transaction,119,13);	//타행이체 불능 금액
		otherFee			= CommonUtil.toString(transaction,132,9);	//타행이체 수수료
		extra				= CommonUtil.toString(transaction,141,transaction.length-141);	
		
	}
	
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(account, 15));
		transaction.append(CommonUtil.zerofill(reqCount, 5));
		transaction.append(CommonUtil.zerofill(reqAmount, 13));
		transaction.append(CommonUtil.zerofill(successCount, 5));
		transaction.append(CommonUtil.zerofill(successAmount, 13));
		transaction.append(CommonUtil.zerofill(failCount, 5));
		transaction.append(CommonUtil.zerofill(failAmount, 13));
		transaction.append(CommonUtil.zerofill(fee, 9));
		transaction.append(CommonUtil.zerofill(otherReqCount, 5));
		transaction.append(CommonUtil.zerofill(otherReqAmount, 13));
		transaction.append(CommonUtil.zerofill(otherSuccessCount, 5));
		transaction.append(CommonUtil.zerofill(otherSuccessAmount, 13));
		transaction.append(CommonUtil.zerofill(otherFailCount, 5));
		transaction.append(CommonUtil.zerofill(otherFailAmount, 13));
		transaction.append(CommonUtil.zerofill(otherFee, 9));
		transaction.append(CommonUtil.setFiller(59));
		return transaction.toString();
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the reqCount
	 */
	public String getReqCount() {
		return reqCount;
	}

	/**
	 * @param reqCount the reqCount to set
	 */
	public void setReqCount(String reqCount) {
		this.reqCount = reqCount;
	}

	/**
	 * @return the reqAmount
	 */
	public String getReqAmount() {
		return reqAmount;
	}

	/**
	 * @param reqAmount the reqAmount to set
	 */
	public void setReqAmount(String reqAmount) {
		this.reqAmount = reqAmount;
	}

	/**
	 * @return the successCount
	 */
	public String getSuccessCount() {
		return successCount;
	}

	/**
	 * @param successCount the successCount to set
	 */
	public void setSuccessCount(String successCount) {
		this.successCount = successCount;
	}

	/**
	 * @return the successAmount
	 */
	public String getSuccessAmount() {
		return successAmount;
	}

	/**
	 * @param successAmount the successAmount to set
	 */
	public void setSuccessAmount(String successAmount) {
		this.successAmount = successAmount;
	}

	/**
	 * @return the failCount
	 */
	public String getFailCount() {
		return failCount;
	}

	/**
	 * @param failCount the failCount to set
	 */
	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}

	/**
	 * @return the failAmount
	 */
	public String getFailAmount() {
		return failAmount;
	}

	/**
	 * @param failAmount the failAmount to set
	 */
	public void setFailAmount(String failAmount) {
		this.failAmount = failAmount;
	}

	/**
	 * @return the fee
	 */
	public String getFee() {
		return fee;
	}

	/**
	 * @param fee the fee to set
	 */
	public void setFee(String fee) {
		this.fee = fee;
	}

	/**
	 * @return the otherReqCount
	 */
	public String getOtherReqCount() {
		return otherReqCount;
	}

	/**
	 * @param otherReqCount the otherReqCount to set
	 */
	public void setOtherReqCount(String otherReqCount) {
		this.otherReqCount = otherReqCount;
	}

	/**
	 * @return the otherReqAmount
	 */
	public String getOtherReqAmount() {
		return otherReqAmount;
	}

	/**
	 * @param otherReqAmount the otherReqAmount to set
	 */
	public void setOtherReqAmount(String otherReqAmount) {
		this.otherReqAmount = otherReqAmount;
	}

	/**
	 * @return the otherSuccessCount
	 */
	public String getOtherSuccessCount() {
		return otherSuccessCount;
	}

	/**
	 * @param otherSuccessCount the otherSuccessCount to set
	 */
	public void setOtherSuccessCount(String otherSuccessCount) {
		this.otherSuccessCount = otherSuccessCount;
	}

	/**
	 * @return the otherSuccessAmount
	 */
	public String getOtherSuccessAmount() {
		return otherSuccessAmount;
	}

	/**
	 * @param otherSuccessAmount the otherSuccessAmount to set
	 */
	public void setOtherSuccessAmount(String otherSuccessAmount) {
		this.otherSuccessAmount = otherSuccessAmount;
	}

	/**
	 * @return the otherFailCount
	 */
	public String getOtherFailCount() {
		return otherFailCount;
	}

	/**
	 * @param otherFailCount the otherFailCount to set
	 */
	public void setOtherFailCount(String otherFailCount) {
		this.otherFailCount = otherFailCount;
	}

	/**
	 * @return the otherFailAmount
	 */
	public String getOtherFailAmount() {
		return otherFailAmount;
	}

	/**
	 * @param otherFailAmount the otherFailAmount to set
	 */
	public void setOtherFailAmount(String otherFailAmount) {
		this.otherFailAmount = otherFailAmount;
	}

	/**
	 * @return the otherFee
	 */
	public String getOtherFee() {
		return otherFee;
	}

	/**
	 * @param otherFee the otherFee to set
	 */
	public void setOtherFee(String otherFee) {
		this.otherFee = otherFee;
	}

	/**
	 * @return the extra
	 */
	public String getExtra() {
		return extra;
	}

	/**
	 * @param extra the extra to set
	 */
	public void setExtra(String extra) {
		this.extra = extra;
	}

	
	
	
	
	
	
}
