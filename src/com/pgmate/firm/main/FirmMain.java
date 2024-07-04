package com.pgmate.firm.main;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.pgmate.firm.coocon.CooconBean;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.dozn.DoznBaseBean;
import com.pgmate.firm.dozn.DoznBean;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBaseBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.util.*;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.ksnet.FBHeaderBean;

/**
 * @author Administrator
 *
 */
public class FirmMain{
	private final static Logger logger 	= (Logger) LoggerFactory.getLogger(com.pgmate.firm.main.FirmMain.class);
	private Firm firm = null;
	private KsnetComm comm			= null;
	private HyphenComm hyphenComm 	= null;
	private DoznComm doznComm		= null;
	private CooconComm cooconComm	= null;

	public FirmMain(Firm firm) {
		this.firm = firm;
		comm = new KsnetComm(firm.server);
		hyphenComm = new HyphenComm();
		doznComm = new DoznComm(firm);
		cooconComm = new CooconComm(firm);
	}



	public void wireTransfer(){
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO(firm.bank);
		List<FBHeaderBean> list = firmTrxDAO.select();
		for(int i=0;i<list.size();i++){
			logger.info("TRX TRANSFER : {}/{}",(i+1),list.size());
			FBHeaderBean headerBean = (FBHeaderBean)list.get(i);
			logger.info("TRX STATUS UPDATE : {} : {}",(i+1),firmTrxDAO.updateStatus(headerBean.getIndex(), "I"));
			FBHeaderBean resHeader = comm.ksnet(headerBean);
			logger.info("TRX RESULT {},[{}]",resHeader.getBankResponseCode(),resHeader.getMessage());
			logger.info("TRX RESULT UPDATE : {} : {}",(i+1),firmTrxDAO.update(resHeader));
		}
	}

	public void transMaster(){
		FirmMasterDAO firmMasterDAO = new FirmMasterDAO(firm.bank);
		List<FBHeaderBean> list = firmMasterDAO.select();
		for(int i=0;i<list.size();i++){
			logger.info("MASTER TRANSFER : {}/{}",(i+1),list.size());
			FBHeaderBean headerBean = (FBHeaderBean)list.get(i);
			logger.info("MASTER TRANSFER : {}",headerBean.getSpecCode()+headerBean.getClassificationCode());
			logger.info("MASTER STATUS UPDATE : {} : {}",(i+1),firmMasterDAO.updateStatus(headerBean.getIndex(), "I"));
			FBHeaderBean resHeader = comm.ksnet(headerBean);
			logger.info("MASTER RESULT {},[{}]",resHeader.getBankResponseCode(),resHeader.getMessage());
			logger.info("MASTER RESULT UPDATE : {} : {}",(i+1),firmMasterDAO.update(resHeader));
		}
	}

	/**
	 * PYS : 하이픈에 맞게 새로 생성
	 */
	public void firmTrx() {
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO(firm.bank);
		List<HyphenBean> list = firmTrxDAO.selectbyHyphen();
		for(int i=0;i<list.size();i++){
			logger.info("TRX TRANSFER : {}/{}",(i+1),list.size());
			HyphenBean hyphenBean = (HyphenBean)list.get(i);
			logger.info("TRX STATUS UPDATE : {} : {}",(i+1),firmTrxDAO.updateStatus(hyphenBean.getIndex(), "I"));
			//FBHeaderBean resHeader = comm.ksnet(headerBean);
			String resData = hyphenComm.connect(hyphenBean);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);
				String replayCode = apiRes.get("replyCode").toString();
				String enReplayCode = CommonUtil.nToB(FirmUtil.changeCharset(replayCode,"MS949"));
				hyphenBean.setSuccessYn(apiRes.get("successYn").toString());
				hyphenBean.setReplyCode(enReplayCode);
			} catch (Exception e) {
				hyphenBean.setSuccessYn("N");
				hyphenBean.setReplyCode("XXXX");
			}

			hyphenBean.setResdata(resData);

