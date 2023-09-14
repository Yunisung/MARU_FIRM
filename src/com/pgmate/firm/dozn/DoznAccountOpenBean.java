package com.pgmate.firm.dozn;

/**
 * 개시 전문
 */
public class DoznAccountOpenBean extends DoznBaseBean{
    private long telegram_no;      //거래고유번호
    private String drw_bank_code;    //대표은행코드

    public DoznAccountOpenBean() {}

    public void setTelegram_no(long telegram_no) {
        this.telegram_no = telegram_no;
    }

    public void setDrw_bank_code(String drw_bank_code) {
        this.drw_bank_code = drw_bank_code;
    }

    public long getTelegram_no() {
        return telegram_no;
    }

    public String getDrw_bank_code() {
        return drw_bank_code;
    }
}
