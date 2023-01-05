package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.main.Daemon;
import com.pgmate.firm.server.executor.InterExcuter;
import com.pgmate.firm.server.executor.InterHyphenFirmExcuter;
import com.pgmate.firm.server.executor.InterKsnetExcuter;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import com.pgmate.lib.util.map.SharedMap;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class InterHyphenFirmExecutorTest {

    InterProcess interProcess;
    Firm firm;
    private static Logger logger = LoggerFactory.getLogger(InterHyphenFirmExecutorTest.class);


    @Before
    public void setup() throws Exception {
        firm = FirmLoader.getConfig();
        interProcess = new InterProcess(firm);
        Daemon.main(null);
    }

    @Test
    public void holder() {
        logger.info("예금주조회");
        FirmBean firmBean = new FirmBean();
        //PYS : 예금주 조회는 무조건 은행코드 099로 보내야 정상처리됨
        firmBean.bankCd 	= "099";
        firmBean.msgType 	= "0600400";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("bankCd", "032");
        firmBean.data.put("account", "087120852531");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));
    }



}
