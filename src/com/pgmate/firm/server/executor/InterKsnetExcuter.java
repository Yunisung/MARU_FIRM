package com.pgmate.firm.server.executor;

import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.ksnet.*;
import com.pgmate.firm.util.KsnetComm;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterKsnetExcuter implements InterExcuter {

    private Logger logger = LoggerFactory.getLogger( getClass() );
    private Firm firm = null;

    public InterKsnetExcuter(Firm firm) {
        this.firm = firm;
    }

    @Override
    public FirmBean proc0800(FirmBean firmBean){
        FirmMasterDAO masterDAO = new FirmMasterDAO();

        long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, "");
        return processCheck(idx,firmBean,masterDAO);
    }

    /**
     * 잔액조회
     * @param firmBean
     * @return
     */
    @Override
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

    /**
     * 예금주조회
     * 성명조회 은행코드를 099 를 사용하면 KSNET 그외는 각 은행
     * data.bankCd, data.account , data.socialNumber, data.socialCheck
     * @param firmBean
     * @return
     */
    @Override
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
        // 나중에 주석처리 하던가 할것.
//        if(firmBean.bankCd.equals("099") && firmBean.data.isNullOrSpace("socialCheck")) {
//            String holder = masterDAO.selectAccnt(firmBean.data.getString("bankCd"), firmBean.data.getString("account"));
//            logger.info("INTER ACCNT CHECK: {}",holder);
//            if(!CommonUtil.isNullOrSpace(holder)) {
//                firmBean.data.put("name", holder);
//                firmBean.resultCd ="0000";
//                firmBean.resultMsg = "정상조회";
//
//                return firmBean;
//            }
//        }

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
     * 집계
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
     * 송금이체
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0100100(FirmBean firmBean){
        // 이체가능시간 외 데이터 등록 막기
        //Firm firm = FirmLoader.getConfig();
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
     * 처리결과조회
     * @param firmBean
     * @return
     */
    /*
    @Override
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
    */

    /**
     * 처리결과조회
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0600101(FirmBean firmBean){
        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FB0600101Bean fbBean = new FB0600101Bean();
            fbBean.setRootSpecNumber(firmBean.data.getString("orgSeqNo")) ;

            long idx = masterDAO.setMasterAddSearchDate(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, fbBean.getTransaction());
            firmBean = processCheck(idx,firmBean,masterDAO);

            //OSC: 처리결과없음 (은행에 아예 거래내역이 존재하지 않음)
            if("KS10".equals(firmBean.resultCd)) {
                // 아무것도 하지 않음
            } else {
                //PYS : 개별부의 응답코드를 리턴해주는걸로 변경
                fbBean = new FB0600101Bean(firmBean.data.getString("resData"));
                firmBean.resultCd = fbBean.getResultCd();
                firmBean.resultMsg = FirmDAO.getResultMsg(firmBean.resultCd);
            }

            if(firmBean.resultCd.equals("0000")){
                // 아무것도 하지 않음
            }
        }catch (Exception e) {
            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="처리결과조회 오류";

            e.printStackTrace();
            logger.error("처리결과조회 Error : [{}]", e.getMessage());
        }

        logger.info("===================================================");

        return firmBean;
    }

    /**
     * 이체 재시도
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0600102(FirmBean firmBean){
        long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));

        if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="이체 가능 시간 아님";
            return firmBean;
        }
        logger.error("이체 재시도 : [{}]", firmBean.data.getString("trxId"));

        FirmTrxDAO trxDAO = new FirmTrxDAO();
        SharedMap<String, Object> trxMap = trxDAO.getTrxData(firmBean.data.getString("trxId"));
        long idx = trxDAO.insertReTrx(trxMap);

        if(idx == 0){
            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="이체데이터 등록실패";
        }else{
            firmBean = processCheck(idx,firmBean,trxDAO);
        }
        return firmBean;
    }

    /**
     * 가상계좌 출금정보 등록
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0900400(FirmBean firmBean){
        FirmMasterDAO masterDAO = new FirmMasterDAO();
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
        //230405_PYS : 경남은행 추가
        if("011".equals(firmBean.bankCd) ||  "012".equals(firmBean.bankCd) || "039".equals(firmBean.bankCd)) {
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

        //가상계좌용 식별코드 : KSNETVR
        headerBean.setIdentificationCode("KSNETVR");

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
        //230405_PYS : 경남은행 추가
        if("088".equals(firmBean.bankCd) || "011".equals(firmBean.bankCd) ||
                "012".equals(firmBean.bankCd) || "081".equals(firmBean.bankCd) ||
                "039".equals(firmBean.bankCd)) {
            fb0900400Bean.setIdentity(firmBean.data.getString("identity"));
        }

        //가상계좌가 농협은행일때만 세팅
        //230405_PYS : 경남은행 추가
        if("011".equals(firmBean.bankCd) || "012".equals(firmBean.bankCd) ||
                "039".equals(firmBean.bankCd)) {
            fb0900400Bean.setRegType(firmBean.data.getString("regType"));
        }
        //가상계좌가 케이뱅크일때 세팅
        if("089".equals(firmBean.bankCd)) {
            fb0900400Bean.setCustomerName(firmBean.data.getString("customerName"));
        }

        headerBean.setTransactionIndex(fb0900400Bean.getTransaction());

        KsnetComm comm	= new KsnetComm(firm.server);
        FBHeaderBean resHeader = comm.ksnet(headerBean);

        String resCode = resHeader.getBankResponseCode();

        resMsg = FirmDAO.getVactResultMsg(resCode);

        logger.info("MASTER TRANSFER : {}",headerBean.getSpecCode()+headerBean.getClassificationCode());
        long idx = masterDAO.setMasterForResponse(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4)
                , firmBean.bankCd, fb0900400Bean.getTransaction(), "I");
        resHeader.setIndex(idx);

        logger.info("MASTER RESULT {},[{}]",resHeader.getBankResponseCode(),resHeader.getMessage());
        logger.info("MASTER RESULT UPDATE : {}", masterDAO.update(resHeader));

        logger.info("가상계좌 출금정보 등록 : [{}][{}]",resCode,resMsg);
        logger.info("===================================================");

        firmBean.resultCd = resCode;
        firmBean.resultMsg = resMsg;

        return firmBean;
    }

    @Override
    public FirmBean procArsAuth(FirmBean firmBean) {
        return null;
    }

    /**
     * 가상계좌 출금정보 등록
     * firm.json 에 아래와 같이 세팅되어야 동작함. 현재는 KSBPAY로 구성되어 있음
     * "089": {
     *       "trCd": "KSNETVR",
     *       "compCd": "",
     *       "bankCd": "089",
     *       "account": "70110001999557"
     *     },
     * @param firmBean
     * @return
     */
    /*@Override
    public FirmBean proc0900400(FirmBean firmBean){
        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FB0900400Bean fbBean = new FB0900400Bean();

            logger.info("고객명 	  	: {}",firmBean.data.getString("customerName"));

            logger.info("======================== 가상계좌 출금정보 등록 =================================");

            fbBean.setTrxType(firmBean.data.getString("trxType"));
            fbBean.setVirtualAccount(firmBean.data.getString("virtualAccount"));
            fbBean.setWithdrawBankCd(firmBean.data.getString("withdrawBankCd"));
            fbBean.setWithdrawAccount(firmBean.data.getString("withdrawAccount"));
            fbBean.setCustomerName(firmBean.data.getString("customerName"));

            long idx = masterDAO.setMaster(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, fbBean.getTransaction());
            firmBean = processCheck(idx,firmBean,masterDAO);
            if(firmBean.resultCd.equals("0000")){
                // 아무것도 하지 않음
            }
        }catch (Exception e) {
            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="처리결과조회 오류";

            e.printStackTrace();
            logger.error("처리결과조회 Error : [{}]", e.getMessage());
        }

        logger.info("===================================================");

        return firmBean;
    }*/


    public FirmBean processCheck(long idx,FirmBean firmBean,FirmMasterDAO masterDAO){
        int limit = 40;
        int count = 1;
        try{
            while(count < limit){
                Thread.sleep(1000);
                firmBean = masterDAO.checkResult(idx,firmBean);
                firmBean.resultMsg = FirmDAO.getResultMsg(firmBean.resultCd);
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
