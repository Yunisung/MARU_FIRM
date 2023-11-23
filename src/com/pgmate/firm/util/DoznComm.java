package com.pgmate.firm.util;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.conf.ServerBean;
import com.pgmate.firm.dozn.DoznBean;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.lib.util.lang.CommonUtil;
import kr.co.dozn.secure.base.CryptoUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class DoznComm {
    private static final Logger logger = LoggerFactory.getLogger(com.pgmate.firm.util.DoznComm.class);
    private SmsGw smsGw = null;
    private Firm firm = null;

    //����
    private static final String defaultURL = "https://test-gw-firm.dozn.co.kr/";
    private static final String kycURL = "https://test-vacc-pub.dozn.co.kr/";
    //�
    private static final String defaultURL = "https://firmapi-pub.dozn.co.kr/";
//    private static final String key = "bkwinners0123456";
//    private static final String kycURL = "https://vacc-pub.dozn.co.kr/";

    private static final String key = "f657a924f4db69f745909f462c0f1a2e";
    private static final String iv = "4a9acfb04bf38a5b";

    public DoznComm(Firm firm) {
        this.firm = firm;
    }

    public String connect(DoznBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get("034");

        StringBuffer stringBuffer = new StringBuffer();
        String urlAddress = defaultURL + bean.getUrl();
        logger.info("SEND-URL : " + urlAddress);

        //��ȣȭ��� ����
        CryptoUtil cryptoUtil = CryptoUtil.getInstance(key, iv);

        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlAddress);
            String postData = bean.getReqData();
            logger.info("REQUEST DATA: {} ", postData);

            //��ȣȭ ����
            if(bankBean.crypto.equals("Y")) {
                postData = cryptoUtil.encrypt(postData, "UTF-8");
            }

            //logger.info("REQUEST CRYPTO DATA : {}", postData);

            byte[] postDataBytes = postData.getBytes("utf-8");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");

            conn.getOutputStream().write(postDataBytes);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();

            InputStream is = null;

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine.replace("\\", ""));
            }
            bufferedReader.close();

            result = stringBuffer.toString();

            //��ȣȭ ����
            if(responseCode == HttpsURLConnection.HTTP_OK && bankBean.crypto.equals("Y")) {
                result = cryptoUtil.decrypt(result, "UTF-8");
            }

            logger.info("RESPONSE DATA : " + result);

            conn.disconnect();

        } catch (Exception e) {
            logger.error("DoznComm Exception : {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }

        return result;
    }

    public String connectKyc(DoznBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get("007");

        StringBuffer stringBuffer = new StringBuffer();
        String urlAddress = kycURL + bean.getUrl();
        logger.info("SEND-URL : " + urlAddress);

        //��ȣȭ��� ����
        //CryptoUtil cryptoUtil = CryptoUtil.getInstance(key, iv);

        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlAddress);
            String postData = bean.getReqData();
            logger.info("REQUEST DATA: {} ", postData);

            //��ȣȭ ����
//            if(bankBean.crypto.equals("Y")) {
//                postData = cryptoUtil.encrypt(postData, "UTF-8");
//            }

            byte[] postDataBytes = postData.getBytes("utf-8");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("api-key", bankBean.api_key);
            conn.setRequestProperty("org-c", bankBean.org_code);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");

            conn.getOutputStream().write(postDataBytes);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();
            bean.setStatus(CommonUtil.toString(responseCode));

            InputStream is = null;

            if(responseCode == HttpsURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine.replace("\\", ""));
            }
            bufferedReader.close();

            result = stringBuffer.toString();

            //��ȣȭ ����
//            if(responseCode == HttpsURLConnection.HTTP_OK && bankBean.crypto.equals("Y")) {
//                result = cryptoUtil.decrypt(result, "UTF-8");
//            }

            logger.info("RESPONSE DATA : " + result);

            conn.disconnect();

        } catch (Exception e) {
            logger.error("DoznComm Exception : {}", e.getMessage());
            e.printStackTrace();
        } finally {
            if(conn != null)
                conn.disconnect();
        }

        return result;
    }
}
