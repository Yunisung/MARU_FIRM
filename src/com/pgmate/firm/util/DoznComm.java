package com.pgmate.firm.util;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.pgmate.firm.conf.ServerBean;
import com.pgmate.firm.dozn.DoznBean;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBean;
import com.pgmate.firm.ksnet.FBHeaderBean;
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

public class DoznComm {
    private static final Logger logger = LoggerFactory.getLogger(com.pgmate.firm.util.DoznComm.class);
    private SmsGw smsGw = null;

    //개발
    private static final String defaultURL = "https://test-gw-firm.dozn.co.kr/";
    //운영
//    private static final String defaultURL = "https://firmapi-pub.dozn.co.kr/";

    public DoznComm() { }

    public String connect(DoznBean bean) {

        StringBuffer stringBuffer = new StringBuffer();
        String urlAddress = defaultURL + bean.getUrl();
        logger.info("SEND-URL : " + urlAddress);

        try {
            URL url = new URL(urlAddress);
            String postData = bean.getReqData();
            logger.info("REQUEST DATA: {} ", postData);

            byte[] postDataBytes = postData.getBytes("utf-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

            String result = stringBuffer.toString();
            logger.info("RESPONSE DATA : " + result);

        } catch (Exception e) {
            logger.error("DoznComm Exception : {}", e.getMessage());
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

}
