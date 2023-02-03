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
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.firm.util.HyphenComm;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterHyphenFirmExcuter implements InterExcuter {

    private Logger logger = LoggerFactory.getLogger( getClass() );
    private Firm firm = null;
    private HyphenComm hyphenComm = null;

    public InterHyphenFirmExcuter(Firm firm, HyphenComm hyphenComm) {
        this.firm = firm;
        this.hyphenComm = hyphenComm;
    }

    public InterHyphenFirmExcuter(Firm firm) {
        this.firm = firm;
    }

    @Override
    public FirmBean proc0800(FirmBean firmBean){
        throw new UnsupportedOperationException();
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

    /**
     * 예금주조회
     * 성명조회 은행코드를 099 를 사용하면 KSNET 그외는 각 은행
     * data.bankCd, data.account , data.socialNumber, data.socialCheck
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0600400(FirmBean firmBean){
        logger.info("======================== Holder ========================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            /*HolderBean holderBean = new HolderBean();
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
            hyphenBean.setSendurl("rfb/retail/account/accountname");*/


            //230104_PYS : PCS 로직 추가, 기존로직 주석
            FcsBean fcsBean = new FcsBean();
            fcsBean.setFcs_cd(configBean.fcs_cd);
            fcsBean.setBank_cd(firmBean.data.getString("bankCd"));
            fcsBean.setAcct_no(firmBean.data.getString("account"));
            fcsBean.setId_no(firmBean.data.getString("socialNumber"));

            HyphenBean hyphenBean = new HyphenBean();
            hyphenBean.setAuth_key(configBean.auth_key);
            hyphenBean.setReqdata(fcsBean);
            hyphenBean.setSendurl("ksnet/auth/account");



            String holderCode = firmBean.data.getString("bankCd");
            String holderAccount = firmBean.data.getString("account");

            String jsonParams = new Gson().toJson(hyphenBean);

            long idx = masterDAO.setMasterbyHyphen(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, hyphenBean.getSendurl(), jsonParams);
            firmBean = processCheck(idx,firmBean,masterDAO);
            if(firmBean.resultCd.equals("0000")){
                String resJson = firmBean.data.getString("resData");

                /*holderBean = (HolderBean) GsonUtil.fromJson(resJson, HolderBean.class);
                String name = holderBean.getAccountName();*/
                
                fcsBean = (FcsBean) GsonUtil.fromJson(resJson, FcsBean.class);
                String name = fcsBean.getName();
                firmBean.data.put("accountName", name.trim());

                //조회 성공하면 PG_FIRM_ACCNT에 INSERT
                masterDAO.insertAccnt(holderCode, holderAccount, name.trim());
            } else {
                //230105_PYS : 하이픈에서 에러값 이상할때 DB에서 resultMsg 세팅
                if(CommonUtil.isNullOrSpace(firmBean.resultCd)) {
                    //resultCd가 없을때
                    firmBean.resultCd = "XXXX";
                    firmBean.resultMsg = "성명조회 오류. 결과코드 없음";
                } else {
                    //resultCd가 있을때
                    if(CommonUtil.isNullOrSpace(firmBean.resultMsg)) {
                        //resultMsg가 없을때
                        firmBean.resultMsg = masterDAO.getFcsErrorMsg(firmBean.resultCd);
                    } else {
                        //resultMsg가 있을때
                        logger.info("FCS ERROR [{}][{}]", firmBean.resultCd, firmBean.resultMsg);
                    }
                }
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

    /**
     * 집계
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0700100(FirmBean firmBean){
        throw new UnsupportedOperationException();
    }

    /**
     * 송금이체
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0100100(FirmBean firmBean){
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
     * 처리결과조회
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0600101(FirmBean firmBean){
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

        HyphenComm hyphenComm = new HyphenComm();
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

    /**
     * 가상계좌 출금정보 등록
     * @param firmBean
     * @return
     */
    @Override
    public FirmBean proc0900400(FirmBean firmBean){
        throw new UnsupportedOperationException();
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

    @Override
    public FirmBean procArsAuth(FirmBean firmBean) {
        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            ArsBean arsBean = new ArsBean();
            arsBean.setCompcode(configBean.compCd);
            arsBean.setPhoneno(firmBean.data.getString("phoneNo"));
            arsBean.setService("0001");
            arsBean.setSvc_type("03");
            arsBean.setUsedrecord("N");
            arsBean.setAuthno(firmBean.data.getString("authNo"));
            arsBean.setFiller1("출금계좌 등록 가상계좌 서비스가 일반거래 외에 보이스 피싱, 코인거래등 불법을 목적으로 사용 될 경우 모든 법적책임이 본인에게 있다는점을 인지 하여 등록바랍니다. 계속 진행");

            HyphenBean hyphenBean = new HyphenBean();
            hyphenBean.setAuth_key(configBean.auth_key);
            hyphenBean.setReqdata(arsBean);
            hyphenBean.setSendurl("ksnet/auth/ars");

            String jsonParams = new Gson().toJson(hyphenBean);

            long idx = masterDAO.setMasterbyHyphen(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, hyphenBean.getSendurl(), jsonParams);

            //230203_PYS : 등록만 하고 정상처리
            if(idx > 0) {
                firmBean.resultCd = "0000";
                firmBean.resultMsg = "ARS 요청이 성공하였습니다.";
                firmBean.data.put("firmIdx", idx);
            } else {
                firmBean.resultCd = "XXXX";
                firmBean.resultMsg = "ARS 요청실패.";
            }

            /*firmBean = processCheck(idx,firmBean,masterDAO);
            if(firmBean.resultCd.equals("0000")){
                String resJson = firmBean.data.getString("resData");

                arsBean = (ArsBean) GsonUtil.fromJson(resJson, ArsBean.class);
                String trace_no = arsBean.getTrace_no();
                String record = arsBean.getRecord();
                firmBean.data.put("traceNo", trace_no);
                firmBean.data.put("record", record);

                logger.info("ARS인증: [{}]", arsBean.toString());
            } else {
                if(CommonUtil.isNullOrSpace(firmBean.resultCd)) {
                    //resultCd가 없을때
                    firmBean.resultCd = "XXXX";
                    firmBean.resultMsg = "ARS인증 오류. 결과코드 없음";
                } else {
                    //resultCd가 있을때
                    if(CommonUtil.isNullOrSpace(firmBean.resultMsg)) {
                        //resultMsg가 없을때
                        firmBean.resultMsg = masterDAO.getArsErrorMsg(firmBean.resultCd);
                    } else {
                        //resultMsg가 있을때
                        logger.info("ARS ERROR [{}][{}]", firmBean.resultCd, firmBean.resultMsg);
                    }
                }
            }*/
        }catch (Exception e) {

            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="ARS 오류";

            e.printStackTrace();
            logger.error("ARS인증 Error : [{}]", e.getMessage());
        }

        logger.info("===================================================");

        return firmBean;
    }



    
}
