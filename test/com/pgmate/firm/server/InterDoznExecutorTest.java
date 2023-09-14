package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.main.Daemon;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterDoznExecutorTest {
    InterProcess interProcess;
    Firm firm;
    private static Logger logger = LoggerFactory.getLogger(InterDoznExecutorTest.class);

    @Before
    public void setup() throws Exception {
        firm = FirmLoader.getConfig();
        interProcess = new InterProcess(firm);
        Daemon.main(null);
    }

    @Test
    public void balance() {
        logger.info("잔액조회");
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0600300";
        firmBean.userId		= "SYSTEM";

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void holder() {
        logger.info("예금주조회");
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0600400";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("bankCd", "088");
        firmBean.data.put("account", "123123123");
        firmBean.data.put("socialNumber", "900101"); // 주민번호

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.HYPHEN, CommonUtil.toString(send));
    }

    @Test
    public void Aggregate() {
        logger.info("집계");
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0700100";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("searchDate", "20230913");
        firmBean.data.put("searchCode", "1"); //1: 예금주조회 2: 점유인증 3. 이체

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.HYPHEN, CommonUtil.toString(send));
    }

    @Test
    public void transfer() {
        // 이체
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0100100";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("amount",1000);
        firmBean.data.put("recvBankCd","088");
        firmBean.data.put("recvAccount","123123123");
        firmBean.data.put("sender", "sender");
        firmBean.data.put("procType", "CS");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

    }

    @Test
    public void getExecutionResult() {
        // 처리결과조회
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0600101";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("orgSeqNo", "000296");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void AccountAuth() {
        // 계좌점유인증
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ACCAUTH";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("recvBankCd", "088");
        firmBean.data.put("recvAccount", "123123123");
        firmBean.data.put("sender", "BK1234");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void ArsAuth() {
        // ARS인증
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ARSAUTH";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("phoneNo", "01012341234");
        firmBean.data.put("authNo", "11");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void ArsCheck() {
        // ARS인증
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ARSCHCK";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("orgSeqNo", "000123");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }


}
