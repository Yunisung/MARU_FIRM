package com.pgmate.firm.coocon;

/**
 * 이체 전문
 */
public class CooconTransferBean extends CooconBaseBean {
    private String TRT_INST_CD;         //취급기관코드
    private String TRSC_DT;             //거래일자
    private String TRSC_SEQ_NO;         //거래일련번호
    private String BANK_CD;             //입금은행코드
    private String ACCT_NO;             //입금계좌번호
    private String MO_BANK_CD;          //출금은행코드
    private String MO_ACCT_NO;          //출금계좌번호
    private String OUT_NAME;            //출금계좌성명
    private String IN_NAME;             //입금계좌성명
    private String TRSC_AMT;            //이체금액
    private String SEC_MARK;            //복기부호


    public String getTRT_INST_CD() {
        return TRT_INST_CD;
    }

    public void setTRT_INST_CD(String TRT_INST_CD) {
        this.TRT_INST_CD = TRT_INST_CD;
    }

    public String getTRSC_DT() {
        return TRSC_DT;
    }

    public void setTRSC_DT(String TRSC_DT) {
        this.TRSC_DT = TRSC_DT;
    }

    public String getTRSC_SEQ_NO() {
        return TRSC_SEQ_NO;
    }

    public void setTRSC_SEQ_NO(String TRSC_SEQ_NO) {
        this.TRSC_SEQ_NO = TRSC_SEQ_NO;
    }

    public String getBANK_CD() {
        return BANK_CD;
    }

    public void setBANK_CD(String BANK_CD) {
        this.BANK_CD = BANK_CD;
    }

    public String getACCT_NO() {
        return ACCT_NO;
    }

    public void setACCT_NO(String ACCT_NO) {
        this.ACCT_NO = ACCT_NO;
    }

    public String getMO_BANK_CD() {
        return MO_BANK_CD;
    }

    public void setMO_BANK_CD(String MO_BANK_CD) {
        this.MO_BANK_CD = MO_BANK_CD;
    }

    public String getMO_ACCT_NO() {
        return MO_ACCT_NO;
    }

    public void setMO_ACCT_NO(String MO_ACCT_NO) {
        this.MO_ACCT_NO = MO_ACCT_NO;
    }

    public String getOUT_NAME() {
        return OUT_NAME;
    }

    public void setOUT_NAME(String OUT_NAME) {
        this.OUT_NAME = OUT_NAME;
    }

    public String getIN_NAME() {
        return IN_NAME;
    }

    public void setIN_NAME(String IN_NAME) {
        this.IN_NAME = IN_NAME;
    }

    public String getTRSC_AMT() {
        return TRSC_AMT;
    }

    public void setTRSC_AMT(String TRSC_AMT) {
        this.TRSC_AMT = TRSC_AMT;
    }

    public String getSEC_MARK() {
        return SEC_MARK;
    }

    public void setSEC_MARK(String SEC_MARK) {
        this.SEC_MARK = SEC_MARK;
    }
}
