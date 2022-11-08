
package com.pgmate.firm.ksnet;

import com.pgmate.lib.util.lang.CommonUtil;

public class FB0100100Bean extends CommBean {

	private String mAccount 		= "";	//��ݰ��¹�ȣ(�����)
	private String mAccountPassword	= "";	//�����й�ȣ(����º��)
	private String sign				= "";	//�����ȣ(����ο�����)
	private String amount			= "";	//��ݱݾ�(��ü�ݾ�)
	private String remainAmountSign	= "";	//����� �ܾ׺�ȣ(+/-)
	private String remainAmount		= "";	//������ܾ�(�����ܾ�)
	private String receiveBankCode	= "";	//�Ա������ڵ�
	private String receiveAccount	= "";	//�Աݰ���
	private String commission		= "";	//������
	private String trasferTime		= "";	//��ü�ð�(HHMMSS)
	private String senderName		= "";	//�Ա��μ���
	private String cmsCode			= "";	//CMS CODE(SPACE)
	private String socialNumber		= "";	//�ֹι�ȣ(����ֹι�ȣ)
	private String ATMCode			= "";	//�ڵ���ü����
	private String receiverName		= "";	//Ÿ����ü�� ��ݰ��¿� �Աݰ����� �����ָ� ������ ����� �� �ֵ��� �Ա� ���� �����ָ� ���� �Է�(��������)
	private String receiveNewBankCode= "";	//�Ա������ڵ带 3�ڸ��� �߰�

	//PYS : ������������ �߰��Ȱ�
	private String salary			= "";	//�޿�

	private String extra			= "";	//���񿵿�
	
	public FB0100100Bean(){
		
	}
	
	public FB0100100Bean(String transaction){
		this(transaction.getBytes());
	}

	public FB0100100Bean(byte[] transaction){
		this.rootTransaction = transaction;
		mAccount 			= CommonUtil.toString(transaction,0,15).trim();	//��ݰ��¹�ȣ(�����)
		mAccountPassword	= CommonUtil.toString(transaction,15,8).trim();	//�����й�ȣ(����º��)
		sign				= CommonUtil.toString(transaction,23,6).trim();	//�����ȣ(����ο�����)
		amount				= CommonUtil.toString(transaction,29,13).trim();//��ݱݾ�(��ü�ݾ�)
		remainAmountSign	= CommonUtil.toString(transaction,42,1).trim();	//����� �ܾ׺�ȣ(+/-)
		remainAmount		= CommonUtil.toString(transaction,43,13).trim();//������ܾ�(�����ܾ�)
		receiveBankCode		= CommonUtil.toString(transaction,56,2).trim();	//�Ա������ڵ�
		receiveAccount		= CommonUtil.toString(transaction,58,15).trim();//�Աݰ���
		commission			= CommonUtil.toString(transaction,73,9).trim();	//������
		trasferTime			= CommonUtil.toString(transaction,82,6).trim();	//��ü�ð�(HHMMSS)
		senderName			= CommonUtil.toString(transaction,88,20).trim();//�Ա��μ���
		cmsCode				= CommonUtil.toString(transaction,108,16).trim();//CMS CODE(SPACE)
		socialNumber		= CommonUtil.toString(transaction,124,13).trim();//�ֹι�ȣ(����ֹι�ȣ)
		ATMCode				= CommonUtil.toString(transaction,137,2).trim();//�ڵ���ü����
		receiverName		= CommonUtil.toString(transaction,139,20).trim();//��ü��������
		receiveNewBankCode	= CommonUtil.toString(transaction,159,3).trim();//�Ա������ڵ�3
		salary				= CommonUtil.toString(transaction, 162, 1).trim();//�޿�����
		extra				= CommonUtil.toString(transaction,163,37).trim();//����
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
		transaction.append(CommonUtil.byteFiller(receiveNewBankCode,3));
		transaction.append(CommonUtil.byteFiller(salary,1));
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
