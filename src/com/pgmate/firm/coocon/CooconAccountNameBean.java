package com.pgmate.firm.coocon;

public class CooconAccountNameBean extends CooconBaseBean{
    private String BANK_CD;
    private String SEARCH_ACCT_NO;
    private String ACNM_NO;
    private String ICHE_AMT;
    private String TRSC_SEQ_NO;

    public void setBANK_CD(String BANK_CD) {
        this.BANK_CD = BANK_CD;
    }

    public String getBANK_CD() {
        return BANK_CD;
    }

    public void setTRSC_SEQ_NO(String TRSC_SEQ_NO) {
        this.TRSC_SEQ_NO = TRSC_SEQ_NO;
    }

    public String getTRSC_SEQ_NO() {
        return TRSC_SEQ_NO;
    }

    public String getACNM_NO() {
        return ACNM_NO;
    }

    public String getICHE_AMT() {
        return ICHE_AMT;
    }

    public String getSEARCH_ACCT_NO() {
        return SEARCH_ACCT_NO;
    }

    public void setACNM_NO(String ACNM_NO) {
        this.ACNM_NO = ACNM_NO;
    }

    public void setICHE_AMT(String ICHE_AMT) {
        this.ICHE_AMT = ICHE_AMT;
    }

    public void setSEARCH_ACCT_NO(String SEARCH_ACCT_NO) {
        this.SEARCH_ACCT_NO = SEARCH_ACCT_NO;
    }
}
