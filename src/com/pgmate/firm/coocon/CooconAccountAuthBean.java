package com.pgmate.firm.coocon;

public class CooconAccountAuthBean extends CooconBaseBean{
    private String fnni_cd;
    private String acct_no;
    private String memb_nm;
    private String verify_tp;
    private String verify_len;
    private String ptst_txt;

    public void setAcct_no(String acct_no) {
        this.acct_no = acct_no;
    }

    public String getAcct_no() {
        return acct_no;
    }

    public String getFnni_cd() {
        return fnni_cd;
    }

    public String getMemb_nm() {
        return memb_nm;
    }

    public String getPtst_txt() {
        return ptst_txt;
    }

    public String getVerify_len() {
        return verify_len;
    }

    public String getVerify_tp() {
        return verify_tp;
    }

    public void setFnni_cd(String fnni_cd) {
        this.fnni_cd = fnni_cd;
    }

    public void setMemb_nm(String memb_nm) {
        this.memb_nm = memb_nm;
    }

    public void setPtst_txt(String ptst_txt) {
        this.ptst_txt = ptst_txt;
    }

    public void setVerify_len(String verify_len) {
        this.verify_len = verify_len;
    }

    public void setVerify_tp(String verify_tp) {
        this.verify_tp = verify_tp;
    }
}
