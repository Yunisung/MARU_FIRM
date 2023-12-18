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

//        String reqJson = GsonUtil.toJson(firmBean);
//        logger.info("reqJson: {} ", reqJson);
//        String send = interProcess.execute(reqJson);
//        logger.info("<- {} [{}]", FirmUtil.KSNET, CommonUtil.toString(send));

        comm(firmBean);
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
