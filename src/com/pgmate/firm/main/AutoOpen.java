package com.pgmate.firm.main;

import com.google.gson.Gson;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.dozn.DoznAccountOpenBean;
import com.pgmate.firm.dozn.DoznBalanceCheckBean;
import com.pgmate.firm.dozn.DoznBaseBean;
import com.pgmate.lib.util.lang.CommonUtil;
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
public class AutoOpen {

	private final static Logger logger 	= (Logger) LoggerFactory.getLogger(com.pgmate.firm.main.AutoOpen.class);
	
	public AutoOpen() {
		// TODO Auto-generated constructor stub
	}
	
	public void execute(){
		SharedMap<String,BankBean> firm = FirmLoader.getConfig().bank;

		for(String bankCd : firm.keySet()){
			if(bankCd.equals("039")){
				String seqNo = FirmDAO.getSeqNO();
				logger.info("autoopen : bank:{} ,seq:{},insert : {}", bankCd,seqNo,new FirmMasterDAO(firm).insert0800100(bankCd,seqNo));
			} else if(bankCd.equals("034")) {
				FirmDAO.resetSeqNo("FIRM_034");
				String seqNo = FirmDAO.getSeqNO("FIRM_034");
				String crypto = firm.get(bankCd).crypto;

				//BEAN ¼¼ÆÃ
				DoznAccountOpenBean bean = new DoznAccountOpenBean();
				bean.setApiKey(firm.get(bankCd).api_key);
				bean.setOrgCode(firm.get(bankCd).org_code);
				bean.setTelegram_no(CommonUtil.parseLong(seqNo));
				bean.setDrw_bank_code(bankCd);

				String sendUrl = "api/rt/v1/account/open";
				if(crypto.equals("Y")) {
					sendUrl = "crypto/rt/v1/account/open";
				}

				String jsonParams = new Gson().toJson(bean);

				long idx = new FirmMasterDAO().setMasterbyDozn("0800", "100", bankCd, seqNo, sendUrl, jsonParams);

				logger.info("autoopen : bank:{} ,seq:{}, Index : {}", bankCd,seqNo, idx);
			} else if(bankCd.equals("007")) {
				FirmDAO.resetSeqNo("KYC_007");
				FirmDAO.resetSeqNo("FIRM_007");
				String seqNo = FirmDAO.getSeqNO("FIRM_007");
				String crypto = firm.get(bankCd).crypto;

				DoznAccountOpenBean bean = new DoznAccountOpenBean();
				bean.setApiKey(firm.get(bankCd).api_key);
				bean.setOrgCode(firm.get(bankCd).org_code);
				bean.setTelegram_no(CommonUtil.parseLong(seqNo));
				bean.setDrw_bank_code(bankCd);

				String sendUrl = "api/rt/v1/account/open";
				if(crypto.equals("Y")) {
					sendUrl = "crypto/rt/v1/account/open";
				}

				String jsonParams = new Gson().toJson(bean);

				long idx = new FirmMasterDAO().setMasterbyDozn("0800", "100", bankCd, seqNo, sendUrl, jsonParams);

				logger.info("autoopen : bank:{} ,seq:{}, Index : {}", bankCd,seqNo, idx);
			}
		}
	}
	
	public static void main(String[] args){
		new AutoOpen().execute();
	}

}
