package com.pgmate.firm.util;

import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.coocon.CooconBean;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class CooconComm {
    private static final Logger logger = LoggerFactory.getLogger(com.pgmate.firm.util.CooconComm.class);
    private SmsGw smsGw = null;
    private Firm firm = null;

    //개발
    private String defaultURL = "https://dev2.coocon.co.kr:8443/sol/gateway/webilling_wapi.jsp";
    private String kycURL = "https://dev2.coocon.co.kr:8443/sol/gateway/vapg_wapi.jsp";

    //운영
//    private String defaultURL = "https://gw.coocon.co.kr/sol/gateway/webilling_wapi.jsp"; //운영
//    private String kycURL = "https://apigw.coocon.co.kr/sol/gateway/vapg_wapi.jsp"; //운영


    public CooconComm(Firm firm) {
        this.firm = firm;
    }

    public String connect(CooconBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get(bean.getBankCd());

        String urlAddress = defaultURL;
        logger.info("SEND_URL : [{}]", urlAddress);

        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlAddress);
            String reqData = bean.getReqData();
            logger.info("REQUEST_DATA : [{}]", reqData);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());

            JSONObject inputObj = new JSONObject();
            inputObj.put("REQ_DATA", reqData);

            os.write(inputObj.toString());
            os.flush();
            os.close();

            DataInputStream in = new DataInputStream(conn.getInputStream());
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int bcount = 0;
            byte[] buf = new byte[2048];
            while(true) {
                int n = in.read(buf);
                if (n == -1) break;
                bout.write(buf, 0, n);
            }

            bout.flush();
            byte[] resMessage = bout.toByteArray();
            conn.disconnect();

            result = new String(resMessage, "EUC-KR");
            result = result.replaceAll("\r\n", "");
            result = result.replaceAll("\r", "");
            result = result.replaceAll("\n", "");
            result = result.trim();
        } catch (MalformedURLException e) {
            logger.info("CooconComm MalformedURLException");
        } catch (IOException e) {
            logger.error("CooconComm Exception : [{}]", e.getMessage());
        }

        return result;
    }

    public String connectKyc(CooconBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get(bean.getBankCd());

        String urlAddress = kycURL;
        logger.info("SEND_URL : [{}]", urlAddress);

        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlAddress);
            String reqData = bean.getReqData();
            logger.info("REQUEST_DATA : [{}]", reqData);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setUseCaches(false);

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            reqData = URLEncoder.encode(URLEncoder.encode(reqData, "UTF-8"), "UTF-8");
            String postString = "JSONData=" + reqData;

            os.write(postString);
            os.flush();
            os.close();

            DataInputStream in = new DataInputStream(conn.getInputStream());
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int bcount = 0;
            byte[] buf = new byte[2048];
            while(true) {
                int n = in.read(buf);
                if (n == -1) break;
                bout.write(buf, 0, n);
            }

            bout.flush();
            result = new String(bout.toByteArray(), "UTF-8");
            conn.disconnect();

            result = result.trim();
        } catch (Exception e) {
            logger.error("CooconComm KYC Exception : [{}]", e.getMessage());
        }

        return result;
    }
}
