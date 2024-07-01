package com.pgmate.firm.coocon;

public class CooconTransferCheckBean extends CooconBaseBean{
    private String TRT_INST_CD;
    private String TRSC_DT;
    private String TRSC_SEQ_NO;
    private String RQRE_TMSG_NO;
    private String BANK_CD;

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

    public void setTRSC_DT(String TRSC_DT) {
        this.TRSC_DT = TRSC_DT;
    }

    public String getTRSC_DT() {
        return TRSC_DT;
    }

    public void setTRT_INST_CD(String TRT_INST_CD) {
        this.TRT_INST_CD = TRT_INST_CD;
    }

    public String getTRT_INST_CD() {
        return TRT_INST_CD;
    }

    public String getRQRE_TMSG_NO() {
        return RQRE_TMSG_NO;
    }

    public void setRQRE_TMSG_NO(String RQRE_TMSG_NO) {
        this.RQRE_TMSG_NO = RQRE_TMSG_NO;
    }

    @Override
    public String toString() {
        return "CooconTransferCheckBean{" +
                "TRT_INST_CD='" + TRT_INST_CD + '\'' +
                ", TRSC_DT='" + TRSC_DT + '\'' +
                ", TRSC_SEQ_NO='" + TRSC_SEQ_NO + '\'' +
                ", RQRE_TMSG_NO='" + RQRE_TMSG_NO + '\'' +
                ", BANK_CD='" + BANK_CD + '\'' +
                '}';
    }
}