			logger.info("TRX RESULT {},[{}]",hyphenBean.getSuccessYn(),hyphenBean.getReplyCode());
			logger.info("TRX RESULT UPDATE : {} : {}",(i+1),firmTrxDAO.updatebyHyphen(hyphenBean));
		}
	}

	/**
	 * PYS: 하이픈에 맞게 새로 생성
	 */
	public void HyphenfirmMaster() {
		FirmMasterDAO firmMasterDAO = new FirmMasterDAO(firm.bank);
		List<HyphenBean> list = firmMasterDAO.selectbyHyphen();
		for(int i=0;i<list.size();i++){
			logger.info("MASTER TRANSFER : {}/{}",(i+1),list.size());
			HyphenBean hyphenBean = (HyphenBean)list.get(i);
			logger.info("MASTER STATUS UPDATE : {} : {}",(i+1),firmMasterDAO.updateStatus(hyphenBean.getIndex(), "I"));
			String resData = hyphenComm.connect(hyphenBean);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);

				//230104_PYS : FCS용
				if(hyphenBean.getSendurl().equals("account")) {
					hyphenBean.setReply(apiRes.get("reply").toString());
					hyphenBean.setReply_msg(apiRes.get("reply_msg").toString());

					hyphenBean.setSuccessYn(apiRes.get("reply_msg").toString());
					hyphenBean.setReplyCode(apiRes.get("reply").toString());
				} else {
					hyphenBean.setSuccessYn(apiRes.get("successYn").toString());
					hyphenBean.setReplyCode(apiRes.get("replyCode").toString());
					String replayCode = apiRes.get("replyCode").toString();

					if(!hyphenBean.getReplyCode().equals("0000")) {
						String resultMsg = "";

						if(hyphenBean.getReplyCode().startsWith("KS")) {
							resultMsg = FirmDAO.getCodeDesc("ERR", hyphenBean.getReplyCode());
						} else {
							resultMsg = FirmDAO.getCodeDesc("039", hyphenBean.getReplyCode());
						}

						hyphenBean.setSuccessYn(FirmUtil.changeCharset(resultMsg, "UTF-8"));

					}
				}

			} catch (ParseException e) {
				logger.error("Hyphen Firm Master Error : [{}] [{}]", resData, e.getMessage());
				hyphenBean.setSuccessYn("X");
				hyphenBean.setReplyCode("XXXX");
			}

			hyphenBean.setResdata(resData);

			logger.info("MASTER RESULT {},[{}]",hyphenBean.getReplyCode(), hyphenBean.getSuccessYn());
			logger.info("MASTER RESULT UPDATE : {} : {}",(i+1),firmMasterDAO.updatebyHyphen(hyphenBean));
		}
	}

	public void HyphenfirmArs() {
		FirmMasterDAO firmMasterDAO = new FirmMasterDAO(firm.bank);
		List<HyphenBean> list = firmMasterDAO.arsByHyphen();
		for(int i=0;i<list.size();i++){
			logger.info("ARS TRANSFER : {}/{}",(i+1),list.size());
			HyphenBean hyphenBean = (HyphenBean)list.get(i);
			logger.info("ARS STATUS UPDATE : {} : {}",(i+1),firmMasterDAO.updateArsStatus(hyphenBean.getIndex(), "I"));
			String resData = hyphenComm.connect(hyphenBean);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);

				if(hyphenBean.getSendurl().equals("ksnet/auth/ars")) {
					hyphenBean.setReply(apiRes.get("reply").toString());
					hyphenBean.setReply_msg(apiRes.get("reply_msg").toString());

					hyphenBean.setSuccessYn(apiRes.get("reply_msg").toString());
					hyphenBean.setReplyCode(apiRes.get("reply").toString());
				}else {
					hyphenBean.setSuccessYn(apiRes.get("successYn").toString());
					hyphenBean.setReplyCode(apiRes.get("replyCode").toString());
					String replayCode = apiRes.get("replyCode").toString();

					if(!hyphenBean.getReplyCode().equals("0000")) {
						String resultMsg = "";

						if(hyphenBean.getReplyCode().startsWith("KS")) {
							resultMsg = FirmDAO.getCodeDesc("ERR", hyphenBean.getReplyCode());
						} else {
							resultMsg = FirmDAO.getCodeDesc("039", hyphenBean.getReplyCode());
						}

						hyphenBean.setSuccessYn(FirmUtil.changeCharset(resultMsg, "UTF-8"));

					}
				}

			} catch (ParseException e) {
				logger.error("Hyphen Firm ARS Error : [{}] [{}]", resData, e.getMessage());
				hyphenBean.setSuccessYn("X");
				hyphenBean.setReplyCode("XXXX");
			}

			hyphenBean.setResdata(resData);

			logger.info("ARS RESULT {},[{}]",hyphenBean.getReplyCode(), hyphenBean.getSuccessYn());
			logger.info("ARS RESULT UPDATE : {} : {}",(i+1),firmMasterDAO.updateArsByHyphen(hyphenBean));
		}
	}

	/**
	 * PYS : 더즌용 펌 마스터 전송
	 */
	public void DoznfirmMaster() {
		FirmMasterDAO firmMasterDAO = new FirmMasterDAO(firm.bank);
		List<DoznBean> list = firmMasterDAO.selectByDozn();
		for(int i=0;i<list.size();i++){
			logger.info("DOZN MASTER TRANSFER : {}/{}",(i+1),list.size());
			DoznBean doznBean = list.get(i);
			logger.info("DOZN MASTER STATUS UPDATE : {} : {}",(i+1),firmMasterDAO.updateStatus(doznBean.getIndex(), "I"));

			//출금계좌등록 로직처리
			String resData = "";

			if(doznBean.getUrl().equals("api/v1/kyc")) {
				resData = doznComm.connectKyc(doznBean);
			} else {
				resData = doznComm.connect(doznBean);
			}

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);

				//출금계좌등록 로직처리
				if(doznBean.getUrl().equals("api/v1/kyc")) {
					if(doznBean.getStatus().equals("200")) {
						doznBean.setResultCode("0000");
						doznBean.setResultMsg("정상");
					} else {
						doznBean.setResultCode(doznBean.getStatus());
						doznBean.setResultMsg(apiRes.get("message").toString());
					}

				} else {
					doznBean.setStatus(apiRes.get("status").toString());
					if(doznBean.getStatus().equals("200")) {
						doznBean.setResultCode("0000");
						doznBean.setResultMsg("정상");
					} else {
						doznBean.setResultCode(apiRes.get("error_code").toString());
						doznBean.setResultMsg(apiRes.get("error_message").toString());
					}
				}




				doznBean.setResData(resData);

			} catch (ParseException e) {
				logger.error("DOZN MASTER Error : [{}] [{}]", resData, e.getMessage());
				doznBean.setResultCode("XXXX");
				doznBean.setResultMsg("통신실패");
				doznBean.setResData("");
			}



			logger.info("DOZN MASTER RESULT {},[{}]", doznBean.getStatus(), doznBean.getResData());
			logger.info("DOZN MASTER RESULT UPDATE : {} : {}",(i+1),firmMasterDAO.updateByDozn(doznBean));
		}
	}

	/**
	 * PYS : 더즌용 이체전문 전송
	 */
	public void DoznFirmTrx() {
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO(firm.bank);
		List<DoznBean> list = firmTrxDAO.selectByDozn();
		for(int i=0;i<list.size();i++){
			logger.info("DOZN TRX TRANSFER : {}/{}",(i+1),list.size());
			DoznBean doznBean = list.get(i);
			logger.info("DOZN TRX STATUS UPDATE : {} : {}",(i+1),firmTrxDAO.updateStatus(doznBean.getIndex(), "I"));
			String resData = doznComm.connect(doznBean);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);

				doznBean.setStatus(apiRes.get("status").toString());
				if(doznBean.getStatus().equals("200")) {
					doznBean.setVanTrxId(apiRes.get("natv_tr_no").toString()); //더즌거래번호
					doznBean.setResultCode("0000");
					doznBean.setResultMsg("정상");
				} else {
					doznBean.setResultCode(apiRes.get("error_code").toString());
					doznBean.setResultMsg(apiRes.get("error_message").toString());
				}
				doznBean.setResData(resData);

			} catch (ParseException e) {
				logger.error("DOZN TRX Error : [{}] [{}]", resData, e.getMessage());
				doznBean.setResultCode("XXXX");
				doznBean.setResultMsg("통신실패");
				doznBean.setResData("");
			}



			logger.info("DOZN TRX RESULT {},[{}]", doznBean.getStatus(), doznBean.getResData());
			logger.info("DOZN TRX RESULT UPDATE : {} : {}",(i+1),firmTrxDAO.updateByDozn(doznBean));
		}
	}

	public void CooconFirmMaster() {
		FirmMasterDAO firmMasterDAO = new FirmMasterDAO(firm.bank);
		List<CooconBean> list = firmMasterDAO.selectByCoocon();
		for(int i=0;i<list.size();i++){
			logger.info("COOCON MASTER TRANSFER : {}/{}",(i+1),list.size());
			CooconBean cooconBean = list.get(i);
			logger.info("COOCON MASTER STATUS UPDATE : {} : {}",(i+1),firmMasterDAO.updateStatus(cooconBean.getIndex(), "I"));

			//출금계좌등록 로직처리
			String resData = "";

//			if(cooconBean.getReqUrl().equals("kyc")) {
//				resData = cooconComm.connectKyc(cooconBean);
//			} else {
//				resData = cooconComm.connect(cooconBean);
//			}

			resData = cooconComm.connect(cooconBean);

			logger.info("RES_DATA : [{}]", resData);
			cooconBean.setResData(resData);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);

				//출금계좌등록 로직처리
				if(cooconBean.getReqUrl().equals("kyc")) {
					String resultCd = apiRes.get("RESP_CD").toString();
					String resultMsg = apiRes.get("RESP_MSG").toString();

					if(resultCd.equals("0000")) {
						cooconBean.setResultCode("0000");
					} else {
						cooconBean.setResultCode(resultCd);
					}

					cooconBean.setResultMsg(resultMsg);
				} else {
					String resultCd = apiRes.get("RSLT_CD").toString();
					String resultMsg = apiRes.get("RSLT_MSG").toString();

					if(resultCd.equals("000")) {
						cooconBean.setResultCode("0000");
					} else {
						cooconBean.setResultCode(resultCd);
					}

					cooconBean.setResultMsg(resultMsg);
				}
			} catch (ParseException e) {
				logger.error("COOCON Firm Master Error : [{}] [{}]", resData, e.getMessage());
				cooconBean.setResultCode("XXXX");
				cooconBean.setResultMsg("통신실패");
				cooconBean.setResData("");
			}



			logger.info("COOCON MASTER RESULT {},[{}]", cooconBean.getResultMsg(), cooconBean.getResData());
			logger.info("COOCON MASTER RESULT UPDATE : {} : {}",(i+1),firmMasterDAO.updateByCoocon(cooconBean));
		}

	}

	/**
	 * PYS : 쿠콘용 이체전문 전송
	 */
	public void CooconFirmTrx() {
		FirmTrxDAO firmTrxDAO = new FirmTrxDAO(firm.bank);
		List<CooconBean> list = firmTrxDAO.selectByCoocon();
		for(int i=0;i<list.size();i++){
			logger.info("COOCON TRX TRANSFER : {}/{}",(i+1),list.size());
			CooconBean cooconBean = list.get(i);
			logger.info("COOCON TRX STATUS UPDATE : {} : {}",(i+1),firmTrxDAO.updateStatus(cooconBean.getIndex(), "I"));
			String resData = cooconComm.connect(cooconBean);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);

				String resultCd = apiRes.get("RSLT_CD").toString();
				String resultMsg = apiRes.get("RSLT_MSG").toString();

				if(resultCd.equals("000")) {
					cooconBean.setResultCode("0000");
				} else {
					cooconBean.setResultCode(resultCd);
				}

				cooconBean.setResultMsg(resultMsg);

			} catch (ParseException e) {
				logger.error("DOZN TRX Error : [{}] [{}]", resData, e.getMessage());
				cooconBean.setResultCode("XXXX");
				cooconBean.setResultMsg("통신실패");
				cooconBean.setResData("");
			}

			logger.info("DOZN TRX RESULT {},[{}]", cooconBean.getResultMsg(), cooconBean.getResData());
			logger.info("DOZN TRX RESULT UPDATE : {} : {}",(i+1),firmTrxDAO.updateByCoocon(cooconBean));
		}
	}

}
