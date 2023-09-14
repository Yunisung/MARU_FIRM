package com.pgmate.firm.dozn;

/**
 * 잔액조회 전문
 */
public class DoznBalanceCheckBean extends DoznBaseBean {
    //Request Parameter
    private long telegram_no;     //거래고유번호
    private String drw_bank_code;   //모은행코드
    private String drw_account;     //모계좌번호

    public DoznBalanceCheckBean() {

    }

    public long getTelegram_no() { return telegram_no;}
    public void setTelegram_no(long data) { telegram_no = data;}

    public String getDrw_bank_code() { return drw_bank_code;}
    public void setDrw_bank_code(String data) { drw_bank_code = data;}

    public String getDrw_account() { return drw_account;}
    public void setDrw_account(String data) { drw_account = data;}
}
