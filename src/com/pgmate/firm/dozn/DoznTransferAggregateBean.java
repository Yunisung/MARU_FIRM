package com.pgmate.firm.dozn;

/**
 * 집계 전문
 */
public class DoznTransferAggregateBean extends DoznBaseBean{
    private long telegram_no;     //거래고유번호
    private String tr_dt;           //조회일자 yyyyMMdd
    private String drw_bank_code;   //은행코드
    private long division_code;   //1: 예금주조회, 2: 점유인증, 3: 송금(지급이체)


    public DoznTransferAggregateBean() {}

    public String getDrw_bank_code() {
        return drw_bank_code;
    }

    public long getTelegram_no() {
        return telegram_no;
    }

    public void setDrw_bank_code(String drw_bank_code) {
        this.drw_bank_code = drw_bank_code;
    }

    public void setTelegram_no(long telegram_no) {
        this.telegram_no = telegram_no;
    }

    public long getDivision_code() {
        return division_code;
    }

    public String getTr_dt() {
        return tr_dt;
    }

    public void setDivision_code(long division_code) {
        this.division_code = division_code;
    }

    public void setTr_dt(String tr_dt) {
        this.tr_dt = tr_dt;
    }
}
