package com.pgmate.firm.util;

import cc.checkpay.common.CcSecurityUtil;
import com.google.gson.Gson;
import com.pgmate.firm.conf.BankBean;
import com.pgmate.firm.conf.Firm;
import com.pgmate.firm.coocon.*;
import net.sf.json.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CooconComm {
    private static final Logger logger = LoggerFactory.getLogger(com.pgmate.firm.util.CooconComm.class);
    private SmsGw smsGw = null;
    private Firm firm = null;

    //개발
//    private String defaultURL = "https://dev2.coocon.co.kr:8443/sol/gateway/";
//    private String kycURL = "https://dev2.coocon.co.kr:8443/sol/gateway/vapg_wapi.jsp";
//    private String accountAuthURL = "https://dev.checkpay.co.kr/";

    //운영
    private String defaultURL = "https://gw.coocon.co.kr/sol/gateway/";
    private String kycURL = "https://apigw.coocon.co.kr/sol/gateway/vapg_wapi.jsp";
    private String accountAuthURL = "https://www.checkpay.co.kr/";

    public CooconComm(Firm firm) {
        this.firm = firm;
    }

    public String connect(CooconBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get(bean.getBankCd());

        String urlAddress = defaultURL + bean.getReqUrl();
        logger.info("SEND_URL : [{}]", urlAddress);

        HttpsURLConnection conn = null;

        try {
            URL url = new URL(urlAddress);
            String reqData = bean.getReqData().replaceAll("\\\\", "");
            logger.info("REQUEST_DATA : [{}]", reqData);

            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            conn.getOutputStream().write(reqData.getBytes("UTF-8"));
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

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
            return result;
        } catch (IOException e) {
            logger.error("CooconComm Exception : [{}]", e.getMessage());
            return result;
        } finally {
            if(conn != null) conn.disconnect();

        }

        return result;
    }

    public String connectKyc(CooconBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get(bean.getBankCd());

        String urlAddress = kycURL;
        logger.info("SEND_URL : [{}]", urlAddress);

        HttpsURLConnection conn = null;

        try {
            URL url = new URL(urlAddress);
            String reqData = bean.getReqData().replaceAll("\\\\", "");
            logger.info("REQUEST_DATA : [{}]", reqData);

            conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);


            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            reqData = URLEncoder.encode(URLEncoder.encode(reqData, "UTF-8"), "UTF-8");
            String postString = "JSONData=" + reqData;

            os.write(postString);
            os.flush();
            os.close();

            InputStream is = conn.getInputStream();
            InputStreamReader ir = new InputStreamReader(is, "EUC-KR");
            BufferedReader bufferedReader = new BufferedReader(ir);
            String inputLine;

            StringBuffer stringBuffer = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null)  {
                stringBuffer.append(inputLine.replace("\\", ""));
                logger.info("input : {}", inputLine);
            }
            bufferedReader.close();
            result = stringBuffer.toString();
            logger.info("Result : {}", result);
            logger.info("convert : {}", convertKR(result));


//            DataInputStream in = new DataInputStream(conn.getInputStream());
//            ByteArrayOutputStream bout = new ByteArrayOutputStream();
//            int bcount = 0;
//            byte[] buf = new byte[2048];
//            while(true) {
//                int n = in.read(buf);
//                if (n == -1) break;
//                bout.write(buf, 0, n);
//            }
//
//            bout.flush();
//            result = new String(bout.toByteArray(),"UTF-8");
//            logger.info("Result : {}", result);
//
//            logger.info("convert : {}", convertKR(result));

            conn.disconnect();

            result = result.trim();
        } catch (Exception e) {
            logger.error("CooconComm KYC Exception : [{}]", e.getMessage());
            return result;
        }

        return result;
    }

    public String convertKR(String s) {
        if(s == null) {
            return null;
        }

        try {
//            return new String(s.getBytes("8859_1"), "KSC5601");
//            return new String(s.getBytes("8859_1"), "EUC-KR");
//            return URLDecoder.decode((URLDecoder.decode(s, "8859_1")), "UTF-8");
            return new String(s.getBytes("UTF-8"), "MS949");
        }catch (Exception e) {
            return s;
        }

    }

    public String connectAccountAuth(CooconBean bean) {
        String result = "";
        BankBean bankBean = firm.bank.get(bean.getBankCd());

        String urlAddress = accountAuthURL;

        if(bean.getReqUrl().equals("accountAuth")) {
            urlAddress += "CPIF_AFFL_720.jct";
        } else if(bean.getReqUrl().equals("accountAuthCheck")) {
            urlAddress += "CPIF_AFFL_721.jct";
        }

        logger.info("SEND_URL : [{}]", urlAddress);

        //전송데이터 세팅
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String trx_dt = sdf.format(d);
        sdf.applyPattern("HHmmss");
        String trx_tm = sdf.format(d);

        Gson gson = new Gson();


        JSONObject param = new JSONObject();
        if(bean.getReqUrl().equals("accountAuth")) {
            CooconAccountAuthBean authBean = gson.fromJson(bean.getReqData(), CooconAccountAuthBean.class);
            param.put("fnni_cd", authBean.getFnni_cd());
            param.put("acct_no", authBean.getAcct_no());
            param.put("memb_nm", authBean.getMemb_nm());
            param.put("ptst_txt", authBean.getPtst_txt());
            param.put("verify_tp", authBean.getVerify_tp());
            param.put("verify_len", authBean.getVerify_len());
        } else if(bean.getReqUrl().equals("accountAuthCheck")) {
            CooconAccountAuthCheckBean authCheckBean = gson.fromJson(bean.getReqData(), CooconAccountAuthCheckBean.class);
            param.put("verify_tr_dt", authCheckBean.getVerify_tr_dt());
            param.put("verify_tr_no", authCheckBean.getVerify_tr_no());
            param.put("verify_val", authCheckBean.getVerify_val());
        }

        HttpURLConnection conn = null;
        BufferedWriter bwriter = null;
        DataInputStream in = null;
        ByteArrayOutputStream bout = null;

        try {
            //암호화 데이터 세팅
            String EV = CcSecurityUtil.EncryptAes256Base64(trx_dt + trx_tm + param.toJSONString(), bankBean.coocon_accountAuth_key, true);
            String W = CcSecurityUtil.getHmacSha256(param.toJSONString(), bankBean.coocon_accountAuth_key, true);

            String data = "ID=" + bankBean.coocon_accountAuth_code +
                    "&RQ_DTIME=" + trx_dt+trx_tm +
                    "&TNO=" + trx_dt+trx_dt +
                    "&EV=" + EV +
                    "&VV=" + W +
                    "&EM=AES" +
                    "&VM=HmacSHA256";

            URL url = new URL(urlAddress);
            logger.info("REQUEST_DATA : [{}]", data);

            conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(10000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            bwriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bwriter.write(data);
            bwriter.flush();

            in = new DataInputStream(conn.getInputStream());
            bout = new ByteArrayOutputStream();

            byte[] buf = new byte[2048];
            while(true) {
                int n = in.read(buf);
                if (n == -1) break;
                bout.write(buf, 0, n);
            }

            bout.flush();
        } catch (Exception e) {
            logger.error("CooconComm AccountAuth Exception : [{}]", e.getMessage());
            return result;
        }finally {
            try {
                if(bwriter != null) bwriter.close();
                if(in != null) in.close();
                if(bout != null) bout.close();
                if(conn != null) conn.disconnect();
            } catch (Exception se) {}
        }

        String respData = new String(bout.toByteArray());
        result = respData;

        try {
            net.sf.json.JSONObject rtn = net.sf.json.JSONObject.fromObject(respData);
            String rEV = rtn.getString("EV");
            String rVV = rtn.getString("VV");

            if(!CcSecurityUtil.VerifyMac(bankBean.coocon_accountAuth_key, rEV, rVV, "UTF-8", true)) {
                logger.info("Coocon 1원인증 암호화 검증 Faild");

            }
        } catch (Exception e) {
            logger.error("CooconComm AccountAuth Exception : [{}]", e.getMessage());
            return "";
        }

        return result;
    }

}
