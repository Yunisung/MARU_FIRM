
package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0900100Bean extends CommBean {

	private String virtualAccount 		= "";		//가상계좌번호
	private String companyName			= "";		//업체명
	private String bankCode				= "";		//은행코드
	private String startDay				= "";		//시작일자	(SPACE)
	private String endTime				= "";		//종료시간	(YYYYMMDDHHMMSS)
	private String amount				= "";		//입금금액
	private String classificationCode	= "10";		//구분코드  	(등록:10 , 해제:40)
	private String transactionType		= "";		//거래종류     (10:수취,20:입금,51:취소)
	private String requestorName		= "";		//의뢰인명
	//PYS : 하이픈 전문 추가된것
	private String newBankCode			= "";		//은행코드3자리

	private String extra				= "";		//예비 
	
	public FB0900100Bean(){
		
	}
	
	public FB0900100Bean(String transaction){
		this(transaction.getBytes());
	}

	public FB0900100Bean(byte[] transaction){
		this.rootTransaction = transaction;
		virtualAccount 	= CommonUtil.toString(transaction,0,16).trim();		//가상계좌번호
		companyName		= CommonUtil.toString(transaction,16,30).trim();	//업체명
		bankCode		= CommonUtil.toString(transaction,46,2).trim();		//은행코드
		startDay		= CommonUtil.toString(transaction,48,8).trim();		//시작일자	(SPACE)
		endTime			= CommonUtil.toString(transaction,56,14).trim();	//종료시간	(YYYYMMDDHHMMSS)
		amount			= CommonUtil.toString(transaction,70,13).trim();	//입금금액
		classificationCode=CommonUtil.toString(transaction,83,2).trim();	//구분코드(등록:10 , 해제:40)
		transactionType = CommonUtil.toString(transaction,85,2).trim();	//거래종류
		requestorName 	= CommonUtil.toString(transaction,87,20).trim();	//의뢰인명
		newBankCode		= CommonUtil.toString(transaction, 107, 3).trim(); //은행코드3자리
		extra			= CommonUtil.toString(transaction,110,90).trim();	//예비
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(virtualAccount,16));
		transaction.append(CommonUtil.byteFiller(companyName,30));
		transaction.append(CommonUtil.byteFiller(bankCode,2));
		transaction.append(CommonUtil.byteFiller(startDay,8));
		transaction.append(CommonUtil.byteFiller(endTime,14));
		transaction.append(CommonUtil.zerofill(amount,13));
		transaction.append(CommonUtil.byteFiller(classificationCode,2));
		transaction.append(CommonUtil.byteFiller(transactionType,2));
		transaction.append(CommonUtil.byteFiller(requestorName,20));
		transaction.append(CommonUtil.byteFiller(newBankCode,3));
		transaction.append(CommonUtil.byteFiller(extra,90));
		return transaction.toString();
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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	 * @return the startDay
	 */
	public String getStartDay() {
		return startDay;
	}

	/**
	 * @param startDay the startDay to set
	 */
	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the requestorName
	 */
	public String getRequestorName() {
		return requestorName;
	}

	/**
	 * @param requestorName the requestorName to set
	 */
	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
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

	public String getNewBankCode() { return newBankCode; }

	public void setNewBankCode(String newBankCode) { this.newBankCode = newBankCode; }
	
	
	
}
