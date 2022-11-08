
package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0100100Bean extends CommBean {

	private String mAccount 		= "";	//출금계좌번호(모계좌)
	private String mAccountPassword	= "";	//통장비밀번호(모계좌비번)
	private String sign				= "";	//복기부호(은행부여공식)
	private String amount			= "";	//출금금액(이체금액)
	private String remainAmountSign	= "";	//출금후 잔액부호(+/-)
	private String remainAmount		= "";	//출금후잔액(원장잔액)
	private String receiveBankCode	= "";	//입금은행코드
	private String receiveAccount	= "";	//입금계좌
	private String commission		= "";	//수수료
	private String trasferTime		= "";	//이체시각(HHMMSS)
	private String senderName		= "";	//입금인성명
	private String cmsCode			= "";	//CMS CODE(SPACE)
	private String socialNumber		= "";	//주민번호(고객주민번호)
	private String ATMCode			= "";	//자동이체구분
	private String receiverName		= "";	//타행이체시 출금계좌에 입금계좌의 예금주명 정보를 출력할 수 있도록 입금 계좌 예금주명 정보 입력(제일은행)
	private String receiveNewBankCode= "";	//입금은행코드를 3자리로 추가

	//PYS : 하이픈전문에 추가된것
	private String salary			= "";	//급여

	private String extra			= "";	//예비영역

	public FB0100100Bean(){

	}

	public FB0100100Bean(String transaction){
		this(transaction.getBytes());
	}

	public FB0100100Bean(byte[] transaction){
		this.rootTransaction = transaction;
		mAccount 			= CommonUtil.toString(transaction,0,15).trim();	//출금계좌번호(모계좌)
		mAccountPassword	= CommonUtil.toString(transaction,15,8).trim();	//통장비밀번호(모계좌비번)
		sign				= CommonUtil.toString(transaction,23,6).trim();	//복기부호(은행부여공식)
		amount				= CommonUtil.toString(transaction,29,13).trim();//출금금액(이체금액)
		remainAmountSign	= CommonUtil.toString(transaction,42,1).trim();	//출금후 잔액부호(+/-)
		remainAmount		= CommonUtil.toString(transaction,43,13).trim();//출금후잔액(원장잔액)
		receiveBankCode		= CommonUtil.toString(transaction,56,2).trim();	//입금은행코드
		receiveAccount		= CommonUtil.toString(transaction,58,15).trim();//입금계좌
		commission			= CommonUtil.toString(transaction,73,9).trim();	//수수료
		trasferTime			= CommonUtil.toString(transaction,82,6).trim();	//이체시각(HHMMSS)
		senderName			= CommonUtil.toString(transaction,88,20).trim();//입금인성명
		cmsCode				= CommonUtil.toString(transaction,108,16).trim();//CMS CODE(SPACE)
		socialNumber		= CommonUtil.toString(transaction,124,13).trim();//주민번호(고객주민번호)
		ATMCode				= CommonUtil.toString(transaction,137,2).trim();//자동이체구분
		receiverName		= CommonUtil.toString(transaction,139,20).trim();//업체통장적요
		receiveNewBankCode	= CommonUtil.toString(transaction,159,3).trim();//입금은행코드3
		salary				= CommonUtil.toString(transaction, 162, 1).trim();//급여구분
		extra				= CommonUtil.toString(transaction,163,37).trim();//예비
	}

	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.byteFiller(mAccount,15));
		transaction.append(CommonUtil.byteFiller(mAccountPassword,8));
		transaction.append(CommonUtil.byteFiller(sign,6));
		transaction.append(CommonUtil.zerofill(amount,13));
		transaction.append(CommonUtil.byteFiller(remainAmountSign,1));
		transaction.append(CommonUtil.zerofill(remainAmount,13));
		transaction.append(CommonUtil.byteFiller(receiveBankCode,2));
		transaction.append(CommonUtil.byteFiller(receiveAccount,15));
		transaction.append(CommonUtil.zerofill(commission,9));
		transaction.append(CommonUtil.byteFiller(trasferTime,6));
		transaction.append(CommonUtil.byteFiller(senderName,20));
		transaction.append(CommonUtil.byteFiller(cmsCode,16));
		transaction.append(CommonUtil.byteFiller(socialNumber,13));
		transaction.append(CommonUtil.byteFiller(ATMCode,2));
		transaction.append(CommonUtil.byteFiller(receiverName,20));
		transaction.append(CommonUtil.zerofill(receiveNewBankCode,3));
		transaction.append(CommonUtil.zerofill(salary,1));
		transaction.append(CommonUtil.byteFiller(extra,37));

		return transaction.toString();
	}

	public String getMAccount() {
		return mAccount;
	}

	public void setMAccount(String account) {
		mAccount = account;
	}

	public String getMAccountPassword() {
		return mAccountPassword;
	}

	public void setMAccountPassword(String accountPassword) {
		mAccountPassword = accountPassword;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getRemainAmountSign() {
		return remainAmountSign;
	}

	public void setRemainAmountSign(String remainAmountSign) {
		this.remainAmountSign = remainAmountSign;
	}

	public String getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(String remainAmount) {
		this.remainAmount = remainAmount;
	}

	public String getReceiveBankCode() {
		return receiveBankCode;
	}

	public void setReceiveBankCode(String receiveBankCode) {
		this.receiveBankCode = receiveBankCode;
	}

	public String getReceiveAccount() {
		return receiveAccount;
	}

	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getTrasferTime() {
		return trasferTime;
	}

	public void setTrasferTime(String trasferTime) {
		this.trasferTime = trasferTime;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getCmsCode() {
		return cmsCode;
	}

	public void setCmsCode(String cmsCode) {
		this.cmsCode = cmsCode;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}

	public String getATMCode() {
		return ATMCode;
	}

	public void setATMCode(String code) {
		ATMCode = code;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiveNewBankCode() {
		return receiveNewBankCode;
	}

	public void setReceiveNewBankCode(String receiveNewBankCode) {
		this.receiveNewBankCode = receiveNewBankCode;
	}

	public String getSalary() { return salary; }

	public void setSalary(String salary) { this.salary = salary; }


}
