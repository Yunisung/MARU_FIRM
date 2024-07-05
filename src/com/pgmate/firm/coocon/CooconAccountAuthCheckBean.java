package com.pgmate.firm.coocon;

public class CooconAccountAuthCheckBean extends CooconBaseBean{
    private String verify_tr_dt;
    private String verify_tr_no;
    private String verify_val;

    public String getVerify_tr_dt() {
        return verify_tr_dt;
    }

    public String getVerify_tr_no() {
        return verify_tr_no;
    }

    public String getVerify_val() {
        return verify_val;
    }

    public void setVerify_tr_dt(String verify_tr_dt) {
        this.verify_tr_dt = verify_tr_dt;
    }
    public void setVerify_tr_no(String verify_tr_no) {
        this.verify_tr_no = verify_tr_no;
    }

    public void setVerify_val(String verify_val) {
        this.verify_val = verify_val;
    }

    @Override
    public String toString() {
        return "CooconAccountAuthCheckBean{" +
                "verify_tr_dt='" + verify_tr_dt + '\'' +
                ", verify_tr_no='" + verify_tr_no + '\'' +
                ", verify_val='" + verify_val + '\'' +
                '}';
    }
}
