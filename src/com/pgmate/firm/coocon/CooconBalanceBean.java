package com.pgmate.firm.coocon;

public class CooconBalanceBean extends CooconBaseBean{
    private String TRT_INST_CD;
    private String TRSC_DT;
    private String TRSC_SEQ_NO;
    private String BANK_CD;
    private String ACCT_NO;

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

    @Override
    public String toString() {
        return "CooconBalanceBean{" +
                "TRT_INST_CD='" + TRT_INST_CD + '\'' +
                ", TRSC_DT='" + TRSC_DT + '\'' +
                ", TRSC_SEQ_NO='" + TRSC_SEQ_NO + '\'' +
                ", BANK_CD='" + BANK_CD + '\'' +
                ", ACCT_NO='" + ACCT_NO + '\'' +
                '}';
    }
}
