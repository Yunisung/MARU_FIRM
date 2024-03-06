package com.pgmate.firm.dozn;

public class DoznBean {
    private long index;
    private String bankCd;

    //REQ DATA
    private String url;
    private String reqData;

    //RES DATA
    private String status;
    private String vanTrxId;
    private String resData;
    private String resultCode;
    private String resultMsg;

    public DoznBean() {}

    public long getIndex() {
        return index;
    }

    public void setIndex(long idx) {
        this.index = idx;
    }

    public String getBankCd() { return bankCd; }

    public void setBankCd(String bankCd) { this.bankCd = bankCd; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReqData() {
        return reqData;
    }

    public void setReqData(String data) {
        this.reqData = data;
    }

    public String getStatus() {
        return status;
    }

    public String getVanTrxId() {
        return vanTrxId;
    }

    public void setVanTrxId(String vanTrxId) {
        this.vanTrxId = vanTrxId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResData() {
        return resData;
    }

    public void setResData(String resData) {
        this.resData = resData;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }
}
