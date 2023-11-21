package com.pgmate.firm.dozn;

/**
 * 출금계좌등록
 */
public class DoznKycBean {
    private String trNatvNo;    //거래고유번호
    private String bnkC;        //은행코드
    private String regDsc;      //발급구분(1:신규, 2:변경, 3:해지)
    private String vrAcno;      //가상계좌번호
    private String dprnm;       //예금주명
    private String drwBnkC;     //출금은행코드
    private String drwAcno;     //출금계좌번호

    public DoznKycBean() {}

    public String getBnkC() {
        return bnkC;
    }

    public String getDprnm() {
        return dprnm;
    }

    public String getDrwAcno() {
        return drwAcno;
    }

    public String getDrwBnkC() {
        return drwBnkC;
    }

    public String getRegDsc() {
        return regDsc;
    }

    public String getTrNatvNo() {
        return trNatvNo;
    }

    public String getVrAcno() {
        return vrAcno;
    }

    public void setBnkC(String bnkC) {
        this.bnkC = bnkC;
    }

    public void setDprnm(String dprnm) {
        this.dprnm = dprnm;
    }

    public void setDrwAcno(String drwAcno) {
        this.drwAcno = drwAcno;
    }

    public void setDrwBnkC(String drwBnkC) {
        this.drwBnkC = drwBnkC;
    }

    public void setRegDsc(String regDsc) {
        this.regDsc = regDsc;
    }

    public void setTrNatvNo(String trNatvNo) {
        this.trNatvNo = trNatvNo;
    }

    public void setVrAcno(String vrAcno) {
        this.vrAcno = vrAcno;
    }
}
