package com.pgmate.firm.server;

import com.google.gson.Gson;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HolderBean;
import com.pgmate.firm.hyphen.HyphenBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.ksnet.FB0600101Bean;
import com.pgmate.firm.ksnet.FB0600300Bean;
import com.pgmate.firm.ksnet.FB0600400Bean;
import com.pgmate.firm.ksnet.FB0700100Bean;
import com.pgmate.firm.ksnet.FB0900400Bean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.firm.util.KsnetComm;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;

/**
 * @author Administrator
 *
 */
public class InterProcess implements java.io.Serializable{

	private Logger logger = LoggerFactory.getLogger( getClass() );
	private Firm firm = null;
	
	public InterProcess(Firm firm) {
		this.firm = firm;
	}
	
	
	public String execute(String json){
		FirmBean firmBean = (FirmBean)GsonUtil.fromJson(json, FirmBean.class);
		
		firmBean = valid(firmBean);
		
		if(firmBean.resultCd.equals("9999")){
			return GsonUtil.toJson(firmBean);
		}else{
			if(firmBean.msgType.startsWith("0800")){
				firmBean = proc0800(firmBean);
			}else if(firmBean.msgType.startsWith("0600300")){
				//�ܾ���ȸ
//				firmBean = proc0600300(firmBean);
				firmBean = procBalance(firmBean);
			}else if(firmBean.msgType.startsWith("0600400")){
				//������ȸ
				//firmBean = proc0600400(firmBean);
				firmBean = procHolder(firmBean);
			}else if(firmBean.msgType.startsWith("0700100")){
				//����
				firmBean = proc0700100(firmBean);
			}else if(firmBean.msgType.startsWith("0100100")){
				//��ü
//				firmBean = proc0100100(firmBean);
				firmBean = procDeposit(firmBean);
			}else if(firmBean.msgType.startsWith("0600101")){
				//ó�������ȸ
//				firmBean = proc0600101(firmBean);
				firmBean = procResultChk(firmBean);
			}else if(firmBean.msgType.startsWith("0900400")){
				//������� ������� ���
				firmBean = proc0900400(firmBean);
			}
		}
		
		
		return GsonUtil.toJson(firmBean);
		
	}
	
	
	
