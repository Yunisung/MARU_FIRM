package com.pgmate.firm.server.executor;

import com.google.gson.Gson;
import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.hyphen.*;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.ksnet.*;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.firm.util.HyphenComm;
import com.pgmate.firm.util.KsnetComm;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterHyphenExcuter implements InterExcuter {

    private Logger logger = LoggerFactory.getLogger( getClass() );
    private Firm firm = null;
    private KsnetComm ksnetComm = null;

    public InterHyphenExcuter(Firm firm) {
        this.firm = firm;
        this.ksnetComm	= new KsnetComm(firm.server);
    }

    @Override
    public FirmBean proc0800(FirmBean firmBean){
        FirmMasterDAO masterDAO = new FirmMasterDAO();

        long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, "");
        return processCheck(idx,firmBean,masterDAO);
    }

    /**
     * �ܾ���ȸ
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0600300(FirmBean firmBean){
        FirmMasterDAO masterDAO = new FirmMasterDAO();
        FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
        FBHeaderBean headerBean = new FBHeaderBean();

        logger.info("=================== �ܾ���ȸ ===================");
        logger.info("idx 	  : {}",firmBean.idx);
        logger.info("bankCd   : {}",firmBean.bankCd);
        logger.info("mAccnt : {}",firmBean.mAccnt);

        headerBean.setIndex(firmBean.idx);
        headerBean.setNewBankCode(firmBean.bankCd);

        BankBean configBean = firm.bank.get(headerBean.getNewBankCode());
        headerBean.setIdentificationCode(configBean.trCd);
        headerBean.setCompanyCode(configBean.compCd);
        headerBean.setSpecCode("0600");
        headerBean.setClassificationCode("300");
        headerBean.setFrequency("1");
        headerBean.setTransactionTime(CommonUtil.getCurrentDate("yyyyMMddHHmmss"));
        headerBean.setSpecNumber(firmTrxDAO.getBankSeq()) ;
        headerBean.setInquiryDay(firmTrxDAO.getTranDate(firmBean.data.getString("orgSeqNo")));

        FB0600300Bean fbBean = new FB0600300Bean();
        fbBean.setAccount(firmBean.mAccnt);

        headerBean.setTransactionIndex(fbBean.getTransaction());

        FBHeaderBean resHeader = ksnetComm.ksnet(headerBean);
        logger.info("TRX RESULT {},[{}]",resHeader.getBankResponseCode(),resHeader.getMessage());

        String resCode = resHeader.getBankResponseCode();
        String resMsg = FirmDAO.getCodeDesc(headerBean.getNewBankCode(), resCode);

        firmBean.resultCd = resCode;
        firmBean.resultMsg = resMsg;

        if(firmBean.resultCd.equals("0000")){
            fbBean = new FB0600300Bean(firmBean.data.getString("resData"));
            String amount = fbBean.getSign()+fbBean.getCurrentAmount();
            firmBean.data.put("amount", CommonUtil.parseLong(amount.trim()));
            masterDAO.insertBalance(firmBean.bankCd, fbBean.getAccount(), amount.trim());
        }

        logger.info("TRX RESULT CHECK [{}],[{}]",resCode,resMsg);
        logger.info("TRANSFER CHECK UPDATE : {} ",firmTrxDAO.updateResultCheck(resHeader, resCode, resMsg));

        logger.info("===================================================");

        return firmBean;
    }

    /**
     * ��������ȸ
     * ������ȸ �����ڵ带 099 �� ����ϸ� KSNET �׿ܴ� �� ����
     * data.bankCd, data.account , data.socialNumber, data.socialCheck
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0600400(FirmBean firmBean){
        logger.info("=================== ��������ȸ ========================");
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

        //PYS : �̹� ��ȸ�� ���´� ���̺��� ��ȸ
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

    /**
     * ����
     * @param firmBean
     * @return
     */
    @Override
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

    /**
     * �۱���ü
     * @param firmBean
     * @return
     */
    @Override
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
     * ó�������ȸ
     * @param firmBean
     * @return
     */
    @Override
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

    /**
     * ������� ������� ���
     * @param firmBean
     * @return
     */
    @Override
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

        //������°� ���̹�ũ�϶� ����
        if("089".equals(firmBean.bankCd)) {
            logger.info("���� 	  	: {}",firmBean.data.getString("customerName"));
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

        if("089".equals(firmBean.bankCd)) {
            fb0900400Bean.setCustomerName(firmBean.data.getString("customerName"));
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

    @Override
    public FirmBean procArsAuth(FirmBean firmBean) {
        return null;
    }

    @Override
    public FirmBean proc0600102(FirmBean firmBean) {
        return null;
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


    
}
