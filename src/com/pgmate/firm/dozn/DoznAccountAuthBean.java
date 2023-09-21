package com.pgmate.firm.dozn;

/**
 * 계좌점유인증(1원인증)
 */
public class DoznAccountAuthBean extends DoznBaseBean{
    private long telegram_no;           //거래고유번호
    private String rv_bank_code;        //입금은행코드
    private String rv_account;          //입금계좌번호
    private String rv_account_cntn;     //입금계좌적요(값이없을경우 난수2자리생성)
    private long amount;                //점유인증금액(값이없을경우 1원 송금)

    public DoznAccountAuthBean() {}

    public void setRv_bank_code(String rv_bank_code) {
        this.rv_bank_code = rv_bank_code;
    }

    public void setRv_account_cntn(String rv_account_cntn) {
        this.rv_account_cntn = rv_account_cntn;
    }

    public void setRv_account(String rv_account) {
        this.rv_account = rv_account;
    }

    public String getRv_bank_code() {
        return rv_bank_code;
    }

    public String getRv_account() {
        return rv_account;
    }

    public String getRv_account_cntn() {
        return rv_account_cntn;
    }

    public long getTelegram_no() {
        return telegram_no;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setTelegram_no(long telegram_no) {
        this.telegram_no = telegram_no;
    }
}
