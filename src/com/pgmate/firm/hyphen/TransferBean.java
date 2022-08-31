package com.pgmate.firm.hyphen;

public class TransferBean extends HyphenBaseBean {
    //Input parameter
    private String seqNo;
    private String oriSeqNo;

    //output parameter
    private String outAccountNo;
    private String inAccountNo;
    private String amount;
    private String svcCharge;
    private String tradeTime;
    private String resultCode;
    private String procBankCode;
    private String payerNo;

    public TransferBean() {

    }

    public TransferBean(String seqNo, String oriSeqNo) {
        this.seqNo = seqNo;
        this.oriSeqNo = oriSeqNo;
    }

    public String getSeqNo() { return seqNo; }
    public void setSeqNo(String data) { this.seqNo = data; }
    public String getOriSeqNo() { return oriSeqNo; }
    public void setOriSeqNo(String data) { this.oriSeqNo = data; }
    public String getOutAccountNo() { return outAccountNo; }
    public void setOutAccountNo(String data) { this.outAccountNo = data; }
    public String getInAccountNo() { return inAccountNo; }
    public void setInAccountNo(String data) { this.inAccountNo = data; }
    public String getAmount() { return amount; }
    public void setAmount(String data) { this.amount = data; }
    public String getSvcCharge() { return svcCharge; }
    public void setSvcCharge(String data) { this.svcCharge = data; }
    public String getTradeTime() { return tradeTime; }
    public void setTradeTime(String data) { this.tradeTime = tradeTime; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String data) { this.resultCode = data; }
    public String getProcBankCode() { return procBankCode; }
    public void setProcBankCode(String data) { this.procBankCode = data; }
    public String getPayerNo() { return payerNo; }
    public void setPayerNo(String data) { this.payerNo = data; }
}
