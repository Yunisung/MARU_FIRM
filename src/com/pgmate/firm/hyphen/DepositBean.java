package com.pgmate.firm.hyphen;

public class DepositBean extends HyphenBaseBean{
    //Input parameters
    private String seqNo;
    private String outAccount;
    private long amount;
    private String inBankCode;
    private String inAccount;
    private String inPrintContent;

    //Output parameters
    private String tradeTime;
    private String sign;
    private String balance;
    private String svcCharge;

    public DepositBean() {
    }

    public DepositBean(String seqNo, String outAccount, long amount, String inBankCode, String inAccount, String inPrintContent) {
        this.seqNo = seqNo;
        this.outAccount = outAccount;
        this.amount = amount;
        this.inBankCode = inBankCode;
        this.inAccount = inAccount;
        this.inPrintContent = inPrintContent;
    }

    public String getSeqNo() { return seqNo; }
    public void setSeqNo(String data) { seqNo = data; }
    public String getOutAccount() { return outAccount; }
    public void setOutAccount(String data) { outAccount = data; }
    public long getAmount() { return amount; }
    public void setAmount(long data) { amount = data; }
    public String getInBankCode() { return inBankCode; }
    public void setInBankCode(String data) { inBankCode = data; }
    public String getInAccount() { return inAccount; }
    public void setInAccount(String data) { inAccount = data; }
    public String getInPrintContent() { return inPrintContent; }
    public void setInPrintContent(String data) { inPrintContent = data; }
    public String getTradeTime() { return tradeTime; }
    public void setTradeTime(String data) { tradeTime = data; }
    public String getSign() { return sign; }
    public void setSign(String data) { sign = data; }
    public String getBalance() { return balance; }
    public void setBalance(String data) { balance = data; }
    public String getSvcCharge() { return svcCharge; }
    public void setSvcCharge(String data) { svcCharge = data;}
}
