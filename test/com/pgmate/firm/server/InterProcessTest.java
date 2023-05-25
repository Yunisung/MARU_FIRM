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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
        firmBean.mAccnt     = "70022000000008";

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
//        String send = interProcess.execute(reqJson);
//        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

        comm(firmBean);
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
        firmBean.data.put("socialNumber", "");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.HYPHEN, CommonUtil.toString(send));
//        comm(firmBean);
    }

    @Test
    public void getExecutionResult() {
        // 처리결과조회
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "089";
        firmBean.msgType 	= "0600101";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("orgSeqNo", "000169");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
//        String send = interProcess.execute(reqJson);
//        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

        comm(firmBean);
    }

    @Test
    public void transfer() {
        // 이체
        String sender = "홍옥주";
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
        firmBean.bankCd 	= "039";
        firmBean.msgType 	= "0100100";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("amount",1000);
        firmBean.data.put("recvBankCd","003");
        firmBean.data.put("recvAccount","51807417401012");
        firmBean.data.put("sender", sender);
        firmBean.data.put("procType", "RS");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

    }

    @Test
    public void withdrawAccountReg케이() {
        // 출금계좌등록
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd 	= "089";
        firmBean.msgType 	= "0900400";
        firmBean.userId		= "SYSTEM";
        firmBean.data.put("trxType", "1");         // (거래구분) '1':신규, '4':해지, '8':변경, '9':조회
        firmBean.data.put("virtualAccount", "70022000105591");  // (가상계좌번호)
        firmBean.data.put("withdrawBankCd", "032");  // (출금은행코드) PG_CODE 테이블 참조
        firmBean.data.put("withdrawAccount", "087120852531"); // (출금계좌번호)
        firmBean.data.put("customerName", "오세창");    // (고객명)

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

//        ServerTest(reqJson);
    }

    @Test
    public void withdrawAccountReg경남() {
        // 출금계좌등록
        FirmBean firmBean = new FirmBean();
        firmBean.bankCd    = "039";
        firmBean.msgType    = "0900400";
        firmBean.userId      = "SYSTEM";
        firmBean.data.put("trxType", "3");         // (거래구분) '1':신규, '3':해지
        firmBean.data.put("companyCd", "MBR00246");
        firmBean.data.put("virtualAccount", "8008308818739");  // (가상계좌번호)
        firmBean.data.put("withdrawBankCd", "088");  // (출금은행코드) PG_CODE 테이블 참조
        firmBean.data.put("withdrawAccount", "100035419428"); // (출금계좌번호)
        firmBean.data.put("customerName", "김정미");    // (고객명)
        firmBean.data.put("regType", "2");
        firmBean.data.put("identity", "970105");

        String reqJson = GsonUtil.toJson(firmBean);
        logger.info("reqJson: {} ", reqJson);
        String send = interProcess.execute(reqJson);
        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

//      comm(reqJson);
    }

    public FirmBean comm(FirmBean firmBean){

        Socket socket = null;
        OutputStream output = null;
        InputStream input = null;
        String reqJson = GsonUtil.toJson(firmBean);
        String resJson = "";
        long time = System.currentTimeMillis();
        try{
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
