package com.pgmate.firm.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.lib.util.map.SharedMap;

/**
 * @author Administrator
 */
public class AutoClose {

	private final static Logger logger 	= (Logger) LoggerFactory.getLogger(com.pgmate.firm.main.AutoClose.class);
	
	public AutoClose() {
		// TODO Auto-generated constructor stub
	}
	
	public void execute(){
		SharedMap<String,BankBean> firm = FirmLoader.getConfig().bank;
		
		for(String bankCd : firm.keySet()){
			if(!bankCd.equals("099")){
				String seqNo = FirmDAO.getSeqNO();
				logger.info("autoclose : bank:{} ,seq:{},insert : {}", bankCd,seqNo,new FirmMasterDAO(firm).insert0800300(bankCd,seqNo));
			}
		}
	}
	
	public static void main(String[] args){
		new AutoClose().execute();
	}

}
