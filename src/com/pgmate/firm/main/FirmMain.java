package com.pgmate.firm.main;

import java.util.List;

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
	
	public FirmMain(Firm firm) {
		this.firm = firm;
		comm = new KsnetComm(firm.server);
		
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
	


}
