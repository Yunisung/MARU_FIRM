package com.pgmate.firm.server.executor;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.coocon.*;
import com.pgmate.firm.dao.FirmDAO;
import com.pgmate.firm.dao.FirmMasterDAO;
import com.pgmate.firm.dao.FirmTrxDAO;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.lib.util.lang.CommonUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CooconExcuter implements InterExcuter{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Firm firm = null;

    public CooconExcuter(Firm firm) {
        this.firm = firm;
    }

    @Override
    public FirmBean proc0800(FirmBean firmBean) {
        return firmBean;
    }

    @Override
    public FirmBean proc0600300(FirmBean firmBean) {
        logger.info("=============COOCON 잔액조회==============");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmDAO firmDAO = new FirmDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqName = "FIRM_" + firmBean.bankCd;
            String seqNo = "0"+FirmDAO.getSeqNO(seqName);
            String account = "";
            if(CommonUtil.isNullOrSpace(firmBean.mAccnt)) {
                account = configBean.account;
            } else {
                account = firmBean.mAccnt;
            }

            //Bean Setting
            CooconBalanceBean bean = new CooconBalanceBean();
            bean.setTRT_INST_CD(configBean.coocon_firm_code);
            bean.setTRSC_DT(CommonUtil.getCurrentDate("yyyyMMdd"));
            bean.setTRSC_SEQ_NO(seqNo);
            bean.setBANK_CD(firmBean.bankCd);
            bean.setACCT_NO(account);

            CooconReqBean reqBean = new CooconReqBean(bean);
            reqBean.setSECR_KEY(configBean.coocon_firm_key);
            reqBean.setKEY("WAPI_2100");

            String jsonParams = new Gson().toJson(reqBean);
            logger.info("coocon JSON : [{}]", jsonParams);

            long idx = masterDAO.setMasterbyCoocon(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, "webilling_wapi.jsp", jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            //logger.info("coocon response : [{}]", resJson);

            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);

            if(firmBean.resultCd.equals("0000")) {
                String balance_amount = apiRes.get("BAL_AMT").toString();
                firmBean.data.put("amount", CommonUtil.parseLong(balance_amount.trim()));
                masterDAO.insertBalance(firmBean.bankCd, account, balance_amount.trim());
            }


        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신오류";

            e.printStackTrace();
            logger.error("쿠콘 잔액조회 에러 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean proc0600400(FirmBean firmBean) {
        logger.info("=============COOCON 예금주 조회==============");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmDAO firmDAO = new FirmDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqName = "FIRM_" + firmBean.bankCd;
            String seqNo = "0"+FirmDAO.getSeqNO(seqName);
            String bankCd = firmBean.data.getString("bankCd");
            String account = firmBean.data.getString("account");
            String socialNumber = firmBean.data.getString("socialNumber");

            //Bean Setting
            CooconAccountNameBean bean = new CooconAccountNameBean();
            bean.setBANK_CD(bankCd);
            bean.setSEARCH_ACCT_NO(account);
            bean.setACNM_NO(socialNumber);
            bean.setICHE_AMT("");
            bean.setTRSC_SEQ_NO(seqNo);

            CooconReqBean reqBean = new CooconReqBean(bean);
            reqBean.setSECR_KEY(configBean.coocon_name_key);
            reqBean.setKEY("ACCTNM_RCMS_WAPI");

            if(!CommonUtil.isNullOrSpace(socialNumber)) {
                reqBean.setSECR_KEY(configBean.coocon_realname_key);
            }

            String jsonParams = new Gson().toJson(reqBean);
            logger.info("coocon JSON : [{}]", jsonParams);

            long idx = masterDAO.setMasterbyCoocon(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, "acctnm_rcms_wapi.jsp", jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            logger.info("coocon response : [{}]", resJson);

            JSONParser jsonParser = new JSONParser();
            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);
            logger.info("JSON : [{}]", apiRes.toString());

            if(firmBean.resultCd.equals("0000")) {
                JSONArray respDataArr = (JSONArray) apiRes.get("RESP_DATA");
                if(respDataArr.size() > 0) {
                    JSONObject respData = (JSONObject) respDataArr.get(0);
                    String name = respData.get("ACCT_NM").toString();
                    firmBean.data.put("accountName", name);
                    masterDAO.insertAccnt(firmBean.data.getString("bankCd"), firmBean.data.getString("account"), firmBean.data.getString("accountName"));
                } else {
                    firmBean.data.put("accountName", "");
                }
            }


        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신 오류";

            e.printStackTrace();
            logger.error("쿠콘 예금주 조회 오류 에러 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean proc0700100(FirmBean firmBean) {
        return firmBean;
    }

    @Override
    public FirmBean proc0100100(FirmBean firmBean) {
        logger.info("=============COOCON 이체==============");

        long currentTime = CommonUtil.parseLong(CommonUtil.getCurrentDate("HHmmss"));

        if(firm.daemon.startTime > currentTime || currentTime > firm.daemon.stopTime){
            firmBean.resultCd ="XXXX";
            firmBean.resultMsg ="이체 가능 시간 아님";
            return firmBean;
        }

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();

            String seqName = "FIRM_" + firmBean.bankCd;
            String seqNo = FirmDAO.getSeqNO(seqName);

            long idx = firmTrxDAO.insertTrx(seqNo, firmBean.bankCd, firmBean.data.getLong("amount"), firmBean.data.getString("recvBankCd"), firmBean.data.getString("recvAccount"), firmBean.data.getString("sender"), firmBean.data.getString("recordInfo"), firmBean.data.getString("procType"));

            if(idx == 0) {
                firmBean.resultCd ="XXXX";
                firmBean.resultMsg ="이체데이터 등록실패";
            } else {
                firmBean = processCheck(idx, firmBean, firmTrxDAO);
            }


        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신오류";

            e.printStackTrace();
            logger.error("쿠콘 이체 에러 : [{}]", e.getMessage());
        }
        return null;
    }

    @Override
    public FirmBean proc0600101(FirmBean firmBean) {
        logger.info("=============COOCON 처리결과조회==============");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmTrxDAO firmTrxDAO = new FirmTrxDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String org_SeqNo = firmBean.data.getString("orgSeqNo");
            String org_TranDate = firmTrxDAO.getTranDate(firmBean.data.getString("orgSeqNo"));
            String seqName = "FIRM_" + firmBean.bankCd;
            String seqNo = "0"+FirmDAO.getSeqNO(seqName);

            //Bean Setting
            CooconTransferCheckBean bean = new CooconTransferCheckBean();
            bean.setTRT_INST_CD(configBean.coocon_firm_code);
            bean.setTRSC_DT(CommonUtil.getCurrentDate("yyyyMMdd"));
            bean.setTRSC_SEQ_NO(seqNo);
            bean.setBANK_CD(firmBean.bankCd);
            bean.setRQRE_TMSG_NO(org_SeqNo);

            CooconReqBean reqBean = new CooconReqBean(bean);
            reqBean.setSECR_KEY(configBean.coocon_firm_key);
            reqBean.setKEY("WAPI_6113");

            String jsonParams = new Gson().toJson(reqBean);
            logger.info("coocon JSON : [{}]", jsonParams);

            long idx = masterDAO.setMasterAddSearchDateByCoocon(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, "webilling_wapi.jsp", jsonParams, org_TranDate);
            firmBean = processCheck(idx, firmBean, masterDAO);



        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신오류";

            e.printStackTrace();
            logger.error("쿠콘 처리결과조회 에러 : [{}]", e.getMessage());
        }
        logger.info("===================================================");

        return firmBean;
    }

    @Override
    public FirmBean proc0900400(FirmBean firmBean) {
        logger.info("=============COOCON 출금계좌등록==============");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmDAO firmDAO = new FirmDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqName = "KYC_" + firmBean.bankCd;
            String seqNo = FirmDAO.getSeqNO(seqName);
            String vactAccount = firmBean.data.getString("virtualAccount");
            String name = firmBean.data.getString("customerName");
            String trxType = firmBean.data.getString("trxType");
            String bankCd = firmBean.data.getString("withdrawBankCd");
            String account = firmBean.data.getString("withdrawAccount");
            String sts = "";
            String startAmount = firmBean.data.getString("startAmount");
            String endAmount = firmBean.data.getString("endAmount");

            //STS : 11(등록), 22(변경), 41(해지)
            if(trxType.equals("1")) {
                sts = "11";
            } else if(trxType.equals("3")) {
                sts = "41";
            }

            //Bean Setting
            CooconRegAccountBean bean = new CooconRegAccountBean();
            bean.setSECR_KEY(configBean.coocon_kyc_key);
            bean.setKEY("8160");
            bean.setTRT_INST_CD(configBean.coocon_kyc_code);
            bean.setBANK_CD(firmBean.bankCd);
            bean.setTRSC_SEQ_NO(CommonUtil.getCurrentDate("yyMMdd") + seqNo);
            bean.setVA_ACCT_NO(vactAccount);
            bean.setVA_ACCT_NM(name);
            bean.setSTS(sts);
            bean.setD_COMPANY_NO("");
            bean.setWDRW_BNK_CD(bankCd);
            bean.setWDRW_ACCT_NO(account);
            bean.setCUST_NO("");

            if(CommonUtil.isNullOrSpace(startAmount)) {
                bean.setCUST_GUBUN("2");
                bean.setFIXED_AMT("0");
                bean.setRANGE_START_AMT("0");
                bean.setRANGE_END_AMT("0");
            } else {
                if(startAmount.equals(endAmount)) {
                    bean.setCUST_GUBUN("1");
                    bean.setFIXED_AMT(startAmount);
                    bean.setRANGE_START_AMT("0");
                    bean.setRANGE_END_AMT("0");
                } else if(Long.valueOf(startAmount) < Long.valueOf(endAmount)) {
                    bean.setCUST_GUBUN("3");
                    bean.setFIXED_AMT("0");
                    bean.setRANGE_START_AMT(startAmount);
                    bean.setRANGE_END_AMT(endAmount);
                }
            }

            if(bean.getSTS().equals("41")) {
                bean.setCUST_GUBUN("2");
            }

            bean.setPARENT_ACCT(configBean.account);

            String jsonParams = new Gson().toJson(bean);
            logger.info("coocon JSON : [{}]", jsonParams);

            long idx = masterDAO.setMasterbyCoocon(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, "kyc", jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            logger.info("coocon response : [{}]", resJson);
//
//            //JSON ???
//            JSONParser jsonParser = new JSONParser();
//            JSONObject apiRes  = (JSONObject) jsonParser.parse(resJson);
//
//            //??????
//            if(firmBean.resultCd.equals("0000")) {
//                String balance_amount = apiRes.get("BAL_AMT").toString();
//                firmBean.data.put("amount", CommonUtil.parseLong(balance_amount.trim()));
//                masterDAO.insertBalance(firmBean.bankCd, account, balance_amount.trim());
//            }




        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신 오류";

            e.printStackTrace();
            logger.error("쿠콘 가상계좌 에러 : [{}]", e.getMessage());
        }

        return firmBean;
    }

    @Override
    public FirmBean procArsAuth(FirmBean firmBean) {
        return firmBean;
    }

    @Override
    public FirmBean proc0600102(FirmBean firmBean) {
        return firmBean;
    }

    @Override
    public FirmBean procAccAuth(FirmBean firmBean) {
        logger.info("=============COOCON 계좌인증=============");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmDAO firmDAO = new FirmDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqName = "FIRM_" + firmBean.bankCd;
            String seqNo = "0"+FirmDAO.getSeqNO(seqName);
            String bankCd = firmBean.data.getString("recvBankCd");
            String account = firmBean.data.getString("recvAccount");
            String name = firmBean.data.getString("recvName");

            //Bean Setting
            CooconAccountAuthBean bean = new CooconAccountAuthBean();
            bean.setFnni_cd(bankCd);
            bean.setAcct_no(account);
            bean.setMemb_nm(name);
            bean.setVerify_tp("N");
            bean.setVerify_len("4");
            bean.setPtst_txt("BK");


            //Insert DB
            String jsonParams = new Gson().toJson(bean);
            logger.info("coocon JSON : [{}]", jsonParams);

            long idx = masterDAO.setMasterbyCoocon(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, "accountAuth", jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);


        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신 에러";

            e.printStackTrace();
            logger.error("쿠콘 계좌인증 에러 : [{}]", e.getMessage());
        }
        logger.info("===================================================");

        return firmBean;
    }

    @Override
    public FirmBean procAccChck(FirmBean firmBean) {
        logger.info("=============COOCON 계좌인증 확인==============");

        try {
            FirmMasterDAO masterDAO = new FirmMasterDAO();
            FirmDAO firmDAO = new FirmDAO();
            BankBean configBean = firm.bank.get(firmBean.bankCd);

            String seqName = "FIRM_" + firmBean.bankCd;
            String seqNo = "0"+FirmDAO.getSeqNO(seqName);
            String orgSeqNo = firmBean.data.getString("orgSeqNo");
            String orgResData = masterDAO.getResData(orgSeqNo);

            Gson gson = new Gson();
            CooconAccountAuthCheckBean bean = gson.fromJson(orgResData, CooconAccountAuthCheckBean.class);
            //TEST ONLY
            bean.setVerify_val("123");


            //INSERT DB
            String jsonParams = new Gson().toJson(bean);
            logger.info("coocon JSON : [{}]", jsonParams);

            long idx = masterDAO.setMasterbyCoocon(firmBean.msgType.substring(0,4), firmBean.msgType.substring(4), firmBean.bankCd, seqNo, "accountAuthCheck", jsonParams);
            firmBean = processCheck(idx, firmBean, masterDAO);

            String resJson = firmBean.data.getString("resData");
            logger.info("coocon response : [{}]", resJson);

        } catch (Exception e) {
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "쿠콘 통신 에러";

            e.printStackTrace();
            logger.error("쿠콘 계좌인증 확인 에러 : [{}]", e.getMessage());
        }
        logger.info("===================================================");

        return firmBean;
    }

    @Override
    public FirmBean procArsChck(FirmBean firmBean) {
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
                firmBean = trxDAO.checkResultByCoocon(idx,firmBean);
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
