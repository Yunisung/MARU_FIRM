package com.pgmate.firm.server.executor;

import com.google.gson.Gson;
import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.dozn.*;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.lib.util.lang.CommonUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoznExcuter implements InterExcuter {
    private Logger logger = LoggerFactory.getLogger( getClass() );
    private Firm firm = null;

    public DoznExcuter(Firm firm) {
        this.firm = firm;
    }

    @Override
    public FirmBean proc0800(FirmBean firmBean) {
        return firmBean;
    }

    @Override
    public FirmBean proc0600300(FirmBean firmBean) {
        //더즌 잔액조회 세팅
        logger.info("===================DOZN 잔액조회 ===================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            //BEAN 세팅
            DoznBalanceCheckBean bean = new DoznBalanceCheckBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);

            bean.setDrw_bank_code(firmBean.bankCd);
            if(CommonUtil.isNullOrSpace(firmBean.mAccnt)) {
                bean.setDrw_account(configBean.account);
            } else {
                bean.setDrw_account(firmBean.mAccnt);
            }


            String seqNo = firmTrxDAO.getBankSeq();
            bean.setTelegram_no(CommonUtil.parseLong(seqNo));

            //URL 세팅
            String sendUrl = "api/rt/v1/balance/check";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/balance/check";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterbyDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, sendUrl, jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("dozn response : [{}]", resJson);

            //JSON 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            //결과처리
            if(firmBean.resultCd.equals("0000")) {
                String balance_amount = apiRes.get("sign").toString() + apiRes.get("balance_amount").toString();
                String payable_amount = apiRes.get("sign").toString() + apiRes.get("payable_amount").toString();
                firmBean.data.put("amount", CommonUtil.parseLong(balance_amount.trim()));
                firmBean.data.put("payable_amount", CommonUtil.parseLong(payable_amount.trim()));

                masterDAO.insertBalance(firmBean.bankCd, firmBean.mAccnt, balance_amount.trim());
            }

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 잔액조회 오류";

            e.printStackTrace();
            logger.error("DOZN 잔액조회(0600300) 오류 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean proc0600400(FirmBean firmBean) {
        logger.info("===================DOZN 예금주 조회 ========================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            //BEAN 세팅
            DoznInquireDepositorBean bean = new DoznInquireDepositorBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);

            bean.setBank_code(firmBean.data.getString("bankCd"));
            bean.setAccount(firmBean.data.getString("account"));
            bean.setIdentify_no(CommonUtil.parseLong(firmBean.data.getString("socialNumber")));

            if(CommonUtil.isNullOrSpace(firmBean.data.getString("socialNumber"))) {
                bean.setCheck_depositor("N");
            }else {
                bean.setCheck_depositor("Y");
            }

            String seqNo = firmTrxDAO.getBankSeq();
            bean.setTelegram_no(CommonUtil.parseLong(seqNo));

            //URL 세팅
            String sendUrl = "api/rt/v1/inquireDepositor";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/inquireDepositor";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterbyDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, sendUrl, jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("dozn response : [{}]", resJson);

            //JSON 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            //결과처리
            if(firmBean.resultCd.equals("0000")) {
                String name = apiRes.get("depositor").toString();
                firmBean.data.put("accountName", name);

                masterDAO.insertAccnt(firmBean.data.getString("bankCd"), firmBean.data.getString("account"), firmBean.data.getString("name"));
            }

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 예금주 조회 오류";

            e.printStackTrace();
            logger.error("DOZN 예금주 조회(0600400) 오류 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean proc0700100(FirmBean firmBean) {
        logger.info("===================DOZN 집계 ========================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            //BEAN 세팅
            DoznTransferAggregateBean bean = new DoznTransferAggregateBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);

            bean.setTr_dt(firmBean.data.getString("searchDate"));
            bean.setDrw_bank_code(firmBean.bankCd);
            bean.setDivision_code(CommonUtil.parseLong(firmBean.data.getString("searchCode")));

            String seqNo = firmTrxDAO.getBankSeq();
            bean.setTelegram_no(CommonUtil.parseLong(seqNo));

            //URL 세팅
            String sendUrl = "api/rt/v1/transfer/aggregate";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/transfer/aggregate";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterbyDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, sendUrl, jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("dozn response : [{}]", resJson);

            //JSON 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            //결과처리
            if(firmBean.resultCd.equals("0000")) {
                firmBean.data.put("sucCount", CommonUtil.parseLong(apiRes.get("success_total_count").toString()));
                firmBean.data.put("sucAmount", CommonUtil.parseLong(apiRes.get("success_total_amount").toString()));
                firmBean.data.put("failCount", CommonUtil.parseLong(apiRes.get("fail_total_count").toString()));
                firmBean.data.put("timeOutCount", CommonUtil.parseLong(apiRes.get("timeout_total_count").toString()));
            }

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 집계 오류";

            e.printStackTrace();
            logger.error("DOZN 집계 (0700100) 오류 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean proc0100100(FirmBean firmBean) {
        logger.info("===================DOZN 이체 ========================");

        long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));

        if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="이체 가능 시간 아님";
            return firmBean;
        }

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();

            //MASTER DB 저장
            long idx = firmTrxDAO.insertTrx(firmBean.bankCd, firmBean.data.getLong("amount"), firmBean.data.getString("recvBankCd"), firmBean.data.getString("recvAccount"), firmBean.data.getString("sender"), firmBean.data.getString("recordInfo"), firmBean.data.getString("procType"));

            if(idx == 0) {

            } else {
                firmBean = processCheck(idx, firmBean, firmTrxDAO);
            }


        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "이체데이터등록실패";

            e.printStackTrace();
            logger.error("DOZN 이체 (0100100) 오류 : [{}]", e.getMessage());
        }

        return firmBean;

    }

    @Override
    public FirmBean proc0600101(FirmBean firmBean) {
        logger.info("===================DOZN 처리결과조회 ========================");
        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String org_SeqNo = firmBean.data.getString("orgSeqNo");
            String org_TranDate = firmTrxDAO.getTranDate(firmBean.data.getString("orgSeqNo"));

            //BEAN 세팅
            DoznTransferCheckBean bean = new DoznTransferCheckBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);

            bean.setDrw_bank_code(configBean.bankCd);
            bean.setOrg_telegram_no(CommonUtil.parseLong(org_SeqNo));
            bean.setTr_dt(org_TranDate);

            //URL 세팅
            String sendUrl = "api/rt/v1/transfer/check";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/transfer/check";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterAddSearchDateByDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, sendUrl, jsonParams, org_TranDate);
            firmBean = processCheck(idx, firmBean, masterDAO);

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 처리결과 조회 오류";

            e.printStackTrace();
            logger.error("DOZN 처리결과 조회(0600101) 오류 : [{}]", e.getMessage());
        }

        logger.info("===================================================");

        return firmBean;
    }

    @Override
    public FirmBean proc0900400(FirmBean firmBean) {
        return null;
    }

    @Override
    public FirmBean procArsAuth(FirmBean firmBean) {
        logger.info("===================DOZN ARS인증 ========================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqNo = firmTrxDAO.getBankSeq();

            //BEAN 세팅
            DoznArsAuthenticateBean bean = new DoznArsAuthenticateBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);
            bean.setTelegram_no(CommonUtil.parseLong(seqNo));
            bean.setPhone_no(firmBean.data.getString("phoneNo"));
            bean.setAuth_no(firmBean.data.getString("authNo"));


            //URL 세팅
            String sendUrl = "api/rt/v1/ars/authenticate";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/ars/authenticate";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterbyDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, sendUrl, jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("dozn response : [{}]", resJson);

            //JSON 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            //결과처리
            if(firmBean.resultCd.equals("0000")) {
                firmBean.data.put("firmIdx", String.valueOf(idx));
            }

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 ARS인증 오류";

            e.printStackTrace();
            logger.error("DOZN ARS 오류 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean proc0600102(FirmBean firmBean) {
        return null;
    }

    @Override
    public FirmBean procAccAuth(FirmBean firmBean) {
        logger.info("===================DOZN 계좌점유인증(1원인증) ========================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqNo = firmTrxDAO.getBankSeq();

            //BEAN 세팅
            DoznAccountAuthBean bean = new DoznAccountAuthBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);
            bean.setTelegram_no(CommonUtil.parseLong(seqNo));
            bean.setRv_bank_code(firmBean.data.getString("recvBankCd"));
            bean.setRv_account(firmBean.data.getString("recvAccount"));
            bean.setRv_account_cntn(firmBean.data.getString("sender"));
            bean.setAmount(1);

            //URL 세팅
            String sendUrl = "api/rt/v1/account/auth";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/account/auth";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterbyDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, sendUrl, jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("dozn response : [{}]", resJson);

            //JSON 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            //결과처리

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 계좌점유인증(1원인증) 오류";

            e.printStackTrace();
            logger.error("DOZN 계좌점유인증(1원인증) 오류 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean procArschck(FirmBean firmBean) {
        logger.info("===================DOZN ARS인증결과 ========================");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqNo = firmTrxDAO.getBankSeq();

            //BEAN 세팅
            DoznArsAuthenticateCheckBean bean = new DoznArsAuthenticateCheckBean();
            bean.setApiKey(configBean.api_key);
            bean.setOrgCode(configBean.org_code);
            bean.setOrg_telegram_no(CommonUtil.parseLong(firmBean.data.getString("orgSeqNo")));
            bean.setTr_dt(masterDAO.getTranDate(firmBean.data.getString("orgSeqNo")));


            //URL 세팅
            String sendUrl = "api/rt/v1/ars/authenticate/check";
            if(configBean.crypto.equals("Y")) {
                sendUrl = "crypto/rt/v1/ars/authenticate/check";
            }

            String jsonParams = new Gson().toJson(bean);

            //logger.info("dozn json data : [{}]", jsonParams);

            //MASTER DB 저장
            long idx = masterDAO.setMasterbyDozn(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, sendUrl, jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("dozn response : [{}]", resJson);

            //JSON 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            //결과처리

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "더즌 ARS인증결과 오류";

            e.printStackTrace();
            logger.error("DOZN ARS인증결과 오류 : [{}]", e.getMessage());
        }

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
                firmBean = trxDAO.checkResultByDozn(idx,firmBean);
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