	public FirmBean proc0800(FirmBean firmBean){
		FirmMasterDAO masterDAO = new FirmMasterDAO();
		
		long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, "");
		return processCheck(idx,firmBean,masterDAO);
	}
	
	
	/*
	 * �ܾ���ȸ
	 */
	public FirmBean proc0600300(FirmBean firmBean){
		logger.info("=================== �ܾ���ȸ ===================");
		
		try {	
			FirmMasterDAO masterDAO = new FirmMasterDAO();
			FB0600300Bean fbBean = new FB0600300Bean();
			fbBean.setAccount(firmBean.mAccnt);
			//fbBean.setAccount(firm.bank.get(firmBean.bankCd).account);
			
			long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, fbBean.getTransaction());
			firmBean = processCheck(idx,firmBean,masterDAO);
			if(firmBean.resultCd.equals("0000")){
				fbBean = new FB0600300Bean(firmBean.data.getString("resData"));
				firmBean.data.put("amount", CommonUtil.parseLong(fbBean.getSign()+fbBean.getCurrentAmount()));
				masterDAO.insertBalance(firmBean.bankCd, fbBean.getAccount(), fbBean.getSign()+fbBean.getCurrentAmount());
			}
		}catch (Exception e) {
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="�ܾ���ȸ ����";
			
			e.printStackTrace();
			logger.error("�ܾ���ȸ Error : [{}]", e.getMessage());
		}
		
		logger.info("===================================================");
		
		return firmBean;
	}

	/*
	�ܾ���ȸ ���������� �����
	 */
	public FirmBean procBalance(FirmBean firmBean) {
		logger.info("=================== �ܾ���ȸ ===================");

		try {
			FirmMasterDAO masterDAO = new FirmMasterDAO();
			BankBean configBean = firm.bank.get(firmBean.bankCd);

			BalanceBean balanceBean = new BalanceBean();
			balanceBean.setCompCode(configBean.compCd);
			balanceBean.setBankCode(configBean.bankCd);
			balanceBean.setAccountNo(configBean.account);

			HyphenBean hyphenBean = new HyphenBean();
			hyphenBean.setKscode(configBean.kscode);
			hyphenBean.setEkey(configBean.ekey);
			hyphenBean.setMsalt(configBean.msalt);
			hyphenBean.setReqdata(balanceBean);
			hyphenBean.setSendurl("rfb/retail/inquiry/balance");

			String jsonParams = new Gson().toJson(hyphenBean);

			long idx = masterDAO.setMasterbyHyphen(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, hyphenBean.getSendurl(), jsonParams);
			firmBean = processCheck(idx,firmBean,masterDAO);
			if(firmBean.resultCd.equals("0000")){
				String resJson = firmBean.data.getString("resData");
				balanceBean = (BalanceBean) GsonUtil.fromJson(resJson, BalanceBean.class);
				logger.info("�ܾ���ȸ ����");
				String amount = balanceBean.getSign()+balanceBean.getTotalBalance();
				firmBean.data.put("amount", CommonUtil.parseLong(amount.trim()));
				masterDAO.insertBalance(configBean.bankCd, configBean.account, amount.trim());
			}
		}catch (Exception e) {
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="�ܾ���ȸ ����";

			e.printStackTrace();
			logger.error("�ܾ���ȸ Error : [{}]", e.getMessage());
		}

		logger.info("===================================================");

		return firmBean;
	}
	
	public FirmBean procHolder(FirmBean firmBean) {
		logger.info("======================== Holder ========================");
		
		try {
			FirmMasterDAO masterDAO = new FirmMasterDAO();
			BankBean configBean = firm.bank.get(firmBean.bankCd);
			
			HolderBean holderBean = new HolderBean();
			holderBean.setCompCode(configBean.compCd);
			holderBean.setBankCode(configBean.bankCd);
			holderBean.setAccountBankCode(firmBean.data.getString("bankCd"));
			holderBean.setAccountNo(firmBean.data.getString("account"));
			holderBean.setAgencyYn(firmBean.data.getString("agencyYn"));
			holderBean.setCompAccountNo(firmBean.data.getString("compAccountNo"));
			holderBean.setSocialId(firmBean.data.getString("socialId"));
			holderBean.setAmount(firmBean.data.getString("amount"));
			
			HyphenBean hyphenBean = new HyphenBean();
			hyphenBean.setKscode(configBean.kscode);
			hyphenBean.setEkey(configBean.ekey);
			hyphenBean.setMsalt(configBean.msalt);
			hyphenBean.setReqdata(holderBean);
			hyphenBean.setSendurl("rfb/retail/account/accountname");
			
			String jsonParams = new Gson().toJson(hyphenBean);
			
			long idx = masterDAO.setMasterbyHyphen(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, hyphenBean.getSendurl(), jsonParams);
			firmBean = processCheck(idx,firmBean,masterDAO);
			if(firmBean.resultCd.equals("0000")){
				String resJson = firmBean.data.getString("resData");
				holderBean = (HolderBean) GsonUtil.fromJson(resJson, HolderBean.class);
				String name = holderBean.getAccountName();
				firmBean.data.put("accountName", CommonUtil.parseLong(name.trim()));
				masterDAO.insertAccnt(configBean.bankCd, configBean.account, name.trim());
			}
		}catch (Exception e) {
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="�ܾ���ȸ ����";

			e.printStackTrace();
			logger.error("�ܾ���ȸ Error : [{}]", e.getMessage());
		}

		logger.info("===================================================");

		return firmBean;
	}
	
	/*
	 * ������ȸ �����ڵ带 099 �� ����ϸ� KSNET �׿ܴ� �� ���� 
	 * data.bankCd, data.account , data.socialNumber, data.socialCheck
	 */
	public FirmBean proc0600400(FirmBean firmBean){
		
		
		
		FirmMasterDAO masterDAO = new FirmMasterDAO();
		FB0600400Bean fbBean = new FB0600400Bean();
		
		fbBean.setTransactionDay(CommonUtil.getCurrentDate("MMdd"));
		fbBean.setNewBankCode(firmBean.data.getString("bankCd"));
		fbBean.setAccount(firmBean.data.getString("account"));
		fbBean.setSocialNumber(firmBean.data.getString("socialNumber"));
		fbBean.setSocialCheck(firmBean.data.getString("socialCheck"));
		fbBean.setName(firmBean.data.getString("holder"));
		
		if(firmBean.bankCd.equals("099") && firmBean.data.isNullOrSpace("socialCheck")) {
			String holder = masterDAO.selectAccnt(firmBean.data.getString("bankCd"), firmBean.data.getString("account"));
			logger.info("INTER ACCNT CHECK: {}",holder);
			if(!CommonUtil.isNullOrSpace(holder)) {
				firmBean.data.put("name", holder);
				firmBean.resultCd ="0000";
				firmBean.resultMsg = "������ȸ";
				
				return firmBean;
			}
		}
		
		
		long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, fbBean.getTransaction());
		firmBean = processCheck(idx,firmBean,masterDAO);
		if(firmBean.resultCd.equals("0000")){
			fbBean = new FB0600400Bean(firmBean.data.getString("resData"));
			firmBean.data.put("name", fbBean.getName());
			masterDAO.insertAccnt(firmBean.data.getString("bankCd"), firmBean.data.getString("account"), firmBean.data.getString("name"));
		}
		
		return firmBean;
	}
	
	
	/*
	 * ����
	 */
	public FirmBean proc0700100(FirmBean firmBean){
		FirmMasterDAO masterDAO = new FirmMasterDAO();
		FB0700100Bean fbBean = new FB0700100Bean();
		fbBean.setAccount(firm.bank.get(firmBean.bankCd).account);
		long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, fbBean.getTransaction());
		firmBean = processCheck(idx,firmBean,masterDAO);
		if(firmBean.resultCd.equals("0000")){
			fbBean = new FB0700100Bean(firmBean.data.getString("resData"));
			
			firmBean.data.put("reqCount", CommonUtil.parseLong(fbBean.getReqCount()));
			firmBean.data.put("reqAmount", CommonUtil.parseLong(fbBean.getReqAmount()));
			firmBean.data.put("sucCount", CommonUtil.parseLong(fbBean.getSuccessCount()));
			firmBean.data.put("sucAmount", CommonUtil.parseLong(fbBean.getSuccessAmount()));
			firmBean.data.put("failCount", CommonUtil.parseLong(fbBean.getFailCount()));
			firmBean.data.put("failAmount", CommonUtil.parseLong(fbBean.getFailAmount()));
			firmBean.data.put("fee", CommonUtil.parseLong(fbBean.getFee()));
			
			firmBean.data.put("oReqCount", CommonUtil.parseLong(fbBean.getOtherReqCount()));
			firmBean.data.put("oReqAmount", CommonUtil.parseLong(fbBean.getOtherReqAmount()));
			firmBean.data.put("oSucCount", CommonUtil.parseLong(fbBean.getOtherSuccessCount()));
			firmBean.data.put("oSucAmount", CommonUtil.parseLong(fbBean.getOtherSuccessAmount()));
			firmBean.data.put("oFailCount", CommonUtil.parseLong(fbBean.getOtherFailCount()));
			firmBean.data.put("oFailAmount", CommonUtil.parseLong(fbBean.getOtherFailAmount()));
			firmBean.data.put("oFee", CommonUtil.parseLong(fbBean.getOtherFee()));
			
		}
		
		return firmBean;
	}
	
	
	
	public FirmBean proc0100100(FirmBean firmBean){
		// ��ü���ɽð� �� ������ ��� ����
		Firm firm = FirmLoader.getConfig();
		long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));
		
		if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="��ü ���� �ð� �ƴ�";
			return firmBean;
		}
		
		FirmTrxDAO trxDAO = new FirmTrxDAO();
		
		//String sign = 	WooriSign.getSign(firmBean.data.getString("recvAccount"), CommonUtil.getAmountFormat(firmBean.data.getString("amount")), firmBean.data.getString("recvBankCd"), firm.bank.get(firmBean.bankCd).account);
		
		logger.info("account : {}",firm.bank.get(firmBean.bankCd).account);
		logger.info("recvBankCd : {}",firmBean.data.getString("recvBankCd"));
		logger.info("recvAccount : {}",firmBean.data.getString("recvAccount"));
		logger.info("sender : {}",firmBean.data.getString("sender"));
		logger.info("amount : {}",CommonUtil.getAmountFormat(firmBean.data.getString("amount")));
		logger.info("procType : {}",firmBean.data.getString("procType"));
		
		long idx = trxDAO.insertTrx(firmBean.bankCd, firmBean.data.getLong("amount"), firmBean.data.getString("recvBankCd"), firmBean.data.getString("recvAccount"), firmBean.data.getString("sender"), firmBean.data.getString("recordInfo"), firmBean.data.getString("procType"));
		if(idx == 0){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="��ü������ ��Ͻ���";
		}else{
			firmBean = processCheck(idx,firmBean,trxDAO);
		}
		return firmBean;
	}

	/**
	 * �۱� ���������� �����
	 * @param firmBean
	 * @return
	 */
	public FirmBean procDeposit(FirmBean firmBean){
		// ��ü���ɽð� �� ������ ��� ����
		Firm firm = FirmLoader.getConfig();
		long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));

		if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="��ü ���� �ð� �ƴ�";
			return firmBean;
		}

		FirmTrxDAO trxDAO = new FirmTrxDAO();

		//String sign = 	WooriSign.getSign(firmBean.data.getString("recvAccount"), CommonUtil.getAmountFormat(firmBean.data.getString("amount")), firmBean.data.getString("recvBankCd"), firm.bank.get(firmBean.bankCd).account);

		logger.info("account : {}",firm.bank.get(firmBean.bankCd).account);
		logger.info("recvBankCd : {}",firmBean.data.getString("recvBankCd"));
		logger.info("recvAccount : {}",firmBean.data.getString("recvAccount"));
		logger.info("sender : {}",firmBean.data.getString("sender"));
		logger.info("amount : {}",CommonUtil.getAmountFormat(firmBean.data.getString("amount")));
		logger.info("procType : {}",firmBean.data.getString("procType"));

		long idx = trxDAO.insertTrx(firmBean.bankCd, firmBean.data.getLong("amount"), firmBean.data.getString("recvBankCd"), firmBean.data.getString("recvAccount"), firmBean.data.getString("sender"), firmBean.data.getString("recordInfo"), firmBean.data.getString("procType"));
		if(idx == 0){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="��ü������ ��Ͻ���";
		}else{
			firmBean = processCheck(idx,firmBean,trxDAO);

			if(firmBean.resultCd.equals("0000")){
				logger.info("��ü ����");
				String amount = firmBean.data.getString("balance");
				new FirmMasterDAO().insertBalance(firm.bank.get(firmBean.bankCd).bankCd, firm.bank.get(firmBean.bankCd).account, amount.trim());
			}

		}
		return firmBean;
	}

	/*
	 * ó����� ��ȸ
	 */
	public FirmBean proc0600101(FirmBean firmBean){
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
		FBHeaderBean headerBean = new FBHeaderBean();
		String resMsg = "";
		
		logger.info("=================== �� ó����� ��ȸ ===================");
		logger.info("idx 	  : {}",firmBean.idx);
		logger.info("bankCd   : {}",firmBean.bankCd);
		logger.info("orgSeqNo : {}",firmBean.data.getString("orgSeqNo"));
		
		headerBean.setIndex(firmBean.idx);
		headerBean.setNewBankCode(firmBean.bankCd);
		
		BankBean configBean = firm.bank.get(headerBean.getNewBankCode());
		headerBean.setIdentificationCode(configBean.trCd);
		headerBean.setCompanyCode(configBean.compCd);
		headerBean.setSpecCode("0600");
		headerBean.setClassificationCode("101");
		headerBean.setFrequency("1");
		headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
		headerBean.setSpecNumber(firmTrxDAO.getBankSeq()) ;
		headerBean.setInquiryDay(firmTrxDAO.getTranDate(firmBean.data.getString("orgSeqNo"))); 
		
		FB0600101Bean fb0600101Bean = new FB0600101Bean();
		fb0600101Bean.setRootSpecNumber(firmBean.data.getString("orgSeqNo")) ;
		headerBean.setTransactionIndex(fb0600101Bean.getTransaction());
		
		logger.info("�ĺ��ڵ� 	: {}",configBean.trCd);
		logger.info("��ü�ڵ� : {}",configBean.compCd);
		logger.info("�����Ϸù�ȣ : {}",firmBean.data.getString("orgSeqNo"));
		
		KsnetComm comm	= new KsnetComm(firm.server);
		FBHeaderBean resHeader = comm.ksnet(headerBean);
		
		FB0600101Bean fb06001001bean = new FB0600101Bean(resHeader.getTransactionIndex());
		
		String resCode = "";
		
		if(!"0000".equals(resHeader.getBankResponseCode())) {
			resCode = resHeader.getBankResponseCode();
		}else {
			resCode = fb06001001bean.getResultCd();
		}
		
		resMsg = FirmDAO.getCodeDesc(headerBean.getNewBankCode(), resCode);
		
		logger.info("TRX RESULT CHECK [{}],[{}]",resCode,resMsg);
		
		logger.info("TRANSFER CHECK UPDATE : {} ",firmTrxDAO.updateResultCheck(resHeader, resCode, resMsg));
		
		logger.info("===================================================");
		
		firmBean.resultCd = resCode;
		firmBean.resultMsg = resMsg;
		
		return firmBean;
	}
	
	/*
	 * ������� ������� ���
	 */
	public FirmBean proc0900400(FirmBean firmBean){
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
		FBHeaderBean headerBean = new FBHeaderBean();
		String resMsg = "";
		
		logger.info("=================== ������� ������� ��� ===================");
		logger.info("idx 	  	: {}",firmBean.idx);
		logger.info("�����ڵ�    	: {}",firmBean.bankCd);
		logger.info("��ü�ڵ�    	: {}",firmBean.data.getString("companyCd"));
		logger.info("�ŷ����� 	  	: {}",firmBean.data.getString("trxType"));
		logger.info("������¹�ȣ 	: {}",firmBean.data.getString("virtualAccount"));
		logger.info("��������ڵ� 	: {}",firmBean.data.getString("withdrawBankCd"));
		logger.info("��ݰ��¹�ȣ 	: {}",firmBean.data.getString("withdrawAccount"));
		
		//������°� ���������϶��� ����
		if("088".equals(firmBean.bankCd)) {
			logger.info("���� 	  	: {}",firmBean.data.getString("customerName"));
			logger.info("�߹�ŷ ��ü�ڵ�	: {}",firmBean.data.getString("firmCompanyCd"));
			logger.info("�޴�����ȣ 	: {}",firmBean.data.getString("phoneNo"));
			logger.info("�Ǹ��ȣ 		: {}",firmBean.data.getString("identity"));
		}
		
		//������°� ���������϶��� ����
		if("011".equals(firmBean.bankCd) ||  "012".equals(firmBean.bankCd)) {
			logger.info("���� 	  	: {}",firmBean.data.getString("customerName"));
			logger.info("������� 		: {}",firmBean.data.getString("regType"));
			logger.info("�Ǹ��ȣ 		: {}",firmBean.data.getString("identity"));
		}
		
		//������°� �ϳ������϶��� ����
		if("081".equals(firmBean.bankCd)) {
			logger.info("���� 	  	: {}",firmBean.data.getString("customerName"));
		}
		
		//������°� ���������϶��� ����
		if("004".equals(firmBean.bankCd)) {
			logger.info("���� 	  	: {}",firmBean.data.getString("customerName"));
		}
				
		logger.info("=========================================================");
		
		headerBean.setIndex(firmBean.idx);
		headerBean.setNewBankCode(firmBean.bankCd);
		headerBean.setIdentificationCode("");
		headerBean.setCompanyCode(firmBean.data.getString("companyCd"));
		headerBean.setSpecCode("0900");
		headerBean.setClassificationCode("400");
		headerBean.setFrequency("1");
		headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
		headerBean.setSpecNumber(firmTrxDAO.getBankSeq()) ;

		FB0900400Bean fb0900400Bean = new FB0900400Bean();
		fb0900400Bean.setTrxType(firmBean.data.getString("trxType"));
		fb0900400Bean.setVirtualAccount(firmBean.data.getString("virtualAccount"));
		fb0900400Bean.setWithdrawBankCd(firmBean.data.getString("withdrawBankCd"));
		fb0900400Bean.setWithdrawAccount(firmBean.data.getString("withdrawAccount"));
		
		
		//������°� ���������϶��� ����
		if("088".equals(firmBean.bankCd)) {
			fb0900400Bean.setCustomerName(firmBean.data.getString("customerName"));
			fb0900400Bean.setFirmCompanyCd(firmBean.data.getString("firmCompanyCd"));
			fb0900400Bean.setPhoneNo(firmBean.data.getString("phoneNo"));
			fb0900400Bean.setIdentity(firmBean.data.getString("identity"));
		}
		
		//������°� ����,����,�ϳ� �����϶��� ����
		if("088".equals(firmBean.bankCd) || "011".equals(firmBean.bankCd) || 
		   "012".equals(firmBean.bankCd) || "081".equals(firmBean.bankCd)) {
			fb0900400Bean.setIdentity(firmBean.data.getString("identity"));
		}
		
		//������°� ���������϶��� ����
		if("011".equals(firmBean.bankCd) || "012".equals(firmBean.bankCd)) {
			fb0900400Bean.setRegType(firmBean.data.getString("regType"));
		}

		headerBean.setTransactionIndex(fb0900400Bean.getTransaction());
		
		KsnetComm comm	= new KsnetComm(firm.server);
		FBHeaderBean resHeader = comm.ksnet(headerBean);
		
		String resCode = resHeader.getBankResponseCode();
		
		resMsg = FirmDAO.getCodeDesc(headerBean.getNewBankCode(), resCode);
		
		logger.info("������� ������� ��� : [{}][{}]",resCode,resMsg);
		logger.info("===================================================");
		
		firmBean.resultCd = resCode;
		firmBean.resultMsg = resMsg;
		
		return firmBean;
	}
	
	public FirmBean processCheck(long idx,FirmBean firmBean,FirmMasterDAO masterDAO){
		int limit = 40;
		int count = 1;
		try{
			while(count < limit){
				Thread.sleep(1000);
				firmBean = masterDAO.checkResult(idx,firmBean);
				if(!firmBean.resultCd.equals("")){
					count = limit;
					break;
				}
			}
		}catch(Exception e){
			
		}
		return firmBean;
	}
	
	
	public FirmBean processCheck(long idx,FirmBean firmBean,FirmTrxDAO trxDAO){
		int limit = 70;
		int count = 1;
		try{
			while(count < limit){
				Thread.sleep(1000);
				firmBean = trxDAO.checkResult(idx,firmBean);
				if(!firmBean.resultCd.equals("")){
					count = limit;
					break;
				}
			}
		}catch(Exception e){
			
		}
		return firmBean;
	}
	
	public FirmBean valid(FirmBean firmBean){
		if(firmBean == null){
			return formatError(firmBean,"�޼��� ���� ����");
		}else{
			if(firmBean.msgType.length() !=7){
				return formatError(firmBean,"msgType ���� :"+firmBean.msgType);
			}
			if(firm.bank.get(firmBean.bankCd) == null){
				return formatError(firmBean,"bankCd ���� , �������� �ʴ� �����ڵ��Դϴ�. "+firmBean.bankCd);
			}
			if(firmBean.userId.equals("")){
				return formatError(firmBean,"userId ���� , userId �� �ʼ����Դϴ�. ");
			}
			firmBean.resultCd = "";
		}
		return firmBean;
	}
	
	
	public FirmBean formatError(FirmBean firmBean,String resultMsg){
		if(firmBean == null){
			firmBean = new FirmBean();
		}
		firmBean.resultCd = "9999";
		firmBean.resultMsg = "�޼��� ���� ����";
		return firmBean;
	}
}
