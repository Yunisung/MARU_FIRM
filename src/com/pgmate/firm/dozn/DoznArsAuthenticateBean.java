package com.pgmate.firm.dozn;

/**
 * ARS인증
 */
public class DoznArsAuthenticateBean extends DoznBaseBean{
    private long telegram_no;   //거래고유번호
    private String bank_code;   //은행코드
    private String account;     //계좌번호
    private String depositor;   //예금주명
    private String phone_no;    //휴대전화번호
    private String auth_no;     //인증번호
    private String send_no;     //발신번호 : 미입력시 더즌번호 사용
    private String org_nm;      //이용기관명

    public DoznArsAuthenticateBean() {}

    public void setTelegram_no(long telegram_no) {
        this.telegram_no = telegram_no;
    }

    public long getTelegram_no() {
        return telegram_no;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBank_code() {
        return bank_code;
    }

    public String getAccount() {
        return account;
    }

    public String getAuth_no() {
        return auth_no;
    }

    public String getDepositor() {
        return depositor;
    }

    public String getOrg_nm() {
        return org_nm;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getSend_no() {
        return send_no;
    }

    public void setAuth_no(String auth_no) {
        this.auth_no = auth_no;
    }

    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    public void setOrg_nm(String org_nm) {
        this.org_nm = org_nm;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public void setSend_no(String send_no) {
        this.send_no = send_no;
    }
}
