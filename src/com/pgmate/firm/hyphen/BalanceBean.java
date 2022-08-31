package com.pgmate.firm.hyphen;

public class BalanceBean extends HyphenBaseBean{
    //Input Parameters
    private String seqNo;
    private String accountNo;

    //Output parameters
    private String sign;
    private String totalBalance;
    private String balance1;
    private String balance2;
    private String balance3;
    private String payableAmount;

    public BalanceBean() {
    }

    public BalanceBean(String seqNo, String accountNo) {
        this.seqNo = seqNo;
        this.accountNo = accountNo;
    }

    public String getSeqNo() {return seqNo;}
    public void setSeqNo(String data) {seqNo = data;}

    public String getAccountNo() {return accountNo;}
    public void setAccountNo(String data) {accountNo = data;}


    public String getSign() {return sign;}
    public void setSign(String data) {sign = data;}
    public String getTotalBalance() {return totalBalance;}
    public void setTotalBalance(String data) {totalBalance = data;}
    public String getBalance1() {return balance1;}
    public void setBalance1(String data) {balance1 = data;}
    public String getBalance2() {return balance2;}
    public void setBalance2(String data) {balance2 = data;}
    public String getBalance3() {return balance3;}
    public void setBalance3(String data) {balance3 = data;}
    public String getPayableAmount() {return payableAmount;}
    public void setPayableAmount(String data) {payableAmount = data;}

    public String toString() {
        return "\nsign="+sign+
                "\ntotalBalance="+totalBalance+
                "\nbalance1="+balance1+
                "\nbalance2="+balance2+
                "\nbalance3="+balance3+
                "\npayableAmount="+payableAmount;
    }
}
