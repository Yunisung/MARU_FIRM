package com.pgmate.firm.main;

import java.util.List;

import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBaseBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.util.HyphenComm;
import com.pgmate.lib.util.gson.GsonUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.firm.util.KsnetComm;

/**
 * @author Administrator
 *
 */
public class FirmMain{
	private final static Logger logger 	= (Logger) LoggerFactory.getLogger(com.pgmate.firm.main.FirmMain.class);
	private Firm firm = null;
	private KsnetComm comm			= null;
	private HyphenComm hyphenComm 	= null;
	
	public FirmMain(Firm firm) {
		this.firm = firm;
		comm = new KsnetComm(firm.server);
		hyphenComm = new HyphenComm(firm.server);
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
	 * PYS: 하이픈에 맞게 새로 생성
	 */
	public void firmMaster() {
		FirmMasterDAO firmMasterDAO = new FirmMasterDAO(firm.bank);
		List<HyphenBean> list = firmMasterDAO.selectbyHyphen();
		for(int i=0;i<list.size();i++){
			logger.info("MASTER TRANSFER : {}/{}",(i+1),list.size());
			HyphenBean hyphenBean = (HyphenBean)list.get(i);
			//logger.info("MASTER TRANSFER : {}",headerBean.getSpecCode()+headerBean.getClassificationCode());
			logger.info("MASTER STATUS UPDATE : {} : {}",(i+1),firmMasterDAO.updateStatus(hyphenBean.getIndex(), "I"));
//			FBHeaderBean resHeader = comm.ksnet(headerBean);
			String resData = hyphenComm.connect(hyphenBean);

			JSONObject apiRes = new JSONObject();
			JSONParser jsonParser = new JSONParser();
			try {
				apiRes = (JSONObject) jsonParser.parse(resData);
				hyphenBean.setSuccessYn(apiRes.get("successYn").toString());
				hyphenBean.setReplyCode(apiRes.get("replyCode").toString());
			} catch (Exception e) {
				hyphenBean.setSuccessYn("N");
				hyphenBean.setReplyCode("XXXX");
			}

			hyphenBean.setResdata(resData);

			//logger.info("MASTER RESULT {},[{}]",resHeader.getBankResponseCode(),resHeader.getMessage());
			logger.info("MASTER RESULT UPDATE : {} : {}",(i+1),firmMasterDAO.updatebyHyphen(hyphenBean));
		}
	}


}
