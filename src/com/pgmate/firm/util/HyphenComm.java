package com.pgmate.firm.util;

import com.google.gson.Gson;
import com.pgmate.firm.conf.ServerBean;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.ksnet.FBHeaderBean;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final String defaultURL = "https://cmsars.ksnet.co.kr/";

    public HyphenComm() { }

    public String connect(HyphenBean bean) {
        FBHeaderBean resHeaderBean = null;

        JSONObject apiRes = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        StringBuffer stringBuffer = new StringBuffer();

        String jsonParams = new Gson().toJson(bean);
        String urlAddress = defaultURL + bean.getSendurl();
        logger.info("SEND-URL : " + urlAddress);

        try {
            URL url = new URL(urlAddress);
            String postData = "JSONData="+jsonParams;
            logger.info("request : {} ", postData);

            byte[] postDataBytes = postData.getBytes("utf-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.getOutputStream().write(postDataBytes);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"), conn.getContentLength());
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine);
            }
            bufferedReader.close();

            String result = stringBuffer.toString();
            logger.info("response : " + result);

        } catch (Exception e) {
            logger.info(e.getMessage().toString());
        }

        return stringBuffer.toString();
    }

}
