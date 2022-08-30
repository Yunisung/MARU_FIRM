package com.pgmate.firm.hyphen;

import java.util.ArrayList;

public class HyphenBean {
    //Commmon Parameters
    private String kscode;
    private String ekey;
    private String msalt;

    private ArrayList<HyphenBaseBean> reqdata = new ArrayList<>();

    //Response Parameter
    private String resdata;
    protected String replyCode;
    protected String successYn;

    protected long index;
    protected String sendurl;

    public HyphenBean() {
    }

    public long getIndex() { return index;}
    public void setIndex(long data) {index = data;}

    public String getSendurl() { return sendurl;}
    public void setSendurl(String data) {sendurl = data;}

    public String getKscode() {return kscode;}
    public void setKscode(String data) {kscode=data;}

    public String getEkey() {return ekey;}
    public void setEkey(String data) {ekey=data;}

    public String getMsalt() {return msalt;}
    public void setMsalt(String data) {msalt = data;}

    public HyphenBaseBean getReqData(int index) { return reqdata.get(index);}
    public void setReqdata(HyphenBaseBean data) {reqdata.add(data);}

    public String getResData() {return resdata;}
    public void setResdata(String data) { resdata = data;}

    public String getReplyCode() {return replyCode;}
    public void setReplyCode(String data) {replyCode = data;}

    public String getSuccessYn() {return successYn;}
    public void setSuccessYn(String data) {successYn = data;}
    
}
