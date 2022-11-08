/* 
 * Project      : TrustMate_SPEC
 * File Name    : net.trustmate.model.comm.ksnet.fb.FB0600300Bean.java
 * Date         : Nov 2, 2009
 * Version      : 1.0
 * Author       : ginaida@trustmate.net
 * Comment      :  
 */

package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FB0600300Bean extends CommBean {
	private Logger logger = LoggerFactory.getLogger( getClass() );

	private String account			= "";	//계좌번호
	private String sign				= "";	//부호
	private String currentAmount	= "";	//현재금액
	private String cashierCheck		= "";	//자기앞
	private String personalCheck	= "";	//가계
	private String billCheck		= "";	//일반
	private String payAmount		= "";	//지급가능금액
	private String extra			= "";	//예비
	
	
	public FB0600300Bean() {
	}
	
	public FB0600300Bean(String transaction){
		this(transaction.getBytes());
	}
	
	public FB0600300Bean(byte[] transaction){
		super.rootTransaction = transaction;
		account			= CommonUtil.toString(transaction,0,15);
		sign			= CommonUtil.toString(transaction,15,1);
		currentAmount	= CommonUtil.toString(transaction,16,13);
		cashierCheck	= CommonUtil.toString(transaction,29,13);
		personalCheck	= CommonUtil.toString(transaction,42,13);
		billCheck		= CommonUtil.toString(transaction,55,13);
		payAmount		= CommonUtil.toString(transaction,68,13);
		extra			= CommonUtil.toString(transaction,81,119);
		
		log();
	}

	public void log() {
		logger.info("=================== 잔액조회 결과 ===================");
		logger.info("account : {}", account);
		logger.info("sign : {}", sign);
		logger.info("currentAmount : {}", currentAmount);
		logger.info("cashierCheck : {}", cashierCheck);
		logger.info("personalCheck : {}", personalCheck);
		logger.info("billCheck : {}", billCheck);
		logger.info("payAmount : {}", payAmount);
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(account, 15));
		transaction.append(CommonUtil.setFiller(185));
		return transaction.toString();
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getCurrentAmount() {
		return currentAmount;
	}


	public void setCurrentAmount(String currentAmount) {
		this.currentAmount = currentAmount;
	}


	public String getCashierCheck() {
		return cashierCheck;
	}


	public void setCashierCheck(String cashierCheck) {
		this.cashierCheck = cashierCheck;
	}


	public String getPersonalCheck() {
		return personalCheck;
	}


	public void setPersonalCheck(String personalCheck) {
		this.personalCheck = personalCheck;
	}


	public String getBillCheck() {
		return billCheck;
	}


	public void setBillCheck(String billCheck) {
		this.billCheck = billCheck;
	}


	public String getPayAmount() {
		return payAmount;
	}


	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}


	public String getExtra() {
		return extra;
	}


	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	

}
