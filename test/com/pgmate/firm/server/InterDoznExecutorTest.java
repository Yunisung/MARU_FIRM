package com.pgmate.firm.server;

import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.FirmLoader;
import com.pgmate.firm.inter.FirmBean;
import com.pgmate.firm.main.Daemon;
import com.pgmate.firm.util.FirmUtil;
import com.pgmate.lib.util.gson.GsonUtil;
import com.pgmate.lib.util.lang.CommonUtil;
import kr.co.dozn.secure.base.CryptoUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

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
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));

//        comm(firmBean);
    }

    @Test
    public void holder() {
        logger.info("예금주조회");
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0600400";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("bankCd", "032");
        firmBean.data.put("account", "087120852531");
        firmBean.data.put("socialNumber", ""); // 주민번호

        comm(firmBean);

//        String reqJson = GsonUtil.toJson(firmBean);
//        logger.info("reqJson: {} ", reqJson);
//        String send = interProcess.execute(reqJson);
//        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void Aggregate() {
        logger.info("집계");
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0700100";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("searchDate", "20230921");
        firmBean.data.put("searchCode", "1"); //1: 예금주조회 2: 점유인증 3. 이체

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void transfer() {
        // 이체
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0100100";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("amount",1000);
        firmBean.data.put("recvBankCd","007");
        firmBean.data.put("recvAccount","113000509251");
        firmBean.data.put("sender", "");
        firmBean.data.put("procType", "CS");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));

    }

    @Test
    public void getExecutionResult() {
        // 처리결과조회
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "0600101";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("orgSeqNo", "000455");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void AccountAuth() {
        // 계좌점유인증
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ACCAUTH";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("recvBankCd", "088");
        firmBean.data.put("recvAccount", "110487944164");
        firmBean.data.put("sender", "BK1234");

//        comm(firmBean);

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void AccountAuthTest() {
        // 계좌점유인증 결과조회
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ACCCHCK";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("orgSeqNo", "000458");

//        comm(firmBean);

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void ArsAuth() {
        // ARS인증
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ARSAUTH";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("phoneNo", "01091697725");
        firmBean.data.put("authNo", "11");

        //comm(firmBean);

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void ArsCheck() {
        // ARS인증 결과조회
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "034";
        firmBean.msgType 	= "ARSCHCK";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("orgSeqNo", "000449");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.DOZN, CommonUtil.toString(send));
    }

    @Test
    public void SeedCBCEncrypt() throws Exception{
        String key = "f657a924f4db69f745909f462c0f1a2e";
        String IV = "4a9acfb04bf38a5b";
        String text = "{\"telegram_no\":378,\"drw_bank_code\":\"034\",\"drw_account\":\"019107542861\",\"api_key\":\"e98722d5-a701-4731-ae12-5e09b72ca4be\",\"org_code\":\"10000614\"}";

        CryptoUtil util = CryptoUtil.getInstance(key, IV);
        String enc2 = util.encrypt(text);
        logger.info("DOZN Enc : {}", enc2);

        String text2 = "lo5ii5kfExAfIkTM7prQbaZsRi/EigMnAM8wT8pGOvAjKShWkbnOdu7nIq+97HJWDt2Ad0Ndt/8d5pKDjJUf+osODjoMrYq6ZWDOXJ9gmnbw/CZxHnQQpAFUoJTXbFk1MB5hb5v7y5mCwHw7E7Ek5RAjzSYtR7U16NS2N8pf5h1dMD9syMvgQIr+h8ci6lKX";
        text2 = enc2;
        String dec2 = util.decrypt(text2);
        logger.info("DOZN Dec : {}", dec2);

    }


    public FirmBean comm(FirmBean firmBean){

        Socket socket = null;
        OutputStream output = null;
        InputStream input = null;
        String reqJson = GsonUtil.toJson(firmBean);
        String resJson = "";
        long time = System.currentTimeMillis();
        try{
//            socket = new Socket("10.100.200.10", 10006);
            socket = new Socket("10.100.100.13", 10006);
            socket.setSoTimeout(40000);

            output = socket.getOutputStream();
            output.write(reqJson.getBytes());
            output.flush();

            input = socket.getInputStream();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int bcount = 0;
            byte[] buf = new byte[2048];
            int read_retry_count = 0;
            while(true) {
                int n = input.read(buf);
                if ( n > 0 ) { bcount += n; bout.write(buf,0,n); }
                else if (n == -1) break;
                else  { // n == 0
                    if (++read_retry_count >= 5)
                        throw new IOException("inputstream-read-retry-count(5) exceed !");
                }
                if(input.available() == 0){ break; }
            }
            bout.flush();
            byte[] res = bout.toByteArray();
            bout.close();

            firmBean = (FirmBean)GsonUtil.fromJson(new String(res), FirmBean.class);

        }catch(Exception e){
            firmBean.resultCd = "XXXX";
            firmBean.resultMsg = "펌뱅킹 시스템과의 통신장애 :"+e.getMessage();
        }finally{
            logger.info("-> FIRM : [{}]",reqJson);
            logger.info("<- FIRM : [{}],{}",resJson,(System.currentTimeMillis()-time));

            try{
                if(input != null){ input.close();}
                if(output != null){ output.close();}
                if(socket != null){ socket.close();}
            }catch(Exception ex){

            }
        }

        return firmBean;

    }
}
