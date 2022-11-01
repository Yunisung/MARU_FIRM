package com.pgmate.firm.server;

import com.google.gson.Gson;
import com.pgmate.firm.hyphen.*;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.firm.util.HyphenComm;
import com.pgmate.lib.util.map.SharedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class InterProcess implements java.io.Serializable{

	private Logger logger = LoggerFactory.getLogger( getClass() );
	private Firm firm = null;
	private HyphenComm hyphenComm = null;
	public InterProcess(Firm firm) {
		this.firm = firm;
		this.hyphenComm = new HyphenComm(firm.server);

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
				//잔액조회
				firmBean = proc0600300(firmBean);
//				firmBean = procBalance(firmBean);
			}else if(firmBean.msgType.startsWith("0600400")){
				//성명조회
				firmBean = proc0600400(firmBean);
//				firmBean = procHolder(firmBean);
			}else if(firmBean.msgType.startsWith("0700100")){
				//집계
				firmBean = proc0700100(firmBean);
			}else if(firmBean.msgType.startsWith("0100100")){
				//이체
				firmBean = proc0100100(firmBean);
//				firmBean = procDeposit(firmBean);
			}else if(firmBean.msgType.startsWith("0600101")){
				//처리결과조회
				firmBean = proc0600101(firmBean);
//				firmBean = procTransfer(firmBean);
			}else if(firmBean.msgType.startsWith("0900400")){
				//가상계좌 출금정보 등록
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
	 * 잔액조회
	 */
	public FirmBean proc0600300(FirmBean firmBean){
		logger.info("=================== 잔액조회 ===================");

		try {
			FirmMasterDAO masterDAO = new FirmMasterDAO();
			FB0600300Bean fbBean = new FB0600300Bean();
			fbBean.setAccount(firmBean.mAccnt);
			//fbBean.setAccount(firm.bank.get(firmBean.bankCd).account);

			long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, fbBean.getTransaction());
			firmBean = processCheck(idx,firmBean,masterDAO);
			if(firmBean.resultCd.equals("0000")){
				fbBean = new FB0600300Bean(firmBean.data.getString("resData"));
				String amount = fbBean.getSign()+fbBean.getCurrentAmount();
				firmBean.data.put("amount", CommonUtil.parseLong(amount.trim()));
				masterDAO.insertBalance(firmBean.bankCd, fbBean.getAccount(), amount.trim());
			}
		}catch (Exception e) {
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="잔액조회 오류";

			e.printStackTrace();
			logger.error("잔액조회 Error : [{}]", e.getMessage());
		}

		logger.info("===================================================");

		return firmBean;
	}

	/*
	잔액조회 하이픈으로 만들기
	 */
	public FirmBean procBalance(FirmBean firmBean) {
		logger.info("=================== 잔액조회 ===================");

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
				String amount = balanceBean.getSign()+balanceBean.getTotalBalance();
				logger.info("잔액조회 성공");
				logger.info("[{}] 의 잔액 : [{}]", firmBean.bankCd, amount);
				firmBean.data.put("amount", CommonUtil.parseLong(amount.trim()));
				masterDAO.insertBalance(configBean.bankCd, configBean.account, amount.trim());
			}
		}catch (Exception e) {
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="잔액조회 오류";

			e.printStackTrace();
			logger.error("잔액조회 Error : [{}]", e.getMessage());
		}

		logger.info("===================================================");

		return firmBean;
	}


	/*
	 * 성명조회 은행코드를 099 를 사용하면 KSNET 그외는 각 은행
	 * data.bankCd, data.account , data.socialNumber, data.socialCheck
	 */
	public FirmBean proc0600400(FirmBean firmBean){
		logger.info("=================== 예금주조회 ========================");
		BankBean configBean = firm.bank.get(firmBean.bankCd);

		FirmMasterDAO masterDAO = new FirmMasterDAO();
		FB0600400Bean fbBean = new FB0600400Bean();

		fbBean.setTransactionDay(CommonUtil.getCurrentDate("MMdd"));
		fbBean.setNewBankCode(firmBean.data.getString("bankCd"));
		fbBean.setAccount(firmBean.data.getString("account"));
		fbBean.setSocialNumber(firmBean.data.getString("socialNumber"));
		fbBean.setSocialCheck(firmBean.data.getString("socialCheck"));
		fbBean.setMAccount(configBean.account);
		fbBean.setName(firmBean.data.getString("holder"));

		//PYS : 이미 조회한 계좌는 테이블에서 조회
		if(firmBean.bankCd.equals("099") && firmBean.data.isNullOrSpace("socialCheck")) {
			String holder = masterDAO.selectAccnt(firmBean.data.getString("bankCd"), firmBean.data.getString("account"));
			logger.info("INTER ACCNT CHECK: {}",holder);
			if(!CommonUtil.isNullOrSpace(holder)) {
				firmBean.data.put("name", holder);
				firmBean.resultCd ="0000";
				firmBean.resultMsg = "정상조회";

				return firmBean;
			}
		}

		logger.info("INSERT MASTSER TABLE");

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
	 * 성명조회
	 */
	public FirmBean procHolder(FirmBean firmBean) {
		logger.info("======================== Holder ========================");

		try {
			FirmMasterDAO masterDAO = new FirmMasterDAO();
			BankBean configBean = firm.bank.get(firmBean.bankCd);

			HolderBean holderBean = new HolderBean();
			holderBean.setCompCode(configBean.compCd);
			//PYS : 이름조회는 099 고정
			holderBean.setBankCode("099");
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

			String holderCode = firmBean.data.getString("bankCd");
			String holderAccount = firmBean.data.getString("account");

			String jsonParams = new Gson().toJson(hyphenBean);

			long idx = masterDAO.setMasterbyHyphen(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, hyphenBean.getSendurl(), jsonParams);
			firmBean = processCheck(idx,firmBean,masterDAO);
			if(firmBean.resultCd.equals("0000")){
				String resJson = firmBean.data.getString("resData");
				holderBean = (HolderBean) GsonUtil.fromJson(resJson, HolderBean.class);
				String name = holderBean.getAccountName();
				firmBean.data.put("accountName", CommonUtil.parseLong(name.trim()));
				masterDAO.insertAccnt(holderCode, holderAccount, name.trim());
			}
		}catch (Exception e) {

			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="성명조회 오류";

			e.printStackTrace();
			logger.error("성명조회Error : [{}]", e.getMessage());
		}

		logger.info("===================================================");

		return firmBean;
	}

	/*
	 * 집계
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
		// 이체가능시간 외 데이터 등록 막기
		Firm firm = FirmLoader.getConfig();
		long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));

		if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="이체 가능 시간 아님";
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
			firmBean.resultMsg ="이체데이터 등록실패";
		}else{
			firmBean = processCheck(idx,firmBean,trxDAO);
		}
		return firmBean;
	}

	/**
	 * 송금 하이픈으로 만들기
	 * @param firmBean
	 * @return
	 */
	public FirmBean procDeposit(FirmBean firmBean){
		// 이체가능시간 외 데이터 등록 막기
		Firm firm = FirmLoader.getConfig();
		BankBean bankBean = firm.bank.get(firmBean.bankCd);

		long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));

		if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="이체 가능 시간 아님";
			return firmBean;
		}

		FirmTrxDAO trxDAO = new FirmTrxDAO(firm.bank);

		//String sign = 	WooriSign.getSign(firmBean.data.getString("recvAccount"), CommonUtil.getAmountFormat(firmBean.data.getString("amount")), firmBean.data.getString("recvBankCd"), firm.bank.get(firmBean.bankCd).account);

		String sender = firmBean.data.getString("sender");
		if("".equals(sender)) {
			sender = "(주)부국위너스";
		}

		logger.info("account : {}",bankBean.account);
		logger.info("recvBankCd : {}",firmBean.data.getString("recvBankCd"));
		logger.info("recvAccount : {}",firmBean.data.getString("recvAccount"));
		logger.info("sender : {}",sender);
		logger.info("amount : {}",CommonUtil.getAmountFormat(firmBean.data.getString("amount")));
		logger.info("procType : {}",firmBean.data.getString("procType"));

		String seqNo = trxDAO.getBankSeq();

		long idx = trxDAO.insertTrx(firmBean.bankCd, firmBean.data.getLong("amount"), firmBean.data.getString("recvBankCd"), firmBean.data.getString("recvAccount"), sender, firmBean.data.getString("recordInfo"), firmBean.data.getString("procType"), seqNo);
		if(idx == 0){
			firmBean.resultCd ="XXXX";
			firmBean.resultMsg ="이체데이터 등록실패";
		}else{
			//데몬 안쓰고 직접 통신하기

			//상태값 I로 update
			logger.info("TRX STATUS UPDATE : {}",trxDAO.updateStatus(idx, "I"));

			//통신하기 위해 클래스화
			DepositBean depositBean = new DepositBean();
			depositBean.setCompCode(bankBean.compCd);
			depositBean.setBankCode(bankBean.bankCd);

			depositBean.setSeqNo(CommonUtil.nToB(seqNo));
			depositBean.setOutAccount(bankBean.account);
			depositBean.setAmount(firmBean.data.getLong("amount"));
			depositBean.setInBankCode(firmBean.data.getString("recvBankCd"));
			depositBean.setInAccount(firmBean.data.getString("recvAccount"));
			depositBean.setInPrintContent(sender);

			HyphenBean hyphenBean = new HyphenBean();
			hyphenBean.setIndex(idx);
			hyphenBean.setKscode(bankBean.kscode);
			hyphenBean.setEkey(bankBean.ekey);
			hyphenBean.setMsalt(bankBean.msalt);
			hyphenBean.setSendurl("rfb/retail/deposit");
			hyphenBean.setReqdata(depositBean);

			//통신
			String resData = hyphenComm.connect(hyphenBean);

			//통신결과 클래스화
			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);
				String replayCode = apiRes.get("replyCode").toString();

				hyphenBean.setReplyCode(replayCode);
				hyphenBean.setSuccessYn(apiRes.get("successYn").toString());

				//오류메세지 세팅
				if(!replayCode.equals("0000")) {
					if(replayCode.startsWith("KS")) {
						hyphenBean.setSuccessYn(FirmDAO.getCodeDesc("ERR", replayCode));
					} else {
						hyphenBean.setSuccessYn(FirmDAO.getCodeDesc(bankBean.bankCd, replayCode));
					}
				}


				String balance = apiRes.get("sign").toString() + apiRes.get("balance").toString();
				String fee = apiRes.get("svcCharge").toString();
				String transferTime = apiRes.get("tradeTime").toString();

				depositBean.setBalance(balance.trim());
				depositBean.setSvcCharge(fee.trim());
				depositBean.setTradeTime(transferTime);

			} catch (Exception e) {
				hyphenBean.setReplyCode("XXXX");
				hyphenBean.setSuccessYn("X");
			}
			hyphenBean.setResdata(resData);
			logger.info("TRX RESULT {},[{}]",hyphenBean.getSuccessYn(),hyphenBean.getReplyCode());

			//통신결과 update
			logger.info("TRX RESULT UPDATE : {}",trxDAO.updatebyHyphen(hyphenBean));

			//firmbean 채우기
			firmBean.resultCd = hyphenBean.getReplyCode();
			firmBean.resultMsg =  FirmUtil.changeCharset(hyphenBean.getSuccessYn(), "UTF-8");
			firmBean.idx = idx;
			if(firmBean.data == null){
				firmBean.data = new SharedMap<String,Object>();
			}
			firmBean.data.put("recvHolder", sender);
			firmBean.data.put("balance", depositBean.getBalance());
			firmBean.data.put("fee", depositBean.getSvcCharge());
			firmBean.data.put("transferTime", depositBean.getTradeTime());

			if(firmBean.resultCd.equals("0000")){
				String amount = firmBean.data.getString("balance");
				logger.info("잔액 : [{}]원", amount);
				new FirmMasterDAO().insertBalance(firm.bank.get(firmBean.bankCd).bankCd, firm.bank.get(firmBean.bankCd).account, amount.trim());
			}

		}
		return firmBean;
	}


	/**
	 * PYS : 이체결과조회 (하이픈)
	 * @param firmBean
	 * @return
	 */
	public FirmBean procTransfer(FirmBean firmBean){
		logger.info("=================== 펌 처리결과 조회 ===================");

		FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
		BankBean configBean = firm.bank.get(firmBean.bankCd);

		logger.info("idx 	  : {}",firmBean.idx);
		logger.info("bankCd   : {}",firmBean.bankCd);
		logger.info("orgSeqNo : {}",firmBean.data.getString("orgSeqNo"));

		TransferBean transferBean = new TransferBean();
		transferBean.setCompCode(configBean.compCd);
		transferBean.setBankCode(configBean.bankCd);
		transferBean.setOriSeqNo(firmBean.data.getString("orgSeqNo"));
		transferBean.setSeqNo(firmTrxDAO.getBankSeq());

		HyphenBean hyphenBean = new HyphenBean();
		hyphenBean.setKscode(configBean.kscode);
		hyphenBean.setEkey(configBean.ekey);
		hyphenBean.setMsalt(configBean.msalt);
		hyphenBean.setSendurl("rfb/retail/inquiry/transfer");
		hyphenBean.setReqdata(transferBean);

		HyphenComm hyphenComm = new HyphenComm(firm.server);
		String resData = hyphenComm.connect(hyphenBean);

		JSONObject apiRes = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		try {
			apiRes = (JSONObject) jsonParser.parse(resData);
			hyphenBean.setSuccessYn(apiRes.get("successYn").toString());
			hyphenBean.setReplyCode(apiRes.get("replyCode").toString());

			transferBean.setOutAccountNo(apiRes.get("outAccountNo").toString());
			transferBean.setInAccountNo(apiRes.get("inAccountNo").toString());
			transferBean.setAmount(apiRes.get("amount").toString());
			transferBean.setSvcCharge(apiRes.get("svcCharge").toString());
			transferBean.setTradeTime(apiRes.get("tradeTime").toString());
			transferBean.setResultCode(apiRes.get("resultCode").toString());
			transferBean.setProcBankCode(apiRes.get("procBankCode").toString());
			transferBean.setPayerNo(apiRes.get("payerNo").toString());
		} catch (Exception e) {
			hyphenBean.setSuccessYn("N");
			hyphenBean.setReplyCode("XXXX");
		}

		hyphenBean.setResdata(resData);

		String resCode = "";
		String resMsg = "";

		if(!transferBean.getResultCode().equals("") && !transferBean.getResultCode().equals("0000")) {
			resCode = transferBean.getResultCode();
			resMsg = FirmDAO.getCodeDesc(configBean.bankCd, resCode);
		} else {
			resCode = hyphenBean.getReplyCode();
			resMsg = FirmDAO.getCodeDesc("ERR", resCode);
		}

		logger.info("TRX RESULT CHECK [{}],[{}]",resCode,resMsg);
		logger.info("TRANSFER CHECK UPDATE : {} ",firmTrxDAO.updateResultCheckbyHyphen(hyphenBean, resCode, resMsg));
		logger.info("===================================================");

		firmBean.resultCd = resCode;
		firmBean.resultMsg = resMsg;

		return firmBean;
	}


	/*
	 * 처리결과 조회
	 */
	public FirmBean proc0600101(FirmBean firmBean){
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
		FBHeaderBean headerBean = new FBHeaderBean();
		String resMsg = "";

		logger.info("=================== 펌 처리결과 조회 ===================");
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

		logger.info("식별코드 	: {}",configBean.trCd);
		logger.info("업체코드 : {}",configBean.compCd);
		logger.info("전문일련번호 : {}",firmBean.data.getString("orgSeqNo"));

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
	 * 가상계좌 출금정보 등록
	 */
	public FirmBean proc0900400(FirmBean firmBean){
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
		FBHeaderBean headerBean = new FBHeaderBean();
		String resMsg = "";

		logger.info("=================== 가상계좌 출금정보 등록 ===================");
		logger.info("idx 	  	: {}",firmBean.idx);
		logger.info("은행코드    	: {}",firmBean.bankCd);
		logger.info("업체코드    	: {}",firmBean.data.getString("companyCd"));
		logger.info("거래구분 	  	: {}",firmBean.data.getString("trxType"));
		logger.info("가상계좌번호 	: {}",firmBean.data.getString("virtualAccount"));
		logger.info("출금은행코드 	: {}",firmBean.data.getString("withdrawBankCd"));
		logger.info("출금계좌번호 	: {}",firmBean.data.getString("withdrawAccount"));

		//가상계좌가 신한은행일때만 세팅
		if("088".equals(firmBean.bankCd)) {
			logger.info("고객명 	  	: {}",firmBean.data.getString("customerName"));
			logger.info("펌뱅킹 업체코드	: {}",firmBean.data.getString("firmCompanyCd"));
			logger.info("휴대폰번호 	: {}",firmBean.data.getString("phoneNo"));
			logger.info("실명번호 		: {}",firmBean.data.getString("identity"));
		}

		//가상계좌가 농협은행일때만 세팅
		if("011".equals(firmBean.bankCd) ||  "012".equals(firmBean.bankCd)) {
			logger.info("고객명 	  	: {}",firmBean.data.getString("customerName"));
			logger.info("등록유형 		: {}",firmBean.data.getString("regType"));
			logger.info("실명번호 		: {}",firmBean.data.getString("identity"));
		}

		//가상계좌가 하나은행일때만 세팅
		if("081".equals(firmBean.bankCd)) {
			logger.info("고객명 	  	: {}",firmBean.data.getString("customerName"));
		}

		//가상계좌가 국민은행일때만 세팅
		if("004".equals(firmBean.bankCd)) {
			logger.info("고객명 	  	: {}",firmBean.data.getString("customerName"));
		}

		//가상계좌가 케이뱅크일때 세팅
		if("089".equals(firmBean.bankCd)) {
			logger.info("고객명 	  	: {}",firmBean.data.getString("customerName"));
		}

		logger.info("=========================================================");

		headerBean.setIndex(firmBean.idx);
		headerBean.setNewBankCode(firmBean.bankCd);

		BankBean configBean = firm.bank.get(headerBean.getNewBankCode());
		headerBean.setIdentificationCode(configBean.trCd);

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


		//가상계좌가 신한은행일때만 세팅
		if("088".equals(firmBean.bankCd)) {
			fb0900400Bean.setCustomerName(firmBean.data.getString("customerName"));
			fb0900400Bean.setFirmCompanyCd(firmBean.data.getString("firmCompanyCd"));
			fb0900400Bean.setPhoneNo(firmBean.data.getString("phoneNo"));
			fb0900400Bean.setIdentity(firmBean.data.getString("identity"));
		}

		//가상계좌가 신한,농협,하나 은행일때만 세팅
		if("088".equals(firmBean.bankCd) || "011".equals(firmBean.bankCd) ||
				"012".equals(firmBean.bankCd) || "081".equals(firmBean.bankCd)) {
			fb0900400Bean.setIdentity(firmBean.data.getString("identity"));
		}

		//가상계좌가 농협은행일때만 세팅
		if("011".equals(firmBean.bankCd) || "012".equals(firmBean.bankCd)) {
			fb0900400Bean.setRegType(firmBean.data.getString("regType"));
		}

		if("089".equals(firmBean.bankCd)) {
			fb0900400Bean.setCustomerName(firmBean.data.getString("customerName"));
		}

		headerBean.setTransactionIndex(fb0900400Bean.getTransaction());

		KsnetComm comm	= new KsnetComm(firm.server);
		FBHeaderBean resHeader = comm.ksnet(headerBean);

		String resCode = resHeader.getBankResponseCode();

		resMsg = FirmDAO.getCodeDesc(headerBean.getNewBankCode(), resCode);

		logger.info("가상계좌 출금정보 등록 : [{}][{}]",resCode,resMsg);
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
			return formatError(firmBean,"메세지 포맷 오류");
		}else{
			if(firmBean.msgType.length() !=7){
				return formatError(firmBean,"msgType 오류 :"+firmBean.msgType);
			}
			if(firm.bank.get(firmBean.bankCd) == null){
				return formatError(firmBean,"bankCd 오류 , 지원하지 않는 은행코드입니다. "+firmBean.bankCd);
			}
			if(firmBean.userId.equals("")){
				return formatError(firmBean,"userId 오류 , userId 는 필수값입니다. ");
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
		firmBean.resultMsg = "메세지 포맷 오류";
		return firmBean;
	}
}
