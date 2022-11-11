package com.pgmate.firm.test;

import com.google.gson.Gson;
import com.pgmate.firm.hyphen.BalanceBean;
import com.pgmate.firm.hyphen.HyphenBean;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FirmTest {

    public FirmTest() {

    }

    public static void main(String[] args) throws Exception{
        JSONObject apiRes = new JSONObject();
        JSONParser jsonParser = new JSONParser();

        BalanceBean balanceBean = new BalanceBean();
        balanceBean.setCompCode("BKWIN001");
        balanceBean.setBankCode("039");
        balanceBean.setSeqNo("000001");
        balanceBean.setAccountNo("8003344299939");

        HyphenBean hyphenBean = new HyphenBean();
        hyphenBean.setKscode("6596");
        hyphenBean.setEkey("E3F93E5201969E953066C7911F6AC8BB");
        hyphenBean.setMsalt("MA01");
        hyphenBean.setReqdata(balanceBean);
        String jsonParams = new Gson().toJson(hyphenBean);


        String urlAddress = "https://cmsapitest.ksnet.co.kr/ksnet/rfb/retail/inquiry/balance";
        try {
            URL url = new URL(urlAddress);
            String postData = "JSONData="+jsonParams;
            System.out.println(postData);

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(postData);
            BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bf.readLine()) != null) {
                System.out.println(line);
                stringBuffer.append(line);
            }

            apiRes = (JSONObject) jsonParser.parse(stringBuffer.toString());

            balanceBean.setBalance1(apiRes.get("balance1").toString());
            balanceBean.setBalance2(apiRes.get("balance2").toString());
            balanceBean.setBalance3(apiRes.get("balance3").toString());
            balanceBean.setTotalBalance(apiRes.get("totalBalance").toString());
            balanceBean.setSign(apiRes.get("sign").toString());
            balanceBean.setPayableAmount(apiRes.get("payableAmount").toString());

            hyphenBean.setReplyCode(apiRes.get("replyCode").toString());
            hyphenBean.setSuccessYn(apiRes.get("successYn").toString());

        } catch (Exception e) {
            hyphenBean.setReplyCode(apiRes.get("replyCode").toString());
            hyphenBean.setSuccessYn(apiRes.get("successYn").toString());
        }

        System.out.println("[Balance Result] " + balanceBean.toString());

//        URL url = new URL(urlAddress);
//        String postData = "JSONData="+jsonParams;
//        System.out.println(postData);
//
//        URLConnection conn = url.openConnection();
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//        try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
//            dos.writeBytes(postData);
//        }
//
//
//
//        try (BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
//            String line;
//            StringBuffer stringBuffer = new StringBuffer();
//            while ((line = bf.readLine()) != null) {
//                System.out.println(line);
//                stringBuffer.append(line);
//            }
//
//            apiRes = (JSONObject) jsonParser.parse(stringBuffer.toString());
//            balanceBean.setBalance1(apiRes.get("balance1").toString());
//            balanceBean.setBalance2(apiRes.get("balance2").toString());
//            balanceBean.setBalance3(apiRes.get("balance3").toString());
//            balanceBean.setTotalBalance(apiRes.get("totalBalance").toString());
//            balanceBean.setSign(apiRes.get("sign").toString());
//            balanceBean.setReplyCode(apiRes.get("replyCode").toString());
//            balanceBean.setSuccessYn(apiRes.get("successYn").toString());
//            balanceBean.setPayableAmount(apiRes.get("payableAmount").toString());
//        } catch (Exception e) {
//            balanceBean.setReplyCode(apiRes.get("replyCode").toString());
//            balanceBean.setSuccessYn(apiRes.get("successYn").toString());
//        }
//
//        System.out.println("[Balance Result] " + balanceBean.toString());
    }
}
