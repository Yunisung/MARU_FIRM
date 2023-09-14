package com.pgmate.firm.dozn;

/**
 * 예금주 조회 전문
 */
public class DoznInquireDepositorBean extends DoznBaseBean {
    private String drw_bank_code;       //대표은행코드
    private long telegram_no;         //거래고유번호
    private String bank_code;           //은행코드
    private String account;             //계좌번호
//    private long amount;              //금액 (금액체크 필요한 경우 사용)
    private String check_depositor;     //실명확인여부 (Y, N) - 미입력시 N
    private long identify_no;         //식별번호 (실명확인여부가 Y일때 사용) - 생년월일(YYMMDD)/사업자번호

    public DoznInquireDepositorBean() {}

    /*public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }*/

    public String getAccount() {
        return account;
    }

    public String getBank_code() {
        return bank_code;
    }

    public String getCheck_depositor() {
        return check_depositor;
    }

    public String getDrw_bank_code() {
        return drw_bank_code;
    }

    public long getIdentify_no() {
        return identify_no;
    }

    public long getTelegram_no() {
        return telegram_no;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public void setCheck_depositor(String check_depositor) {
        this.check_depositor = check_depositor;
    }

    public void setIdentify_no(long identify_no) {
        this.identify_no = identify_no;
    }

    public void setDrw_bank_code(String drw_bank_code) {
        this.drw_bank_code = drw_bank_code;
    }

    public void setTelegram_no(long telegram_no) {
        this.telegram_no = telegram_no;
    }
}
