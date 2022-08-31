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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HyphenComm {
    private static final Logger logger = LoggerFactory.getLogger(com.pgmate.firm.util.HyphenComm.class);
    private ServerBean conf = null;
    private SmsGw smsGw = null;

    //개발
//    private static final String defaultURL = "https://cmsapitest.ksnet.co.kr/ksnet/";
    //운영
    private static final String defaultURL = "https://cmsapi.ksnet.co.kr/ksnet/";

    public HyphenComm(ServerBean conf) { this.conf = conf;}

    public String connect(HyphenBean bean) {
        FBHeaderBean resHeaderBean = null;

        JSONObject apiRes = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        StringBuffer stringBuffer = new StringBuffer();

        String jsonParams = new Gson().toJson(bean);
        logger.info("Hyphen ReqData : " + jsonParams);

        String urlAddress = defaultURL + bean.getSendurl();
        logger.info("SEND-URL : " + urlAddress);
        try {
            URL url = new URL(urlAddress);
            String postData = "JSONData="+jsonParams;
            System.out.println(postData);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(postData);
            BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
            String line;

            while ((line = bf.readLine()) != null) {
                System.out.println(line);
                stringBuffer.append(line);
            }

            apiRes = (JSONObject) jsonParser.parse(stringBuffer.toString());

        } catch (Exception e) {
            logger.info(e.getMessage().toString());
        }

        return stringBuffer.toString();
    }

}
