package com.pgmate.firm.hyphen;

public class ArsBean extends HyphenBaseBean{
    //Request Parameter
    private String compcode;        //ARS업체코드
    private String phoneno;         //휴대폰번호
    private String service;         //서비스분류
    private String svc_type;        //기능분류
    private String usedrecord;      //녹취파일사용여부
    private String authno;          //사용자가 입력할 인증번호
    private String filler1;         //인증사용 목적

    //Response Parameter
    private String record;          //녹취내용
    private String trace_no;        //처리일련번호

    public ArsBean() {

    }

    public void setAuthno(String authno) {
        this.authno = authno;
    }

    public String getAuthno() {
        return authno;
    }

    public String getCompcode() {
        return compcode;
    }

    public String getFiller1() {
        return filler1;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getRecord() {
        return record;
    }

    public String getService() {
        return service;
    }

    public String getSvc_type() {
        return svc_type;
    }

    public String getTrace_no() {
        return trace_no;
    }

    public String getUsedrecord() {
        return usedrecord;
    }

    @Override
    public String toString() {
        return "ArsBean{" +
                "compcode='" + compcode + '\'' +
                ", phoneno='" + phoneno + '\'' +
                ", service='" + service + '\'' +
                ", svc_type='" + svc_type + '\'' +
                ", usedrecord='" + usedrecord + '\'' +
                ", authno='" + authno + '\'' +
                ", filler1='" + filler1 + '\'' +
                ", record='" + record + '\'' +
                ", trace_no='" + trace_no + '\'' +
                '}';
    }

    public void setCompcode(String compcode) {
        this.compcode = compcode;
    }

    public void setFiller1(String filler1) {
        this.filler1 = filler1;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setSvc_type(String svc_type) {
        this.svc_type = svc_type;
    }

    public void setTrace_no(String trace_no) {
        this.trace_no = trace_no;
    }

    public void setUsedrecord(String usedrecord) {
        this.usedrecord = usedrecord;
    }
}
