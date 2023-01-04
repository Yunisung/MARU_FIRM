package com.pgmate.firm.hyphen;

public class FcsBean extends HyphenBaseBean{
    //Request Parameter
    private String fcs_cd;      // [필수]계좌인증 업체코드
    private String bank_cd;     // [필수]계좌은행
    private String acct_no;     // [필수]계좌번호
    private String acct_nm;     // 예금주명
    private String id_no;       // 신원확인번호
    private String amount;      // 금액
    private String seq_no;      // [필수]일련번호

    //Response Parameter
    private String name;        //조회된 계좌주

    public FcsBean() {

    }



    public String getFcs_cd() {
        return fcs_cd;
    }

    public String getAcct_nm() {
        return acct_nm;
    }

    public String getAcct_no() {
        return acct_no;
    }

    public String getAmount() {
        return amount;
    }

    public String getBank_cd() {
        return bank_cd;
    }

    public String getId_no() {
        return id_no;
    }

    public String getName() {
        return name;
    }


    public String getSeq_no() {
        return seq_no;
    }

    public void setFcs_cd(String fcs_cd) {
        this.fcs_cd = fcs_cd;
    }

    public void setAcct_nm(String acct_nm) {
        this.acct_nm = acct_nm;
    }

    public void setAcct_no(String acct_no) {
        this.acct_no = acct_no;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setBank_cd(String bank_cd) {
        this.bank_cd = bank_cd;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public void setNAME(String NAME) {
        this.name = NAME;
    }

    public void setSeq_no(String seq_no) {
        this.seq_no = seq_no;
    }

}
