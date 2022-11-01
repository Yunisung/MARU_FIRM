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

public class InterProcessTest {

    InterProcess interProcess;
    Firm firm;
    Server ss;
    private static Logger logger = LoggerFactory.getLogger(InterProcessTest.class);


    @Before
    public void setup() throws Exception {
        firm = FirmLoader.getConfig();
        interProcess = new InterProcess(firm);

        Daemon.main(null);
    }

    @Test
    public void balance() {
        // 0600300
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "089";
        firmBean.msgType 	= "0600300";
        firmBean.userId		= "SYSTEM";
        firmBean.mAccnt     = "70110001999557";

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.HYPHEN, CommonUtil.toString(send));

        //KsnetComm comm	= new KsnetComm(firm.server);
        //FBHeaderBean resHeader = comm.ksnet(headerBean);
    }



}
