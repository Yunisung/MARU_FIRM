
package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0900400Bean extends CommBean {
	private String trxType		 		= "";		//거래구분 (1:신규, 4:해지, 8:변경, 9:조회)
	private String virtualAccount 		= "";		//가상계좌번호
	private String withdrawBankCd		= "";		//출금은행코드
	private String withdrawAccount		= "";		//출금계좌번호
	private String customerName			= "";		//고객명
	private String regStatus			= "";		//등록상태
	private String depositStartDate		= "";		//입금시작일자
	private String depositEndDate		= "";		//입금종료일자
	private String firmCompanyCd		= "";		//펌뱅킹업체코드 (신한은행만 사용)
	private String regType				= "";		//등록유형 (농협만 사용)(1:개인, 2:법인, 3:미성년자, 4:외국인)
	private String identity				= "";		//실명번호 (농협,신한)(개인 : 샌영월일 6자리+성별1자리, 법인:사업자번호)
	private String phoneNo				= "";		//휴대폰번호 
	private String extra				= "";		//예비 
	
	public FB0900400Bean(){
		
	}
	
	public FB0900400Bean(String transaction){
		this(transaction.getBytes());
	}

	public FB0900400Bean(byte[] transaction){
		this.rootTransaction = transaction;
		trxType 			= CommonUtil.toString(transaction,0,1).trim();		//거래구분 (1:신규, 4:해지, 8:변경, 9:조회)
		virtualAccount 		= CommonUtil.toString(transaction,1,16).trim();		//가상계좌번호
		withdrawBankCd		= CommonUtil.toString(transaction,17,3).trim();		//출금은행코드
		withdrawAccount		= CommonUtil.toString(transaction,20,16).trim();	//출금계좌번호
		customerName		= CommonUtil.toString(transaction,36,52).trim();	//고객명
		regStatus			= CommonUtil.toString(transaction,88,1).trim();		//등록상태
		depositStartDate	= CommonUtil.toString(transaction,89,8).trim();		//입금시작일자
		depositEndDate		= CommonUtil.toString(transaction,97,8).trim();		//입금종료일자
		firmCompanyCd 		= CommonUtil.toString(transaction,105,8).trim();	//펌뱅킹업체코드 (신한은행만 사용)
		regType 			= CommonUtil.toString(transaction,113,1).trim();	//등록유형 (농협만 사용)(1:개인, 2:법인, 3:미성년자, 4:외국인)
		identity	 		= CommonUtil.toString(transaction,114,10).trim();	//실명번호 (농협,신한)(개인 : 샌영월일 6자리+성별1자리, 법인:사업자번호)
		phoneNo 			= CommonUtil.toString(transaction,124,12).trim();	//휴대폰번호
		extra				= CommonUtil.toString(transaction,136,transaction.length-136).trim();	//예비 
	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(trxType,1));
		transaction.append(CommonUtil.byteFiller(virtualAccount,16));
		transaction.append(CommonUtil.byteFiller(withdrawBankCd,3));
		transaction.append(CommonUtil.byteFiller(withdrawAccount,16));
		transaction.append(CommonUtil.byteFiller(customerName,52));
		transaction.append(CommonUtil.byteFiller(regStatus,1));
		transaction.append(CommonUtil.byteFiller(depositStartDate,8));
		transaction.append(CommonUtil.byteFiller(depositEndDate,8));
		transaction.append(CommonUtil.byteFiller(firmCompanyCd,8));
		transaction.append(CommonUtil.byteFiller(regType,1));
		transaction.append(CommonUtil.byteFiller(identity,10));
		transaction.append(CommonUtil.byteFiller(phoneNo,12));
		transaction.append(CommonUtil.byteFiller(extra,64));
		return transaction.toString();
	}

	public String getTrxType() {
		return trxType;
	}

	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}

	public String getVirtualAccount() {
		return virtualAccount;
	}

	public void setVirtualAccount(String virtualAccount) {
		this.virtualAccount = virtualAccount;
	}

	public String getWithdrawBankCd() {
		return withdrawBankCd;
	}

	public void setWithdrawBankCd(String withdrawBankCd) {
		this.withdrawBankCd = withdrawBankCd;
	}

	public String getWithdrawAccount() {
		return withdrawAccount;
	}

	public void setWithdrawAccount(String withdrawAccount) {
		this.withdrawAccount = withdrawAccount;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(String regStatus) {
		this.regStatus = regStatus;
	}

	public String getDepositStartDate() {
		return depositStartDate;
	}

	public void setDepositStartDate(String depositStartDate) {
		this.depositStartDate = depositStartDate;
	}

	public String getDepositEndDate() {
		return depositEndDate;
	}

	public void setDepositEndDate(String depositEndDate) {
		this.depositEndDate = depositEndDate;
	}

	public String getFirmCompanyCd() {
		return firmCompanyCd;
	}

	public void setFirmCompanyCd(String firmCompanyCd) {
		this.firmCompanyCd = firmCompanyCd;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
}
