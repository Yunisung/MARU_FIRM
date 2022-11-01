package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.main.Daemon;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class InterProcessTest {

    InterProcess interProcess;
    Firm firm;
    private static Logger logger = LoggerFactory.getLogger(InterProcessTest.class);


    @Before
    public void setup() throws Exception {
        firm = FirmLoader.getConfig();
        interProcess = new InterProcess(firm);

        Daemon.main(null);
    }

    @Test
    public void balance() {
        // 잔액조회
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "089";
        firmBean.msgType 	= "0600300";
        firmBean.userId		= "SYSTEM";
        firmBean.mAccnt     = "70110001999557";

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void holder() {
        logger.info("예금주조회");
        FirmBean firmBean = new FirmBean();
        //PYS : 예금주 조회는 무조건 은행코드 099로 보내야 정상처리됨
        firmBean.bankCd 	= "099";
        firmBean.msgType 	= "0600400";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("bankCd", "088");
        firmBean.data.put("account", "110487944164");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void getExecutionResult() {
        // 처리결과조회 - PG_FIRM_MASTER로 전송하지 않고 직접 comm으로 전송함.
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "089";
        firmBean.msgType 	= "0600101";
        firmBean.userId		= "SYSTEM";
        firmBean.mAccnt     = "70110001999557";
        firmBean.data.put("orgSeqNo", "000152");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }

    @Test
    public void transfer() {
        // 이체
        String sender = "test";
        byte[] sendByte = sender.getBytes(StandardCharsets.UTF_8);
        try {
            //UTF-8
            //ksc5601
            //euc-kr
            sender = new String(sendByte, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info(sender);

        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "089";
        firmBean.msgType 	= "0100100";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("amount",1000);
        firmBean.data.put("recvBankCd","088");
        firmBean.data.put("recvAccount","110487944164");
        firmBean.data.put("sender", sender);
        firmBean.data.put("procType", "RS");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

    }


}
