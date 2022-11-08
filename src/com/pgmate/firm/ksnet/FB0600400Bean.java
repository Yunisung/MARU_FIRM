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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class FB0600400Bean extends CommBean {

	private Logger logger = LoggerFactory.getLogger( getClass() );

	private String transactionDay 	= "";	//MMDD
	private String bankCode			= "";	//��������
	private String account			= "";	//���¹�ȣ
	private String name				= "";	//���¼���
	private String socialNumber		= "";	//������ �ֹι�ȣ
	private String socialCheck      = "";	//������ �ֹι�ȣ üũ ���� 99:�Ϲ�üũ 88:������Ģüũ
	private String mAccount			= "";   //������ �ݰ� �̿��Ҷ� (����� ����� �� ���) ����¹�ȣ
	private String newBankCode		= "";	//���������ڵ�3�ڸ� 

//	private String companySpace 	= "";	//ȸ�������� SPACE
//	private String hangulSpace		= "";	//�ѱۻ������ SPACE
//	private String bankSpace		= "";	//�������࿹�� SPACE

	//PYS: ������������ �߰��Ȱ�
	private String amount			= ""; 	//�ݾ�
	private String dotcomSpace		= "";	//�������� ��ȸ : �츮���� ������ȸ�� 'D'����, �츮���� ����ü�� ����
	private String otherBankSpace	= "";	//��Ÿ����������
	private String nhSpace			= "";	//�������±���

	private String extra			= "";	//����(������) SPACE

	public FB0600400Bean(){	
	}
	
	public FB0600400Bean(String transaction){
		this(transaction.getBytes(Charset.forName("euc-kr")));
	}
	
	public FB0600400Bean(byte[] transaction){
		super.rootTransaction = transaction;
		transactionDay 	= CommonUtil.toString(transaction,0,4).trim();		//MMDD
		bankCode		= CommonUtil.toString(transaction,4,2).trim();		//��������
		account			= CommonUtil.toString(transaction,6,16).trim();		//���¹�ȣ
		name			= CommonUtil.toString(transaction,22,22).trim();	//���¼���
		socialNumber	= CommonUtil.toString(transaction,44,13).trim();	//������ �ֹι�ȣ
		socialCheck		= CommonUtil.toString(transaction,57,2).trim();		//������ �ֹι�ȣ üũ ���� 99:�Ϲ�üũ 88:������Ģüũ
		mAccount		= CommonUtil.toString(transaction,59,20).trim();	//������ �ݰ� �̿��Ҷ� (����� ����� �� ���) ����¹�ȣ
		newBankCode		= CommonUtil.toString(transaction,79,3).trim();		//�����ڵ� 3�ڸ�
		amount			= CommonUtil.toString(transaction, 82, 13).trim();	//�ݾ�
		dotcomSpace		= CommonUtil.toString(transaction, 95, 1).trim();//����������ȸ
		otherBankSpace  = CommonUtil.toString(transaction, 96, 1).trim();//��Ÿ����������
		nhSpace			= CommonUtil.toString(transaction, 97, 1).trim();//�������±���
		extra			= CommonUtil.toString(transaction,98,102).trim();	//����(������) SPACE

//		companySpace 	= CommonUtil.toString(transaction,175,20).trim();	//ȸ�������� SPACE
//		hangulSpace		= CommonUtil.toString(transaction,195,1).trim();	//�ѱۻ������ SPACE
//		bankSpace		= CommonUtil.toString(transaction,196,4).trim();	//�������࿹�� SPACE

		log();
	}

	public void log() {
		logger.info("=================== ������ ��ȸ ��� ===================");
		logger.info("transactionDay : {}", transactionDay);
		logger.info("bankCode : {}", bankCode);
		logger.info("account : {}", account);
		logger.info("name : {}", name);
		logger.info("socialNumber : {}", socialNumber);
		logger.info("socialCheck : {}", socialCheck);
		logger.info("mAccount : {}", mAccount);
		logger.info("newBankCode : {}", newBankCode);
		logger.info("amount : {}", amount);
		logger.info("dotcomSpace : {}", dotcomSpace);
		logger.info("otherBankSpace : {}", otherBankSpace);
		logger.info("nhSpace : {}", nhSpace);
		logger.info("extra : {}", extra);

	}
	
	public String getTransaction(){
		StringBuffer transaction = new StringBuffer();
		transaction.append(CommonUtil.zerofill(transactionDay,4));
		transaction.append(CommonUtil.byteFiller(bankCode,2));
		transaction.append(CommonUtil.byteFiller(account,16));
		transaction.append(CommonUtil.byteFiller(name,22));
		transaction.append(CommonUtil.byteFiller(socialNumber,13));
		transaction.append(CommonUtil.byteFiller(socialCheck,2));
		transaction.append(CommonUtil.byteFiller(mAccount,20));
		transaction.append(CommonUtil.byteFiller(newBankCode,3));
		transaction.append(CommonUtil.byteFiller(dotcomSpace, 1));
		transaction.append(CommonUtil.byteFiller(otherBankSpace, 1));
		transaction.append(CommonUtil.byteFiller(nhSpace, 1));
		transaction.append(CommonUtil.byteFiller(extra,115));
//		transaction.append(CommonUtil.byteFiller(companySpace,20));
//		transaction.append(CommonUtil.byteFiller(hangulSpace,1));
//		transaction.append(CommonUtil.zerofill(bankSpace,4));
		
		return transaction.toString();
	}

	public String getTransactionDay() {
		return transactionDay;
	}

	public void setTransactionDay(String transactionDay) {
		this.transactionDay = transactionDay;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}

	public String getSocialCheck() {
		return socialCheck;
	}

	public void setSocialCheck(String socialCheck) {
		this.socialCheck = socialCheck;
	}

	public String getMAccount() {
		return mAccount;
	}

	public void setMAccount(String account) {
		mAccount = account;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

//	public String getCompanySpace() {
//		return companySpace;
//	}
//
//	public void setCompanySpace(String companySpace) {
//		this.companySpace = companySpace;
//	}
//
//	public String getHangulSpace() {
//		return hangulSpace;
//	}
//
//	public void setHangulSpace(String hangulSpace) {
//		this.hangulSpace = hangulSpace;
//	}
//
//	public String getBankSpace() {
//		return bankSpace;
//	}
//
//	public void setBankSpace(String bankSpace) {
//		this.bankSpace = bankSpace;
//	}

	public String getNewBankCode() {
		return newBankCode;
	}

	public void setNewBankCode(String newBankCode) {
		this.newBankCode = newBankCode;
	}

	public String getDotcomSpace() { return dotcomSpace; }

	public void setDotcomSpace(String dotcom) { this.dotcomSpace = dotcom; }

	public String getOtherBankSpace() { return otherBankSpace; }

	public void setOtherBankSpace(String otherbank) { this.otherBankSpace = otherbank; }

	public String getNhSpace() { return nhSpace; }

	public void setNhSpace(String nh) { this.nhSpace = nh; }
	
	
	
	
	
	
	
	
	
}
