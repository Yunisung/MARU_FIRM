package com.pgmate.firm.dozn;

public class DoznBaseBean {
    protected String api_key; // 이용기관 API KEY
    protected String org_code; // 이용기관코드

    public String getApiKey() { return api_key;}
    public void setApiKey(String data) { api_key = data;}

    public String getOrgCode() { return org_code;}
    public void setOrgCode(String data) { org_code = data;}

}
