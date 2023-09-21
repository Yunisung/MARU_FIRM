package com.pgmate.firm.dozn;

public class DoznAccountAuthInquireBean extends DoznBaseBean{
    private long org_telegram_no;   //원거래고유번호
    private String tr_dt;           //원거래일자

    public DoznAccountAuthInquireBean() {}

    public String getTr_dt() {
        return tr_dt;
    }

    public void setTr_dt(String tr_dt) {
        this.tr_dt = tr_dt;
    }

    public long getOrg_telegram_no() {
        return org_telegram_no;
    }

    public void setOrg_telegram_no(long org_telegram_no) {
        this.org_telegram_no = org_telegram_no;
    }
}
