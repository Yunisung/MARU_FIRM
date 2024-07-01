package com.pgmate.firm.coocon;

public class CooconBean {
    private long index;
    private String bankCd;

    private String reqUrl;
    private String reqData;

    private String resData;
    private String resultCode;
    private String resultMsg;

    public String getBankCd() {
        return bankCd;
    }

    public long getIndex() {
        return index;
    }

    public String getReqData() {
        return reqData;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setBankCd(String bankCd) {
        this.bankCd = bankCd;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public void setReqData(String reqData) {
        this.reqData = reqData;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setResData(String resData) {
        this.resData = resData;
    }

    public String getResData() {
        return resData;
    }

}
