package com.pgmate.firm.hyphen;

public class HolderBean extends HyphenBaseBean{
    //Input Parameters
    private String seqNo;
    private String agencyYn;
    private String compAccountNo;
    private String accountBankCode;
    private String accountNo;
    private String socialId;
    private String amount;

    //Output Parameters
    private String replyCode;
    private String accountName;
    private String successYn;

    public HolderBean() {
    }

    public String getSeqNo() {return seqNo;}
    public void setSeqNo(String seqNo) {this.seqNo = seqNo;}

    public String getAgencyYn() {return agencyYn;}
    public void setAgencyYn(String agencyYn) {this.agencyYn = agencyYn;}

    public String getCompAccountNo() {return compAccountNo;}
    public void setCompAccountNo(String compAccountNo) {this.compAccountNo = compAccountNo;}

    public String getAccountBankCode() {return accountBankCode;}
    public void setAccountBankCode(String accountBankCode) {this.accountBankCode = accountBankCode;}

    public String getAccountNo() {return accountNo;}
    public void setAccountNo(String accountNo) {this.accountNo = accountNo;}

    public String getSocialId() {return socialId;}
    public void setSocialId(String socialId) {this.socialId = socialId;}

    public String getAmount() {return amount;}
    public void setAmount(String amount) {this.amount = amount;}

    public String getReplyCode() {return replyCode;}
    public void setReplyCode(String replyCode) {this.replyCode = replyCode;}
    public String getAccountName() {return accountName;}
    public void setAccountName(String accountName) {this.accountName = accountName;}
    public String getSuccessYn() {return successYn;}
    public void setSuccessYn(String successYn) {this.successYn = successYn;}

}