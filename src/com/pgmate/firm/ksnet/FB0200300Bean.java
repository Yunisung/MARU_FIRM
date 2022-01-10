
package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0200300Bean extends CommBean {

	private String mAccount				= "";	//계좌번호
	private String count				= "";	//조립건수
	private String classificationCode	= "";	//거래구분
	private String bankCode				= "";	//은행코드
	private String amount				= "";	//금액
	private String remainAmount			= "";	//잔액
	private String giroCode				= "";	//입금점지로코드
	private String name					= "";	//성명
	private String supyoNumber			= "";	//수표번호
	private String cash					= "";	//현금(현금+당좌수표)
	private String otherBankSupyoAmount = "";	//타행수표금액
	private String otherSupyo			= "";	//가계수표,기타
	private String virtualAccount		= "";	//가상계좌번호
	private String transactionTime		= "";	//거래일자시간
	private String transactionNumber	= "";	//통장거래 일련번호
	private String newBankCode			= "";	//은행코드 3자리
	private String branchCode			= "";	//거래지점코드 
	private String extra				= "";	//예비 
	
	public FB0200300Bean(){
		
	}
	
	public FB0200300Bean(String transaction){
		this(transaction.getBytes());
	}

	public FB0200300Bean(byte[] transaction){
		this.rootTransaction = transaction;
		mAccount 			= CommonUtil.toString(transaction,0,15).trim();		//계좌번호
		count				= CommonUtil.toString(transaction,15,2).trim();		//조립건수
		classificationCode	= CommonUtil.toString(transaction,17,2).trim();		//거래구분
		bankCode			= CommonUtil.toString(transaction,19,2).trim();		//은행코드
		amount				= CommonUtil.toString(transaction,21,13).trim();	//금액
		remainAmount		= CommonUtil.toString(transaction,34,13).trim();	//잔액
		giroCode			= CommonUtil.toString(transaction,47,6).trim();		//입금점지로코드
		name				= CommonUtil.toString(transaction,53,14).trim();	//성명
		supyoNumber			= CommonUtil.toString(transaction,67,10).trim();	//수표번호
		cash				= CommonUtil.toString(transaction,77,13).trim();	//현금(현금+당좌수표)
		otherBankSupyoAmount= CommonUtil.toString(transaction,90,13).trim();	//타행수표금액
		otherSupyo			= CommonUtil.toString(transaction,103,13).trim();	//가계수표,기타
		virtualAccount		= CommonUtil.toString(transaction,116,16).trim();	//가상계좌번호
		transactionTime		= CommonUtil.toString(transaction,132,14).trim();	//거래일자시간
		transactionNumber	= CommonUtil.toString(transaction,146,6).trim();	//통장거래 일련번호
		newBankCode			= CommonUtil.toString(transaction,152,3).trim();	//입금은행코드 3자리
		branchCode			= CommonUtil.toString(transaction,155,7).trim();	//입금지점코드 7자리 
		extra				= CommonUtil.toString(transaction,162,transaction.length-162).trim();	//예비
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(mAccount,15));
		transaction.append(CommonUtil.zerofill(count,2));
		transaction.append(CommonUtil.byteFiller(classificationCode,2));
		transaction.append(CommonUtil.byteFiller(bankCode,2));
		transaction.append(CommonUtil.zerofill(amount,13));
		transaction.append(CommonUtil.zerofill(remainAmount,13));
		transaction.append(CommonUtil.zerofill(giroCode,6));
		transaction.append(CommonUtil.byteFiller(name,14));
		transaction.append(CommonUtil.byteFiller(supyoNumber,10));
		transaction.append(CommonUtil.zerofill(cash,13));
		transaction.append(CommonUtil.zerofill(otherBankSupyoAmount,13));
		transaction.append(CommonUtil.zerofill(otherSupyo,13));
		transaction.append(CommonUtil.byteFiller(virtualAccount,16));
		transaction.append(CommonUtil.byteFiller(transactionTime,14));
		transaction.append(CommonUtil.zerofill(transactionNumber,6));
		transaction.append(CommonUtil.byteFiller(newBankCode,3));
		transaction.append(CommonUtil.byteFiller(branchCode,7));
		transaction.append(CommonUtil.byteFiller(extra,38));
		return transaction.toString();
	}

	/**
	 * @return the mAccount
	 */
	public String getmAccount() {
		return mAccount;
	}

	/**
	 * @param mAccount the mAccount to set
	 */
	public void setmAccount(String mAccount) {
		this.mAccount = mAccount;
	}

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the classificationCode
	 */
	public String getClassificationCode() {
		return classificationCode;
	}

	/**
	 * @param classificationCode the classificationCode to set
	 */
	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}

	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the remainAmount
	 */
	public String getRemainAmount() {
		return remainAmount;
	}

	/**
	 * @param remainAmount the remainAmount to set
	 */
	public void setRemainAmount(String remainAmount) {
		this.remainAmount = remainAmount;
	}

	/**
	 * @return the giroCode
	 */
	public String getGiroCode() {
		return giroCode;
	}

	/**
	 * @param giroCode the giroCode to set
	 */
	public void setGiroCode(String giroCode) {
		this.giroCode = giroCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the supyoNumber
	 */
	public String getSupyoNumber() {
		return supyoNumber;
	}

	/**
	 * @param supyoNumber the supyoNumber to set
	 */
	public void setSupyoNumber(String supyoNumber) {
		this.supyoNumber = supyoNumber;
	}

	/**
	 * @return the cash
	 */
	public String getCash() {
		return cash;
	}

	/**
	 * @param cash the cash to set
	 */
	public void setCash(String cash) {
		this.cash = cash;
	}

	/**
	 * @return the otherBankSupyoAmount
	 */
	public String getOtherBankSupyoAmount() {
		return otherBankSupyoAmount;
	}

	/**
	 * @param otherBankSupyoAmount the otherBankSupyoAmount to set
	 */
	public void setOtherBankSupyoAmount(String otherBankSupyoAmount) {
		this.otherBankSupyoAmount = otherBankSupyoAmount;
	}

	/**
	 * @return the otherSupyo
	 */
	public String getOtherSupyo() {
		return otherSupyo;
	}

	/**
	 * @param otherSupyo the otherSupyo to set
	 */
	public void setOtherSupyo(String otherSupyo) {
		this.otherSupyo = otherSupyo;
	}

	/**
	 * @return the virtualAccount
	 */
	public String getVirtualAccount() {
		return virtualAccount;
	}

	/**
	 * @param virtualAccount the virtualAccount to set
	 */
	public void setVirtualAccount(String virtualAccount) {
		this.virtualAccount = virtualAccount;
	}

	/**
	 * @return the transactionTime
	 */
	public String getTransactionTime() {
		return transactionTime;
	}

	/**
	 * @param transactionTime the transactionTime to set
	 */
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	/**
	 * @return the transactionNumber
	 */
	public String getTransactionNumber() {
		return transactionNumber;
	}

	/**
	 * @param transactionNumber the transactionNumber to set
	 */
	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	/**
	 * @return the newBankCode
	 */
	public String getNewBankCode() {
		return newBankCode;
	}

	/**
	 * @param newBankCode the newBankCode to set
	 */
	public void setNewBankCode(String newBankCode) {
		this.newBankCode = newBankCode;
	}

	/**
	 * @return the branchCode
	 */
	public String getBranchCode() {
		return branchCode;
	}

	/**
	 * @param branchCode the branchCode to set
	 */
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
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
