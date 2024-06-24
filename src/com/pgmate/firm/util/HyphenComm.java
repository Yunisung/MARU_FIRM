package com.pgmate.firm.util;

import com.google.gson.Gson;
import com.pgmate.firm.conf.ServerBean;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import com.pgmate.lib.util.lang.CommonUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HyphenComm {
    private static final Logger logger = LoggerFactory.getLogger(com.pgmate.firm.util.HyphenComm.class);
    private SmsGw smsGw = null;

    //개발
//    private static final String defaultURL = "https://cmsarstest.ksnet.co.kr/";
    //운영
    private static final String defaultURL = "https://fbapi.hyphen.im/firmbk/auth/";
    private static final String distURL = "https://fbapi.hyphen.im/firmbk/";

    public HyphenComm() { }

    public String connect(HyphenBean bean) {
        FBHeaderBean resHeaderBean = null;

        JSONObject apiRes = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        StringBuffer stringBuffer = new StringBuffer();

        String jsonParams = new Gson().toJson(bean);
        String sendUrl = bean.getSendurl();
        String urlAddress = defaultURL + sendUrl;
        //하이픈 대행 처리
        if(sendUrl.equals("rfb/retail/deposit") || sendUrl.equals("rfb/retail/inquiry/transfer") || sendUrl.equals("rfb/retail/inquiry/balance")) {
            urlAddress = distURL + sendUrl;
        }

        logger.info("SEND-URL : " + urlAddress);

        HttpsURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(urlAddress);
            String postData = "JSONData="+jsonParams;
            logger.info("request : {} ", postData);

            byte[] postDataBytes = postData.getBytes("utf-8");

            conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");

            conn.getOutputStream().write(postDataBytes);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            is = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine.replace("\\", ""));
            }
            bufferedReader.close();

            String result = stringBuffer.toString();
            logger.info("response : " + result);

        } catch (Exception e) {
            logger.error("hyphen connection error: ", e);
        } finally {
            if(is != null) try {is.close();} catch (IOException e) {}
            if(conn != null) conn.disconnect();
        }

        return stringBuffer.toString();
    }

}
