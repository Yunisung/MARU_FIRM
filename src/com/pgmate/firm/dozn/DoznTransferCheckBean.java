package com.pgmate.firm.dozn;

/**
 * 처리결과조회 전문
 */
public class DoznTransferCheckBean extends DoznBaseBean{
    private long org_telegram_no;   //원거래고유번호
    private String tr_dt;           //원거래일자 YYYYMMDD
    private String drw_bank_code;   //원거래 모은행코드

    public DoznTransferCheckBean() {}

    public String getDrw_bank_code() {
        return drw_bank_code;
    }

    public void setDrw_bank_code(String drw_bank_code) {
        this.drw_bank_code = drw_bank_code;
    }

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
