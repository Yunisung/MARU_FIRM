package com.pgmate.firm.dozn;

/**
 * 이체 전문
 */
public class DoznTransferBean extends DoznBaseBean{
    private String drw_bank_code;       //모은행코드
    private long telegram_no;           //거래고유번호
    private String drw_account;         //모계좌번호
    private String drw_account_cntn;    //모계좌적요 (출금계좌에 찍히는 내용)
    private String rv_bank_code;        //입금은행코드
    private String rv_account;          //입금계좌번호
    private String rv_account_cntn;     //입금계좌적요
    private long amount;                //거래금액
    private String sign_no;             //복기부호
    private String tr_dt;               //전송일자
    private String tr_tm;               //전송시간

    public DoznTransferBean() {}

    public void setTr_dt(String tr_dt) {
        this.tr_dt = tr_dt;
    }

    public String getTr_dt() {
        return tr_dt;
    }

    public void setTelegram_no(long telegram_no) {
        this.telegram_no = telegram_no;
    }

    public void setDrw_bank_code(String drw_bank_code) {
        this.drw_bank_code = drw_bank_code;
    }

    public String getDrw_bank_code() {
        return drw_bank_code;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }

    public long getTelegram_no() {
        return telegram_no;
    }

    public String getDrw_account() {
        return drw_account;
    }

    public String getDrw_account_cntn() {
        return drw_account_cntn;
    }

    public String getRv_account() {
        return rv_account;
    }

    public String getRv_account_cntn() {
        return rv_account_cntn;
    }

    public String getRv_bank_code() {
        return rv_bank_code;
    }

    public String getSign_no() {
        return sign_no;
    }

    public String getTr_tm() {
        return tr_tm;
    }

    public void setDrw_account(String drw_account) {
        this.drw_account = drw_account;
    }

    public void setDrw_account_cntn(String drw_account_cntn) {
        this.drw_account_cntn = drw_account_cntn;
    }

    public void setRv_account(String rv_account) {
        this.rv_account = rv_account;
    }

    public void setRv_account_cntn(String rv_account_cntn) {
        this.rv_account_cntn = rv_account_cntn;
    }

    public void setRv_bank_code(String rv_bank_code) {
        this.rv_bank_code = rv_bank_code;
    }

    public void setSign_no(String sign_no) {
        this.sign_no = sign_no;
    }

    public void setTr_tm(String tr_tm) {
        this.tr_tm = tr_tm;
    }
}
